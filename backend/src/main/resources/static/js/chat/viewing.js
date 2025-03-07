$(function () {
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();
  const nickname = $("#nickname").val();
  const role = $("#role").val();
  const shareId = $("#shareId").val();
  const now = new Date();

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
    const scheduledDate2 = new Date(scheduledDate);

    if (scheduledDate2 < now) {
      alert("입력하신 날짜는 현재보다 이전입니다.");
      return;
    }

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

// viewing 날짜 수정
function updateViewing() {
  // 현재 버튼이 속한 행(row)을 선택
  let row = $(this).closest("tr");
  // 두 번째 셀(예약 날짜 셀)을 선택 (0부터 시작하므로 eq(1))
  let scheduledCell = row.find("td:eq(1)");
  let currentDate = scheduledCell.text().trim();

  // 기존 날짜 문자열을 날짜와 시간으로 분리 (예: "2025-03-07T15:30:00")
  let dateValue = "";
  let timeValue = "";
  if (currentDate && currentDate !== "날짜 없음") {
    if (currentDate.indexOf("T") > -1) {
      var parts = currentDate.split("T");
      dateValue = parts[0];
      // 시간은 HH:MM 형식으로 사용 (초는 생략)
      timeValue = parts[1].substring(0, 5);
    } else {
      dateValue = currentDate;
    }
  }

  // 예약 날짜 셀을 날짜와 시간 입력창으로 교체
  scheduledCell.html(
    '<input type="date" class="updateViewingDate" value="' +
      dateValue +
      '" />' +
      '<input type="time" class="updateViewingTime" value="' +
      timeValue +
      '" />'
  );

  // 버튼의 텍스트를 "저장"으로 변경하고, 기존 이벤트 핸들러 제거
  let btn = $(this);
  btn.val("저장");
  btn.off("click").on("click", function () {
    // 입력된 날짜와 시간 값을 가져옴
    let newDate = row.find(".updateViewingDate").val();
    let newTime = row.find(".updateViewingTime").val();

    if (!newDate || !newTime) {
      alert("날짜와 시간을 모두 입력해주세요.");
      return;
    }

    // 새로운 예약 날짜를 ISO 형식(예: "2025-03-07T15:30:00")으로 생성
    let newScheduledDate = newDate + "T" + newTime + ":00";
    // 새로운 날짜를 Date 객체로 변환하여 현재 날짜와 비교
    let newScheduledDateObj = new Date(newScheduledDate);
    let now = new Date();
    if (newScheduledDateObj < now) {
      alert("입력하신 날짜는 현재보다 이전입니다.");
      return;
    }

    let viewingId = btn.attr("data-seq");

    // 서버로 보낼 데이터 구성 (필요한 값에 맞게 수정)
    let data = {
      viewingId: viewingId,
      scheduledDate: newScheduledDate,
      shareId: $("#shareId").val(),
    };

    // PATCH 요청으로 서버의 changeDate API 호출
    $.ajax({
      url: "/viewing/changeDate",
      method: "PATCH",
      contentType: "application/json",
      data: JSON.stringify(data),
      success: function (resp) {
        alert(resp);
        // 성공 시 셀의 내용을 새로운 날짜 값으로 갱신
        scheduledCell.text(newScheduledDate);
        // 버튼을 원래 "날짜 수정"으로 복원하고 updateViewing 이벤트 다시 바인딩
        btn.val("날짜 수정");
        btn.off("click").on("click", updateViewing);
      },
      error: function (err) {
        alert("수정에 실패했습니다.");
      },
    });
  });
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
