document.addEventListener("DOMContentLoaded", function() {
    var csrfToken = $('[name="_csrf"]').attr('content');
    var csrfHeader = $('[name="_csrf_header"]').attr('content');

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

    const titleElement = document.getElementById('title');
    const descriptionElement = document.getElementById('description');
    const submitButton = document.getElementById('submit-button');

    if (titleElement && descriptionElement && submitButton) {
        submitButton.addEventListener('click', function() {
            const title = titleElement.value;
            const description = descriptionElement.value;

            fetch('/blog/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ title, description })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Succes:', data);
                })
                .catch(error => {
                    console.error('Eroare:', error);
                });
        });
    } else {
        console.error('Elemente negasite.');
    }
});