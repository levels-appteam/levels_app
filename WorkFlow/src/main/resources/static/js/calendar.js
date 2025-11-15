//ページが読まれたときに下記を実行
document.addEventListener('DOMContentLoaded', function() {

	//カレンダーの要素を取得
	const calendarEl = document.getElementById('calendar');

	// オブジェクトを作成 FullCalendar.Calendar() を実行。引数として要素と表示するカレンダーの設定
	const calendar = new FullCalendar.Calendar(calendarEl, {
		initialView: 'dayGridMonth',
		events: {
			url: 'api/calendar/events',
			method: 'GET',
		}

	});

	//カレンダーのレンダリング
	calendar.render();
});
