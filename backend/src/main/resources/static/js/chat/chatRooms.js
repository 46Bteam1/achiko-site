$(function () {
  // TODO: 로그인한 유저 아이디 받아오기

  // 유저가 속한 전체 채팅방들 출력
  initChatRooms();
});

function initChatRooms() {
  $.ajax({
    url: "/chat/selectRooms",
    method: "GET",
    data: { userId: 1 },
    success: output,
  });
}

function output(resp) {
  let tag = `<table>`;

  $.each(resp, function (index, item) {
    tag += `
        <tr>
            <td>${index + 1}</td>
            <td>${item["chatroomId"]}</td>
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

  // TODO: 채팅방 삭제 구현하기

  // $.ajax({
  //   url: "/chat/deleteRoom",
  //   method: "DELETE",
  //   data: { chatroomId: chatroomId },
  //   success: initChatRooms,
  // });
}

/* 채팅방 입장 함수 */
function enterRoom() {
  let chatroomId = $(this).attr("data-seq");

  $.ajax({
    url: "/chat",
  });
}
