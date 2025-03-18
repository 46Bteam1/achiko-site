$(document).ready(function () {
  tab();

  // 프로필 등록/수정 모달 동작
  $("#openModal").on("click", function () {
    $("#profileModal").addClass("show").css("display", "block");
    $("#profileModal").addClass("modal-open");
    $("body").addClass("modalopen").css("overflow-y", "hidden");
  });
  // 모달 닫기
  $("#closeModal").on("click", function () {
    $("#profileModal").removeClass("show").css("display", "none");
    $("#profileModal").removeClass("modal-open");
    $("body").removeClass("modalopen").css("overflow-y", "auto");
  });

  // 모달 바깥을 클릭하면 닫기
  $("#profileModal").on("click", function (event) {
    if ($(event.target).is("#profileModal")) {
      $("#profileModal").removeClass("show").css("display", "none");
      $("#profileModal").removeClass("modal-open");
      $("body").removeClass("modalopen").css("overflow-y", "auto");
    }
  });

  // 각 입력 필드에서 포커스가 벗어날 때 유효성 검사 실행
  $("#nickname").blur(validateNickname);
  $("#bio").blur(validateBio);
  $("#age").blur(validateAge);
  $("#gender").blur(validateGender);
  $("#religion").blur(validateReligion);
  $("#languages").change(validateLanguages);
  // 프로필 업데이트
  $("#updateBtn").click(function () {
    if (validation()) {
      updateBtn();
    }
  });

  // 프로필 사진 미리보기
  $("#profileImageInput").change(previewProfileImage);
  // 프로필 사진 등록 버튼 클릭 시 registImageModal
  $("#registImage").on("click", function () {
    $("#registImageModal").addClass("show").css("display", "block");
    $("#registImageModal").addClass("modal-open");
    $("body").addClass("modalopen").css("overflow-y", "hidden");
  });

  // 모달 닫기
  $("#closeModal2").on("click", function () {
    $("#registImageModal").removeClass("show").css("display", "none");
    $("#registImageModal").removeClass("modal-open");
    $("body").removeClass("modalopen").css("overflow-y", "auto");
  });

  // 모달 바깥을 클릭하면 닫기
  $("#registImageModal").on("click", function (event) {
    if ($(event.target).is("#registImageModal")) {
      $("#registImageModal").removeClass("show").css("display", "none");
      $("#registImageModal").removeClass("modal-open");
      $("body").removeClass("modalopen").css("overflow-y", "auto");
    }
  });

  // 회원 탈퇴 버튼 클릭 시 openDUModal
  $("#openDUModal").on("click", function () {
    $("#deleteUserModal").addClass("show").css("display", "block");
    $("#deleteUserModal").addClass("modal-open");
    $("body").addClass("modalopen").css("overflow-y", "hidden");
  });

  // 모달 닫기
  $("#closeModal3").on("click", function () {
    $("#deleteUserModal").removeClass("show").css("display", "none");
    $("#deleteUserModal").removeClass("modal-open");
    $("body").removeClass("modalopen").css("overflow-y", "auto");
  });

  // 모달 바깥을 클릭하면 닫기
  $("#deleteUserModal").on("click", function (event) {
    if ($(event.target).is("#deleteUserModal")) {
      $("#deleteUserModal").removeClass("show").css("display", "none");
      $("#deleteUserModal").removeClass("modal-open");
      $("body").removeClass("modalopen").css("overflow-y", "auto");
    }
  });

  // 모달창 내 등록하기 버튼 누르면 프로필 이미지 등록
  $("#registImageBtn").on("click", handleImageUpload);

  // 모달이 닫힐 때 페이지 새로고침 (리다이렉트)
  $("#registImageModal").on("hidden.bs.modal", function () {
    window.location.reload();
  });

  initChatRooms();

  $(".delete-btn").on("click", deleteShare);

  // 버튼 클릭 시 chatList 이동
  $("#chatRoomsBtn").on("click", function () {
    window.location.href = "/chatRooms";
  });

  // 회원 탈퇴
  $("#confirmDeleteBtn").on("click", deleteUser);

  // 회원 탈퇴 기능 - 모달 닫을 때 입력된 비밀번호 초기화
  $("#deleteUserModal").on("hidden.bs.modal", function () {
    $("#passwordInput").val("");
  });
});

