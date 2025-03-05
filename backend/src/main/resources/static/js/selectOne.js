// ================================
// selectOne.js
// ================================

// 기존 기능: 공유하기, 찜하기, 편의시설 검색, 지도 기능 등

document.addEventListener("DOMContentLoaded", function () {
  // 공유하기 모달 관련
  const shareModal = document.getElementById("shareModal");
  const shareButton = document.getElementById("shareButton");
  const closeShareModalBtn = document.getElementById("closeShareModalBtn");

  // 공유하기 버튼 클릭 시 공유 모달 열기
  shareButton.addEventListener("click", function () {
    shareModal.style.display = "block";
  });

  // 공유 모달 닫기 버튼 클릭 시 모달 닫기
  closeShareModalBtn.addEventListener("click", function () {
    shareModal.style.display = "none";
  });

  // 모달 외부 클릭 시 모달 닫기 (공유 모달)
  window.addEventListener("click", function (event) {
    if (event.target === shareModal) {
      shareModal.style.display = "none";
    }
  });

  // 찜하기/찜취소하기 버튼 이벤트
  const favoriteButton = document.getElementById("favoriteButton");
  favoriteButton.addEventListener("click", function() {
    const shareId = favoriteButton.getAttribute("data-share-id");
    const isFavorite = favoriteButton.getAttribute("data-is-favorite") === "true";

    if (!isFavorite) {
      fetch("/favorite/set", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ shareId: shareId })
      })
      .then(response => {
        if (response.ok) {
          favoriteButton.innerText = "찜 취소하기";
          favoriteButton.setAttribute("data-is-favorite", "true");
        } else {
          alert("찜하기 실패!");
        }
      })
      .catch(err => console.error(err));
    } else {
      fetch("/favorite/cancel?shareId=" + shareId, {
        method: "DELETE"
      })
      .then(response => {
        if (response.ok) {
          favoriteButton.innerText = "찜하기";
          favoriteButton.setAttribute("data-is-favorite", "false");
        } else {
          alert("찜 취소 실패!");
        }
      })
      .catch(err => console.error(err));
    }
  });

  // 모달 창: 편의시설 검색 기능
  const facilityModal = document.getElementById("facilityModal");
  const openFacilityModalBtn = document.getElementById("openFacilityModalBtn");
  const closeFacilityModal = document.getElementById("facilityModalClose");

  openFacilityModalBtn.addEventListener("click", function () {
    // 기존 편의시설 목록과 마커 초기화
    document.getElementById("place-list").innerHTML = "";
    clearFacilityMarkers();
    facilityModal.style.display = "block";
    initFacilityMap();
  });
  
  closeFacilityModal.addEventListener("click", function () {
    facilityModal.style.display = "none";
  });
  
  window.addEventListener("click", function (event) {
    if (event.target === facilityModal) {
      facilityModal.style.display = "none";
    }
  });

  // 각 검색 버튼 이벤트 등록
  document.getElementById("searchConvenienceBtn").addEventListener("click", function(){
    searchNearbyPlaces("convenience store");
  });
  document.getElementById("searchSupermarketBtn").addEventListener("click", function(){
    searchNearbyPlaces("supermarket");
  });
  document.getElementById("searchCafeBtn").addEventListener("click", function(){
    searchNearbyPlaces("cafe");
  });
  document.getElementById("searchRestaurantBtn").addEventListener("click", function(){
    searchNearbyPlaces("restaurant");
  });
  document.getElementById("searchDrugstoreBtn").addEventListener("click", function(){
    searchNearbyPlaces("drugstore");
  });
  document.getElementById("searchLaundromatBtn").addEventListener("click", function(){
      searchNearbyPlaces("laundromat");
  });
  // 카페 체인 검색 버튼 이벤트 등록
  document.getElementById("searchStarbucksBtn").addEventListener("click", function(){
    searchNearbyPlaces("スターバックス");
  });
  document.getElementById("searchDoutorBtn").addEventListener("click", function(){
    searchNearbyPlaces("ドトールコーヒー");
  });
  document.getElementById("searchTullysBtn").addEventListener("click", function(){
    searchNearbyPlaces("タリーズコーヒー");
  });
  document.getElementById("searchKomedaBtn").addEventListener("click", function(){
    searchNearbyPlaces("コメダ珈琲店");
  });


  // 신고 모달 창 기능
  const reportModal = document.getElementById("reportModal");
  const reportButton = document.getElementById("reportButton");
  const reportModalClose = document.getElementById("reportModalClose");
  const submitReport = document.getElementById("submitReport");

  // 신고 버튼 클릭 시 신고 모달 열기
  reportButton.addEventListener("click", function () {
    reportModal.style.display = "block";
  });

  // 신고 모달의 × 버튼 클릭 시 닫기
  reportModalClose.addEventListener("click", function () {
    reportModal.style.display = "none";
  });

  // 모달 외부 클릭 시 닫기 (신고 모달)
  window.addEventListener("click", function (event) {
    if (event.target === reportModal) {
      reportModal.style.display = "none";
    }
  });

  // 신고 제출 버튼 이벤트
  submitReport.addEventListener("click", function () {
    const reason = document.getElementById("reportReason").value;
    const description = document.getElementById("reportDescription").value.trim();

    if (!reason) {
      alert("신고 사유를 선택하세요.");
      return;
    }

    const shareId = document.getElementById("favoriteButton").getAttribute("data-share-id");

    const reportData = {
      shareId: shareId,
      reason: reason,
      description: description
    };

    fetch("/report", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reportData)
    })
    .then(response => {
      if (response.ok) {
        alert("신고가 접수되었습니다.");
        reportModal.style.display = "none";
      } else {
        alert("신고 접수 중 오류가 발생했습니다.");
      }
    })
    .catch(error => console.error("신고 오류:", error));
  });

  // (추가) 방 사진 영역을 드래그로 스크롤할 수 있게 하는 코드
  const roomPhotos = document.querySelector(".room-photos");
  if (roomPhotos) {
    let isDown = false;
    let startX;
    let scrollLeft;

    roomPhotos.addEventListener("mousedown", (e) => {
      isDown = true;
      startX = e.pageX - roomPhotos.offsetLeft;
      scrollLeft = roomPhotos.scrollLeft;
      roomPhotos.style.cursor = "grabbing";
    });

    roomPhotos.addEventListener("mouseleave", () => {
      isDown = false;
      roomPhotos.style.cursor = "grab";
    });

    roomPhotos.addEventListener("mouseup", () => {
      isDown = false;
      roomPhotos.style.cursor = "grab";
    });

    roomPhotos.addEventListener("mousemove", (e) => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - roomPhotos.offsetLeft;
      const walk = (x - startX) * 1;
      roomPhotos.scrollLeft = scrollLeft - walk;
    });
  }

  // ★ 게스트 조회 모달 관련 (추가)
  const guestModal = document.getElementById("guestModal");
  const confirmedGuestButton = document.getElementById("confirmedGuest");
  const guestModalClose = document.getElementById("guestModalClose");

  confirmedGuestButton.addEventListener("click", function () {
    guestModal.style.display = "block";
  });

  guestModalClose.addEventListener("click", function () {
    guestModal.style.display = "none";
  });

  // 모달 외부 클릭 시 닫기 (게스트 조회 모달)
  window.addEventListener("click", function (event) {
    if (event.target === guestModal) {
      guestModal.style.display = "none";
    }
  });
});

