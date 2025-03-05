// 리뷰 삭제 메서드
function deleteReview() {
	let reviewId = $(this).attr("data-review-id"); // `data-review-id` 속성에서 ID 가져오기
	console.log("삭제 버튼 클릭됨, 리뷰 ID:", reviewId); // 디버깅용 로그

	if (!reviewId) {
		alert("삭제할 리뷰 ID를 찾을 수 없습니다.");
		return;
	}

	let confirmDelete = confirm("정말 삭제하시겠습니까?");

	if (!confirmDelete) return;

	$.ajax({
		url: `/review/delete/${reviewId}`, // ✅ 백엔드의 `DELETE` API 경로와 일치해야 함
		method: "DELETE", // ❗ `GET`이 아닌 `DELETE`로 변경
		success: function() {
			console.log(`리뷰 ${reviewId} 삭제 완료`); // 삭제 완료 로그
			$(`#review-${reviewId}`).remove(); // 삭제된 리뷰 UI에서도 제거
			let newCount = parseInt($('#reviewCount').text().replace('총 ', '').replace('개', '')) - 1;
			$('#reviewCount').text(`총 ${newCount}개`);
		},
		error: function(xhr, status, error) {
			console.error("삭제 오류:", xhr.responseText);
			alert('삭제 실패');
		}
	});
}

$(document).ready(function() {
	// loadReviews();

	// 검색 버튼 클릭 이벤트
	//$('#searchBtn').click(function () {
	//let query = $('#searchInput').val();
	//loadReviews(query);
	//});

	// 삭제 버튼 클릭 이벤트 (첨부 코드 방식 적용)
	$(document).on("click", ".delete-review", deleteReview);
});

// 리뷰 목록 불러오기 (AJAX)
/*function loadReviews(searchQuery = '') {
	$.ajax({
		url: '/api/reviews', // 리뷰 목록을 가져오는 API 엔드포인트
		type: 'GET',
		data: { search: searchQuery },
		success: function (reviews) {
			console.log("불러온 리뷰 데이터:", reviews); // 디버깅용 로그
			$('#reviewList').empty();
			$('#reviewCount').text(`총 ${reviews.length}개`);

			if (reviews.length === 0) {
				$('#reviewList').append('<p class="text-muted">리뷰가 없습니다.</p>');
				return;
			}

			reviews.forEach(review => {
				let reviewCard = `
					<div class="review-card" id="review-${review.reviewId}">
						<div class="review-left">
							<img class="reviewer-img" src="/images/default-profile.png" alt="프로필">
							<div class="review-content">
								<p class="review-name">리뷰 작성자: ${review.reviewerId}</p>
								<p class="review-meta">${review.createdAt}</p>
								<p class="stars">★★★★★</p>
								<p class="review-text">${review.comment}</p>
								<div class="d-flex mt-2">
									<a href="/review/reviewUpdate?reviewId=${review.reviewId}"
										class="btn btn-sm btn-outline-primary me-2">수정</a>
									<button class="btn btn-sm btn-outline-danger delete-review"
										data-review-id="${review.reviewId}">삭제</button>
								</div>
							</div>
						</div>
					</div>`;
				$('#reviewList').append(reviewCard);
			});

			console.log("삭제 버튼 개수:", $('.delete-review').length); // 삭제 버튼 개수 확인
		}
	});
}
*/