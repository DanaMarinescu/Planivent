function myMenuFunction() {
    var i = document.getElementById("navMenu");
    if (i.className === "nav-menu") {
        i.className += " responsive";
    } else {
        i.className = "nav-menu";
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const setClickHandler = (id, url) => {
        const element = document.getElementById(id);
        if (element) {
            element.onclick = function() {
                window.location.href = url;
            };
        } else {
            console.error(`Elementul cu ID ${id} nu poate fi găsit.`);
        }
    };

    setClickHandler("personal-info-btn", "/user/profile");
    setClickHandler("my-products-btn", "/user/products");
    setClickHandler("calendar-btn", "/calendar");
    setClickHandler("messages-btn", "/messages");
    setClickHandler("change-password-btn", "/password-request");
    setClickHandler("logout-btn", "/logout");

    const userId = getUserIdFromURL();
    setClickHandler("notes-btn", `/notes/${userId}`);

    const dropdown = document.getElementById('categoriesDropdown');
    const dropdownMenu = document.querySelector('.dropdown-menu');
    if (dropdown && dropdownMenu) {
        dropdown.addEventListener('click', function(event) {
            event.preventDefault();
            const isVisible = dropdownMenu.style.display === 'flex';
            dropdownMenu.style.display = isVisible ? 'none' : 'flex';
            dropdown.classList.toggle('active', !isVisible);
        });
    } else {
        console.error("Categoriile nu pot fi încărcate.");
    }
});

function getUserIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('userId');
}