// static/js/correction.js
document.addEventListener("DOMContentLoaded", function() {
	const filter = document.getElementById("status-filter");
	const rows = document.querySelectorAll("#correction-table tbody tr");

	function applyFilter() {
		const selected = filter.value;
		rows.forEach(tr => {
			const status = tr.getAttribute("data-status");
			tr.style.display = (selected === "ALL" || status === selected) ? "" : "none";
		});
	}

	applyFilter("PENDING");
	filter.value = "PENDING";

	filter.addEventListener("change", function() {
		applyFilter(filter.value);
	});
});