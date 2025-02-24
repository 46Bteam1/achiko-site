$(document).ready(function () {
  console.log("✅ subscribe.js 로드됨");

  // ✅ 결제 요청 버튼 클릭 이벤트
  $("#subscribeButton").click(function () {
    const selectedPG = $("#pgSelect").val(); // 사용자가 선택한 PG
    const price = $("#price").val(); // 결제 금액
    const userId = $("#userId").val(); // 사용자 ID
    const userPhone = $("#userPhone").val(); // 휴대폰 번호

    if (!userId || !price || !userPhone) {
      alert("모든 정보를 입력해주세요.");
      return;
    }

    // ✅ Bootpay 결제 요청
    Bootpay.requestPayment({
      application_id: "67b8250dcc5274a3ac3fcd7c", // Bootpay에서 발급받은 Application ID 입력
      price: price, // 입력된 결제 금액
      order_name: "테스트 결제", // 상품명
      order_id: "TEST_ORDER_" + new Date().getTime(), // 고유 주문번호
      // pg: "토스", // 사용자가 선택한 PG (TossPay, NaverPay, KakaoPay)
      // method: "카드", // 결제 방식
      pg: "네이버", // 사용자가 선택한 PG (TossPay, NaverPay, KakaoPay)
      method: "간편", // 결제 방식
      // pg: "카카오페이", // 사용자가 선택한 PG (TossPay, NaverPay, KakaoPay)
      // method: "간편", // 결제 방식
      user: {
        id: userId, // 유저 아이디
        username: "테스트 사용자",
        phone: userPhone,
        email: "test@example.com",
      },
      extra: {
        sandbox: true, // 샌드박스 모드 활성화
        // open_type: "iframe", // 결제창 타입 (iframe / popup / redirect 가능)
        open_type: "popup", // 결제창 타입 (iframe / popup / redirect 가능)
      },
    })
      .then(function (res) {
        console.log("✅ 결제 성공:", res);
        alert("결제 성공!\n" + JSON.stringify(res));

        // ✅ 결제 성공 시 영수증 ID 저장
        $("#receiptId").val(res.receipt_id);
      })
      .catch(function (err) {
        console.log("❌ 결제 실패:", err);
        alert("결제 실패: " + JSON.stringify(err));
      });
  });

  // ✅ 결제 취소 버튼 클릭 이벤트
  $("#cancelButton").click(function () {
    const receiptId = $("#receiptId").val();
    const reason = $("#cancelReason").val();

    if (!receiptId || !reason) {
      alert("영수증 ID와 취소 사유를 입력해주세요.");
      return;
    }

    // ✅ 결제 취소 요청
    $.ajax({
      url: "/api/subscribe/cancel",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({ receiptId: receiptId, reason: reason }),
      success: function (response) {
        alert("결제 취소 성공!\n" + JSON.stringify(response));
      },
      error: function (xhr) {
        alert("결제 취소 실패: " + xhr.responseText);
      },
    });
  });
});
