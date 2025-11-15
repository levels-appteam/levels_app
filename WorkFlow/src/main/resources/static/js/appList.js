document.addEventListener("DOMContentLoaded", function() {
	const filter = document.getElementById("status-filter");
	const rows = document.querySelectorAll("tbody tr[data-status]");

	function apply(status) {
		rows.forEach(tr => {
			const s = tr.getAttribute("data-status");
			tr.style.display = (status === "ALL" || s === status) ? "" : "none";
		});
	}

	filter.addEventListener("change", () => apply(filter.value));
	// 初期表示：未承認のみ
	filter.value = "PENDING";
	apply("PENDING");
});