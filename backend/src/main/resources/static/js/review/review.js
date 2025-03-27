$(document).ready(function () {
  // loadChart(); //  페이지 로드 시 차트 실행

  //  정렬 이벤트 발생 시 차트 다시 로드
  $("#reviewFilter").on("change", function () {
    sortReviews(); //  리뷰 정렬
    // loadChart(); //  차트 업데이트
  });

  $("#writeReviewBtn").on("click", writeReview);

  // 삭제 버튼 클릭 이벤트
  $(document).on("click", ".delete-review", deleteReview);

  // ✅ 리뷰 정렬 이벤트
  $("#reviewFilter").on("change", sortReviews);

  const shareModal = document.getElementById("shareModal");
  const closeShareModalBtn = document.getElementById("closeShareModalBtn");

  // 공유하기 버튼 클릭 시 공유 모달 열기
  shareButton.addEventListener("click", function () {
    $("#shareUrl").val(window.location.href);
    shareModal.style.display = "block";
    document.body.classList.add("modal-open");
  });

  // 공유 모달 닫기 버튼 클릭 시 모달 닫기
  closeShareModalBtn.addEventListener("click", function () {
    shareModal.style.display = "none";
    document.body.classList.remove("modal-open");
  });

  // 모달 외부 클릭 시 모달 닫기 (공유 모달)
  window.addEventListener("click", function (event) {
    if (event.target === shareModal) {
      shareModal.style.display = "none";
      document.body.classList.remove("modal-open");
    }
  });

  // ✅ URL 복사 기능
  $("#copyUrlButton").on("click", function () {
    let copyText = $("#shareUrl");
    copyText.select();
    document.execCommand("copy");
    alert("URL이 복사되었습니다! 📋");
  });
});

// ✅ 카카오톡 공유 버튼 이벤트 등록
window.addEventListener("load", function () {
  const reviewedUserId = new URL(window.location.href).searchParams.get(
    "reviewedUserId"
  );
  const shareUrl =
    window.location.origin +
    `/review/reviewPage?reviewedUserId=${reviewedUserId}`;

  Kakao.Link.createDefaultButton({
    container: "#kakaoShareButton",
    objectType: "feed",
    content: {
      title: "하우스메이트 공유",
      description: "하우스메이트의 리뷰를 확인해보세요!",
      imageUrl: window.profileImageUrl,
      link: {
        mobileWebUrl: shareUrl,
        webUrl: shareUrl,
      },
    },
    buttons: [
      {
        title: "웹으로 보기",
        link: {
          mobileWebUrl: shareUrl,
          webUrl: shareUrl,
        },
      },
    ],
  });

  // 공유 완료 후 모달창 닫기
  $("#shareModal").fadeOut();
  document.body.classList.remove("modal-open");
});

// ✅ 리뷰 삭제 함수
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
      // loadChart(); // ✅ 리뷰 삭제 후 차트 업데이트
    },
    error: function (xhr) {
      alert("삭제 실패");
    },
  });
}

// ✅ 리뷰 개수 업데이트 함수
function updateReviewCount(change) {
  let reviewCountElem = $("#reviewCount");
  let currentCount = parseInt(reviewCountElem.text().replace(/\D/g, "")) || 0;
  let newCount = Math.max(0, currentCount + change);
  reviewCountElem.text(`총 ${newCount}개`);
}

// ✅ 리뷰 정렬 기능
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

        // ⭐ 별점 생성 함수
        const getStars = (rating) => {
          return (
            "★".repeat(rating) +
            '<span class="gray-stars">' +
            "★".repeat(5 - rating) +
            "</span>"
          );
        };

        let reviewHtml = `
        <div class="review-card" id="review-${review.reviewId}">
            <div class="review-left">
                <!-- 프로필 이미지 -->
                <img class="reviewer-img" src="${
                  review.reviewerDTO.profileImage
                }" alt="프로필">

                <!-- 리뷰 내용 -->
                <div class="review-content">
                    <div class="review-header">
                        <p class="review-name"><strong>${
                          review.reviewerDTO.nickname
                        }</strong></p>
                        <p class="review-meta">${formattedDate}</p>
                    </div>

                    <!-- 리뷰 본문 -->
                    <p class="review-text">${review.comment}</p>

                    <!-- 별점 -->
                    <div class="review-ratings">
                        <div class="each-ratings">
                            <span><strong>청결도</strong></span>
                            <div class="stars">${getStars(
                              review.cleanlinessRating
                            )}</div>
                        </div>
                        <div class="each-ratings">
                            <span><strong>신뢰도</strong></span>
                            <div class="stars">${getStars(
                              review.trustRating
                            )}</div>
                        </div>
                        <div class="each-ratings">
                            <span><strong>소통</strong></span>
                            <div class="stars">${getStars(
                              review.communicationRating
                            )}</div>
                        </div>
                        <div class="each-ratings">
                            <span><strong>매너</strong></span>
                            <div class="stars">${getStars(
                              review.mannerRating
                            )}</div>
                        </div>
                    </div>

                    <!-- 하단 영역: 수정/삭제 버튼 (우측 정렬) -->
                    ${
                      loggedUserId === review.reviewerDTO.userId
                        ? `
                    <div class="review-actions">
                        <a href="/review/reviewUpdate?reviewId=${review.reviewId}" class="btn btn-sm btn-outline-primary">수정</a>
                        <button class="btn btn-sm btn-outline-danger delete-review" data-review-id="${review.reviewId}">삭제</button>
                    </div>`
                        : ""
                    }
                </div>
            </div>
        </div>`;

        // 리뷰 목록에 추가
        $("#reviewList").append(reviewHtml);
        $("#reviewList").show();
      });

      // ✅ 정렬 후 차트 다시 로드
      // loadChart();
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
        window.location.href =
          reviewRegistUrl + "?reviewedUserId=" + reviewedUserId;
      }
    },
    error: function () {
      alert("리뷰 확인 중 오류가 발생했습니다.");
    },
  });
}
