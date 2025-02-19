$(function () {
  $.ajax({
    url: "/chat/selectRooms",
    method: "GET",
    data: { userId: 1 },
    success: output,
  });
});

function output(resp) {
  let tag = `<table>`;

  $.each(resp, function (index, item) {
    tag += `
        <tr>
            <td>${index + 1}</td>
            <td>${item["chatroomId"]}</td>
            <td class="btns">
                <input type="button" value="입장">
                <input type="button" value="삭제">
            </td>
        </tr>
    `;
  });
  tag += `</table>`;

  $("#chatRooms").html(tag);
}
