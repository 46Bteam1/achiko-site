// 전역 지도 객체
let mapObj = null;
let facilityMarkers = [];

// Google Maps API 콜백 함수 (전역에 정의)
// window.initMap = function () {
//   console.log("Google Maps API가 정상적으로 로드되었습니다.");
//   var geocoder = new google.maps.Geocoder();
//   mapObj = new google.maps.Map(document.getElementById("map"), {
//     zoom: 15,
//     center: { lat: 35.6895, lng: 139.6917 },
//   });
//   geocoder.geocode({ address: window.fullAddress }, function (results, status) {
//     if (status === "OK" && results.length > 0) {
//       mapObj.setCenter(results[0].geometry.location);
//       new google.maps.Marker({
//         map: mapObj,
//         position: results[0].geometry.location,
//         title: "셰어하우스 위치",
//       });
//     } else {
//       console.error("Geocode 실패: " + status);
//     }
//   });
// };

document.addEventListener("DOMContentLoaded", function () {
  // header 부분
  $(document).on("click", "#menuButton", function (event) {
    event.stopPropagation();
    const $modalMenu = $("#modalMenu");

    if ($modalMenu.is(":visible")) {
      $modalMenu.hide();
    } else {
      $modalMenu.show();
    }
  });

  // 모달 바깥 클릭 시 모달 닫기
  $(document).on("click", function (event) {
    if (
      !$("#modalMenu").is(event.target) &&
      !$("#modalMenu").has(event.target).length &&
      !$("#menuButton").is(event.target)
    ) {
      $("#modalMenu").hide();
    }
  });

  // Thymeleaf로 전달받은 URL을 LINE 버튼에 설정 (공식 LINE 버튼을 사용하지 않는 경우 생략 가능)
  // (참고: 이미지 버튼 방식 사용 시 window.shareUrl 변수를 활용할 수 있습니다.)

  // 공유하기 모달 관련
  const shareModal = document.getElementById("shareModal");
  const shareButton = document.getElementById("shareButton");
  const closeShareModalBtn = document.getElementById("closeShareModalBtn");
  const yesMessageBtn = document.getElementById("yesMessageBtn");
  const shareId = document.getElementById("shareId").value;

  yesMessageBtn.addEventListener("click", function () {
    fetch(`/chat/create?shareId=${shareId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ shareId: shareId }),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("네트워크 응답에 문제가 있습니다.");
        }
        return response.text();
      })
      .then((result) => {
        window.location.href = `/chatList?chatroomId=${result}`;
      })
      .catch((error) => {
        console.error("Fetch 호출 중 에러 발생:", error);
      });
  });

  // 공유하기 버튼 클릭 시 공유 모달 열기
  shareButton.addEventListener("click", function () {
    shareModal.style.display = "block";
    document.body.classList.add("modal-open");
    // 공식 LINE 공유 버튼 관련 스크립트 호출 대신, 이미지 버튼 방식을 사용하므로 별도 호출 없음
  });

  // 공유 모달 닫기 버튼 클릭 시 모달 닫기
  closeShareModalBtn.addEventListener("click", function () {
    shareModal.style.display = "none";
    document.body.classList.remove("modal-open");
  });

  // 모달 외부 클릭 시 모달 닫기 (공유 모달)
  window.addEventListener("click", function (event) {
    if (event.target === shareModal) {
      shareModal.style.display = "none";
      document.body.classList.remove("modal-open");
    }
  });

  // ★ 찜하기(찜취소) 버튼 이벤트 구현
  const favoriteContainer = document.getElementById("favoriteContainer");
  const favoriteButton = document.getElementById("favoriteBtn");
  const favoriteCountSpan = favoriteContainer.querySelector("span");

  favoriteButton.addEventListener("click", function () {
    const shareId = favoriteContainer.getAttribute("data-share-id");
    const isFavorite =
      favoriteButton.getAttribute("data-is-favorite") === "true";
    const icon = favoriteButton.querySelector("i");

    function getCurrentFavoriteCount() {
      const text = favoriteCountSpan.textContent;
      const match = text.match(/(\d+)/);
      return match ? parseInt(match[1], 10) : 0;
    }

    function updateFavoriteCount(newCount) {
      let countText = newCount > 1 ? " LIKES" : " LIKE";
      let countElement = favoriteCountSpan.querySelector("b");
      let textElement = favoriteCountSpan.querySelector("span");

      // 색상 스타일 변경
      countElement.classList.toggle("favorite-active", newCount > 0);
      countElement.classList.toggle("favorite-zero", newCount === 0);

      // 숫자와 텍스트 업데이트
      countElement.textContent = newCount;
      textElement.textContent = countText;
    }

    if (!isFavorite) {
      fetch("/favorite/set", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ shareId: Number(shareId) }),
      })
        .then((response) => {
          if (response.ok) {
            icon.classList.remove("far");
            icon.classList.add("fas");
            favoriteButton.setAttribute("data-is-favorite", "true");
            let currentCount = getCurrentFavoriteCount();
            updateFavoriteCount(currentCount + 1);
          } else {
            console.log("찜하기 실패");
          }
        })
        .catch((err) => console.error(err));
    } else {
      fetch("/favorite/cancel?shareId=" + shareId, {
        method: "DELETE",
      })
        .then((response) => {
          if (response.ok) {
            icon.classList.remove("fas");
            icon.classList.add("far");
            favoriteButton.setAttribute("data-is-favorite", "false");
            let currentCount = getCurrentFavoriteCount();
            updateFavoriteCount(Math.max(0, currentCount - 1));
          } else {
            alert("찜 취소 실패!");
          }
        })
        .catch((err) => console.error(err));
    }
  });

  // 각 검색 버튼 이벤트 등록
  document
    .getElementById("searchConvenienceBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("convenience store");
    });
  document
    .getElementById("searchSupermarketBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("supermarket");
    });
  document
    .getElementById("searchCafeBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("cafe");
    });
  document
    .getElementById("searchRestaurantBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("restaurant");
    });
  document
    .getElementById("searchDrugstoreBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("drugstore");
    });
  document
    .getElementById("searchLaundromatBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("laundromat");
    });
  document
    .getElementById("searchStarbucksBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("スターバックス");
    });
  document
    .getElementById("searchDoutorBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("ドトールコーヒー");
    });
  document
    .getElementById("searchTullysBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("タリーズコーヒー");
    });
  document
    .getElementById("searchKomedaBtn")
    .addEventListener("click", function () {
      searchNearbyPlaces("コメダ珈琲店");
    });

  // 신고 모달 창 기능
  const reportModal = document.getElementById("reportModal");
  const reportButton = document.getElementById("reportButton");
  const reportModalClose = document.getElementById("reportModalClose");
  const submitReport = document.getElementById("submitReport");

  reportButton.addEventListener("click", function () {
    reportModal.style.display = "block";
    document.body.classList.add("modal-open");
  });

  reportModalClose.addEventListener("click", function () {
    reportModal.style.display = "none";
    document.body.classList.remove("modal-open");
  });

  window.addEventListener("click", function (event) {
    if (event.target === reportModal) {
      reportModal.style.display = "none";
      document.body.classList.remove("modal-open");
    }
  });

  submitReport.addEventListener("click", function () {
    const reason = document.getElementById("reportReason").value;
    const description = document
      .getElementById("reportDescription")
      .value.trim();
    if (!reason) {
      alert("신고 사유를 선택하세요.");
      return;
    }
    const shareId = document
      .getElementById("favoriteContainer")
      .getAttribute("data-share-id");
    const reportData = {
      shareId: shareId,
      reason: reason,
      description: description,
    };
    fetch("/report", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reportData),
    })
      .then((response) => {
        if (response.ok) {
          alert("신고가 접수되었습니다.");
          reportModal.style.display = "none";
          document.body.classList.remove("modal-open");
        } else {
          alert("신고 접수 중 오류가 발생했습니다.");
        }
      })
      .catch((error) => console.error("신고 오류:", error));
  });

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

  // ★ 게스트 조회 모달 관련
  const guestModal = document.getElementById("guestModal");
  const confirmedGuestButton = document.getElementById("confirmedGuest");
  const guestModalClose = document.getElementById("guestModalClose");
  confirmedGuestButton.addEventListener("click", function () {
    guestModal.style.display = "block";
    document.body.classList.add("modal-open");
  });
  guestModalClose.addEventListener("click", function () {
    guestModal.style.display = "none";
    document.body.classList.remove("modal-open");
  });
  window.addEventListener("click", function (event) {
    if (event.target === guestModal) {
      guestModal.style.display = "none";
      document.body.classList.remove("modal-open");
    }
  });

  // ★ 메시지 보내기 모달 관련
  const messageModal = document.getElementById("messageModal");
  const messageHostBtn = document.getElementById("messageHostBtn");
  const messageModalClose = document.getElementById("messageModalClose");
  const noMessageBtn = document.getElementById("noMessageBtn");
  messageHostBtn.addEventListener("click", function () {
    messageModal.style.display = "block";
    document.body.classList.add("modal-open");
  });
  messageModalClose.addEventListener("click", function () {
    messageModal.style.display = "none";
    document.body.classList.remove("modal-open");
  });
  noMessageBtn.addEventListener("click", function () {
    messageModal.style.display = "none";
    document.body.classList.remove("modal-open");
  });
  window.addEventListener("click", function (event) {
    if (event.target === messageModal) {
      messageModal.style.display = "none";
      document.body.classList.remove("modal-open");
    }
  });
});

// 카카오 공유 버튼 이벤트 (window.onload 사용)
window.addEventListener("load", function () {
  const shareId = new URL(window.location.href).searchParams.get("shareId");
  const shareUrl =
    window.location.origin + `/share/selectOne?shareId=${shareId}`;
  Kakao.Link.createDefaultButton({
    container: "#kakao-link-btn",
    objectType: "feed",
    content: {
      title: document.querySelector(".share-title").innerText,
      description: "숙소 공유 게시글입니다.",
      imageUrl: window.firstImageUrl,
      link: {
        mobileWebUrl: shareUrl,
        webUrl: shareUrl,
      },
    },
    buttons: [
      {
        title: "웹으로 보기",
        link: {
          mobileWebUrl: shareUrl,
          webUrl: shareUrl,
        },
      },
    ],
  });
});

// LINE 공유 버튼 이벤트 (이미지 버튼 방식)
document.addEventListener("DOMContentLoaded", function () {
  const lineShareBtn = document.getElementById("lineShareBtn");
  if (lineShareBtn) {
    lineShareBtn.addEventListener("click", function () {
      const shareId = new URL(window.location.href).searchParams.get("shareId");
      const shareUrl =
        window.location.origin + `/share/selectOne?shareId=${shareId}`;
      const shareText = encodeURIComponent(
        "숙소 공유 게시글입니다. " + shareUrl
      );
      const lineShareUrl = `https://line.me/R/msg/text/?text=${shareText}`;
      window.open(lineShareUrl, "_blank", "width=500,height=600");
    });
  }
});

