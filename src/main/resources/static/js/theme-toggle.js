(() => {
    const storageKey = 'todoapp-theme';
    const accentPalettes = {
        blue: {
            light: ['#2563eb', '#1d4ed8', '#1d4ed8', '#dbeafe', '#93c5fd'],
            dark: ['#3b82f6', '#2563eb', '#93c5fd', '#1e3a8a', '#3b82f6']
        },
        purple: {
            light: ['#7c3aed', '#6d28d9', '#6d28d9', '#ede9fe', '#c4b5fd'],
            dark: ['#8b5cf6', '#7c3aed', '#c4b5fd', '#3b1d69', '#8b5cf6']
        },
        green: {
            light: ['#15803d', '#166534', '#166534', '#dcfce7', '#86efac'],
            dark: ['#22c55e', '#16a34a', '#86efac', '#14532d', '#22c55e']
        },
        orange: {
            light: ['#c2410c', '#9a3412', '#9a3412', '#ffedd5', '#fdba74'],
            dark: ['#f97316', '#ea580c', '#fdba74', '#7c2d12', '#f97316']
        },
        pink: {
            light: ['#be185d', '#9d174d', '#9d174d', '#fce7f3', '#f9a8d4'],
            dark: ['#ec4899', '#db2777', '#f9a8d4', '#831843', '#ec4899']
        }
    };
    const settingsLink = document.createElement('a');
    settingsLink.className = 'theme-toggle';
    settingsLink.href = '/settings';
    settingsLink.setAttribute('aria-label', '\u8a2d\u5b9a');
    settingsLink.title = '\u8a2d\u5b9a';
    settingsLink.innerHTML = '<svg viewBox="0 0 24 24" width="25" height="25" aria-hidden="true"><path d="M12 8.5a3.5 3.5 0 1 0 0 7 3.5 3.5 0 0 0 0-7Zm9 4.7v-2.4l-2.2-.7a7.2 7.2 0 0 0-.7-1.6l1.1-2-1.7-1.7-2 1.1a7.2 7.2 0 0 0-1.6-.7L13.2 3h-2.4l-.7 2.2a7.2 7.2 0 0 0-1.6.7l-2-1.1-1.7 1.7 1.1 2a7.2 7.2 0 0 0-.7 1.6L3 10.8v2.4l2.2.7a7.2 7.2 0 0 0 .7 1.6l-1.1 2 1.7 1.7 2-1.1a7.2 7.2 0 0 0 1.6.7l.7 2.2h2.4l.7-2.2a7.2 7.2 0 0 0 1.6-.7l2 1.1 1.7-1.7-1.1-2a7.2 7.2 0 0 0 .7-1.6l2.2-.7Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>';

    const applyTheme = (theme) => {
        const isLight = theme === 'light';
        document.body.classList.toggle('light-mode', isLight);
        document.body.style.color = isLight ? '#1e293b' : '#e2e8f0';
        document.body.style.backgroundColor = isLight ? '#f8fafc' : '#0f172a';
        applyAccent(localStorage.getItem('todoapp-accent') || 'blue');
    };

    const applyAccent = (name) => {
        const palette = accentPalettes[name] || accentPalettes.blue;
        const colors = document.body.classList.contains('light-mode') ? palette.light : palette.dark;
        const properties = ['--accent-strong', '--accent-hover', '--accent-link', '--accent-soft', '--accent-border'];
        properties.forEach((property, index) => {
            document.documentElement.style.setProperty(property, colors[index]);
        });
    };

    window.addEventListener('todoapp-theme-change', (event) => {
        applyTheme(event.detail.theme);
    });
    window.addEventListener('todoapp-accent-change', (event) => {
        applyAccent(event.detail.accent);
    });
    if (window.location.pathname !== '/settings') {
        document.body.appendChild(settingsLink);
    }
    applyTheme(localStorage.getItem(storageKey) || 'dark');
})();