// 프로필 수정 버튼
function updateBtn() {
  let userId = $("#userId").val();
  let newBio = $("#bio").val();
  let newNickname = $("#nickname").val();
  let isHost = $("#isHost").is(":checked");
  let newLanguages = [];
  $("#languages:checked").each(function () {
    newLanguages.push($(this).val());
  });
  let newAge = $("#age").val();
  let newNationality = $("#nationality").val();
  let newReligion = $("#religion").is(":checked");
  let newGender = $("#gender").is(":checked");

  $.ajax({
    url: "/mypage/profileUpdate",
    method: "POST",
    data: {
      userId: userId,
      nickname: newNickname,
      isHost: isHost,
      bio: newBio,
      languages: newLanguages,
      age: newAge,
      nationality: newNationality,
      religion: newReligion,
      gender: newGender,
    },
    success: function (response) {
      Swal.fire({
        icon: "success",
        title: "프로필이 수정되었습니다!",
        text: "프로필 등록/수정이 완료되었습니다.",
        confirmButtonText: "확인",
      }).then(() => {
        window.location.href = "/mypage/mypageView";
      });
    },
  });
}
// 닉네임 유효성 검사
function validateNickname() {
  let nickname = $("#nickname").val().trim();
  if (!nickname || nickname.length === 0) {
    $("#confirmNickname").css("color", "red").html("닉네임을 입력해주세요.");
    return false;
  }
  $("#confirmNickname").html(""); // 오류 메시지 초기화
  return true;
}

// 소개글 유효성 검사
function validateBio() {
  let bio = $("#bio").val().trim();
  if (bio.length > 20) {
    $("#confirmBio")
      .css("color", "red")
      .html("소개글은 최대 20자까지 입력 가능합니다.");
    return false;
  } else if (bio.length == 0) {
    $("#confirmBio")
      .css("color", "red")
      .html("자신을 소개하는 글을 입력해주세요.");
    return false;
  }
  $("#confirmBio").html("");
  return true;
}

// 나이 유효성 검사
function validateAge() {
  let age = $("#age").val().trim();
  if (!age || isNaN(age) || age <= 0) {
    $("#confirmAge").css("color", "red").html("나이를 입력해주세요.");
    return false;
  }
  $("#confirmAge").html("");
  return true;
}

// 성별 유효성 검사
function validateGender() {
  let gender = $("#gender").is(":checked");
  if (!gender) {
    $("#confirmGender").css("color", "red").html("성별을 선택해주세요.");
    return false;
  }
  $("#confirmGender").html("");
  return true;
}

// 종교 유효성 검사
function validateReligion() {
  let religion = $("#religion").is(":checked");
  if (!religion) {
    $("#confirmReligion").css("color", "red").html("종교를 선택해주세요.");
    return false;
  }
  $("#confirmReligion").html("");
  return true;
}

// 구사 언어 유효성 검사
function validateLanguages() {
  let newLanguages = [];
  $("#languages:checked").each(function () {
    newLanguages.push($(this).val());
  });
  if (newLanguages.length === 0) {
    $("#confirmLang")
      .css("color", "red")
      .html("최소 한 개의 구사 언어를 선택해주세요.");
    return false;
  }
  $("#confirmLang").html("");
  return true;
}

// 전체 유효성 검사 (모든 개별 검사 결과가 true일 때만 true 반환)
function validation() {
  return (
    validateNickname() &&
    validateBio() &&
    validateAge() &&
    validateNationality() &&
    validateGender() &&
    validateReligion() &&
    validateLanguages()
  );
}

// 프로필 이미지 미리보기
function previewProfileImage() {
  var reader = new FileReader();
  var fileInput = $("#profileImageInput")[0].files[0];

  reader.onload = function (e) {
    $("#profilePreview").attr("src", e.target.result);
  };

  if (fileInput) {
    reader.readAsDataURL(fileInput);
  }
}

// 프로필 이미지 등록
function handleImageUpload() {
  const fileInput = document.getElementById("profileImageInput");
  const file = fileInput.files[0]; // 선택한 파일

  if (!file) {
    alert("파일을 선택해주세요.");
    return;
  }

  // 이미지 파일을 임시 저장 (파일 객체로 가져오기)
  const reader = new FileReader();

  reader.onloadend = function () {
    // 파일을 base64로 변환하여 처리
    const img = new Image();
    img.src = reader.result;

    img.onload = function () {
      // 이미지를 webp 형식으로 변환
      const canvas = document.createElement("canvas");
      const ctx = canvas.getContext("2d");

      // 이미지 크기 설정
      canvas.width = img.width;
      canvas.height = img.height;

      // 이미지 그리기
      ctx.drawImage(img, 0, 0);

      // webp 형식으로 변환 (압축 비율 0-1 사이)
      const webpImage = canvas.toDataURL("image/webp", 0.8);

      // 변환된 webp 이미지를 서버에 전송하여 DB에 저장
      saveImageToServer(webpImage);
    };
  };

  // 파일을 읽고 base64로 변환
  reader.readAsDataURL(file);
}

