(() => {
    const notificationToggle = document.getElementById('due-notification-toggle');
    const notificationStatus = document.getElementById('notification-setting-status');
    const voiceToggle = document.getElementById('voice-read-toggle');
    const themeToggle = document.getElementById('dark-mode-toggle');
    const accentSelect = document.getElementById('accent-color-select');
    const backgroundSelect = document.getElementById('background-design-select');

    if (notificationToggle) {
        const enabled = localStorage.getItem('todo-due-notifications-enabled') === 'true';
        notificationToggle.checked = enabled;
        if (!('Notification' in window)) {
            notificationToggle.disabled = true;
            notificationStatus.textContent = '\u3053\u306e\u30d6\u30e9\u30a6\u30b6\u306f\u901a\u77e5\u306b\u5bfe\u5fdc\u3057\u3066\u3044\u307e\u305b\u3093\u3002';
        } else if (Notification.permission === 'denied') {
            notificationStatus.textContent = '\u901a\u77e5\u304c\u30d6\u30ed\u30c3\u30af\u3055\u308c\u3066\u3044\u307e\u3059\u3002\u30d6\u30e9\u30a6\u30b6\u306e\u30b5\u30a4\u30c8\u8a2d\u5b9a\u3067\u8a31\u53ef\u3057\u3066\u304f\u3060\u3055\u3044\u3002';
        } else if (enabled) {
            notificationStatus.textContent = '\u901a\u77e5\u306f\u30aa\u30f3\u3067\u3059\u3002';
        }

        notificationToggle.addEventListener('change', async () => {
            if (!notificationToggle.checked) {
                localStorage.setItem('todo-due-notifications-enabled', 'false');
                notificationStatus.textContent = '\u901a\u77e5\u3092\u30aa\u30d5\u306b\u3057\u307e\u3057\u305f\u3002';
                return;
            }
            if (Notification.permission === 'denied') {
                notificationToggle.checked = false;
                notificationStatus.textContent = '\u30d6\u30e9\u30a6\u30b6\u306e\u30b5\u30a4\u30c8\u8a2d\u5b9a\u3067\u901a\u77e5\u3092\u8a31\u53ef\u3057\u3066\u304b\u3089\u3001\u3082\u3046\u4e00\u5ea6\u304a\u8a66\u3057\u304f\u3060\u3055\u3044\u3002';
                return;
            }
            const permission = Notification.permission === 'granted'
                ? 'granted'
                : await Notification.requestPermission();
            if (permission === 'granted') {
                localStorage.setItem('todo-due-notifications-enabled', 'true');
                notificationStatus.textContent = '\u901a\u77e5\u3092\u30aa\u30f3\u306b\u3057\u307e\u3057\u305f\u3002\u6b21\u306b\u4e00\u89a7\u3092\u958b\u3044\u305f\u3068\u304d\u306b\u5bfe\u8c61\u304c\u3042\u308c\u3070\u304a\u77e5\u3089\u305b\u3057\u307e\u3059\u3002';
            } else {
                notificationToggle.checked = false;
                notificationStatus.textContent = '\u901a\u77e5\u306f\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002';
            }
        });
    }

    if (voiceToggle) {
        voiceToggle.checked = localStorage.getItem('todo-voice-read-enabled') !== 'false';
        voiceToggle.addEventListener('change', () => {
            localStorage.setItem('todo-voice-read-enabled', String(voiceToggle.checked));
        });
    }

    if (themeToggle) {
        themeToggle.checked = (localStorage.getItem('todoapp-theme') || 'dark') === 'dark';
        themeToggle.addEventListener('change', () => {
            const theme = themeToggle.checked ? 'dark' : 'light';
            localStorage.setItem('todoapp-theme', theme);
            window.dispatchEvent(new CustomEvent('todoapp-theme-change', { detail: { theme } }));
        });
    }

    if (accentSelect) {
        const savedAccent = localStorage.getItem('todoapp-accent') || 'blue';
        accentSelect.value = savedAccent;
        accentSelect.addEventListener('change', () => {
            localStorage.setItem('todoapp-accent', accentSelect.value);
            window.dispatchEvent(new CustomEvent('todoapp-accent-change', {
                detail: { accent: accentSelect.value }
            }));
        });
    }

    if (backgroundSelect) {
        const savedBackground = localStorage.getItem('todoapp-background-design') || '';
        backgroundSelect.value = savedBackground;
        backgroundSelect.addEventListener('change', () => {
            if (backgroundSelect.value) {
                localStorage.setItem('todoapp-background-design', backgroundSelect.value);
            } else {
                localStorage.removeItem('todoapp-background-design');
            }
            window.dispatchEvent(new CustomEvent('todoapp-background-change', {
                detail: { background: backgroundSelect.value }
            }));
        });
    }
})();
