// 프로필 업데이트
$(document).ready(function () {
  // $("#updateBtn").on("click", updateBtn);
  $("#confirmDeleteBtn").on("click", deleteUser);

  // 회원 탈퇴 기능 - 모달 닫을 때 입력된 비밀번호 초기화
  $("#deleteUserModal").on("hidden.bs.modal", function () {
    $("#passwordInput").val("");
  });

  // 프로필 등록/수정 관련
  $("#openModal").on("click", function () {
    $("#profileModal").modal("show");
  });

  $("#closeModal").on("click", function () {
    $("#profileModal").modal("hide");
  });

  // 프로필 사진 미리보기
  $("#profileImageInput").change(previewProfileImage);

  // 사진 등록 버튼 클릭 시 updateBtn 활성화
  $("#registImage").on("click", function () {
    $("#updateBtn").prop("disabled", false); // 이미지 등록 후 수정 버튼 활성화
  });
});

// let newProfileImage = $("#profileImageInput")[0].files[0]; // 수정
// formData.append("profileImage", newProfileImage);
// let profileImage = profileImageInput.files.length > 0 ? profileImageInput.files[0] : null;

// 프로필 수정 버튼
function updateBtn() {
  let userId = $("#userId").val();
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
      languages: newLanguages,
      age: newAge,
      nationality: newNationality,
      religion: newReligion,
      gender: newGender,
    },
    success: function (response) {
      alert("프로필 등록/수정이 완료되었습니다.");
      window.location.href = "/mypage/mypageSample";
    },
    error: function () {
      alert("서버와의 통신 오류가 발생했습니다.");
    },
  });
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

// 파일 선택 시, 조건부 블록을 표시
function showBlock() {
  const fileInput = document.getElementById("profileImageInput");
  const profileImageBlock = document.getElementById("profileImageBlock");

  // 파일이 선택되었을 경우
  if (fileInput.files.length > 0) {
    profileImageBlock.style.display = "block"; // 블록을 표시
  } else {
    profileImageBlock.style.display = "none"; // 파일이 없으면 숨김
  }
}

// 사진 등록 버튼 클릭 시 실행될 함수
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
      console.log(response);
      // 사진 등록이 완료되면 updateBtn 활성화
      $("#updateBtn").prop("disabled", false);
      // 조건부 렌더링을 위한 th:block 종료
      document.getElementById("profileImageBlock").style.display = "none";
    },
    error: function (error) {
      alert("이미지 업로드 실패!");
      console.log(error);
    },
  });
}

// 회원 탈퇴
function deleteUser() {
  const password = $("#passwordInput").val();

  if (!password) {
    alert("비밀번호를 입력해주세요.");
    return;
  }

  $.ajax({
    type: "POST",
    url: "/mypage/deleteUser",
    data: {
      password: password,
    },
    success: function (response) {
      if (response.success) {
        $("#deleteUserModal").modal("hide");
        alert("회원 탈퇴 접수가 완료되었습니다.");
        window.location.href = "/logout";
      } else {
        alert("비밀번호가 올바르지 않습니다.");
      }
    },
    error: function () {
      alert("서버 오류가 발생했습니다. 다시 시도해 주세요.");
    },
  });
}
