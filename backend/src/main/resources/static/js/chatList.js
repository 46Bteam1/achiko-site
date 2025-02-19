$(function () {
  $.ajax({
    url: "/chat/selectRoom",
    method: "GET",
    data: { chatRoomId: 1 },
    success: chats,
  });
});

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
