// Developement
const API_URL = 'https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots';
const LOGIN_URL = "https://jot-778776887.development.catalystserverless.com/app/"

// //User auth check
// document.addEventListener('DOMContentLoaded', function() {
//     var userManagement = catalyst.auth;
//     var currentUserPromise = userManagement.isUserAuthenticated();
//     currentUserPromise
//     .then((response) => {
//     console.log(response.content);
//     fetchItems();})
//     .catch((err) => {
//     console.log(err);
//     window.location.href = LOGIN_URL;
//     });
// });

const apiUrl = API_URL

// Function to show toast notifications
function showToast(message, type = 'success') {
	const toastEl = document.createElement('div');
	toastEl.classList.add('toast', `bg-${type}`, 'text-white', 'fade', 'show');
	toastEl.innerHTML = `
        <div class="toast-header">
            <strong class="me-auto">${
		type.charAt(0).toUpperCase() + type.slice(1)
	}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
        </div>
        <div class="toast-body">${message}</div>
    `;
	document.getElementById('toast-container').appendChild(toastEl);
	setTimeout(() => {
		toastEl.classList.remove('show');
	}, 3000);
}

// Error handling
function handleError(error) {
	console.error("Error:", error);
	showToast(error.message || "An error occurred", "danger");
}

async function getNotes() {
	try {
		showLoading(true); // Show loading indicator
		const response = await fetch(apiUrl + "?page=1&per_page=100");

		// Check if the response status is 200
		if (response.status === 200) { // Only 200 is allowed
			const result = await response.json(); // Parse JSON response
			const notes = result.data.jots; // Get the notes from the response
			renderNotes(notes); // Call renderNotes only if the response is 200
		} else if (response.status === 204) {
			showEmptyState(true); // Call a function to show a message for empty state
		} else { // Handle errors for other status codes (e.g., 404, 500)
			throw new Error(`Failed to fetch notes! Status: ${
				response.status
			}`);
		}
	} catch (error) {
		handleError(error); // Handle the error
	} finally {
		showLoading(false); // Hide loading indicator
	}
}

// Render notes in the UI
function renderNotes(notes) {
	console.log("data: " + notes);
	const notesList = document.getElementById('notesList');
	notesList.innerHTML = '';

	if (notes.length === 0) {
		showEmptyState(true);
	} else {
		showEmptyState(false);
		notes.forEach(note => {
			const li = document.createElement('li');
			li.classList.add('list-group-item', 'shadow-sm', 'mb-3');
			li.innerHTML = `
                <h5 class="text-primary">${
				note.title || 'Untitled'
			}</h5>
                <p>${
				note.note
			}</p>
                <div class="d-flex justify-content-end">
                    <button class="btn btn-warning btn-sm me-2" onclick="editNote('${
				note.id
			}')">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteNote('${
				note.id
			}')">Delete</button>
                </div>
            `;

			// Add click event to the list item to open the dialog
			li.addEventListener('click', () => {
				openEditDialog(note);
			});

			notesList.appendChild(li);
		});
	}
}

// Function to open the edit dialog and fill it with note data
function openEditDialog(note) {
	const dialog = document.getElementById('editDialog');

    console.log(note);
	// Fill in the form with the note's current values
	document.getElementById('dialog_noteTitle').value = note.title;
	document.getElementById('noteContent').value = note.note;

	// Show the dialog
	document.getElementById('editDialog').style.display = 'block';

	// Close dialog when clicking outside of the dialog content
	window.onclick = function (event) {
		if (event.target === dialog) {
			closeDialog();
		}
	};

	// Handle form submission (You can replace this with your update logic)
	document.getElementById('editNoteForm').onsubmit = function (event) {
		event.preventDefault();
		// Prevent the form from submitting
		// Here you would typically update the note and refresh the list
        editNote(
            note.id,
            document.getElementById('dialog_noteTitle').value.trim(),
            document.getElementById('noteContent').value.trim()
         );
		console.log('Note updated:', {
			title: document.getElementById('dialog_noteTitle').value,
			content: document.getElementById('noteContent').value
		});
		dialog.style.display = 'none'; // Close the dialog
	};
}

function closeDialog() {
	const dialog = document.getElementById('editDialog');
	dialog.style.display = 'none';
	// Hide the dialog
	// Optional: Clear the form fields
	document.getElementById('noteTitle').value = '';
	document.getElementById('noteContent').value = '';
}

// Show or hide loading spinner
function showLoading(isLoading) {
	const loadingIndicator = document.getElementById("z_loader");
	if (isLoading) {
		loadingIndicator.style.opacity = 1; // Show the loading indicator
	} else {
		loadingIndicator.style.opacity = 0; // Hide the loading indicator
	}
}

// Show or hide empty state
function showEmptyState(isEmpty) {
	document.getElementById('emptyState').classList.toggle('d-none', ! isEmpty);
	document.getElementById('notesContainer').classList.toggle('d-none', isEmpty);
}

// Add a new note (CREATE)
async function addNote() {
	const title = document.getElementById('noteTitle').value.trim();
	const note = document.getElementById('noteDescription').value.trim();

	if (title === '' || note === '') {
		return showToast("Please enter both title and description", "warning");
	}

	const payload = {
		jot: {
			note: note,
			title: title
		}
	};

	try {
		const response = await fetch(apiUrl, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(payload)
		});
		if (! response.ok) 
			throw new Error("Failed to add note");
		


		showToast("Note added successfully");
		document.getElementById('noteTitle').value = '';
		document.getElementById('noteDescription').value = '';
		getNotes(); // Refresh notes
	} catch (error) {
		handleError(error);
	}
}

// Edit note (UPDATE)
async function editNote(id,newTitle,newNote) {
	if (! newTitle || ! newNote) 
		return;
	
	try {
		const response = await fetch(`${apiUrl}/${id}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(
				{title: newTitle, note: newNote}
			)
		});
		if (! response.ok) 
			throw new Error("Failed to update note");
		
		showToast("Note updated successfully");
		getNotes(); // Refresh notes
	} catch (error) {
		handleError(error);
	}
}

// Delete note (DELETE)
async function deleteNote(id) { 

	if (!confirm("Are you sure you want to delete this note?")) 
		return;
	
	try {
		const response = await fetch(`${apiUrl}/${id}`, {method: 'DELETE'});
		if (! response.ok) 
			throw new Error("Failed to delete note");
		


		showToast("Note deleted successfully");
		getNotes(); // Refresh notes
	} catch (error) {
		handleError(error);
	}
}

// Initial fetch of notes
document.addEventListener('DOMContentLoaded', getNotes);
