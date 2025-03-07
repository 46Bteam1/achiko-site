// // 예약된 뷰잉 목록 가져오기
// function loadViewingList() {
//   $.ajax({
//     url: "/mypage/viewingList",
//     method: "GET",
//     success: function (data) {
//       let tableBody = $("#viewingTable tbody");
//       tableBody.empty();

//       if (data.length === 0) {
//         tableBody.append(
//           '<tr><td colspan="3" class="no-data">예정된 뷰잉이 없습니다.</td></tr>'
//         );
//       } else {
//         $.each(data, function (index, viewing) {
//           tableBody.append(`
//                         <tr>
//                             <td>${index + 1}</td>
//                             <td><a href="/share/shareDetail?shareId=${
//                               viewing.shareId
//                             }&userId=${viewing.userId}">${viewing.shareTitle}</a></td>
//                             <td>${viewing.scheduledDate}</td>
//                         </tr>
//                     `);
//         });
//       }
//     },
//     error: function () {
//       alert("뷰잉 목록을 불러오는 중 오류가 발생했습니다.");
//     },
//   });
// }

// // 찜한 목록 가져오기
// function loadFavoriteList() {
//   $.ajax({
//     url: "/mypage/favoriteList",
//     method: "GET",
//     success: function (data) {
//       let tableBody = $("#favoriteTable tbody");
//       tableBody.empty();

//       if (data.length === 0) {
//         tableBody.append(
//           '<tr><td colspan="2" class="no-data">찜한 목록이 없습니다.</td></tr>'
//         );
//       } else {
//         $.each(data, function (index, favorite) {
//           tableBody.append(`
//                         <tr>
//                             <td>${index + 1}</td>
//                             <td><a href="/share/shareDetail?shareId=${
//                               favorite.shareId
//                             }&userId=${favorite.userId}">${favorite.shareTitle}</a></td>
//                         </tr>
//                     `);
//         });
//       }
//     },
//     error: function () {
//       alert("찜한 목록을 불러오는 중 오류가 발생했습니다.");
//     },
//   });
// }

// // 내 리뷰 가져오기
// function loadReviewList() {
//   $.ajax({
//     url: "/mypage/reviewList",
//     method: "GET",
//     success: function (data) {
//       // 내 리뷰 테이블
//       let writtenTableBody = $("#myReviewTable tbody");
//       writtenTableBody.empty();

//       if (data.writtenReviewList.length === 0) {
//         writtenTableBody.append(
//           '<tr><td colspan="2" class="no-data">등록한 리뷰가 없습니다.</td></tr>'
//         );
//       } else {
//         $.each(data.writtenReviewList, function (index, review) {
//           writtenTableBody.append(`
//                         <tr>
//                             <td>${index + 1}</td>
//                             <td><a href="/review/reviewDetail?reviewId=${
//                               review.reviewId
//                             }&userId=${review.userId}">${review.reviewContent}</a></td>
//                         </tr>
//                     `);
//         });
//       }

//       // 받은 리뷰 테이블
//       let receivedTableBody = $("#receivedReviewTable tbody");
//       receivedTableBody.empty();

//       if (data.receivedReviewList.length === 0) {
//         receivedTableBody.append(
//           '<tr><td colspan="2" class="no-data">받은 리뷰가 없습니다.</td></tr>'
//         );
//       } else {
//         $.each(data.receivedReviewList, function (index, review) {
//           receivedTableBody.append(`
//                         <tr>
//                             <td>${index + 1}</td>
//                             <td><a href="/review/reviewDetail?reviewId=${
//                               review.reviewId
//                             }&userId=${review.userId}">${review.reviewContent}</a></td>
//                         </tr>
//                     `);
//         });
//       }
//     },
//     error: function () {
//       alert("내 리뷰를 불러오는 중 오류가 발생했습니다.");
//     },
//   });
// }

// // 내 댓글 가져오기
// function loadReviewReplyList() {
//   $.ajax({
//     url: "/mypage/reviewReplyList",
//     type: "GET",
//     dataType: "json",
//     success: function (data) {
//       let tableBody = $("#reviewReplyTable tbody"); // 특정 테이블만 선택

//       if (data.length === 0) {
//         tableBody.html(
//           "<tr><td colspan='3' class='no-data'>등록된 댓글이 없습니다.</td></tr>"
//         );
//       } else {
//         tableBody.empty(); // 기존 데이터 삭제

//         $.each(data, function (index, reviewReply) {
//           let row = `<tr>
//                             <td>${index + 1}</td>
//                             <td><a href="/review/reviewDetail?reviewId=${
//                               reviewReply.reviewId
//                             }&userId=${reviewReply.userId}">${
//             reviewReply.reviewTitle
//           }</a></td>
//                             <td>${reviewReply.content}</td>
//                           </tr>`;
//           tableBody.append(row);
//         });
//       }
//     },
//     error: function () {
//       alert("내 댓글을 불러오는 중 오류가 발생했습니다.");
//     },
//   });
// }

