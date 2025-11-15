const fmt = new Intl.DateTimeFormat('ja-JP', {
	year: 'numeric', month: '2-digit', day: '2-digit',
	hour: '2-digit', minute: '2-digit', second: '2-digit',
	hour12: false, weekday: 'short', timeZone: 'Asia/Tokyo'
});

function updateClock() {
	const el = document.getElementById('clock');
	if (!el) return;
	const now = new Date();
	// 例: 2025/08/17(日) 14:23:45
	const parts = fmt.formatToParts(now).reduce((acc, p) => (acc[p.type] = p.value, acc), {});
	el.textContent =
		`${parts.year}/${parts.month}/${parts.day}(${parts.weekday}) ` +
		`${parts.hour}:${parts.minute}:${parts.second}`;
}

// 初回即時表示＋1秒ごとに更新
updateClock();
setInterval(updateClock, 1000);