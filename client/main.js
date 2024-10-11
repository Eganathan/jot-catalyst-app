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

const markdownStyles = {
    "bold": {
        "token": "*",
        "tag": "strong"
    },
    "italic": {
        "token": "_",
        "tag": "em"
    },
    "underline": {
        "token": "__",
        "tag": "u"
    },
    "strikethrough": {
        "token": "~",
        "tag": "del"
    },
    "unorderedList": {
        "token": "-",
        "tag": "ul"
    },
    "inlineCode": {
        "token": "```",
        "tag": "code"
    },
    "blockQuote": {
        "token": "|  ",
        "tag": "blockquote"
    }
};

function markdownToHtml(markdown) {
    // Handle unordered lists and block quotes
    const lines = markdown.split('\n');
    let html = '';

    lines.forEach(line => {
        // Handle block quotes
        if (line.startsWith(markdownStyles.blockQuote.token)) {
            html += `<${markdownStyles.blockQuote.tag}>${line.slice(1).trim()}</${markdownStyles.blockQuote.tag}>\n`;
        } else if (line.startsWith(markdownStyles.unorderedList.token)) {
            html += `<${markdownStyles.unorderedList.tag}><li>${line.slice(1).trim()}</li></${markdownStyles.unorderedList.tag}>\n`;
        } else {
            // Handle other markdown styles
            line = line
                .replace(new RegExp(`\\${markdownStyles.inlineCode.token}([^${markdownStyles.inlineCode.token}]+)\\${markdownStyles.inlineCode.token}`, 'g'), `<${markdownStyles.inlineCode.tag}>$1</${markdownStyles.inlineCode.tag}>`) // Inline code
                .replace(new RegExp(`\\${markdownStyles.underline.token}([^${markdownStyles.underline.token}]+)\\${markdownStyles.underline.token}`, 'g'), `<${markdownStyles.underline.tag}>$1</${markdownStyles.underline.tag}>`) // Underline
                .replace(new RegExp(`\\${markdownStyles.bold.token}([^${markdownStyles.bold.token}]+)\\${markdownStyles.bold.token}`, 'g'), `<${markdownStyles.bold.tag}>$1</${markdownStyles.bold.tag}>`) // Bold
                .replace(new RegExp(`\\${markdownStyles.italic.token}([^${markdownStyles.italic.token}]+)\\${markdownStyles.italic.token}`, 'g'), `<${markdownStyles.italic.tag}>$1</${markdownStyles.italic.tag}>`) // Italic
                .replace(new RegExp(`\\${markdownStyles.strikethrough.token}([^${markdownStyles.strikethrough.token}]+)\\${markdownStyles.strikethrough.token}`, 'g'), `<${markdownStyles.strikethrough.tag}>$1</${markdownStyles.strikethrough.tag}>`); // Strikethrough
            
            html += line + '\n';
        }
    });

    return html.trim();
}

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
		const response = await fetch(API_URL + "?page=1&per_page=100");

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
                <h5 class="text-primary">${note.title|| 'Untitled'}</h5>
                <p>${markdownToHtml(note.note)}</p>
                <div class="d-flex justify-content-end">
                    <button class="btn btn-warning btn-sm me-2" onclick="editNote('${note.id}')">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="event.stopPropagation(); deleteNote('${note.id}')">Delete</button>
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
		editNote(note.id, document.getElementById('dialog_noteTitle').value.trim(), document.getElementById('noteContent').value.trim());
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
		const response = await fetch(API_URL, {
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
async function editNote(id, newTitle, newNote) {
	if (! newTitle || ! newNote) 
		return;

	const payload = {
		jot: {
			title: newTitle,
			note: newNote
		}
	};

	try {
		const response = await fetch(`${API_URL}/${id}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(payload)
		});
		if (! response.ok) 
			throw new Error("Failed to update note");

		getNotes();
		showToast("Note updated successfully");
	} catch (error) {
		handleError(error);
	}
}

// Delete note (DELETE)
async function deleteNote(id) {

	if (!confirm("Are you sure you want to delete this note?")) 
		return;
	

	try {
		const response = await fetch(`${API_URL}/${id}`, {method: 'DELETE'});
		if (! response.ok) 
			throw new Error("Failed to delete note");
		showToast("Note deleted successfully");
		getNotes(); // Refresh notes
	} catch (error) {
		handleError(error);
	}
}

function getAppTemplate() {
    return `
        <div class="container mt-5">
            <h1 class="text-center mb-4" id="jot_title"><strong>Jot</strong>ter Space</h1>

            <!-- Toast Notifications -->
            <div id="toast-container" class="position-fixed bottom-0 end-0 p-3" style="z-index: 1100;">
                <!-- Toast messages will be appended here -->
            </div>

            <!-- Loading Indicator -->
            <span class="loader" id="z_loader" aria-hidden="true"></span>

            <!-- Add Note Section -->
            ${getAddNoteSection()}

            <!-- Notes List -->
            ${getNotesListSection()}

            <!-- Empty State -->
            ${getEmptyStateSection()}
        </div>

        <!-- Dialog for Editing Note -->
        ${getEditDialogTemplate()}
    `;
}

function getAddNoteSection() {
    return `
        <div class="add-jot-card mb-4 shadow-sm">
            <div class="card-body">
                <div class="mb-3">
                    <input type="text" id="noteTitle" class="form-control" placeholder="Title" aria-label="Note Title" required>
                </div>
                <div class="mb-3">
                    <textarea id="noteDescription" class="form-control" rows="3" placeholder="Jot or note ..." aria-label="Note Description" required></textarea>
                </div>
                <button class="btn btn-primary" id="addNoteBtn" disabled>Add Jot</button>
            </div>
        </div>
    `;
}

function getNotesListSection() {
    return `
        <div id="notesContainer" class="d-none">
            <ul id="notesList" class="list-group">
                <!-- Notes will be displayed here -->
            </ul>
        </div>
    `;
}

function getEmptyStateSection() {
    return `
        <div id="emptyState" class="text-center text-muted d-none">
            <img src="https://cdn-icons-png.flaticon.com/128/8296/8296798.png" alt="No Notes" class="img-fluid" width="200" aria-hidden="true">
            <p class="mt-4">You have no notes yet. Start by adding one!</p>
        </div>
    `;
}

function getEditDialogTemplate() {
    return `
        <div id="editDialog" class="dialog" role="dialog" aria-labelledby="editDialogTitle" aria-hidden="true">
            <div class="dialog-content">
                <span class="close" onclick="closeDialog()" aria-label="Close dialog">&times;</span>
                <h2 id="editDialogTitle">Edit Note</h2>
                <form id="editNoteForm" onsubmit="updateNote(); return false;">
                    <div class="form-group">
                        <label for="dialog_noteTitle">Title:</label>
                        <input type="text" id="dialog_noteTitle" class="form-control" placeholder="Enter note title" required>
                    </div>
                    <div class="form-group">
                        <label for="noteContent">Content:</label>
                        <textarea id="noteContent" class="form-control" rows="5" placeholder="Enter note content" required></textarea>
                    </div>
                    <button id="dialog-update-btn" type="submit" class="btn btn-primary">Update Note</button>
                </form>
            </div>
        </div>
    `;
}

// Function to enable/disable the Add Note button based on input values
function toggleAddNoteButton() {
    const title = document.getElementById('noteTitle').value.trim();
    const description = document.getElementById('noteDescription').value.trim();
    const addNoteBtn = document.getElementById('addNoteBtn');

    // Enable the button if either field has content
    addNoteBtn.disabled = !(title || description);
}

function addingEvents(){
	const noteTitleInput = document.getElementById('noteTitle');
    const noteDescriptionTextarea = document.getElementById('noteDescription');

    // Initial check to set button state
    toggleAddNoteButton();

    // Add event listeners for input changes
    noteTitleInput.addEventListener('input', toggleAddNoteButton);
    noteDescriptionTextarea.addEventListener('input', toggleAddNoteButton);

    document.getElementById('addNoteBtn').addEventListener('click', addNote);
}

function onAuthSuccess() {
    document.body.innerHTML = getAppTemplate();
	addingEvents()
    getNotes();
}