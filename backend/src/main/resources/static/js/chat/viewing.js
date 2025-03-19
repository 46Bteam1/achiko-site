$(function () {
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();
  const nickname = $("#nickname").val();
  const role = $("#role").val();
  const shareId = $("#shareId").val();
  const now = new Date();

  // main 태그가 있으면 Bootstrap을 활성화
  if ($("main").length) {
    $("#bootstrap-css").prop("disabled", false);

    // Bootstrap JS (Popper 포함) 로드
    $.getScript(
      "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
    );
  }

  // 모달이 열릴 때 initModal(role) 실행
  $("#exampleModal").on("shown.bs.modal", function () {
    initModal(role);
  });

  $("#scheduleBtn").on("click", function () {
    const viewingDate = $("#viewingDate").val();
    const viewingTime = $("#viewingTime").val();

    if (!viewingDate || !viewingTime) {
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "날짜와 시간을 모두 입력해주세요.",
      });
      return;
    }

    // LocalDateTime 형식에 맞게 변환
    const scheduledDate = `${viewingDate}T${viewingTime}:00`;
    const scheduledDate2 = new Date(scheduledDate);

    if (scheduledDate2 < now) {
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "입력하신 날짜는 현재보다 이전입니다.",
      });
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
        if (resp.startsWith("이미 viewing 약속이 존재")) {
          Swal.fire({
            title: `${resp}`,
            icon: "warning",
            draggable: true,
          });
        } else if (resp.startsWith("이미 다른 share에서")) {
          Swal.fire({
            title: `${resp}`,
            icon: "warning",
            draggable: true,
          });
        } else {
          Swal.fire({
            title: "뷰잉 예약 성공!",
            icon: "success",
            text: `${resp}`,
            draggable: true,
          });

          initModal(role);
        }
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
    // 데이터가 없을 경우 메시지 출력
    tag = `<div>viewing 약속이 없습니다.</div>`;
  } else {
    // 데이터가 있을 경우 테이블 출력
    tag += `
      <table border="1" style="width: 100%; border-collapse: collapse; margin-top: 20px; background-color: #f9f9f9;">
      <tr style="background-color: #4CAF50; color: white; font-weight: bold;">
        <th style="border: 1px solid black; padding: 10px; text-align: center;">상대방</th>
        <th style="border: 1px solid black; padding: 10px; text-align: center;">예약 날짜</th>
        <th style="border: 1px solid black; padding: 10px; text-align: center;"></th>
      </tr>`;

    const nickname = $("#nickname").val();
    $.each(resp, function (index, item) {
      let nicknameCheck = item["hostNickname"] === nickname;
      let displayNickname = nicknameCheck
        ? item["guestNickname"]
        : item["hostNickname"];
      let scheduledDate = item["scheduledDate"] || "날짜 없음";
      let isCompleted = item["isCompleted"];
      let actionCellContent = isCompleted
        ? '<span style="color: red; font-weight: bold;">이미 완료된 뷰잉입니다</span>'
        : `
          <input type="button" value="날짜 수정" class="updateViewingBtn" data-seq="${
            item["viewingId"]
          }" style="padding: 8px 12px; border: none; border-radius: 5px; cursor: pointer; background-color: #ffcc00; color: black; font-size: 14px;">
          ${
            nicknameCheck
              ? `<input type="button" value="확정" class="confirmViewingBtn" data-seq="${item["viewingId"]}" style="padding: 8px 12px; border: none; border-radius: 5px; cursor: pointer; background-color: #008CBA; color: white; font-size: 14px;">`
              : ""
          }
          <input type="button" value="뷰잉 삭제" class="deleteViewingBtn" data-role="${role}" data-seq="${
            item["viewingId"]
          }" style="padding: 8px 12px; border: none; border-radius: 5px; cursor: pointer; background-color: #f44336; color: white; font-size: 14px;">`;

      tag += `
      <tr style="border: 1px solid #ddd; text-align: center; ${
        index % 2 === 0 ? "background-color: #f2f2f2;" : ""
      }">
        <td style="border: 1px solid black; padding: 10px;">${displayNickname}</td>
        <td style="border: 1px solid black; padding: 10px;">${scheduledDate}</td>
        <td style="border: 1px solid black; padding: 10px;">${actionCellContent}</td>
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
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "날짜와 시간을 모두 입력해주세요.",
      });
      return;
    }

    // 새로운 예약 날짜를 ISO 형식(예: "2025-03-07T15:30:00")으로 생성
    let newScheduledDate = newDate + "T" + newTime + ":00";
    // 새로운 날짜를 Date 객체로 변환하여 현재 날짜와 비교
    let newScheduledDateObj = new Date(newScheduledDate);
    let now = new Date();
    if (newScheduledDateObj < now) {
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "입력하신 날짜는 현재보다 이전입니다.",
      });

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
        Swal.fire({
          title: "날짜 수정 성공!",
          icon: "success",
          text: `${resp}`,
          draggable: true,
        });
        // 성공 시 셀의 내용을 새로운 날짜 값으로 갱신
        scheduledCell.text(newScheduledDate);
        // 버튼을 원래 "날짜 수정"으로 복원하고 updateViewing 이벤트 다시 바인딩
        btn.val("날짜 수정");
        btn.off("click").on("click", updateViewing);
      },
      error: function (err) {
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: "수정에 실패했습니다.",
        });
      },
    });
  });
}

// viewing 취소
function deleteViewing() {
  let viewingId = $(this).attr("data-seq");
  let role = $(this).attr("data-role");

  Swal.fire({
    title: "Viewing Cancel",
    text: "viewing을 취소하시겠습니까? 취소 이후 번복할 수 없습니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Viewing 취소",
  }).then((result) => {
    $.ajax({
      url: "/viewing/cancel",
      method: "DELETE",
      data: { viewingId: viewingId },
      success: function (resp) {
        Swal.fire({
          title: "뷰잉 취소 성공!",
          icon: "success",
          text: `${resp}`,
          draggable: true,
        });
        initModal(role);
      },
    });
  });
}

function confirmViewing() {
  const $btn = $(this);
  let viewingId = $btn.attr("data-seq");

  const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: "btn btn-success",
      cancelButton: "btn btn-danger",
    },
    buttonsStyling: false,
  });
  swalWithBootstrapButtons
    .fire({
      title: "viewing을 완료하시겠습니까?",
      text: "viewing은 확정된 이후 되돌릴 수 없습니다.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "확정!",
      cancelButtonText: "확정 안함!",
      reverseButtons: true,
    })
    .then((result) => {
      if (result.isConfirmed) {
        // Ajax 호출
        $.ajax({
          url: "/viewing/check",
          method: "GET",
          data: { viewingId: viewingId },
          success: function (resp) {
            if (resp) {
              $.ajax({
                url: "/viewing/confirm",
                method: "PATCH",
                data: { viewingId: viewingId },
                success: function (resp) {
                  swalWithBootstrapButtons.fire({
                    title: "확정 완료!",
                    text: "새로운 쉐어 메이트가 생겼네요!",
                    icon: "success",
                  });
                  // 완료 표시
                  $('input[data-seq="' + viewingId + '"]')
                    .closest("td")
                    .html(
                      '<span style="color: red; font-weight: bold;">이미 완료된 뷰잉입니다</span>'
                    );
                },
                error: function () {
                  swalWithBootstrapButtons.fire({
                    icon: "error",
                    title: "에러",
                    text: "확정 중 문제가 발생했습니다.",
                  });
                },
              });
            } else {
              swalWithBootstrapButtons.fire({
                icon: "error",
                title: "Oops...",
                text: "이미 해당 share의 인원이 다 찼습니다.",
              });
            }
          },
          error: function () {
            swalWithBootstrapButtons.fire({
              icon: "error",
              title: "에러",
              text: "확인 중 문제가 발생했습니다.",
            });
          },
        });
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        swalWithBootstrapButtons.fire({
          title: "확정 취소",
          text: "아직 확정하지 않았습니다.",
          icon: "error",
        });
      }
    });

  // if (
  //   confirm(
  //     "viewing을 완료하시겠습니까? viewing은 확정된 이후 되돌릴 수 없습니다."
  //   )
  // ) {
  //   $.ajax({
  //     url: "/viewing/check",
  //     method: "GET",
  //     data: { viewingId: viewingId },
  //     success: function (resp) {
  //       if (resp) {
  //         $.ajax({
  //           url: "/viewing/confirm",
  //           method: "PATCH",
  //           data: { viewingId: viewingId },
  //           success: function (resp) {
  //             Swal.fire({
  //               title: "뷰잉 확정 성공!",
  //               text: `${resp}`,
  //               icon: "success",
  //               draggable: true,
  //             });
  //             // data-seq 값이 같은 모든 input 버튼을 숨기고 '이미 완료된 뷰잉입니다' 메시지 표시
  //             $('input[data-seq="' + viewingId + '"]')
  //               .closest("td")
  //               .html(
  //                 '<span style="color: red; font-weight: bold;">이미 완료된 뷰잉입니다</span>'
  //               );
  //           },
  //         });
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: "Oops...",
  //           text: "이미 해당 share의 인원이 다 찼습니다.",
  //         });
  //       }
  //     },
  //   });
  // } else {
  //   Swal.fire({
  //     title: "수정 취소!",
  //     icon: "success",
  //     draggable: true,
  //   });
  // }
}
