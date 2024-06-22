document.addEventListener("DOMContentLoaded", function() {
    fetchUserList();
});

$(document).ready(function() {
    $('.gallery-thumb').click(function() {
        $('.gallery-thumb').removeClass('active');
        $(this).addClass('active');
        var newImage = $(this).attr('src').replace('thumb', 'cropped');
        $('#main_image img').attr('src', newImage);
    });
});


var selectedUserId = null;

var socket = new SockJS('/chat');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function () {
    stompClient.subscribe('/topic/messages', function (message) {
        showMessage(JSON.parse(message.body).content);
    });
});

function sendMessage() {
    var token = document.querySelector('meta[name="_csrf"]').content;
    var header = document.querySelector('meta[name="_csrf_header"]').content;
    var messageInput = document.getElementById('messageInput');
    var messageText = messageInput.value.trim();
    if (messageText === '') {
        alert('Mesajul nu poate fi gol.');
        return;
    }
    var userId = selectedUserId;
    var chatTitle = document.getElementById('chatTitle');
    var productId = chatTitle.getAttribute('data-product-id');

    if (!userId || !productId) {
        alert('Nu există userul sau produsul selectat.');
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/messages/send', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.setRequestHeader(header, token);

    xhr.onload = function() {
        if (xhr.status >= 200 && xhr.status < 300) {
            messageInput.value = '';
            var messagesArea = document.getElementById('messages');
            var newMessageDiv = document.createElement('div');
            newMessageDiv.className = 'message message-right';
            newMessageDiv.textContent = `${messageText}`;

            const dateDiv = document.createElement('div');
            dateDiv.className = 'message-date';
            dateDiv.textContent = new Date().toLocaleString();

            messagesArea.appendChild(newMessageDiv);
            newMessageDiv.appendChild(dateDiv);
            messagesArea.scrollTop = messagesArea.scrollHeight;

        } else {
            alert('Eroare la trimiterea mesasjului: ' + xhr.responseText);
        }
    };

    xhr.onerror = function() {
        alert('A apărut o eroare.');
    };

    var data = JSON.stringify({
        messageText: messageText,
        recipientId: userId,
        productId: productId
    });
    xhr.send(data);
}

function showMessage(message) {
    var messageArea = document.getElementById('messageArea');
    messageArea.innerHTML += message + '<br/>';
}

function fetchUserList() {
    fetch('/messages/users')
        .then(response => response.json())
        .then(users => {
            console.log(users);
            const userList = document.getElementById('userList');
            userList.innerHTML = '';
            users.forEach(user => {
                const li = document.createElement('li');
                li.className = 'list-group-item d-flex justify-content-between align-items-center';

                const nameSpan = document.createElement('span');
                nameSpan.textContent = user.name;
                li.appendChild(nameSpan);

                const deleteBtn = document.createElement('button');
                deleteBtn.textContent = 'X';
                deleteBtn.className = 'delete-btn';
                deleteBtn.onclick = (event) => {
                    event.stopPropagation();
                    deleteConversation(user.id);
                };

                li.appendChild(deleteBtn);
                li.onclick = () => selectUser(user.id);
                userList.appendChild(li);
            });
        })
        .catch(error => console.error('Eroare la îcărcarea utilizatorilor:', error));
}

function deleteConversation(userId) {
    if (confirm('Sigur vreţi să ştergeţi această conversaţie?')) {
        fetch('/messages/delete/' + userId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        })
            .then(response => {
                if (response.ok) {
                    fetchUserList();
                    const messagesArea = document.getElementById('messages');
                    messagesArea.innerHTML = '';
                    const chatTitle = document.getElementById('chatTitle');
                    chatTitle.textContent = 'Produs de interes';
                    chatTitle.removeAttribute('data-product-id');
                } else {
                    alert('Ştergerea conversaţiei nu s-a putut realiza!');
                }
            })
            .catch(error => console.error('Eroare la ştergerea conversaţiei.: ', error));
    }
}

function selectUser(userId) {
    selectedUserId = userId;
    const messagesArea = document.getElementById('messages');
    const chatTitle = document.getElementById('chatTitle');
    messagesArea.innerHTML = '';

    fetch('/messages/history/' + userId)
        .then(response => {
            if (!response.ok) {
                throw new Error('Eroare de reţea ' + response.statusText);
            }
            return response.json();
        })
        .then(messages => {
            if (!Array.isArray(messages)) {
                throw new Error('Mesaj în format invalid.');
            }
            if (messages.length === 0) {
                messagesArea.textContent = 'Nu există mesaje încă.';
            } else {
                messages.forEach(message => {
                    var existingProductMessages = messagesArea.querySelectorAll('.system-message');
                    var lastSystemMessage = existingProductMessages[existingProductMessages.length - 1];
                    var productMessageAlreadyAdded = lastSystemMessage && lastSystemMessage.textContent.includes(message.productName);

                    if (!productMessageAlreadyAdded) {
                        var systemMessageDiv = document.createElement('div');
                        systemMessageDiv.className = 'message system-message';
                        systemMessageDiv.textContent = `Aţi început conversaţia pentru: ${message.productName}`;
                        messagesArea.appendChild(systemMessageDiv);
                    }

                    chatTitle.textContent = (`${message.productName}`);
                    chatTitle.setAttribute('data-product-id', message.productId)

                    const messageDiv = document.createElement('div');
                    const dateDiv = document.createElement('div');

                    messageDiv.className = 'message';
                    messageDiv.classList.add('message');
                    dateDiv.className = 'message-date';
                    dateDiv.textContent = (`${message.startDate}`);

                    if (message.senderId === userId) {
                        messageDiv.classList.add('message-left');
                        messageDiv.textContent = `${message.messageText}`;
                    } else {
                        messageDiv.classList.add('message-right');
                        messageDiv.textContent = `${message.messageText}`;
                    }
                    messagesArea.appendChild(messageDiv);
                    messageDiv.appendChild(dateDiv);
                });
                messagesArea.scrollTop = messagesArea.scrollHeight;
            }
        })
        .catch(error => {
            messagesArea.textContent = 'Eroare la încărcarea mesajelor.';
        });
}
