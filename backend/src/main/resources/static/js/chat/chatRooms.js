$(function () {
  // 유저가 속한 전체 채팅방들 출력
  initChatRooms();
  const userId = $("#userId").val();

  $("#toMyPageBtn").on("click", function () {
    window.location.href = `http://localhost:8080/mypage/mypageSample?userId=${userId}`;
  });
});

function initChatRooms() {
  $.ajax({
    url: "/chat/selectRooms",
    method: "GET",
    success: output,
  });
}

function output(resp) {
  const nickname = $("#nickname").val();

  let tag = `<table>`;

  $.each(resp, function (index, item) {
    let profileImage = item["profileImage"]
      ? item["profileImage"]
      : "/images/fubao.webp";

    let nicknameCheck = item["hostNickname"] === nickname;
    let displayNickname = nicknameCheck
      ? item["guestNickname"]
      : item["hostNickname"];
    tag += `
        <tr>
            <td><img src="${profileImage}" alt="프로필 이미지" width="100px" height="100px" style="border-radius: 50%; object-fit: cover;"></td>
            <td>${displayNickname}</td>
            <td class="btns">
                <input type="button" value="입장" 
                class="enterBtn"
                data-seq="${item["chatroomId"]}">
                <input type="button" value="삭제"
                class="deleteBtn"
                data-seq="${item["chatroomId"]}">
            </td>
        </tr>
    `;
  });
  tag += `</table>`;

  $("#chatRooms").html(tag);

  $(".deleteBtn").on("click", deleteRoom);
  $(".enterBtn").on("click", enterRoom);
}

/* 채팅방 삭제 함수 */
function deleteRoom() {
  let chatroomId = $(this).attr("data-seq");

  let answer = confirm("삭제하시겠습니까?");

  if (!answer) return;

  $.ajax({
    url: `/chat/deleteRoom?chatRoomId=${chatroomId}`,
    method: "DELETE",
    success: function (resp) {
      alert(resp);
      initChatRooms();
    },
  });
}

/* 채팅방 입장 함수 */
function enterRoom() {
  let chatroomId = $(this).attr("data-seq");
  console.log(chatroomId);

  $.ajax({
    url: "/chatList",
    data: { chatroomId: chatroomId },
    success: function (response) {
      window.location.href = "/chatList?chatroomId=" + chatroomId;
    },
  });
}
