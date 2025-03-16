$(document).ready(function () {
  let bootpayApplicationId = ""; // Bootpay Application ID 저장 변수

  // 서버에서 Bootpay Application ID 가져오기
  $.ajax({
    url: "/api/subscribe/config",
    type: "GET",
    success: function (response) {
      bootpayApplicationId = response.bootpayApplicationId;
    },
    error: function (xhr) {
      console.error("환경 변수 로드 실패:", xhr.responseText);
    },
  });

  // PG와 결제 방법 매핑
  const pgMap = {
    kakaopay: { pg: "카카오", method: "카카오페이" },
    tosspay: { pg: "토스", method: "토스" },
    samsungpay: { pg: "나이스페이", method: "삼성페이" },
    naverpay: { pg: "나이스페이", method: "네이버페이" },
  };

  // 결제 요청 버튼 클릭 이벤트
  $("#subscribeButton").click(function () {
    if (!bootpayApplicationId) {
      alert("Bootpay Application ID를 불러오지 못했습니다. 다시 시도해주세요.");
      return;
    }

    const selectedPG = $("#pgSelect").val(); // 사용자가 선택한 PG
    // const price = $("#price").val(); // 결제 금액
    const price = 1000000000; // 결제 금액
    const userId = $("#userId").val(); // 사용자 ID(DB의 user_id)
    const loginId = $("#loginId").val();
    const email = $("#email").val();

    if (!userId || !price) {
      alert("모든 정보를 입력해주세요.");
      return;
    }

    // 선택된 PG에 따른 `pg`와 `method` 자동 설정
    const pgInfo = pgMap[selectedPG] || { pg: "", method: "" };

    // Bootpay 결제 요청
    Bootpay.requestPayment({
      application_id: bootpayApplicationId, // 서버에서 가져온 Application ID 사용
      price: price, // 입력된 결제 금액
      order_name: "Achiko Premium 결제", // 상품명
      order_id: "TEST_ORDER_" + new Date().getTime(), // 고유 주문번호
      pg: pgInfo.pg, // 자동 설정된 PG
      method: pgInfo.method, // 자동 설정된 결제 방식
      user: {
        id: userId, // 유저 아이디
        username: loginId,
        email: email,
      },
      extra: {
        sandbox: true, // 샌드박스 모드 활성화
        open_type: "popup", // 팝업 모드 설정
        // popupMode: true,
      },
    })
      .then(function (res) {
        let receiptId = res.data.receipt_id;

        // 결제 성공 후 구독 상태 업데이트 요청
        $.ajax({
          url: "/api/subscribe/updateSubscription",
          type: "POST",
          data: { receiptId: receiptId },
          success: function (response) {
            alert("결제가 완료됐습니다!\n더 다양한 서비스를 누려보세요!");
            if (response.redirectUrl) {
              window.location.href = response.redirectUrl;
            }
          },
          error: function (xhr) {
            alert("결제 상태 업데이트 실패");
            console.log("결제 상태 업데이트 실패" + xhr.responseText);
          },
        });
      })
      .catch(function (err) {
        console.log("결제 실패:", err);
        alert("결제 실패");
      });
  });

  $("#subscribeCancelButton").click(function () {
    const userId = $("#userId").val();

    if (!userId) {
      alert("유저 정보를 확인할 수 없습니다.");
      return;
    }

    // 결제 취소 요청
    $.ajax({
      url: "/api/subscribe/cancelSubscription",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({ userId: userId }),
      success: function (response) {
        alert("다음에 또 만나요!");
        if (response.redirectUrl) {
          window.location.href = response.redirectUrl;
        }
      },
      error: function (xhr) {
        console.log("구독 취소 실패: " + xhr.responseText);
        alert("구독 취소 실패");
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

});
