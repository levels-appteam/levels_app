document.addEventListener("DOMContentLoaded", function() {
	var filter = document.getElementById("status-filter");
	if (!filter) return;

	function applyFilter(selected) {
		// ステータス付きの行だけ対象（ヘッダは対象外）
		var rows = document.querySelectorAll("#pending-table tr[data-status]");
		for (var i = 0; i < rows.length; i++) {
			var status = rows[i].getAttribute("data-status");
			rows[i].style.display = (selected === "ALL" || status === selected) ? "" : "none";
		}
	}

	// 初期表示：PENDING だけ出す
	applyFilter("PENDING");
	filter.value = "PENDING";

	filter.addEventListener("change", function() {
		applyFilter(filter.value);
	});
});