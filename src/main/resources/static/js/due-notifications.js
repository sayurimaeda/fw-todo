(() => {
    if (!('Notification' in window)
        || Notification.permission !== 'granted'
        || localStorage.getItem('todo-due-notifications-enabled') !== 'true') return;

    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const todos = [...document.querySelectorAll('.todo-table tbody tr')]
        .filter((row) => row.dataset.completed !== 'true' && row.dataset.dueDate)
        .map((row) => {
            const due = new Date(`${row.dataset.dueDate}T00:00:00`);
            return { title: row.dataset.title || 'ToDo', days: Math.round((due - today) / 86400000) };
        })
        .filter((todo) => todo.days <= 3);

    if (todos.length === 0) return;
    const todayKey = new Date().toLocaleDateString('sv-SE');
    const storageKey = `todo-due-notice-${todayKey}`;
    if (localStorage.getItem(storageKey)) return;

    const overdue = todos.filter((todo) => todo.days < 0).length;
    const dueSoon = todos.length - overdue;
    const summary = [
        overdue ? `\u671f\u9650\u5207\u308c ${overdue}\u4ef6` : '',
        dueSoon ? `3\u65e5\u4ee5\u5185 ${dueSoon}\u4ef6` : ''
    ].filter(Boolean).join('\u3001');
    const names = todos.slice(0, 3).map((todo) => todo.title).join('\u3001');
    const remainder = todos.length > 3 ? ` \u307b\u304b${todos.length - 3}\u4ef6` : '';
    try {
        const notification = new Notification('ToDo\u306e\u671f\u9650\u306e\u304a\u77e5\u3089\u305b', {
            body: `${summary}\n${names}${remainder}`,
            tag: storageKey
        });
        notification.onclick = () => { window.focus(); notification.close(); };
        localStorage.setItem(storageKey, 'shown');
    } catch (error) {
        // Browser or page settings may prevent notifications; keep the todo list usable.
    }
})();
