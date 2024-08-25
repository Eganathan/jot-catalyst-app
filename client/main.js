const loginURL = "https://jot-778776887.development.catalystserverless.com/__catalyst/auth/login";
const isLoggedIn = 
    document.cookie.includes('_iamadt_client_10068768328') || 
    document.cookie.includes('_iambdt_client_10068768328') || 
    document.cookie.includes('_z_identity') || 
    document.cookie.includes('iamcsr');


if (isLoggedIn) {
    console.log('User is authenticated, access granted');
    postLoginLogic();
} else {
    window.location.href = loginURL;
    console.log('Not logged in, redirecting to login');
}


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
        listItem.innerHTML = `
            <h3>${jot.title}</h3>
            <p>${jot.description}</p>
            <p>Created: ${jot.created_time}</p>
            <p>Modified: ${jot.modified_time}</p>
        `;
        jotList.appendChild(listItem);
    });
}

function displayError(message) {
    const errorContainer = document.getElementById('errorContainer'); 
    errorContainer.textContent = message;
    errorContainer.style.display = 'block';
}

