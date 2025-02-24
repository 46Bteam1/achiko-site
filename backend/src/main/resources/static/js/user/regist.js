let emailCheck = false;
	
	$(function(){
		$('#authBtn').on('click', sendEmail);
		$('#chkAuthCodeBtn').on('click', chkAuthCode);
	});
	
	
	function sendEmail(){
		let email = $('#email').val();
		
		$.ajax({
			url: '/user/sendEmail',
			method: 'POST',
			data: { "email" : email },
			success: function (resp) {
				alert("인증메일이 전송되었습니다.");	
			}
		});
		
	}
	
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
