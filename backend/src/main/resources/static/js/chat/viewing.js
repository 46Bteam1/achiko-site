$(function () {
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();
  const nickname = $("#nickname").val();
  const role = $("#role").val();

  initModal(role);
  $("#scheduleBtn").on("click", function () {
    console.log(role);
  });
});

function initModal(role) {
  if (role === "Guest") {
    $.ajax({
      url: "/viewing/findGuests",
    });
  }
}
