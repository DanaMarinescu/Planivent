document.addEventListener("DOMContentLoaded", function() {
    var dropdown = document.getElementById('categoriesDropdown');
    var dropdownMenu = document.querySelector('.dropdown-menu');

    dropdown.addEventListener('click', function(event) {
        event.preventDefault();
        var isVisible = dropdownMenu.style.display === 'flex';
        dropdownMenu.style.display = isVisible ? 'none' : 'flex';

        if (isVisible) {
            dropdown.classList.remove('active');
        } else {
            dropdown.classList.add('active');
        }
    });

    const reviewForm = document.querySelector('.review-form form');
    if (reviewForm) {
        reviewForm.addEventListener('submit', function(event) {
            const titleInput = document.querySelector('input[name="review_title"]');
            const textInput = document.querySelector('textarea[name="review_text"]');
            let valid = true;

            if (!titleInput.value.trim()) {
                alert('Titlu obligatoriu.');
                valid = false;
            }

            if (!textInput.value.trim()) {
                alert('Descriere obligatorie.');
                valid = false;
            }

            if (!valid) {
                event.preventDefault();
            }
        });
    }

    var viewMoreLink = document.getElementById('viewMoreLink');
    var allReviews = document.querySelectorAll('.review-item');

    viewMoreLink.addEventListener('click', function(event) {
        event.preventDefault();
        var visibleReviews = Array.from(allReviews).filter(review => !review.classList.contains('hidden'));
        var isAnyHidden = visibleReviews.length < allReviews.length;

        allReviews.forEach(function(review, index) {
            if (index >= 4) {
                review.classList.toggle('hidden');
            }
        });

        if (isAnyHidden) {
            viewMoreLink.textContent = 'vezi mai puțin...';
        } else {
            viewMoreLink.textContent = 'vezi mai mult...';
        }
    });
});

//chat
function setCurrentDateTime() {
    const now = new Date();
    const utcNow = new Date(now.getTime() + now.getTimezoneOffset() * 6000);
    const romaniaOffset = 3; // Romania is UTC+3
    const romaniaNow = new Date(utcNow.getTime() + romaniaOffset * 3600000);
    document.getElementById('startDate').value = romaniaNow.toISOString().slice(0, 19);
}

$(document).ready(function() {
    $('.gallery-thumb').click(function() {
        $('.gallery-thumb').removeClass('active');
        $(this).addClass('active');
        var newImage = $(this).attr('src').replace('thumb', 'cropped');
        $('#main_image img').attr('src', newImage);
    });
});

var stompClient = null;

function connect() {
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/messages/topic/public', function (chatMessage) {
            displayChatMessage(JSON.parse(chatMessage.body));
        });
    });
}

function sendMessage() {
    var chatMessage = {
        sender: $("#sender").val(),
        content: $("#message").val(),
        type: 'CHAT'
    };
    stompClient.send("/messages/chat.sendMessage", {}, JSON.stringify(chatMessage));
    $("#message").val("");
}

function displayChatMessage(message) {
    $("#chatMessages").append("<tr><td>" + message.sender + ": " + message.content + "</td></tr>");
}

$(document).ready(function() {
    $("#connect").click(function() { connect(); });
    $("#send").click(function() { sendMessage(); });
});