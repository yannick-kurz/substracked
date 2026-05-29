(function () {

    /* ── Nav hide/show ──────────────────────────────── */
    var nav       = document.getElementById('navbar');
    var hoverZone = document.getElementById('nav-hover-zone');
    var lastScroll = 0;
    var hoverActive = false;

    function show() { nav.classList.remove('hidden'); }
    function hide() { if (!hoverActive) nav.classList.add('hidden'); }

    window.addEventListener('scroll', function () {
        var cur = window.scrollY;
        if (cur <= 10)                          show();
        else if (cur < lastScroll - 8)          show();
        else if (cur > lastScroll + 8 && cur > 40) hide();
        lastScroll = cur;
    });

    hoverZone.addEventListener('mouseenter', function () { hoverActive = true;  show(); });
    hoverZone.addEventListener('mouseleave', function () { hoverActive = false; });

    /* ── Theme toggle ───────────────────────────────── */
    window.toggleTheme = function () {
        var html = document.documentElement;
        var next = html.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        html.setAttribute('data-theme', next);
        localStorage.setItem('substracked-theme', next);
        updateThemeIcon(next);
    };

    function updateThemeIcon(theme) {
        var icon = document.getElementById('theme-icon');
        if (icon) icon.className = theme === 'dark' ? 'ti ti-sun' : 'ti ti-moon';
    }

    var savedTheme = localStorage.getItem('substracked-theme');
    if (savedTheme) {
        document.documentElement.setAttribute('data-theme', savedTheme);
        updateThemeIcon(savedTheme);
    }

    /* ── Avatar dropdown ────────────────────────────── */
    window.toggleAvatarMenu = function () {
        var menu = document.getElementById('avatar-menu');
        var btn  = document.getElementById('avatar-btn');
        if (!menu) return;
        var open = !menu.hidden;
        menu.hidden = open;
        btn.setAttribute('aria-expanded', String(!open));

        // Notifications schliessen wenn Avatar öffnet
        closeNotifications();
    };

    /* ── Notification dropdown ──────────────────────── */
    window.toggleNotifications = function () {
        var menu = document.getElementById('notif-menu');
        var btn  = document.getElementById('notif-btn');
        if (!menu) return;
        var open = !menu.hidden;
        menu.hidden = open;
        btn.setAttribute('aria-expanded', String(!open));

        // Avatar schliessen wenn Notifications öffnet
        closeAvatarMenu();

        // Glocke animieren wenn geöffnet
        if (!open) {
            var icon = document.getElementById('notif-icon');
            if (icon) {
                icon.classList.add('notif-shake');
                setTimeout(function () { icon.classList.remove('notif-shake'); }, 600);
            }
        }
    };

    function closeAvatarMenu() {
        var menu = document.getElementById('avatar-menu');
        var btn  = document.getElementById('avatar-btn');
        if (menu && !menu.hidden) {
            menu.hidden = true;
            if (btn) btn.setAttribute('aria-expanded', 'false');
        }
    }

    function closeNotifications() {
        var menu = document.getElementById('notif-menu');
        var btn  = document.getElementById('notif-btn');
        if (menu && !menu.hidden) {
            menu.hidden = true;
            if (btn) btn.setAttribute('aria-expanded', 'false');
        }
    }

    document.addEventListener('click', function (e) {
        var avatarWrap = document.getElementById('avatar-wrap');
        var notifWrap  = document.getElementById('notif-wrap');

        if (avatarWrap && !avatarWrap.contains(e.target)) closeAvatarMenu();
        if (notifWrap  && !notifWrap.contains(e.target))  closeNotifications();
    });

}());