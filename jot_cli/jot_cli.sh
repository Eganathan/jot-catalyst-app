#!/bin/bash

# API URLS
LOGIN_URL="https://jot-778776887.development.catalystserverless.com/app/"
NOTES_URL="https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots?page=1&per_page=100"
BASE_URL="https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots"
COOKIE_FILE="/tmp/jot_cookie.txt"

# Function to open the login page in the browser
# Function to open the login page in the browser and bring it to the front
open_browser_for_login() {
  echo "Opening browser for login..."
  
  # Get the OS type
  OS_TYPE="$(uname -s)"

  case "$OS_TYPE" in
    Darwin)  # macOS
      open "$LOGIN_URL"  # Open the URL in the default browser
      sleep 1  # Wait for the browser to open
      osascript -e "tell application \"System Events\" to activate"  # Bring it to front
      ;;

    Linux)
      xdg-open "$LOGIN_URL"  # Open the URL in the default browser
      sleep 1  # Wait for the browser to open
      wmctrl -a "Browser"  # Replace "Browser" with the name of your browser window
      ;;

    MINGW* | MSYS* | CYGWIN*)  # Windows
      start "$LOGIN_URL"  # Open the URL in the default browser
      # PowerShell command to bring it to the front
      powershell -Command "Start-Process 'powershell' -ArgumentList 'Start-Sleep -Seconds 1; [void][System.Reflection.Assembly]::LoadWithPartialName('System.Windows.Forms'); [System.Windows.Forms.SendKeys]::SendWait('%{TAB}')'"
      ;;

    *)
      echo "Unsupported OS: $OS_TYPE"
      exit 1
      ;;
  esac
  
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

print_jot() {
    local id="$1"
    local title="$2"
    local note="$3"
    local modified_time="$4"

    light_grey_color=$'\033[37;1m'
    color_end=$'\033[0m'

    s_bold=$'\033[1m'
    stopBold=$'\033[0m'

    green_color=$'\033[32m'
        
printf "%s\n" "$light_grey_color $id $color_end" "$s_bold $title $stopBold" " $note " " $modified_time" " -"
}

fetch_notes() {
  clear
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
  echo " "
 jots_array=$(echo "$RESPONSE_BODY" | jq -r '.data.jots[]');
 for element in "${jots_array[@]}"; do
  echo "$element" | jq -r '"\(.id) \(.title) \(.note) \(.modified_time)"' | while read -r id title note modified_time; do
    print_jot "$id" "$title" "$note" "$modified_time"
done
done
}

create_jot_post_request() {
  local title="$1"
  local note="$2"

  echo "Title: $title, Note: $note"

  # Check for cookie file and read its content
  if [[ -f "$COOKIE_FILE" ]]; then
    COOKIE_STRING=$(<"$COOKIE_FILE")
  else
    echo "No cookie found. Opening login URL..."
    open_browser_for_login  # Open login URL if no cookie
    input_cookies  # Ask for cookies after login
    COOKIE_STRING=$(<"$COOKIE_FILE")
  fi

  echo "Cookie check completed"

  # Create JSON payload
  DATA=$(cat <<EOF
{
"jot":{
  "title": "$title",
  "note": "$note"
}
}
EOF
)

  echo "JSON payload created"

  # Send POST request with cookies and additional headers
  response=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "$NOTES_URL" \
    -H "Content-Type: application/json" -H "Accept: */*" \
    --cookie "$COOKIE_STRING" -d "$DATA")

  # Print the response and HTTP code
  echo "Response: $response"
  
}

#prompt to create
create_prompt(){
echo "Please enter the title:"
read title

echo "Please enter the note:"
read note

# Call the function with user input
create_jot_post_request "$title" "$note"
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
  fetch_notes
  echo "Enter a command (fetch, create, clear, logout or exit): "
  read -r COMMAND

  case "$COMMAND" in
    fetch)
      fetch_notes
      ;;
      create)
      create_prompt
      ;;
    logout)
      logout
      clear
      ;;
      clear)
      clear
      ;;
    exit)
      echo "Exiting the app."
      clear
      break
      ;;
    *)
      echo "Invalid command. Please use fetch, logout, or exit."
      ;;
  esac
done
