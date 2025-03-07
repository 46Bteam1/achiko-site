let emailCheck = false;
let idDuplicateTest = false;
let emailDuplicateChk = false;
let lengthCheck = false;
let passwordCheck = false;

	$(function(){
		$('#chkDuplicationBtn').on('click', chkId);
		$('#authBtn').on('click', emailDupTestAndSend);
		$('#chkAuthCodeBtn').on('click', chkAuthCode);
		$('#submitBtn').on('click', chkRegist);
	});
	
	
	function chkAuthCode() {
		let email = $('#email').val();
		let chkAuthCode = $('#chkAuthCode').val();
		
		$.ajax({
			url: '/user/verifyAuthCode',
			method: 'POST',
			data: { "email" : email, "chkAuthCode" : chkAuthCode },
			success: function (resp) {
				if(resp == 2){
					alert("인증되었습니다.");
					emailCheck = true;
					if(emailCheck) {
						$('#authMsg').text("인증완료");
					}
				} else if (resp == 1) {
					alert("만료된 코드입니다.");
				} else {
					alert("코드가 일치하지 않습니다.");
				}
			}
		});
		
	}
	
	function chkId() {
		let loginId = $('#loginId').val();
		let check = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/; 
		
		if (loginId.trim().length < 3 || loginId.trim().length > 30) {	// 길이체크
			$('#confirmId').css('color', 'red');
			$('#confirmId').html('아이디는 3~30자 이내 알파벳과 숫자만 가능합니다.');
			lengthCheck = false;
			return false;
		}
		if(check.test(loginId)){ 		// 한글 체크
			$('#confirmId').css('color', 'red');
			$('#confirmId').html('아이디는 3~30자 이내 알파벳과 숫자만 가능합니다.');
			lengthCheck = false;
			return false;
		}
		lengthCheck = true;
			
		$.ajax({
			url: '/user/chkIdDuplication',
			method: 'POST',
			data: {"loginId" : loginId},
			success: function(resp) {	// resp=true : 중복X / resp=false : 중복O
				if(resp) {
					idDuplicateTest = true;
					$('#confirmId').css('color', 'blue');
					$('#confirmId').html('사용 가능한 아이디입니다.');
				}
				else {
					idDuplicateTest = false;
					$('#confirmId').css('color', 'red');
					$('#confirmId').html('사용 불가능한 아이디입니다.');
				}
			}
		});
	}
	
	function emailDupTestAndSend() {
		let email = $('#email').val();
		let $authBtn = $('#authBtn'); // 버튼 요소 캐싱
		
		// 클릭 이벤트 제거 (한 번만 실행되도록 설정)
		$authBtn.off('click');
		
		$.ajax({
			url: '/user/chkEmailDuplication',
			method: 'POST',
			data: {"email" : email},
			success: function(resp) { // resp=true : 중복X / resp=false : 중복O
				if(resp){
					emailDuplicateChk = true;
					
					$.ajax({
						url: '/user/sendEmail',
						method: 'POST',
						data: { "email" : email },
						success: function (resp) {
							alert("인증메일이 전송되었습니다.");	
						},
						complete: function() {
	                        // 3초 후 다시 이벤트 핸들러 추가 (사용자가 재시도 가능하도록)
	                        setTimeout(() => {
	                            $authBtn.on('click', emailDupTestAndSend);
	                        }, 3000); // 3초 후 다시 버튼 활성화
	                    }
					});
				}
				else {
					emailDuplicateChk = false;
					alert("해당 이메일로 가입된 계정이 존재합니다.");
					
					// 중복된 이메일이면 바로 이벤트 다시 등록
					$authBtn.on('click', emailDupTestAndSend);
					
					return false;
				}
			},
			error: function() {
	            alert("이메일 중복 확인 중 오류가 발생했습니다.");
	            $authBtn.on('click', emailDupTestAndSend); // 오류 발생 시 이벤트 다시 등록
	        }
		});
	}
	
	function chkRegist(){
		let password = $('#password').val();
		let passwordChk = $('#passwordChk').val();
		let languagesChk = $("input[name='languages']:checked").length;
		
		if(password != passwordChk){
			passwordCheck = false;
			alert("비밀번호가 일치하지 않습니다.");
			return false;
		}else {
			passwordCheck = true;
		}
		if(!idDuplicateTest) {
			alert("아이디 중복확인을 해주세요.");
			return false;
		}
		if(!idDuplicateTest) {
			alert("아이디 중복확인을 해주세요.");
			return false;
		}
		if(!lengthCheck) {
			alert("아이디를 확인해주세요.");
			return false;
		}
		if(!emailCheck) {
			alert("이메일을 인증해주세요.");
			return false;
		}
		if (languagesChk === 0) {
		    alert("최소 한 개 이상의 언어를 선택해주세요.");
			return false;
		}
		
		return true;
	}
