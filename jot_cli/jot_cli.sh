#!/bin/bash

# API URLS
LOGIN_URL="https://jot-778776887.development.catalystserverless.com/app/"
NOTES_URL="https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots?page=1&per_page=100"
COOKIE_FILE="/tmp/jots_cookie.txt"

# Function to open the login page in the browser
open_browser_for_login() {
  echo "Opening browser for login..."
  open -a "Google Chrome" "$LOGIN_URL"  # Open in Chrome specifically
  echo "Please log in through the browser. Press any key once you've logged in..."
  read -n 1 -s
}

# Function to ask user for cookies
input_cookies() {
  read -sp "Please paste the cookies (and hit enter): " COOKIE_STRING
  echo # Move to a new line after input
  echo "$COOKIE_STRING" > "$COOKIE_FILE"  # Store cookies for future use
  clear
  echo "Cookies stored successfully."
}

# Function to fetch notes from the API
fetch_notes() {
  echo "Fetching notes..."
  
  # Check if the cookie file exists and read its content
  if [[ -f "$COOKIE_FILE" ]]; then
    COOKIE_STRING=$(<"$COOKIE_FILE")
  else
    echo "No cookie found. Opening login URL..."
    open_browser_for_login  # Open login URL if no cookie
    input_cookies  # Ask for cookies after login
    COOKIE_STRING=$(<"$COOKIE_FILE")
  fi

  # Make the request and capture the response
  RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --cookie "$COOKIE_STRING" -H "Accept: */*" "$NOTES_URL")

  # Extract the response body and HTTP code
  RESPONSE_BODY=$(echo "$RESPONSE" | sed -n '1,/HTTP_CODE:/p' | sed '$d')
  HTTP_CODE=$(echo "$RESPONSE" | tail -n1 | sed 's/HTTP_CODE://')

  # Check for 401 Unauthorized
  if [[ "$HTTP_CODE" -eq 401 ]]; then
    echo "Session expired or invalid cookie. Please log in again."
    rm "$COOKIE_FILE"  # Remove invalid cookie
    open_browser_for_login  # Prompt user to log in
    input_cookies  # Ask for cookies again
    COOKIE_STRING=$(<"$COOKIE_FILE")  # Update cookie string
  fi

  # Print the response body and status code
  echo "Response Code: $HTTP_CODE"
  echo "Response Body: $RESPONSE_BODY"
}

# Function to log out (delete cookie)
logout() {
  if [[ -f "$COOKIE_FILE" ]]; then
    rm "$COOKIE_FILE"
    echo "Logged out successfully. Cookie deleted."
  else
    echo "No cookie found. You are not logged in."
  fi
}

# Main script logic
while true; do
  echo "Enter a command (start, fetch, clear, logout or exit): "
  read -r COMMAND

  case "$COMMAND" in
    start)
      echo "Starting to fetch notes..."
      fetch_notes
      ;;
    fetch)
      fetch_notes
      ;;
    logout)
      logout
      ;;
      clear)
      clear
      ;;
    exit)
      echo "Exiting the app."
      break
      ;;
    *)
      echo "Invalid command. Please use start, fetch, logout, or exit."
      ;;
  esac
done
