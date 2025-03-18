$(function () {
  // header 관련
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

  const socket = new SockJS("/ws");

  const stompClient = Stomp.over(socket);
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();
  const nickname = $("#nickname").val();

  $("#toMyPageBtn").on("click", function () {
    window.location.href = `/mypage/mypageView?userId=${userId}`;
  });

  $("#chatRoomsBtn").on("click", function () {
    window.location.href = "/chatRooms";
  });

  getRoommates(chatRoomId);
  shareInfo(chatRoomId);

  stompClient.connect({}, function () {
    // 채팅 내역 불러오기
    loadChats(chatRoomId);

    // 연결 확인 후에 메시지 전송
    setTimeout(() => {
      const enterMessage = {
        chatroomId: chatRoomId,
        nickname: nickname,
      };
      stompClient.send("/app/chatEnterRoom", {}, JSON.stringify(enterMessage));
    }, 100); // 0.1초 정도 딜레이

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
      sendChat(chatRoomId, nickname);
    });

    $("#message").keydown(function (event) {
      if (event.key === "Enter" && !event.shiftKey) {
        // Shift+Enter는 줄 바꿈, Enter만 누르면 전송
        event.preventDefault(); // 기본 엔터 동작(줄 바꿈) 방지
        sendChat(chatRoomId, nickname);
      }
    });
  });

  // 퇴장 처리 함수
  function leaveChatRoom(chatRoomId, nickname) {
    if (stompClient && stompClient.connected) {
      const leaveMessage = {
        chatroomId: chatRoomId,
        nickname: nickname,
      };
      stompClient.send("/app/chatLeaveRoom", {}, JSON.stringify(leaveMessage));

      stompClient.disconnect(() => {
        console.log("WebSocket 연결 종료됨");
      });
    }
  }

  // 페이지를 떠날 때 자동으로 퇴장 메시지 전송
  window.addEventListener("beforeunload", () => {
    leaveChatRoom(chatRoomId, nickname);
  });

  // 메세지 보내기
  function sendChat(roomId, nickname) {
    const messageContent = $("#message").val().trim();
    if (messageContent !== "") {
      stompClient.send(
        "/app/chatSendMessage", // 백엔드에서 설정한 메시지 전송 경로
        {},
        JSON.stringify({
          chatroomId: roomId, // 채팅방 ID 전달
          nickname: nickname, // 보낸 사람 정보 (이 변수는 필요하면 설정해야 함)
          message: messageContent,
          sentAt: new Date(),
        })
      );
      $("#message").val(""); // 메시지 입력창 초기화
    }
  }
});

function showChat(data) {
  console.log("여기는 showChat:", data);

  let sentAt = "시간 정보 없음";

  if (data["sentAt"]) {
    let date = new Date(data["sentAt"]); // UTC 기준 Date 객체 생성
    date.setHours(date.getHours() + 9);

    sentAt = date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  }

  let tag = `
    <tr>
      <td class="chatNickname">${data["nickname"]}</td>
      <td class="chatMessage">${data["message"]}</td>
      <td class="chatTime">${sentAt}</td>
    </tr>`;

  $("#chats table").append(tag);

  scrollToBottom(); // 메시지가 추가될 때마다 스크롤 이동
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
    let date = new Date(item["sentAt"]); // UTC 시간 기준으로 Date 객체 생성
    date.setHours(date.getHours());

    let formattedDate = date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });

    tag += `
        <tr>
            <td class="chatNickname">${item["nickname"]}</td>
            <td class="chatMessage">${item["message"]}</td>
            <td class="chatTime">${formattedDate}</td>
        </tr>
    `;
  });
  tag += `</table>`;

  $("#chats").html(tag);

  scrollToBottom(); // 채팅을 불러온 후 스크롤 이동
}
// 채팅 스크롤 제일 마지막에 위치하도록
function scrollToBottom() {
  $("#chats").scrollTop($("#chats")[0].scrollHeight);
}

// 현재 share에 확정된 유저들 불러오는 함수
function getRoommates(chatRoomId) {
  $.ajax({
    url: "/roommate/findRoommates",
    method: "GET",
    data: { chatRoomId: chatRoomId },
    success: function (resp) {
      let tag = ``;

      $.each(resp, function (index, item) {
        let profileImage = item["profileImage"]
          ? item["profileImage"]
          : "/images/default-profile.png";

        tag += `
        <div style="display: flex; flex-direction: column; align-items: center;">
          <img src="${profileImage}" alt="프로필 이미지" width="150px" height="150px" style="border-radius: 50%; object-fit: cover;">
          <p class="isHost" style="text-align: center; margin-top: 5px; font-weight: bold; color: ${
            item["isHost"] === 0 ? "#28a745" : "#d9534f"
          };">${item["isHost"] === 0 ? "Guest" : "Host"}</p>
          <p class="guestNickname" style="text-align: center; margin-top: 5px;">${
            item["nickname"]
          }</p>
        </div>
        `;
      });

      $("#guestBox").html(tag);
    },
  });
}

// share 정보 불러오는 함수
function shareInfo(chatRoomId) {
  $.ajax({
    url: "/chat/shareInfo",
    method: "GET",
    data: { chatRoomId: chatRoomId },
    success: function (resp) {
      let description =
        resp.description.length > 20
          ? resp.description.substring(0, 20) + "..."
          : resp.description;

      let tag = `
      <h3>${resp.title}</h3>
      <p>${description}</p>
      `;

      $("#shareBox").html(tag);
    },
  });
}
