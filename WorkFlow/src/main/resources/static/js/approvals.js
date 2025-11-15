document.addEventListener("DOMContentLoaded", function() {
	console.log("JS読み込み成功");

	const userItems = document.querySelectorAll(".user-item");
	const filter = document.getElementById("status-filter");

	// 絞り込みロジック（初期表示含む）
	function applyFilter(status) {
		document.querySelectorAll("#request-list table").forEach(table => {
			const tableStatus = table.getAttribute("data-status");
			table.style.display = (tableStatus === status) ? "block" : "none";
		});
	}

	// フィルター変更イベントは最初に1度だけ登録
	if (filter) {
		filter.addEventListener("change", function() {
			applyFilter(filter.value);
		});
	}

	userItems.forEach(item => {
		item.addEventListener("click", function() {
			const userName = item.textContent;
			const userId = item.getAttribute("data-userid");

			document.getElementById("user-detail").style.display = "block";

			fetch("/approvals/user-requests?id=" + userId)
				.then(response => {
					if (!response.ok) {
						throw new Error("サーバーエラー");
					}
					return response.json();
				})
				.then(data => {
					const requestList = document.getElementById("request-list");
					requestList.innerHTML = "";

					data.forEach(request => {
						const table = document.createElement("table");
						table.classList.add("request-table");
						table.setAttribute("data-status", request.status);

						table.innerHTML = `
							<tr><th>申請者</th><td>${userName}</td></tr>
							<tr><th>申請種別</th><td>${request.kindLabel}</td></tr>
							<tr><th>対象日</th><td>${request.targetDate}</td></tr>
							<tr><th>提出日</th><td>${request.submittedAt}</td></tr>
							<tr><th>ステータス</th><td>${request.status}</td></tr>
							<tr>
								<th>操作</th>
								<td>
									${request.status === "PENDING" ? `
									<form action="/approvals/approve" method="post" style="display:inline;">
										<input type="hidden" name="requestId" value="${request.id}" />
										<button type="submit" name="decision" value="APPROVED">承認</button>
										<button type="submit" name="decision" value="REJECTED">却下</button>
										<button type="submit" name="decision" value="REMAND">差し戻し</button>
									</form>` : `<span>処理済: ${request.status}</span>`}
								</td>
							</tr>
						`;

						requestList.appendChild(table);

						table.addEventListener("click", function() {
							document.getElementById("requestId").value = request.id;
						});
					});

					// 初期表示：未承認のみ
					applyFilter("PENDING");

					// 名前表示
					document.getElementById("detail-name").textContent = "名前: " + userName;
				})
				.catch(error => {
					console.error("エラー:", error);
				});
		});
	});
});