(() => {
    const storageKey = 'todoapp-theme';
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'theme-toggle';
    button.innerHTML = '<svg viewBox="0 0 24 24" width="25" height="25" aria-hidden="true"><path d="M9 18h6m-5 3h4m-2-20a7 7 0 0 0-4 12.75c.7.5 1 1.15 1 2.25h6c0-1.1.3-1.75 1-2.25A7 7 0 0 0 12 1Z" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>';
    button.style.position = 'fixed';
    button.style.top = '16px';
    button.style.right = '20px';
    button.style.left = 'auto';
    button.style.bottom = 'auto';
    button.style.zIndex = '10000';
    button.style.width = '48px';
    button.style.height = '48px';
    button.style.display = 'grid';
    button.style.placeItems = 'center';
    button.style.border = '1px solid #94a3b8';
    button.style.borderRadius = '50%';
    button.style.padding = '0';
    button.style.background = '#334155';
    button.style.color = '#fbbf24';
    button.style.cursor = 'pointer';

    const themeStyles = document.createElement('style');
    themeStyles.textContent = `
        body.light-mode { color: #1e293b !important; background: #f8fafc !important; }
        body.light-mode .page-title { color: #0f172a !important; }
        body.light-mode .todo-table th, body.light-mode .calendar-table th { background: #e2e8f0 !important; }
        body.light-mode .todo-table th, body.light-mode .todo-table td, body.light-mode .calendar-table th, body.light-mode .calendar-table td { border-color: #94a3b8 !important; }
        body.light-mode .todo-table tbody tr.todo-overdue { background: #fee2e2 !important; }
        body.light-mode .flash { border-color: #cbd5e1 !important; background: #f1f5f9 !important; }
        body.light-mode .search-bar input, body.light-mode .search-bar select, body.light-mode .form-input { border-color: #94a3b8 !important; background: #fff !important; color: #1e293b !important; }
        body.light-mode .btn { border-color: #64748b !important; background: #e2e8f0 !important; color: #0f172a !important; }
        body.light-mode .link, body.light-mode a:visited:not(.btn) { color: #1d4ed8 !important; }
    `;
    document.head.appendChild(themeStyles);

    const applyTheme = (theme) => {
        const isLight = theme === 'light';
        document.body.classList.toggle('light-mode', isLight);
        document.body.style.color = isLight ? '#1e293b' : '#e2e8f0';
        document.body.style.backgroundColor = isLight ? '#f8fafc' : '#0f172a';
        button.style.background = isLight ? '#e2e8f0' : '#334155';
        button.style.color = isLight ? '#92400e' : '#fbbf24';
        const label = isLight ? '\u901a\u5e38\u30e2\u30fc\u30c9\u306b\u5207\u308a\u66ff\u3048' : '\u30c0\u30fc\u30af\u30e2\u30fc\u30c9\u306b\u5207\u308a\u66ff\u3048';
        button.title = label;
        button.setAttribute('aria-label', label);
        button.setAttribute('aria-pressed', String(isLight));
    };

    button.addEventListener('click', () => {
        const nextTheme = document.body.classList.contains('light-mode') ? 'dark' : 'light';
        localStorage.setItem(storageKey, nextTheme);
        applyTheme(nextTheme);
    });
    document.body.appendChild(button);
    applyTheme(localStorage.getItem(storageKey) || 'dark');
})();
