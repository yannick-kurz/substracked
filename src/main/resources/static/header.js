(function () {
    var nav = document.getElementById('navbar');
    var hoverZone = document.getElementById('nav-hover-zone');
    var lastScroll = 0;
    var hoverActive = false;

    function show() { nav.classList.remove('hidden'); }
    function hide() { if (!hoverActive) nav.classList.add('hidden'); }

    window.addEventListener('scroll', function () {
        var cur = window.scrollY;
        if (cur <= 10)                     show();
        else if (cur < lastScroll - 8)     show();
        else if (cur > lastScroll + 8 && cur > 40) hide();
        lastScroll = cur;
    });

    hoverZone.addEventListener('mouseenter', function () { hoverActive = true;  show(); });
    hoverZone.addEventListener('mouseleave', function () { hoverActive = false; });

    window.toggleTheme = function () {
        var html  = document.documentElement;
        var next  = html.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        var icon  = document.getElementById('theme-icon');
        html.setAttribute('data-theme', next);
        localStorage.setItem('substracked-theme', next);
        icon.className = next === 'dark' ? 'ti ti-sun' : 'ti ti-moon';
    };

    var saved = localStorage.getItem('substracked-theme');
    if (saved) {
        document.documentElement.setAttribute('data-theme', saved);
        var icon = document.getElementById('theme-icon');
        if (icon) icon.className = saved === 'dark' ? 'ti ti-sun' : 'ti ti-moon';
    }

    window.toggleAvatarMenu = function () {
        var menu = document.getElementById('avatar-menu');
        var btn  = document.getElementById('avatar-btn');
        var open = !menu.hidden;
        menu.hidden = open;
        btn.setAttribute('aria-expanded', String(!open));
    };

    document.addEventListener('click', function (e) {
        var wrap = document.getElementById('avatar-wrap');
        var menu = document.getElementById('avatar-menu');
        if (wrap && menu && !wrap.contains(e.target)) {
            menu.hidden = true;
            document.getElementById('avatar-btn')
                .setAttribute('aria-expanded', 'false');
        }
    });
}());