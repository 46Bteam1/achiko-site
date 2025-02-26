// ★ selectOne.js

		

	// 공유하기 버튼 예시 (단순 alert)
	    document.getElementById('shareButton').addEventListener('click', function(){
	        alert('현재 페이지 URL을 복사하거나, SNS로 공유하세요.');
	    });

	    // 찜하기/찜취소하기 버튼
	    document.getElementById('favoriteButton').addEventListener('click', function(){
	        const button = this;
	        const shareId = button.getAttribute('data-share-id');
	        const isFavorite = button.getAttribute('data-is-favorite') === 'true';
	        
	        if(!isFavorite){
	            // 찜하기(POST)
	            fetch('/favorite/set', {
	                method: 'POST',
	                headers: {'Content-Type': 'application/json'},
	                body: JSON.stringify({shareId: shareId})
	            })
	            .then(response => {
	                if(response.ok){
	                    button.innerText = '찜 취소하기';
	                    button.setAttribute('data-is-favorite', 'true');
	                } else {
	                    alert('찜하기 실패!');
	                }
	            })
	            .catch(err => console.error(err));
	        } else {
	            // 찜 취소하기(DELETE)
	            fetch('/favorite/cancel?shareId=' + shareId, {
	                method: 'DELETE'
	            })
	            .then(response => {
	                if(response.ok){
	                    button.innerText = '찜하기';
	                    button.setAttribute('data-is-favorite', 'false');
	                } else {
	                    alert('찜 취소 실패!');
	                }
	            })
	            .catch(err => console.error(err));
	        }
});
