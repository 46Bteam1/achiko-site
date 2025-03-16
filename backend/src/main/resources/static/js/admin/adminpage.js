/**
 *
 */
$(function () {
  $(".post-delete-btn").on("click", deleteShareByAdmin);

  $(".review-delete-btn").on("click", deleteShareByAdmin);

  $(".userDeleteBtn").on("click", withdraw);

  // 신고 기준(5회) 초과한 사용자 is_malicious 업데이트 요청
  $("#update-malicious-btn").click(function () {
    $.post("/admin/update-malicious", function (response) {
      if (response === "success") {
        alert("악성 사용자 목록이 업데이트되었습니다.");
        loadMaliciousUsers();
      }
    });
  });

  loadMaliciousUsers();
});

function deleteShareByAdmin() {
  let shareId = $(this).attr("data-shareId");

  if (!confirm("정말로 이 게시글을 삭제하시겠습니까?")) return;

  $.ajax({
    type: "GET",
    url: "/share/delete",
    data: { shareId: shareId },
    success: function (response) {
      alert("게시물이 삭제되었습니다.");
      window.location.href = "/admin/adminPage";
    },
    error: function (xhr) {
      alert(xhr.responseText || "삭제에 실패했습니다.");
    },
  });
}

// 악성 사용자 목록 불러오기
function loadMaliciousUsers() {
  $.get("/admin/malicious-users", function (data) {
    let tbody = $("#malicious-users");
    tbody.empty();

    if (data.length === 0) {
      tbody.append(
        '<tr><td colspan="6" class="text-center">등록된 회원이 없습니다.</td></tr>'
      );
    } else {
      data.forEach((user, index) => {
        tbody.append(`
                        <tr>
                            <td>${index + 1}</td>
                            <td>${user.nickname}</td>
                            <td>${user.email}</td>
                            <td>${new Date(
                              user.createdAt
                            ).toLocaleDateString()}</td>
                            <td>${
                              user.isMalicious ? "악성 사용자" : "정상"
                            }</td>
                            <td><button class="btn btn-danger" onclick="banUser(${
                              user.userId
                            })">강제 탈퇴</button></td>
                        </tr>
                    `);
      });
    }
  });
}
// 회원 탈퇴
function withdraw() {
  if (!confirm("정말로 회원을 탈퇴하시겠습니까?")) {
    return;
  }
  $.ajax({
    url: "/user/deleteUser",
    type: "DELETE",
    success: function (response) {
      alert(response); // 서버에서 받은 메시지 출력
      window.location.href = "/"; // 메인페이지로 이동
    },
    error: function (xhr) {
      alert("강제 탈퇴 실패: " + xhr.responseText); // 서버에서 보낸 오류 메시지 출력
    },
  });
}
