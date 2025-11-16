// static/js/correction.js
document.addEventListener("DOMContentLoaded", function() {
	const filter = document.getElementById("status-filter");
	if (!filter) return;

	function applyFilter(selected) {
		// ステータス付きの行だけ対象（ヘッダは対象外）
		var rows = document.querySelectorAll("#correction-table tbody tr");
		for (var i = 0; i < rows.length; i++) {
			var status = rows[i].getAttribute("data-status");
			rows[i].style.display = (selected === "ALL" || status === selected) ? "" : "none";
		}
	}

	applyFilter("PENDING");
	filter.value = "PENDING";

	filter.addEventListener("change", function() {
		applyFilter(filter.value);
	});
});