// // 회원 탈퇴 처리 함수
// function deleteUser() {
//   $.ajax({
//     type: "DELETE",
//     url: `/mypage/deleteUser/${$("#userId").val()}`,
//     success: function () {
//       alert("회원 탈퇴 신청이 완료되었습니다.");
//       window.location.href = "/logout"; // 로그아웃 후 메인 페이지 이동
//     },
//     error: function () {
//       alert("탈퇴 처리 중 오류가 발생했습니다.");
//     },
//   });
// }

// function init() {
//   loadViewingList();
//   loadFavoriteList();
//   loadReviewList();
//   loadReviewReplyList();
// }

// $(document).ready(function () {
//   // init(); // 초기화 함수

//   // 회원 탈퇴 버튼 클릭 시 모달 열기
//   const duModal = $("#deleteUserModal");

//   $("#openDUModal").click(() => duModal.show());

//   // 탈퇴 확인 버튼 클릭 시
//   $("#confirmdelete").click(function () {
//     var password = $("#passwordInput").val();
//     if (!password) {
//       alert("비밀번호를 입력하세요.");
//       return;
//     }

//     // 비밀번호 확인 요청
//     $.ajax({
//       type: "POST",
//       url: `/mypage/pwdCheck/${$("#userId").val()}`,
//       contentType: "application/json",
//       data: JSON.stringify({ password: password }),
//       success: function (response) {
//         if (confirm("정말 탈퇴하시겠습니까?")) {
//           deleteUser();
//         }
//       },
//       error: function () {
//         alert("비밀번호 확인 중 오류가 발생했습니다.");
//       },
//     });
//   });

//   $("#openModal").click(() => pModal.show());
//   $("#closeModal").click(() => pModal.hide());

//   // 프로필 이미지 미리보기
//   $("#profileImageInput").change(function (event) {
//     let file = event.target.files[0];
//     if (file) {
//       let reader = new FileReader();
//       reader.onload = function (e) {
//         $("#profileImagePreview").attr("src", e.target.result);
//       };
//       reader.readAsDataURL(file);
//     }
//   });

//   // 프로필 수정 제출
//   $("#submitBtn").click(function (event) {
//     event.preventDefault(); // 기본 제출 동작 방지

//     let userId = $("#userId").val();
//     let nickname = $("#nickname").val();
//     let bio = $("#bio").val();
//     let isHost = $("input[name='isHost']:checked").val();
//     let languages = $("input[name='languages']:checked")
//       .map(function () {
//         return $(this).val();
//       })
//       .get()
//       .join(",");
//     let age = $("#age").val();
//     let nationality = $("#nationality").val();
//     let religion = $("input[name='religion']:checked").val();
//     let gender = $("input[name='gender']:checked").val();

//     let formData = new FormData();
//     formData.append("nickname", nickname);
//     formData.append("bio", bio);
//     formData.append("isHost", isHost);
//     formData.append("languages", languages);
//     formData.append("age", age);
//     formData.append("nationality", nationality);
//     formData.append("religion", religion);
//     formData.append("gender", gender);

//     let fileInput = $("#profileImageInput")[0];

//     if (fileInput.files.length > 0) {
//       convertToWebP(fileInput.files[0])
//         .then((webpBlob) => {
//           let webpFile = new File([webpBlob], "profile.webp", {
//             type: "image/webp",
//           });
//           formData.append("profileImage", webpFile);
//           sendProfileData(userId, formData);
//         })
//         .catch((error) => {
//           console.error("WebP 변환 실패:", error);
//           alert("이미지 변환 중 오류가 발생했습니다.");
//         });
//     } else {
//       sendProfileData(userId, formData);
//     }

//     // 프로필 데이터 서버로 전송
//     function sendProfileData(userId, formData) {
//       $.ajax({
//         type: "PATCH",
//         url: `/mypage/profileRegist/${userId}`,
//         contentType: false,
//         processData: false,
//         data: formData,
//         success: function () {
//           alert("프로필이 성공적으로 업데이트되었습니다");
//           window.location.href = "/mypage/mypageView";
//         },
//         error: function (xhr) {
//           alert("업데이트 실패: " + xhr.responseText);
//         },
//       });
//     }

//     // 이미지 WebP 변환
//     function convertToWebP(file) {
//       return new Promise((resolve, reject) => {
//         let reader = new FileReader();
//         reader.readAsDataURL(file);
//         reader.onload = function (event) {
//           let img = new Image();
//           img.src = event.target.result;
//           img.onload = function () {
//             let canvas = document.createElement("canvas");
//             let ctx = canvas.getContext("2d");
//             canvas.width = img.width;
//             canvas.height = img.height;
//             ctx.drawImage(img, 0, 0);
//             canvas.toBlob(
//               (blob) => {
//                 blob ? resolve(blob) : reject(new Error("WebP 변환 실패"));
//               },
//               "image/webp",
//               0.8
//             );
//           };
//           img.onerror = reject;
//         };
//         reader.onerror = reject;
//       });
//     }
//   });
// });
