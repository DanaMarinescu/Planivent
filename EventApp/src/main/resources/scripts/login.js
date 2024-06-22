var a = document.getElementById("loginBtn");
var b = document.getElementById("registerBtn");
var x = document.getElementById("login");
var y = document.getElementById("register");

function login() {
    x.style.left = "4px";
    y.style.right = "-520px";
    a.className += " white-btn";
    b.className = "btn";
    x.style.opacity = 1;
    y.style.opacity = 0;
}

function register() {
    x.style.left = "-510px";
    y.style.right = "5px";
    a.className = "btn";
    b.className += " white-btn";
    x.style.opacity = 0;
    y.style.opacity = 1;
}

document.addEventListener('DOMContentLoaded', function () {
    var loginBtn = document.getElementById('loginBtn');
    var registerBtn = document.getElementById('registerBtn');
    var loginForm = document.getElementById('login');
    var registerForm = document.getElementById('register');

    loginBtn.addEventListener('click', function (event) {
        event.preventDefault();
        loginForm.style.display = 'block';
        registerForm.style.display = 'none';
        loginBtn.classList.add('white-btn');
        registerBtn.classList.remove('white-btn');
    });

    registerBtn.addEventListener('click', function (event) {
        event.preventDefault();
        registerForm.style.display = 'block';
        loginForm.style.display = 'none';
        registerBtn.classList.add('white-btn');
        loginBtn.classList.remove('white-btn');
    });
});