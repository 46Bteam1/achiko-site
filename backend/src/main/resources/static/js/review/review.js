$(document).ready(function () {
  // header 관련
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

  console.log("📢 페이지 로드 완료, 차트 실행");
  // loadChart(); //  페이지 로드 시 차트 실행

  //  정렬 이벤트 발생 시 차트 다시 로드
  $("#reviewFilter").on("change", function () {
    console.log("📢 정렬 방식 변경됨:", $(this).val());
    sortReviews(); //  리뷰 정렬
    // loadChart(); //  차트 업데이트
  });

  $("#writeReviewBtn").on("click", writeReview);

  // 삭제 버튼 클릭 이벤트
  $(document).on("click", ".delete-review", deleteReview);

  // ✅ 리뷰 정렬 이벤트
  $("#reviewFilter").on("change", sortReviews);

  // ✅ 카카오 공유 초기화
  Kakao.init("85ca9d17a9851b6fed154a7b6a161304");

  // ✅ 공유 버튼 클릭 시 모달창 열기
  const shareModal = new bootstrap.Modal(
    document.getElementById("shareModal"),
    {
      backdrop: false, // 백드롭 비활성화
    }
  );
  $("#shareButton").on("click", function () {
    console.log("📢 공유 버튼 클릭됨");
    $("#shareUrl").val(window.location.href);
    shareModal.show();
    document.body.classList.add("modal-open");
  });

  // ✅ 카카오톡 공유 버튼 이벤트 등록
  $("#kakaoShareButton").on("click", function () {
    console.log("📢 카카오 공유 버튼 클릭됨");

    Kakao.Share.sendDefault({
      objectType: "feed",
      content: {
        title: "리뷰 공유",
        description: "이 리뷰를 확인해보세요!",
        imageUrl: "https://your-site.com/image.jpg",
        link: {
          mobileWebUrl: window.location.href,
          webUrl: window.location.href,
        },
      },
      buttons: [
        {
          title: "웹으로 보기",
          link: {
            mobileWebUrl: window.location.href,
            webUrl: window.location.href,
          },
        },
      ],
    });

    // 공유 완료 후 모달창 닫기
    $("#shareModal").fadeOut();
    document.body.classList.remove("modal-open");
  });

  // ✅ 모달 닫기 버튼 이벤트
  $("#closeShareModalBtn").on("click", function () {
    console.log("📢 모달 닫기 버튼 클릭됨");
    shareModal.hide();
    document.body.classList.remove("modal-open");
  });

  // ✅ URL 복사 기능
  $("#copyUrlButton").on("click", function () {
    let copyText = $("#shareUrl");
    copyText.select();
    document.execCommand("copy");
    alert("URL이 복사되었습니다! 📋");
  });
});

// ✅ 차트를 생성하는 함수 (전역에서 선언)
// function loadChart() {
//   console.log("📊 차트 로딩 중...");

//   let cleanliness = parseFloat($("#cleanlinessRating").text()) || 0;
//   let trust = parseFloat($("#trustRating").text()) || 0;
//   let communication = parseFloat($("#communicationRating").text()) || 0;
//   let manner = parseFloat($("#mannerRating").text()) || 0;

// let ctx = document.getElementById("reviewDonutChart").getContext("2d");

// if (window.reviewChart) {
//   window.reviewChart.destroy(); // ✅ 기존 차트를 삭제하고 새로 생성
// }

// window.reviewChart = new Chart(ctx, {
//   type: "doughnut",
//   data: {
//     labels: ["청결도", "신뢰도", "소통 능력", "매너"],
//     datasets: [
//       {
//         data: [cleanliness, trust, communication, manner],
//         backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0"],
//         borderWidth: 1,
//       },
//     ],
//   },
//   options: {
//     responsive: true,
//     maintainAspectRatio: false,
//     plugins: {
//       legend: { display: false },
//       tooltip: {
//         callbacks: {
//           label: function (tooltipItem) {
//             return tooltipItem.label + ": " + tooltipItem.raw + " / 5.0";
//           },
//         },
//       },
//     },
//   },
// });

// console.log("✅ 차트 로드 완료!");
// }

// ✅ 리뷰 삭제 함수
function deleteReview() {
  let reviewId = $(this).attr("data-review-id");
  console.log("삭제 버튼 클릭됨, 리뷰 ID:", reviewId);

  if (!reviewId) {
    alert("삭제할 리뷰 ID를 찾을 수 없습니다.");
    return;
  }

  if (!confirm("정말 삭제하시겠습니까?")) return;

  $.ajax({
    url: `/review/delete/${reviewId}`,
    method: "DELETE",
    success: function () {
      console.log(`리뷰 ${reviewId} 삭제 완료`);
      $(`#review-${reviewId}`).remove();
      updateReviewCount(-1);
      // loadChart(); // ✅ 리뷰 삭제 후 차트 업데이트
    },
    error: function (xhr) {
      console.error("삭제 오류:", xhr.responseText);
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
  console.log("📢 정렬 방식 변경됨:", sortBy);

  $.ajax({
    url: `/review/sort?order=${sortBy}`,
    method: "GET",
    dataType: "json",
    success: function (sortedReviews) {
      console.log("✅ 정렬된 리뷰 데이터 수신 완료", sortedReviews);

      if (!Array.isArray(sortedReviews)) {
        console.error("❌ 잘못된 데이터 형식 수신:", sortedReviews);
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
      console.error("❌ 리뷰 정렬 오류:", xhr.status, xhr.responseText);
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