// 서버에 이미지를 저장하는 함수
function saveImageToServer(webpImage) {
  // FormData 객체 생성
  const formData = new FormData();

  // base64 이미지를 Blob으로 변환해서 전송
  const byteCharacters = atob(webpImage.split(",")[1]); // base64 데이터에서 "data:image/webp;base64,"를 제외한 부분을 디코딩
  const byteArray = new Uint8Array(byteCharacters.length);

  for (let i = 0; i < byteCharacters.length; i++) {
    byteArray[i] = byteCharacters.charCodeAt(i);
  }

  // Blob 객체 생성 (WebP 형식)
  const blob = new Blob([byteArray], { type: "image/webp" });

  // FormData에 Blob 추가 (파일명은 "profileImage.webp"로 설정)
  formData.append("image", blob, "profileImage.webp");

  // AJAX 요청을 통해 서버로 이미지를 전송
  $.ajax({
    url: "/mypage/uploadProfileImage", // 이미지 업로드 엔드포인트
    method: "POST",
    data: formData,
    processData: false, // FormData는 자동으로 처리되므로 이 옵션을 false로 설정
    contentType: false, // 서버에 파일을 전송할 때 자동으로 콘텐츠 타입을 설정하지 않음
    success: function (response) {
      alert("이미지 업로드 성공!");
      console.log(response);
      $("#registImageModal").removeClass("show").css("display", "none");
      $("#registImageModal").removeClass("modal-open");
      $("body").removeClass("modalopen").css("overflow-y", "auto");
    },
    error: function (error) {
      alert("이미지 업로드 실패!");
      console.log(error);
      $("#registImageModal").removeClass("show").css("display", "none");
      $("#registImageModal").removeClass("modal-open");
      $("body").removeClass("modalopen").css("overflow-y", "auto");
    },
  });
}

// 회원 탈퇴
function deleteUser() {
  const password = $("#passwordInput").val();

  $.ajax({
    method: "DELETE",
    url: "/mypage/deleteUser",
    contentType: "application/json",
    data: JSON.stringify({
      password: password,
    }),
    success: function (response) {
      if (response.success) {
        $("#deleteUserModal").removeClass("show").css("display", "none");
        $("#profileModal").removeClass("modal-open");
        alert("회원 탈퇴 접수가 완료되었습니다.");
        window.location.href = "/index";
      } else {
        alert(response.message || "비밀번호가 올바르지 않습니다.");
      }
    },
    error: function (xhr) {
      if (xhr.status === 401) {
        alert("로그인이 필요합니다. 다시 로그인해 주세요.");
        window.location.href = "/user/login"; // 로그인 페이지로 리디렉트
      } else {
        alert("서버 오류가 발생했습니다. 다시 시도해 주세요.");
      }
    },
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
      alert("탈퇴 실패: " + xhr.responseText); // 서버에서 보낸 오류 메시지 출력
    },
  });
}

function initChatRooms() {
  $.ajax({
    url: "/chat/selectRooms",
    method: "GET",
    success: getChatRooms,
  });
}

function getChatRooms(resp) {
  let tag = `<table>`;

  if (!resp || resp.length === 0) {
    tag += `
        <tr>
            <td colspan="4" class="no-data">활성화 된 채팅이 없습니다.</td>
        </tr>
    `;
  } else {
    $.each(resp, function (index, item) {
      let profileImage = item["profileImage"]
        ? item["profileImage"]
        : "/images/default-profile.png";
      const nickname = $("#userNickname").val();
      let nicknameCheck = item["hostNickname"] === nickname;
      let displayNickname = nicknameCheck
        ? item["guestNickname"]
        : item["hostNickname"];
      tag += `
        <tr>
            <td>${index + 1}</td>
            <td><img src="${profileImage}" alt="프로필 이미지" width="50px" height="50px" style="border-radius: 50%; object-fit: cover;"></td>
            <td>${displayNickname}</td>
            <td class="btns">
                <input type="button" value="입장" 
                class="enterBtn btn btn-secondary"
                data-seq="${item["chatroomId"]}">
                <input type="button" value="삭제"
                class="deleteBtn btn btn-danger"
                data-seq="${item["chatroomId"]}">
            </td>
        </tr>
    `;
    });
  }
  tag += `</table>`;

  $("#chatroomTable").html(tag);

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

function deleteShare() {
  let shareId = $(this).attr("data-shareId");

  if (!confirm("정말로 삭제하시겠습니까?")) {
    return;
  }

  $.ajax({
    type: "GET",
    url: "/share/delete",
    data: { shareId: shareId },
    success: function (response) {
      alert("게시물이 삭제되었습니다.");
      window.location.href = "/mypage/mypageSample";
    },
    error: function (xhr) {
      alert(xhr.responseText || "삭제에 실패했습니다.");
    },
  });
}

window.onload = function () {
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get("alert") === "1") {
    alert("프로필에 추가 정보를 입력해주세요.");
    setTimeout(function () {
      $("#openModal").click();
    }, 100); // 100ms 지연
  }
};

// 탭 클릭 시 내용 변경
function tab() {
  const tabs = document.querySelectorAll(".tab");
  const contents = document.querySelectorAll(".tab-content");

  tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
      tabs.forEach((t) => t.classList.remove("active"));
      contents.forEach((c) => c.classList.remove("active"));

      tab.classList.add("active");
      document.getElementById(tab.dataset.tab).classList.add("active");
    });
  });
}