// 카카오 공유 버튼 이벤트는 SDK가 완전히 로드된 후에 등록 (window.onload 사용)
window.addEventListener("load", function () {
  const kakaoShareButton = document.getElementById("kakaoShareButton");
  if (kakaoShareButton) {
    kakaoShareButton.addEventListener("click", function () {
      // 공유할 페이지의 URL (현재 페이지 URL 사용)
      var shareUrl = window.location.href;
      
      // Kakao.Link.sendDefault API 호출
      if (Kakao && Kakao.Link && typeof Kakao.Link.sendDefault === "function") {
        Kakao.Link.sendDefault({
          objectType: 'feed',
          content: {
            title: document.querySelector(".share-title").innerText,
            description: "공유글을 확인해보세요!",
            imageUrl: "https://yourdomain.com/path/to/default-image.jpg",
            link: {
              mobileWebUrl: shareUrl,
              webUrl: shareUrl
            }
          },
          buttons: [
            {
              title: '웹으로 보기',
              link: {
                mobileWebUrl: shareUrl,
                webUrl: shareUrl
              }
            }
          ]
        });
      } else {
        console.error("Kakao SDK가 아직 초기화되지 않았거나, Kakao.Link.sendDefault 함수가 정의되지 않았습니다.");
      }
    });
  }
});

