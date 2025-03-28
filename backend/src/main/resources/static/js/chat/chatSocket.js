$(function () {
  const socket = new SockJS("/ws");

  const stompClient = Stomp.over(socket);
  const chatRoomId = $("#chatroomId").val();

  const userId = $("#userId").val();
  const nickname = $("#nickname").val();
  const profileImage = $("#profileImage").val();

  $(document).on("click", ".shareDetailBtn", function () {
    let shareId = $(this).data("share-id");
    if (shareId) {
      window.location.href = `/share/selectOne?shareId=${shareId}`;
    } else {
      alert("Share ID가 없습니다.");
    }
  });

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
      sendChat(chatRoomId, nickname, profileImage);
    });

    $("#message").keydown(function (event) {
      if (event.key === "Enter" && !event.shiftKey) {
        // Shift+Enter는 줄 바꿈, Enter만 누르면 전송
        event.preventDefault(); // 기본 엔터 동작(줄 바꿈) 방지
        sendChat(chatRoomId, nickname, profileImage);
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
      });
    }
  }

  // 페이지를 떠날 때 자동으로 퇴장 메시지 전송
  window.addEventListener("beforeunload", () => {
    leaveChatRoom(chatRoomId, nickname);
  });

  // 메세지 보내기
  function sendChat(roomId, nickname, profileImage) {
    const messageContent = $("#message").val().trim();
    if (messageContent !== "") {
      stompClient.send(
        "/app/chatSendMessage", // 백엔드에서 설정한 메시지 전송 경로
        {},
        JSON.stringify({
          chatroomId: roomId, // 채팅방 ID 전달
          nickname: nickname, // 보낸 사람 정보 (이 변수는 필요하면 설정해야 함)
          message: messageContent,
          profileImage: profileImage,
          sentAt: new Date(),
        })
      );
      $("#message").val(""); // 메시지 입력창 초기화
    }
  }
});

function showChat(data) {
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
    });
  }

  let profileImage = data["profileImage"]
    ? data["profileImage"]
    : "/images/default-profile.png";

  const currentNickname = $("#nickname").val();

  let myMessage =
    currentNickname == data["nickname"] ? "myMessage" : "otherMessage";
  let chatContent =
    currentNickname == data["nickname"] ? "chatContent" : "otherChatContent";
  let mychatMessage =
    currentNickname == data["nickname"] ? "myChatMessage" : "otherChatMessage";

  let tag = `
      <div class="chatMessageContainer ${myMessage}">
        <div class="chatProfileImageContainer">
          <img class="chatProfileImage" src="${profileImage}" alt="프로필 이미지">
        </div>
        <div class="${chatContent}">
          <div class="chatNickname">${data["nickname"]}</div>
          <div class="chatMessage ${mychatMessage}">${data["message"]}</div>
        </div>
        <div class="chatTimeContainer">
          <div class="chatTime">${sentAt}</div>
        </div>
      </div>`;

  $("#chats").append(tag);

  scrollToBottom(); // 메시지가 추가될 때마다 스크롤 이동
}

function showAlert(data) {
  let tag = `
        <tr>
          <td>${data["message"]}</td>
        </tr>`;

  $("#alertTableBody").append(tag);

  // $("#alertBox").scrollTop($("#alertBox")[0].scrollHeight);
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
  let tag = ``;

  $.each(resp, function (index, item) {
    let date = new Date(item["sentAt"]);
    date.setHours(date.getHours());

    let formattedDate = date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      // second: "2-digit",
    });

    const currentNickname = $("#nickname").val();

    let myMessage =
      currentNickname == item["nickname"] ? "myMessage" : "otherMessage";
    let chatContent =
      currentNickname == item["nickname"] ? "chatContent" : "otherChatContent";
    let mychatMessage =
      currentNickname == item["nickname"]
        ? "myChatMessage"
        : "otherChatMessage";

    // profileImage가 null 또는 빈 문자열이면 기본 이미지로 설정
    let profileImage = item["profileImage"]
      ? item["profileImage"]
      : "/images/default-profile.png";

    tag += `
      <div class="chatMessageContainer ${myMessage}">
        <div class="chatProfileImageContainer">
          <img class="chatProfileImage" src="${profileImage}" alt="프로필 이미지">
        </div>
        <div class="${chatContent}">
          <div class="chatNickname">${item["nickname"]}</div>
          <div class="chatMessage ${mychatMessage}">${item["message"]}</div>
        </div>
        <div class="chatTimeContainer">
          <div class="chatTime">${formattedDate}</div>
        </div>
      </div>`;
  });

  $("#chats").html(tag);

  scrollToBottom();
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
        <div class="userProfileContainer">
          <img src="${profileImage}" alt="프로필 이미지" class="profileImg">
          <p class="isHost" style=" color: ${
            item["isHost"] === 0 ? "#28a745" : "#d9534f"
          };">${item["isHost"] === 0 ? "Guest" : "Host"}</p>
          <p class="guestNickname" >${item["nickname"]}</p>
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
      let title = resp.title || "제목 없음";
      let maxGuests = resp.maxGuests || "?";
      let currentGuests = resp.currentGuests || "?";
      let townName = resp.townName || "?";
      let price = resp.price || "?";
      let description =
        resp.description.length > 20
          ? resp.description.substring(0, 20) + "..."
          : resp.description;

      let tag = `
      <h3>${resp.title}</h3>
      <p>${description}</p>
      `;
      $("#chattingroomTitle").text(title);
      $("#maxGuests").text(maxGuests);
      $("#currentGuests").text(currentGuests);
      $("#chattingTownName").text(townName);
      $("#monthlyRent").text(price);
      $("#shareBox").html(tag);
    },
  });
}
