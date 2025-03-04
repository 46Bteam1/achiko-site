$(function () {
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();
  const nickname = $("#nickname").val();
  const role = $("#role").val();

  // 모달이 열릴 때 initModal(role) 실행
  $("#exampleModal").on("shown.bs.modal", function () {
    initModal(role);
  });

  $("#scheduleBtn").on("click", function () {
    console.log(role);
  });
});

function initModal(role) {
  console.log(role === "Host");
  if (role === "Host") {
    $.ajax({
      url: "/viewing/findHosts",
      method: "GET",
      success: function (resp) {
        console.log(resp);
      },
    });
  } else {
    $.ajax({
      url: "/viewing/findGuests",
      method: "GET",
      success: function (resp) {
        console.log(resp);
      },
    });
  }
}