// 편의시설 지도 전용 변수 및 함수
let facilityMap;

function initFacilityMap() {
  console.log("편의시설 지도 초기화 시작");
  var geocoder = new google.maps.Geocoder();
  facilityMap = new google.maps.Map(document.getElementById("facilitymap"), {
    zoom: 16,
    center: { lat: 35.6895, lng: 139.6917 },
  });
  geocoder.geocode({ address: window.fullAddress }, function (results, status) {
    if (status === "OK" && results.length > 0) {
      facilityMap.setCenter(results[0].geometry.location);
      new google.maps.Marker({
        map: facilityMap,
        position: results[0].geometry.location,
        title: "셰어하우스 위치",
      });
    } else {
      console.error("편의시설 지도 Geocode 실패: " + status);
    }
  });
}

function searchNearbyPlaces(keyword) {
  if (!window.mapObj) {
    console.error("기본 지도가 초기화되지 않았습니다.");
    return;
  }
  window.mapObj.setZoom(15);
  var service = new google.maps.places.PlacesService(window.mapObj);
  var request = {
    location: window.mapObj.getCenter(),
    radius: 1000,
    keyword: keyword,
  };
  clearFacilityMarkers();
  service.nearbySearch(request, function (results, status) {
    if (status === google.maps.places.PlacesServiceStatus.OK) {
      console.log("편의시설 검색 결과:", results);
      var placeList = document.getElementById("place-list");
      placeList.innerHTML = "";
      results.forEach((place) => {
        let marker = new google.maps.Marker({
          map: window.mapObj,
          position: place.geometry.location,
          title: place.name,
        });
        facilityMarkers.push(marker);
        // 편의시설 목록 생성
        let nameSpanStyle = "font-size:1.2em; font-weight: bold;";
        let storeColor = "";
        if (place.name.indexOf("ローソン") !== -1) {
          storeColor = "blue";
        } else if (
          place.name.indexOf("セブン-イレブン") !== -1 ||
          place.name.indexOf("セブンイレブン") !== -1
        ) {
          storeColor = "red";
        } else if (place.name.indexOf("ファミリーマート") !== -1) {
          storeColor = "green";
        }
        let storeNames = [
          "ローソン",
          "セブン-イレブン",
          "セブンイレブン",
          "ファミリーマート",
        ];
        let formattedName = place.name;
        storeNames.forEach((store) => {
          if (formattedName.indexOf(store) !== -1) {
            formattedName = formattedName.replace(
              store,
              `<span style="color: ${storeColor};">${store}</span>`
            );
          }
        });
        let vicinitySpanStyle = "font-size:0.8em; color: gray;";
        var listItem = document.createElement("li");
        listItem.classList.add("list-group-item");
        listItem.innerHTML = `<span style="${nameSpanStyle}">${formattedName}</span><br><span style="${vicinitySpanStyle}">${place.vicinity}</span>`;
        placeList.appendChild(listItem);
      });
    } else {
      console.error("근처 편의시설 검색 실패: " + status);
    }
  });
}

function clearFacilityMarkers() {
  facilityMarkers.forEach((marker) => marker.setMap(null));
  facilityMarkers = [];
}
