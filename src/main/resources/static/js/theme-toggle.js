(() => {
    const storageKey = 'todoapp-theme';
    // Keep the existing stored accent keys so previously saved settings continue to work.
    const palettes = {
        blue: {
            light: {
                paper: '#F3EBDD', text: '#292820', button: '#315B63', border: '#C9BDA8',
                surface: '#FAF7F0', surfaceAlt: '#EAE3D6', muted: '#69665D', link: '#315B63',
                soft: '#E6E1D6', hover: '#284C53', buttonText: '#FFFFFF'
            },
            dark: {
                paper: '#202321', text: '#E9E2D3', button: '#80A9A7', border: '#4C5752',
                surface: '#2A2E2B', surfaceAlt: '#343A36', muted: '#B9B5A9', link: '#A8C9C6',
                soft: '#37413D', hover: '#98B9B7', buttonText: '#202321'
            }
        },
        orange: {
            light: {
                paper: '#F7F2E8', text: '#302B27', button: '#A44736', border: '#D5C9B7',
                surface: '#FCF8F1', surfaceAlt: '#EEE5D8', muted: '#71635B', link: '#923C30',
                soft: '#F1E3D8', hover: '#8D3A2D', buttonText: '#FFFFFF'
            },
            dark: {
                paper: '#24211F', text: '#EEE5D8', button: '#D98368', border: '#5B5049',
                surface: '#302B28', surfaceAlt: '#3B3430', muted: '#C1B3A8', link: '#E6A18A',
                soft: '#483832', hover: '#E59A7F', buttonText: '#24211F'
            }
        },
        green: {
            light: {
                paper: '#EEEBDD', text: '#292D24', button: '#526B43', border: '#C8C6AE',
                surface: '#F8F6EB', surfaceAlt: '#E4E3D4', muted: '#656858', link: '#465D38',
                soft: '#E4E8D8', hover: '#435737', buttonText: '#FFFFFF'
            },
            dark: {
                paper: '#20231F', text: '#E5E7D9', button: '#9AAF78', border: '#505747',
                surface: '#2B2F29', surfaceAlt: '#353A31', muted: '#B8BEA9', link: '#B9CA9B',
                soft: '#3B4435', hover: '#AEC58A', buttonText: '#20231F'
            }
        },
        purple: {
            light: {
                paper: '#F3EDF5', text: '#302738', button: '#795493', border: '#D2C5D8',
                surface: '#FAF6FB', surfaceAlt: '#EBE2EF', muted: '#6E6273', link: '#694581',
                soft: '#EDE5F1', hover: '#68477F', buttonText: '#FFFFFF'
            },
            dark: {
                paper: '#26212A', text: '#EFE7F3', button: '#AC8CC5', border: '#584D60',
                surface: '#302A35', surfaceAlt: '#3B3441', muted: '#C2B6C9', link: '#C3A5D7',
                soft: '#443A4B', hover: '#BEA2D2', buttonText: '#26212A'
            }
        },
        pink: {
            light: {
                paper: '#F7ECEF', text: '#33262B', button: '#B76583', border: '#DCC4CC',
                surface: '#FCF6F7', surfaceAlt: '#EFE1E5', muted: '#75616A', link: '#9E4D6C',
                soft: '#F2E2E8', hover: '#9F536F', buttonText: '#24181D'
            },
            dark: {
                paper: '#292126', text: '#F1E5E9', button: '#D98FA8', border: '#5D4851',
                surface: '#342A30', surfaceAlt: '#40343B', muted: '#C7B4BC', link: '#E4A6BA',
                soft: '#493740', hover: '#E2A2B7', buttonText: '#292126'
            }
        }
    };

    const settingsLink = document.createElement('a');
    settingsLink.className = 'theme-toggle';
    settingsLink.href = '/settings';
    settingsLink.setAttribute('aria-label', '\u8a2d\u5b9a');
    settingsLink.title = '\u8a2d\u5b9a';
    settingsLink.innerHTML = '<svg viewBox="0 0 24 24" width="25" height="25" aria-hidden="true"><path d="M12 8.5a3.5 3.5 0 1 0 0 7 3.5 3.5 0 0 0 0-7Zm9 4.7v-2.4l-2.2-.7a7.2 7.2 0 0 0-.7-1.6l1.1-2-1.7-1.7-2 1.1a7.2 7.2 0 0 0-1.6-.7L13.2 3h-2.4l-.7 2.2a7.2 7.2 0 0 0-1.6.7l-2-1.1-1.7 1.7 1.1 2a7.2 7.2 0 0 0-.7 1.6L3 10.8v2.4l2.2.7a7.2 7.2 0 0 0 .7 1.6l-1.1 2 1.7 1.7 2-1.1a7.2 7.2 0 0 0 1.6.7l.7 2.2h2.4l.7-2.2a7.2 7.2 0 0 0 1.6-.7l2 1.1 1.7-1.7-1.1-2a7.2 7.2 0 0 0 .7-1.6l2.2-.7Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>';

    const applyAccent = (name) => {
        const palette = palettes[name] || palettes.blue;
        const mode = document.body.classList.contains('light-mode') ? 'light' : 'dark';
        const colors = palette[mode];
        const root = document.documentElement;
        const properties = {
            '--paper-bg': colors.paper,
            '--text-main': colors.text,
            '--surface': colors.surface,
            '--surface-alt': colors.surfaceAlt,
            '--border-color': colors.border,
            '--muted-text': colors.muted,
            '--accent-strong': colors.button,
            '--accent-hover': colors.hover,
            '--accent-link': colors.link,
            '--accent-soft': colors.soft,
            '--accent-border': colors.border,
            '--button-text': colors.buttonText,
            '--overdue-bg': mode === 'light' ? '#F5DFDA' : '#493331',
            '--due-soon-bg': mode === 'light' ? '#F4E9C8' : '#4B4430',
            '--holiday-bg': mode === 'light' ? '#F1E1B9' : '#55473A',
            '--sunday-bg': mode === 'light' ? '#F1DDDA' : '#493839',
            '--saturday-bg': mode === 'light' ? '#DDE8EC' : '#35434A'
        };
        Object.entries(properties).forEach(([property, value]) => root.style.setProperty(property, value));
    };

    const applyTheme = (theme) => {
        document.body.classList.toggle('light-mode', theme === 'light');
        applyAccent(localStorage.getItem('todoapp-accent') || 'blue');
    };

    window.addEventListener('todoapp-theme-change', (event) => applyTheme(event.detail.theme));
    window.addEventListener('todoapp-accent-change', (event) => applyAccent(event.detail.accent));
    if (window.location.pathname !== '/settings') document.body.appendChild(settingsLink);
    applyTheme(localStorage.getItem(storageKey) || 'dark');
})();
