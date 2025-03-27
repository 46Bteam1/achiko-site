$(document).ready(function () {
  $("#writeReviewBtn").on("click", writeReview);

  // 삭제 버튼 클릭 이벤트
  $(document).on("click", ".delete-review", deleteReview);

  // 리뷰 정렬 이벤트
  $("#reviewFilter").on("change", sortReviews);

  // KakaoTalk 공유
  Kakao.init("YOUR_KAKAO_API_KEY"); // 🔹 카카오 API 키 등록 필수

  $(document).on("click", ".share-kakao", function () {
    let reviewUrl = $(this).attr("data-review-url");

    Kakao.Link.sendDefault({
      objectType: "feed",
      content: {
        title: "리뷰 공유",
        description: "이 리뷰를 확인해보세요!",
        imageUrl: "https://your-site.com/image.jpg", // 리뷰에 맞는 이미지 URL 설정
        link: {
          mobileWebUrl: reviewUrl,
          webUrl: reviewUrl,
        },
      },
    });
  });

  // Fragment가 동적으로 로드된 후 이벤트 바인딩
  $(document).on("click", "#menuButton", function (event) {
    event.stopPropagation();
    const $modalMenu = $("#modalMenu");

    if ($modalMenu.is(":visible")) {
      $modalMenu.hide();
    } else {
      $modalMenu.show();
    }
  });

  // 모달 바깥 클릭 시 모달 닫기
  $(document).on("click", function (event) {
    if (
      !$("#modalMenu").is(event.target) &&
      !$("#modalMenu").has(event.target).length &&
      !$("#menuButton").is(event.target)
    ) {
      $("#modalMenu").hide();
    }
  });

  // header관련
  $("header").addClass("sticky"); // sticky가 add되면 작은 검색창 나옴
  $("header").removeClass("sticky-reappear");
});

// 리뷰 삭제 메서드
function deleteReview() {
  let reviewId = $(this).attr("data-review-id");

  if (!reviewId) {
    alert("삭제할 리뷰 ID를 찾을 수 없습니다.");
    return;
  }

  if (!confirm("정말 삭제하시겠습니까?")) return;

  $.ajax({
    url: `/review/delete/${reviewId}`,
    method: "DELETE",
    success: function () {
      $(`#review-${reviewId}`).remove();
      updateReviewCount(-1);
    },
    error: function (xhr) {
      alert("삭제 실패");
    },
  });
}

// 리뷰 개수 업데이트 함수
function updateReviewCount(change) {
  let reviewCountElem = $("#reviewCount");
  let currentCount = parseInt(reviewCountElem.text().replace(/\D/g, "")) || 0;
  let newCount = Math.max(0, currentCount + change);
  reviewCountElem.text(`총 ${newCount}개`);
}

// 리뷰 정렬 기능
function sortReviews() {
  let sortBy = $("#reviewFilter").val();

  $.ajax({
    url: `/review/sort?order=${sortBy}`,
    method: "GET",
    dataType: "json",
    success: function (sortedReviews) {

      if (!Array.isArray(sortedReviews)) {
        alert("서버 응답 오류: 리뷰 데이터를 불러올 수 없습니다.");
        return;
      }

      let reviewsContainer = $("#reviewList");
      reviewsContainer.empty(); // 기존 리뷰 목록 초기화

      sortedReviews.forEach((review) => {
        let formattedDate = new Date(review.createdAt).toLocaleDateString();
        let reviewHtml = `
                    <div class="review-card d-flex justify-content-between align-items-center" id="review-${review.reviewId}">
                        
                        <!-- 왼쪽 (리뷰 정보) -->
                        <div class="review-left d-flex align-items-start">
                            <img class="reviewer-img" src="/images/default-profile.png" alt="프로필">
                            <div class="review-content">
                                <p class="review-name">리뷰 작성자: <strong>${review.reviewerId}</strong></p>
                                <p class="review-meta">${formattedDate}</p>
                                <p class="review-text">${review.comment}</p>
                                <div class="d-flex mt-2">
                                    <a href="/review/reviewUpdate?reviewId=${review.reviewId}" class="btn btn-sm btn-outline-primary me-2">수정</a>
                                    <button class="btn btn-sm btn-outline-danger delete-review" data-review-id="${review.reviewId}">삭제</button>
                                </div>
                            </div>
                        </div>

                        <!-- 🔹 오른쪽 (점수) -->
                        <div class="review-scores text-end">
                            <p><strong>청결도</strong> ${review.cleanlinessRating}</p>
                            <p><strong>신뢰도</strong> ${review.trustRating}</p>
                            <p><strong>소통</strong> ${review.communicationRating}</p>
                            <p><strong>매너</strong> ${review.mannerRating}</p>
                        </div>

                    </div>
                `;
        reviewsContainer.append(reviewHtml);
      });
    },
    error: function (xhr) {
      alert("리뷰 정렬 중 문제가 발생했습니다.");
    },
  });
}

// 리뷰 작성하기 버튼
function writeReview() {
  let reviewedUserId = $(this).data("reviewed-user-id");
  let loginUserId = $(this).data("login-user-id");
  let reviewRegistUrl = $(this).data("url");

  // 본인이 본인에게 리뷰 작성 불가
  if (reviewedUserId === loginUserId) {
    alert("자신에게 리뷰를 작성할 수 없습니다.");
    return;
  }

  // AJAX로 리뷰 작성 여부 체크
  $.ajax({
    type: "GET",
    url: "/review/checkReview",
    data: { reviewedUserId: reviewedUserId },
    success: function (response) {
      if (response.exists) {
        alert("이미 리뷰를 작성한 사용자입니다.");
      } else {
        window.location.href = reviewRegistUrl + "?reviewedUserId=" + reviewedUserId;
      }
    },
    error: function () {
      alert("리뷰 확인 중 오류가 발생했습니다.");
    },
  });
}
