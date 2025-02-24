$(function () {
  const socket = new SockJS("http://localhost:8080/ws");
  const stompClient = Stomp.over(socket);
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();

  stompClient.connect({}, function () {
    // 채팅 내역 불러오기
    loadChats(chatRoomId);

    // 구독 경로
    stompClient.subscribe(`/topic/chatroom/${chatRoomId}`, function (res) {
      const message = JSON.parse(res.body);

      showChat(message);
    });

    // 메시지 전송 버튼 이벤트
    $("#sendMessage").on("click", function () {
      sendChat(chatRoomId, userId);
    });
  });

  // 로그인한 유저의 아이디 가져오기
  function getUserId() {
    $.ajax({
      url: "/user/getId",
      method: "GET",
      success: function (resp) {
        console.log(typeof resp);
        console.log(resp);

        return resp;
      },
    });
  }

  function sendChat(roomId, userId) {
    const messageContent = $("#message").val().trim();
    if (messageContent !== "") {
      stompClient.send(
        "/app/chatSendMessage", // 백엔드에서 설정한 메시지 전송 경로
        {},
        JSON.stringify({
          chatroomId: roomId, // 채팅방 ID 전달
          senderId: userId, // 보낸 사람 정보 (이 변수는 필요하면 설정해야 함)
          message: messageContent,
          sentAt: new Date(),
        })
      );
      $("#message").val(""); // 메시지 입력창 초기화
    }
  }
});

function showChat(data) {
  let tag = `
        <tr>
            <td>${data["senderId"]}</td>
            <td>${data["message"]}</td>
            <td>${data["sentAt"].split(".")[0]}</td>
        </tr>`;

  $("#chats table").append(tag);
}

function loadChats(chatRoomId) {
  $.ajax({
    url: "/chat/selectRoom",
    method: "GET",
    data: { chatRoomId: chatRoomId },
    success: chats,
  });
}

function chats(resp) {
  let tag = `<table>`;

  $.each(resp, function (index, item) {
    tag += `
        <tr>
            <td>${item["senderId"]}</td>
            <td>${item["message"]}</td>
            <td>${item["sentAt"]}</td>
        </tr>
    `;
  });
  tag += `</table>`;

  $("#chats").html(tag);
}
