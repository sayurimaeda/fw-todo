(() => {
    const dateToString = (date) => {
        const year = date.getUTCFullYear();
        const month = String(date.getUTCMonth() + 1).padStart(2, '0');
        const day = String(date.getUTCDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    const addDays = (date, days) => {
        const result = new Date(date.getTime());
        result.setUTCDate(result.getUTCDate() + days);
        return result;
    };

    const makeDate = (year, month, day) => {
        const date = new Date(Date.UTC(year, month - 1, day));
        return date.getUTCFullYear() === year && date.getUTCMonth() === month - 1 && date.getUTCDate() === day
            ? date
            : null;
    };

    const parseNaturalDate = (rawValue) => {
        const value = rawValue.trim().replace(/\s+/g, '');
        if (!value) return '';

        const todayParts = new Intl.DateTimeFormat('en-CA', {
            timeZone: 'Asia/Tokyo', year: 'numeric', month: '2-digit', day: '2-digit'
        }).formatToParts(new Date());
        const todayValues = Object.fromEntries(todayParts.map((part) => [part.type, part.value]));
        const today = new Date(Date.UTC(Number(todayValues.year), Number(todayValues.month) - 1, Number(todayValues.day)));

        const relativeDays = new Map([
            ['今日', 0], ['本日', 0], ['明日', 1], ['あした', 1], ['明後日', 2], ['あさって', 2]
        ]);
        if (relativeDays.has(value)) return dateToString(addDays(today, relativeDays.get(value)));

        let match = value.match(/^(\d+)日後$/);
        if (match) return dateToString(addDays(today, Number(match[1])));
        match = value.match(/^(\d+)週間後$/);
        if (match) return dateToString(addDays(today, Number(match[1]) * 7));

        if (/^(今週末|来週末|再来週末)$/.test(value)) {
            const weekOffset = value.startsWith('再来週') ? 14 : value.startsWith('来週') ? 7 : 0;
            const mondayOffset = (today.getUTCDay() + 6) % 7;
            return dateToString(addDays(today, weekOffset - mondayOffset + 6));
        }

        match = value.match(/^(今週|来週|再来週)?の?(日|月|火|水|木|金|土)(?:曜(?:日)?)?$/);
        if (match) {
            const weekdayOffsets = { '月': 0, '火': 1, '水': 2, '木': 3, '金': 4, '土': 5, '日': 6 };
            const mondayOffset = (today.getUTCDay() + 6) % 7;
            const weekOffset = match[1] === '再来週' ? 14 : match[1] === '来週' ? 7 : 0;
            const dayOffset = weekdayOffsets[match[2]];
            const offset = match[1]
                ? weekOffset - mondayOffset + dayOffset
                : (dayOffset - mondayOffset + 7) % 7;
            return dateToString(addDays(today, offset));
        }

        if (value === '今月末' || value === '来月末') {
            const monthOffset = value === '来月末' ? 1 : 0;
            return dateToString(new Date(Date.UTC(today.getUTCFullYear(), today.getUTCMonth() + monthOffset + 1, 0)));
        }

        match = value.match(/^(\d{4})[年/-](\d{1,2})[月/-](\d{1,2})日?$/);
        if (match) return makeDate(Number(match[1]), Number(match[2]), Number(match[3]))
            ? dateToString(makeDate(Number(match[1]), Number(match[2]), Number(match[3]))) : null;

        match = value.match(/^(\d{1,2})月(\d{1,2})日?$/);
        if (match) {
            const month = Number(match[1]);
            const day = Number(match[2]);
            let date = makeDate(today.getUTCFullYear(), month, day);
            if (!date) return null;
            if (date < today) date = makeDate(today.getUTCFullYear() + 1, month, day);
            return date ? dateToString(date) : null;
        }

        return null;
    };

    document.querySelectorAll('[data-natural-date]').forEach((naturalInput) => {
        const form = naturalInput.closest('form');
        const dateInput = form?.querySelector('input[type="date"][name="dueDate"], input[type="date"]#dueDate');
        const status = form?.querySelector('[data-date-parse-status]');
        if (!form || !dateInput) return;

        const updateDate = (showError) => {
            const value = naturalInput.value.trim();
            if (!value) {
                if (status) status.textContent = '';
                return true;
            }
            const parsed = parseNaturalDate(value);
            if (!parsed) {
                dateInput.value = '';
                if (status) status.textContent = showError
                    ? '日付に変換できません。例：「明日」「来週月曜」「11月20日」'
                    : '';
                return false;
            }
            dateInput.value = parsed;
            if (status) status.textContent = `期限を ${parsed} に設定しました。`;
            return true;
        };

        naturalInput.addEventListener('input', () => updateDate(false));
        naturalInput.addEventListener('change', () => updateDate(true));
        naturalInput.addEventListener('blur', () => {
            if (naturalInput.value.trim()) updateDate(true);
        });
        dateInput.addEventListener('change', () => {
            naturalInput.value = '';
            if (status) status.textContent = '';
        });
        form.addEventListener('submit', (event) => {
            if (!updateDate(true)) {
                event.preventDefault();
                naturalInput.focus();
            }
        });
    });
})();
