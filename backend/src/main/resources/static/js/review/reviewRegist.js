$(document).ready(function () {
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

  // $("#reviewForm").submit(function (event) {
  //   event.preventDefault();

  //   let formData = {
  //     reviewedUserId: $("input[name='reviewedUserId']").val(),
  //     reviewerId: $("input[name='reviewerId']").val(),
  //     cleanlinessRating: $("input[name='cleanlinessRating']:checked").val(),
  //     trustRating: $("input[name='trustRating']:checked").val(),
  //     comment: $("#comment").val(),
  //   };

  //   $.ajax({
  //     type: "POST",
  //     url: "/review/regist",
  //     contentType: "application/json",
  //     data: JSON.stringify(formData),
  //     success: function (response) {
  //       alert("리뷰가 성공적으로 등록되었습니다!");
  //       window.location.href =
  //         "/reviewPage?reviewedUserId=" + formData.reviewedUserId;
  //     },
  //     error: function (xhr) {
  //       alert("오류 발생: " + xhr.responseText);
  //     },
  //   });
  // });
});
