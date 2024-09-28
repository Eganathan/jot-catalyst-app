const API_URL = 'https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots';


document.addEventListener('DOMContentLoaded', function() {

    var userManagement = catalyst.auth;
    var currentUserPromise = userManagement.isUserAuthenticated();
    currentUserPromise
    .then((response) => {
    console.log(response.content);
    fetchItems();
    jotCreationMeggaseValidation();
    })
    .catch((err) => {
    console.log(err);
    window.location.href = "https://jot-778776887.development.catalystserverless.com/app/";
    });
  });


        async function fetchItems() {
    try {
        const response = await fetch(API_URL + "?per_page=100&page=1");
        if (!response.ok) {
            throw new Error('Failed to fetch items');
        }
        
        const networkResponse = await response.json(); // Parse the JSON response
        const jots = networkResponse.data.jots; // Extract the 'jots' array


        if (!Array.isArray(jots)) {
            throw new Error('Invalid data format: jots is not an array');
        }

        renderItems(jots); // Pass the 'jots' array to renderItems
    } catch (error) {
        showToast('Failed to load items. Please try again later.', 'error');
        document.getElementById('itemList').innerHTML =  error ;//'<div class="error">Failed to load items. Please try again later. $error</div>';
    }
}
        async function addItem() {
            const messageInput = document.getElementById('messageInput');
            const titleInput = document.getElementById('titleInput');
            const message = messageInput.value.trim();
            const title = titleInput.value.trim();

            if (message) {
                try {
                    const response = await fetch(API_URL, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ title, message, tags: [] }),
                    });

                    if (!response.ok) {
                        throw new Error('Failed to add item');
                    }

                    const newItem = await response.json();
                    // items.push(newItem);
                    // renderItems();
                    messageInput.value = '';
                    titleInput.value = '';
                    showToast('Jot added successfully!', 'success');
                } catch (error) {
                    showToast('Failed to add jot. Please try again.', 'error');
                }
            } else {
                showToast('Please enter a message.', 'error');
            }
        }

        async function updateItem(id, updatedData) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(updatedData),
                });

                if (!response.ok) {
                    throw new Error('Failed to update item');
                }

                const updatedItem = await response.json();
                const index = items.findIndex(item => item.id === id);
                if (index !== -1) {
                    items[index] = updatedItem;
                    // renderItems();
                    showToast('Jot updated successfully!', 'success');
                }
            } catch (error) {
                showToast('Failed to update jot. Please try again.', 'error');
            }
        }

        async function deleteItem(id) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {
                    method: 'DELETE',
                });

                if (!response.ok) {
                    throw new Error('Failed to delete item');
                }

                items = items.filter(item => item.id !== id);
                // renderItems();
                showToast('Jot deleted successfully!', 'success');
            } catch (error) {
                showToast('Failed to delete jot. Please try again.', 'error');
            }
        }

        function renderItems(jots) {
            console.log(jots);
            const itemList = document.getElementById('itemList');
            itemList.innerHTML = '';

            jots.forEach(item => {
                const itemElement = document.createElement('div');
                itemElement.className = 'item';
                itemElement.innerHTML = `
                    ${item.title ? `<h3>${item.title}</h3>` : ''}
                    <p>${item.note}</p>
                    <div class="item-actions">
                        <button onclick="editItem(${item.id})">Edit</button>
                        <button onclick="deleteItem(${item.id})">Delete</button>
                        <button onclick="shareItem(${item.id})">Share</button>
                    </div>
                `;
                itemList.appendChild(itemElement);
            });

            //For tags
            // <!-- <div class="tags">
            //              ${item.tags.map(tag => `<span class="tag">${tag}</span>`).join('')}
            //              <button class="add-tag" onclick="addTag(${item.id})">+</button>
            //          </div>-->
        }

        function editItem(id) {
            const item = items.find(item => item.id === id);
            if (item) {
                const newTitle = prompt('Edit title (leave empty to remove):', item.title);
                const newMessage = prompt('Edit your message:', item.message);
                if (newMessage !== null && newMessage.trim() !== '') {
                    updateItem(id, { ...item, title: newTitle.trim(), message: newMessage.trim() });
                } else {
                    showToast('Message cannot be empty.', 'error');
                }
            }
        }

        function shareItem(id) {
            showToast('Sharing functionality not implemented in this demo.', 'error');
        }

        async function addTag(id) {
            const item = items.find(item => item.id === id);
            if (item) {
                const newTag = prompt('Enter a new tag:');
                if (newTag && newTag.trim()) {
                    const updatedTags = [...item.tags, newTag.trim()];
                    await updateItem(id, { ...item, tags: updatedTags });
                }
            }
        }

        function showToast(message, type = 'error') {
            const toastContainer = document.getElementById('toastContainer');
            const toast = document.createElement('div');
            toast.className = `toast ${type}`;
            toast.textContent = message;
            toastContainer.appendChild(toast);

            setTimeout(() => {
                toast.classList.add('show');
            }, 10);

            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => {
                    toastContainer.removeChild(toast);
                }, 300);
            }, 3000);
        }


        //ui
        function jotCreationMeggaseValidation(){
            const messageInput = document.getElementById('messageInput');
            const createJotButton = document.getElementById('jot-create-button');

// Add an event listener to the textarea to monitor input
messageInput.addEventListener('input', function() {
    if (messageInput.value.trim() !== '') {
        createJotButton.disabled = false; // Enable the button
    } else {
        createJotButton.disabled = true; // Disable the button
    }
});
        }