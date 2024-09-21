const loginURL = "https://jot-778776887.development.catalystserverless.com/__catalyst/auth/login";
const isLoggedIn = 
    document.cookie.includes('_iamadt_client') || 
    document.cookie.includes('_z_identity') || 
    document.cookie.includes('iamcsr');


// if (isLoggedIn) {
    console.log('User is authenticated, access granted');
    postLoginLogic();
// } else {
//     window.location.href = loginURL;
//     console.log('Not logged in, redirecting to login');
// }


function postLoginLogic(){
//waiting for dom content loading
    document.addEventListener('DOMContentLoaded', (event) => {
        //Creation
        const addButton = document.getElementById('addButton');
        const noteDescription = document.getElementById('note-description');
        noteDescription.addEventListener('input', () => {
          // Enable the button only if the description has content
          addButton.disabled = noteDescription.value.trim() === '';
        });
        
        
        //Create Jot
        addButton.addEventListener('click', () => {
          const noteTitle = document.getElementById('note-title').value.trim();
          const noteText = noteDescription.value.trim();
        
          // Call your fetch function here, passing both title and description
          fetch('https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(
                {
                    "jot":{
                    "title": noteTitle,
                    "message": noteText
                    }
                  }
            )
          })
          .then(response => { getBulkJots() })
          .catch(error => { /* ... handle error */ });
        
          // Clear the input fields after adding the note
          document.getElementById('note-title').value = '';
          noteDescription.value = '';
          addButton.disabled = true; // Disable the button again
        });

        // GET Request
        getBulkJots()
        
        });
}

//JOTS LIST Logic
function getBulkJots() {
    fetch('https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots?per_page=100&page=1')
    .then(response => {
        console.log(response)
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'success') {
            displayJots(data.jots);
        } else {
            displayError(data.status || 'Error fetching jots');
        }
    })
    .catch(error => {
        displayError('Error fetching jots: ' + error.message);
    });
}

function displayJots(jots) {
    const jotList = document.getElementById('jotList'); 
    jotList.innerHTML = '';

    jots.forEach(jot => {
        const listItem = document.createElement('li');
        listItem.classList.add('jot-item'); // Add a class for styling and event handling

        // Format the last modified date according to your requirements
        const formattedModifiedTime = formatDateTime(jot.modified_time);

        listItem.innerHTML = `
            <h3>${jot.title}</h3>
            <p>${jot.description}</p>
            
            <p class="last-modified">Last Modified: ${formattedModifiedTime}</p>
            
            <div class="jot-actions" style="display: none;"> 
                <button class="edit-button">Edit</button>
                <button class="delete-button">Delete</button>
                <button class="share-button">Share</button>
            </div>
        `;

        // Add event listeners to show/hide buttons on hover/focus
        listItem.addEventListener('mouseenter', () => {
            listItem.querySelector('.jot-actions').style.display = 'grid';
        });
        listItem.addEventListener('mouseleave', () => {
            listItem.querySelector('.jot-actions').style.display = 'none';
        });
        listItem.addEventListener('focusin', () => { // For keyboard focus
            listItem.querySelector('.jot-actions').style.display = 'grid';
        });
        listItem.addEventListener('focusout', () => {
            listItem.querySelector('.jot-actions').style.display = 'none';
        });

        // Add event listeners to the buttons (you'll need to implement the actual functionality)
        listItem.querySelector('.edit-button').addEventListener('click', () => {
           editJot(jot);
        });
        listItem.querySelector('.delete-button').addEventListener('click', () => {
            deleteJot(jot);
        });
        listItem.querySelector('.share-button').addEventListener('click', () => {
            shareJot(jot);
        });

        jotList.appendChild(listItem);
    });
}

function displayError(message) {
    const errorContainer = document.getElementById('errorContainer'); 
    errorContainer.textContent = message;
    errorContainer.style.display = 'block';
}

function editJot(jot){
    console.log("EDIT ==> "+jot.id)
  }

  function deleteJot(jot){
    console.log("DELETE ==> "+jot.id)
  }

function shareJot(jot){
    console.log("SHARE ==> "+jot.id)
}


function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
  
    const day = date.getDate().toString().padStart(2, '0');
    const month = new Intl.DateTimeFormat('en-US', { month: 'short' }).format(date);
    const year = date.getFullYear();   
  
    const hours = date.getHours() % 12 || 12; // Convert to 12-hour format
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const amPm = date.getHours() >= 12   ? 'pm' : 'am';
  
    if (date.toDateString() === today.toDateString()) {
      return `Today at ${hours}:${minutes} ${amPm}`;
    } else if (date.toDateString() === yesterday.toDateString()) {
      return `Yesterday at ${hours}:${minutes} ${amPm}`;
    } else {
      return `${day} ${month}, ${year} at ${hours}:${minutes} ${amPm}`;
    }
}

