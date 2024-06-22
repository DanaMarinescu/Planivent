
document.addEventListener('DOMContentLoaded', function() {
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