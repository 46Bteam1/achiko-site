$(function () {
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();
  const nickname = $("#nickname").val();
  const role = $("#role").val();
  const shareId = $("#shareId").val();

  // 모달이 열릴 때 initModal(role) 실행
  $("#exampleModal").on("shown.bs.modal", function () {
    initModal(role);
  });

  $("#scheduleBtn").on("click", function () {
    const viewingDate = $("#viewingDate").val();
    const viewingTime = $("#viewingTime").val();

    if (!viewingDate || !viewingTime) {
      alert("날짜와 시간을 모두 입력하세요.");
      return;
    }

    // LocalDateTime 형식에 맞게 변환
    const scheduledDate = `${viewingDate}T${viewingTime}:00`;
    const data = {
      shareId: shareId,
      guestNickname: nickname,
      scheduledDate: scheduledDate,
      isCompleted: false,
    };
    $.ajax({
      url: "/viewing/setViewing",
      method: "POST",
      contentType: "application/json", // Content-Type을 application/json으로 설정
      data: JSON.stringify(data),
      success: function (resp) {
        alert(resp);
        initModal(role);
      },
    });
  });
});

function initModal(role) {
  if (role === "Host") {
    $.ajax({
      url: "/viewing/findHosts",
      method: "GET",
      beforeSend: function () {
        $("#existingReviews").html('<div class="loader"></div>'); // ✅ 스피너 표시
        $("#exampleModal").modal("show"); // ✅ 모달은 바로 띄우기
      },
      success: function (resp) {
        viewingTable(resp);
      },
    });
  } else {
    $.ajax({
      url: "/viewing/findGuests",
      method: "GET",
      beforeSend: function () {
        $("#existingReviews").html('<div class="loader"></div>');
        $("#exampleModal").modal("show");
      },
      success: function (resp) {
        viewingTable(resp);
      },
    });
  }
}

function viewingTable(resp) {
  const role = $("#role").val();
  let tag = "";

  if (resp.length === 0) {
    //  데이터가 없을 경우 메시지 출력
    tag = `<div>viewing 약속이 없습니다.</div>`;
  } else {
    //  데이터가 있을 경우 테이블 출력
    tag += `
      <table border="1">
        <tr>
          <th style="border: 1px solid black; padding: 5px;">상대방</th>
          <th style="border: 1px solid black; padding: 5px;">예약 날짜</th>
          <th style="border: 1px solid black; padding: 5px;"></th>
        </tr>`;

    const nickname = $("#nickname").val();
    $.each(resp, function (index, item) {
      let nicknameCheck = item["hostNickname"] === nickname;
      let displayNickname = nicknameCheck
        ? item["guestNickname"]
        : item["hostNickname"];

      let scheduledDate = item["scheduledDate"] || "날짜 없음"; //  기본값 설정
      let isCompleted = item["isCompleted"]; //  완료 여부 확인
      let disabledAttr = isCompleted ? "disabled" : ""; //  비활성화 속성 설정

      tag += `
            <tr>
              <td style="border: 1px solid black; padding: 5px;">${displayNickname}</td>
              <td style="border: 1px solid black; padding: 5px;">${scheduledDate}</td>
              <td>
                <input type="button" value="날짜 수정" class="updateViewingBtn" 
                data-seq="${item["viewingId"]}" ${disabledAttr}>
                ${
                  nicknameCheck
                    ? `<input type="button" value="확정" class="confirmViewingBtn" 
                    data-seq="${item["viewingId"]}" ${disabledAttr}>`
                    : ""
                }
                <input type="button" value="뷰잉 삭제" class="deleteViewingBtn" data-role="${role}"
                data-seq="${item["viewingId"]}" ${disabledAttr}>
              </td>
            </tr>`;
    });

    tag += `</table>`;
  }

  $("#existingReviews").html(tag);

  $(".updateViewingBtn").on("click", updateViewing);
  $(".confirmViewingBtn").on("click", confirmViewing);
  $(".deleteViewingBtn").on("click", deleteViewing);
}

// TODO: 3월 6일에 추가할 것
// viewing 날짜 수정
function updateViewing() {
  let viewingId = $(this).attr("data-seq");
}

// viewing 취소
function deleteViewing() {
  let viewingId = $(this).attr("data-seq");
  let role = $(this).attr("data-role");

  if (confirm("viewing을 취소하시겠습니까? 취소 이후 번복할 수 없습니다.")) {
    $.ajax({
      url: "/viewing/cancel",
      method: "DELETE",
      data: { viewingId: viewingId },
      success: function (resp) {
        alert(resp);
        initModal(role);
      },
    });
  }
}

// viewing 확정(host)
function confirmViewing() {
  const $btn = $(this);
  let viewingId = $btn.attr("data-seq");

  if (
    confirm(
      "viewing을 완료하시겠습니까? viewing은 확정된 이후 되돌릴 수 없습니다."
    )
  ) {
    $.ajax({
      url: "/viewing/confirm",
      method: "PATCH",
      data: { viewingId: viewingId },
      success: function (resp) {
        alert(resp);
        // data-seq 값이 같은 모든 input 버튼 비활성화 처리
        $('input[data-seq="' + viewingId + '"]')
          .prop("disabled", true)
          .addClass("disabled");
      },
    });
  } else {
    alert("viewing 완료 취소");
  }
}