// 편의시설 지도 전용 변수 및 함수
let facilityMap;
let facilityMarkers = [];

function initFacilityMap() {
  console.log("편의시설 지도 초기화 시작");
  var geocoder = new google.maps.Geocoder();
  facilityMap = new google.maps.Map(document.getElementById("facilitymap"), {
    zoom: 16,
    center: { lat: 35.6895, lng: 139.6917 }
  });
  geocoder.geocode({ "address": window.fullAddress }, function(results, status) {
    if (status === "OK" && results.length > 0) {
      facilityMap.setCenter(results[0].geometry.location);
      new google.maps.Marker({
        map: facilityMap,
        position: results[0].geometry.location
      });
    } else {
      console.error("편의시설 지도 Geocode 실패: " + status);
    }
  });
}

function searchNearbyPlaces(keyword) {
  if (!facilityMap) {
    console.error("편의시설 지도가 로드되지 않았습니다.");
    return;
  }
  var currentZoom = facilityMap.getZoom();
  facilityMap.setZoom(15);
  var service = new google.maps.places.PlacesService(facilityMap);
  var request = {
    location: facilityMap.getCenter(),
    radius: 1000,
    keyword: keyword
  };
  clearFacilityMarkers();
  service.nearbySearch(request, function(results, status) {
    if (status === google.maps.places.PlacesServiceStatus.OK) {
      console.log("편의시설 검색 결과:", results);
      var placeList = document.getElementById("place-list");
      placeList.innerHTML = "";
      results.forEach((place) => {
        var marker = new google.maps.Marker({
          map: facilityMap,
          position: place.geometry.location,
          title: place.name
        });
        facilityMarkers.push(marker);

        let nameSpanStyle = "font-size:1.2em; font-weight: bold;";
        let storeColor = "";
        if (place.name.indexOf("ローソン") !== -1) {
          storeColor = "blue";
        } else if (place.name.indexOf("セブン-イレブン") !== -1 || place.name.indexOf("セブンイレブン") !== -1) {
          storeColor = "red";
        } else if (place.name.indexOf("ファミリーマート") !== -1) {
          storeColor = "green";
        }
        let storeNames = ["ローソン", "セブン-イレブン", "セブンイレブン", "ファミリーマート"];
        let formattedName = place.name;
        storeNames.forEach(store => {
          if (formattedName.indexOf(store) !== -1) {
            formattedName = formattedName.replace(store, `<span style="color: ${storeColor};">${store}</span>`);
          }
        });
        let vicinitySpanStyle = "font-size:0.8em; color: gray;";
        var listItem = document.createElement("li");
        listItem.innerHTML = `<span style="${nameSpanStyle}">${formattedName}</span><br><span style="${vicinitySpanStyle}">${place.vicinity}</span>`;
        placeList.appendChild(listItem);
      });
    } else {
      console.error("근처 편의시설 검색 실패: " + status);
    }
  });
}

function clearFacilityMarkers() {
  facilityMarkers.forEach(marker => marker.setMap(null));
  facilityMarkers = [];
}
