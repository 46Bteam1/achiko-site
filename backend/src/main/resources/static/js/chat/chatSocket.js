$(function () {
  const socket = new SockJS("http://localhost:8080/ws");
  const stompClient = Stomp.over(socket);
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();

  $("#toMyPageBtn").on("click", function () {
    // TODO: 마이페이지 돌아가는 버튼 만들기
    $.ajax({});
  });

  $("#chatRoomsBtn").on("click", function () {
    window.location.href = "/chatRooms";
  });

  stompClient.connect({}, function () {
    // 채팅 내역 불러오기
    loadChats(chatRoomId);

    // 채팅방 입장
    const enterMessage = {
      chatroomId: chatRoomId,
      senderId: userId,
    };
    stompClient.send("/app/chatEnterRoom", {}, JSON.stringify(enterMessage));

    // 채팅방 입장, 퇴장 메세지 받는 곳
    stompClient.subscribe(`/topic/chatroomAlert/${chatRoomId}`, function (res) {
      const message = JSON.parse(res.body);

      showAlert(message);
    });

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

  // 퇴장 처리 함수
  function leaveChatRoom(chatRoomId, userId) {
    if (stompClient && stompClient.connected) {
      const leaveMessage = {
        chatroomId: chatRoomId,
        senderId: userId,
      };
      stompClient.send("/app/chatLeaveRoom", {}, JSON.stringify(leaveMessage));

      stompClient.disconnect(() => {
        console.log("WebSocket 연결 종료됨");
      });
    }
  }

  // 페이지를 떠날 때 자동으로 퇴장 메시지 전송
  window.addEventListener("beforeunload", () => {
    leaveChatRoom(chatRoomId, userId);
  });

  // 메세지 보내기
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
      <tr>${data["nickname"]}</tr>
      <tr>${data["message"]}</tr>
      <tr>${data["sentAt"].split(".")[0]}</tr>
    </div>`;

  $("#chats table").append(tag);

  // 스크롤을 맨 아래로 이동
  $("#chats").scrollTop($("#chats")[0].scrollHeight);
}

function showAlert(data) {
  let tag = `
        <tr>
          <td>${data["message"]}</td>
        </tr>`;

  $("#alertTableBody").append(tag);

  $("#alertBox").scrollTop($("#alertBox")[0].scrollHeight);
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
