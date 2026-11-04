(() => {
    const status = document.querySelector('.voice-status');
    const setStatus = (message) => {
        if (status) status.textContent = message;
    };
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

    document.querySelectorAll('.voice-input').forEach((button) => {
        button.addEventListener('click', () => {
            if (!SpeechRecognition) {
                setStatus('このブラウザーでは音声入力を利用できません。別のブラウザーをお試しください。');
                return;
            }
            const target = document.getElementById(button.dataset.voiceTarget);
            if (!target) return;

            const recognition = new SpeechRecognition();
            recognition.lang = 'ja-JP';
            recognition.continuous = false;
            recognition.interimResults = false;
            recognition.onstart = () => setStatus('聞き取り中です。話してください。');
            recognition.onresult = (event) => {
                const spokenText = event.results[0][0].transcript.trim();
                target.value = [target.value.trim(), spokenText].filter(Boolean).join(' ');
                target.dispatchEvent(new Event('input', { bubbles: true }));
                setStatus(`音声を入力しました：${spokenText}`);
            };
            recognition.onerror = (event) => {
                const message = event.error === 'not-allowed' || event.error === 'service-not-allowed'
                    ? 'マイクの使用が許可されていません。ブラウザーの設定を確認してください。'
                    : '音声を認識できませんでした。もう一度お試しください。';
                setStatus(message);
            };
            recognition.onend = () => {
                if (status && status.textContent === '聞き取り中です。話してください。') {
                    setStatus('音声入力を終了しました。');
                }
            };
            try {
                recognition.start();
            } catch (error) {
                setStatus('音声入力を開始できませんでした。少し待ってからお試しください。');
            }
        });
    });

    const readButton = document.getElementById('read-incomplete-todos');
    if (readButton) {
        readButton.addEventListener('click', () => {
            if (!('speechSynthesis' in window)) {
                setStatus('このブラウザーでは音声読み上げを利用できません。');
                return;
            }
            if (window.speechSynthesis.speaking) {
                window.speechSynthesis.cancel();
                setStatus('読み上げを停止しました。');
                return;
            }

            const tasks = [...document.querySelectorAll('.todo-table tbody tr')]
                .filter((row) => row.dataset.completed !== 'true')
                .map((row) => {
                    const title = row.querySelector('td span')?.textContent.trim();
                    const detail = row.querySelector('.todo-detail')?.textContent.trim();
                    return [title, detail].filter(Boolean).join('。');
                })
                .filter(Boolean);
            const text = tasks.length
                ? `この一覧の未完了ToDoは${tasks.length}件です。${tasks.join('。')}`
                : 'この一覧に未完了ToDoはありません。';
            const utterance = new SpeechSynthesisUtterance(text);
            utterance.lang = 'ja-JP';
            utterance.onstart = () => setStatus('未完了ToDoを読み上げています。');
            utterance.onend = () => setStatus('読み上げが終わりました。');
            window.speechSynthesis.cancel();
            window.speechSynthesis.speak(utterance);
        });
    }
})();
