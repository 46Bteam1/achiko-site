$(document).ready(function () {
  if (!sessionStorage.getItem("visited")) {
    sessionStorage.setItem("visited", "true");
    window.location.href = "/torii"; // 처음 방문 시 splinePage.html로 이동
  }

  let lastScrollTop = 0; // 마지막 스크롤 위치 저장
  let isStickyDisabled = false; // sticky가 해제되었는지 상태 확인
  let mapVisible = false; // 지도 표시 여부 상태
  let map, geocoder;
  let markers = [];

  // 스크롤 이벤트: 일정 부분 이상 스크롤되면 sticky 추가
  $(window).on("scroll", function () {
    let scrollTop = $(window).scrollTop();
    let windowHeight = $(window).height();
    let documentHeight = $(document).height();
    let footerHeight = $("footer").outerHeight();

    // footer가 화면에 일정 부분 보이면 버튼 숨기기
    let footerVisibleThreshold =
      documentHeight - footerHeight + footerHeight / 2;
    if (scrollTop + windowHeight > footerVisibleThreshold) {
      $("#mapButton").fadeOut(); // 부드럽게 숨기기
    } else {
      $("#mapButton").fadeIn(); // 다시 보이기
    }

    if (scrollTop > 70) {
      $("header").addClass("sticky"); // sticky가 add되면 작은 검색창 나옴
      $("header").removeClass("sticky-reappear");
    } else if (scrollTop <= 70 && lastScrollTop >= 70 && !mapVisible) {
      $("header").removeClass("sticky");
      $("header").addClass("sticky-reappear");
      isStickyDisabled = false; // 스크롤이 최상단이면 다시 sticky 허용 = 큰 검색창 나옴
    }

    lastScrollTop = scrollTop; // 마지막 스크롤 위치 저장
  });

  // simple-query-form 클릭 시 sticky 해제
  $(".simple-query-form-btn").on("click", function () {
    if ($("header").hasClass("sticky")) {
      $("header").removeClass("sticky");
      $("header").addClass("sticky-reappear");
      $("header").addClass("");
      isStickyDisabled = true; // 사용자가 의도적으로 sticky를 해제했음을 저장
    }
  });

  // 화면의 다른 곳을 클릭하면 다시 sticky 적용
  $(document).on("click", function (event) {
    if (isStickyDisabled) {
      let isClickInsideQueryForm =
        $(event.target).closest(".simple-query-form, .query-form").length > 0;
      if (!isClickInsideQueryForm) {
        $("header").addClass("sticky");
        $("header").removeClass("sticky-reappear");
        isStickyDisabled = false; // 다시 sticky 허용
      }
    }
  });

  // 지역 검색 버튼 클릭 시 쉐어하우스 검색 실행
  $(".query-submit-btn").on("click", function (event) {
    event.preventDefault(); // 폼 제출 방지
    searchShares();
  });

  $("#mapButton").click(function () {
    if (!mapVisible) {
      $(".near-trip").hide(); // 기존 여행지 숨기기
      $("#mapContainer").show(); // 지도 컨테이너 표시
      $(this).text("🌸목록 보기🌸"); // 버튼 텍스트 변경
      $("body").addClass("mapScrollHidden");
      $("header").addClass("sticky"); // sticky가 add되면 작은 검색창 나옴
      $("header").removeClass("sticky-reappear");
      loadGoogleMap(); // 구글 맵 로드
    } else {
      $(".near-trip").show(); // 기존 여행지 다시 표시
      $("#mapContainer").hide(); // 지도 숨기기
      $("body").removeClass("mapScrollHidden");
      $("header").removeClass("sticky");
      $("header").addClass("sticky-reappear");
      $(this).text("🌸지도 표시하기🌸"); // 버튼 텍스트 변경
    }
    mapVisible = !mapVisible; // 상태 변경
  });

  function loadGoogleMap() {
    if (!window.google || !window.google.maps) return;

    // 구글 맵 API 로드
    geocoder = new google.maps.Geocoder();
    map = new google.maps.Map(document.getElementById("googleMap"), {
      center: { lat: 35.6895, lng: 139.6917 }, // 기본 도쿄 위치
      zoom: 10,
    });

    fetchAllShares();
  }

  //  모든 숙소 데이터를 API에서 가져오기
  function fetchAllShares() {
    $.ajax({
      url: "/share/selectAll",
      type: "GET",
      dataType: "json",
      success: function (data) {
        if (!data || data.length === 0) {
          console.warn("숙소 데이터가 없습니다.");
          return;
        }
        updateMapWithSearchResults(data);
      },
      error: function () {
        console.error("숙소 데이터를 불러오는 데 실패했습니다.");
      },
    });
  }

  // 기존 마커 삭제 함수
  function clearMarkers() {
    markers.forEach((marker) => marker.setMap(null)); // 지도에서 제거
    markers = []; // 배열 초기화
  }

  //  마커 추가 (Google Geocoding API 활용)
  let currentInfoWindow = null;
  function addMarker(share) {
    return new Promise((resolve, reject) => {
      let fullAddress = `${share.address} ${share.detailAddress || ""}`.trim();
      geocoder.geocode({ address: fullAddress }, function (results, status) {
        if (status === "OK") {
          let marker = new google.maps.Marker({
            map: map,
            position: results[0].geometry.location,
            title: share.title,
          });

          markers.push(marker); // 배열에 저장

          let infoWindow = new google.maps.InfoWindow({
            content: generateInfoWindowContent(share, fullAddress),
          });

          marker.addListener("click", function () {
            // 이미 열린 InfoWindow가 있다면 닫는다.
            if (currentInfoWindow) {
              currentInfoWindow.close();
            }
            infoWindow.open(map, marker);
            currentInfoWindow = infoWindow;
          });

          resolve(marker);
        } else {
          console.error(`주소 변환 실패: ${fullAddress} (${status})`);
          reject(status);
        }
      });
    });
  }

  // 지도 업데이트 함수
  function updateMapWithSearchResults(shares) {
    if (!map || !geocoder) return;

    clearMarkers();

    let markerPromises = shares.map(
      (share, index) =>
        new Promise((resolve) =>
          setTimeout(() => resolve(addMarker(share)), index * 300)
        )
    );

    // 마커 추가가 완료되면 지도 중심을 이동
    Promise.all(markerPromises)
      .then((results) => {
        let validMarkers = results.filter((marker) => marker);
        if (validMarkers.length > 0) {
          let bounds = new google.maps.LatLngBounds();
          validMarkers.forEach((marker) => bounds.extend(marker.getPosition()));
          map.fitBounds(bounds);
        }
      })
      .catch((error) => console.error("마커 추가 중 오류 발생:", error));
  }

  function searchShares() {
    const provinceId = document.getElementById("provinceId").value;
    const regionId = document.getElementById("regionId").value;
    const cityId = document.getElementById("cityId").value;
    const townId = document.getElementById("townId").value;

    let queryParams = [];

    if (provinceId !== "all") queryParams.push(`provinceId=${provinceId}`);
    if (regionId !== "all") queryParams.push(`regionId=${regionId}`);
    if (cityId !== "all") queryParams.push(`cityId=${cityId}`);
    if (townId !== "all") queryParams.push(`townId=${townId}`);

    const queryString =
      queryParams.length > 0 ? `?${queryParams.join("&")}` : "";

    fetch(`/api/search/shares${queryString}`)
      .then((response) => response.json())
      .then((shares) => {
        updateListings(shares); // 목록 업데이트
        updateMapWithSearchResults(shares); // 지도 업데이트
      })
      .catch((error) => console.error("Error fetching shares:", error));
  }

  // 검색 결과 업데이트 함수
  function updateListings(shares) {
    const listingsContainer = document.getElementById("listings-container");
    listingsContainer.innerHTML = ""; // 기존 목록 초기화

    shares.forEach((listing) => {
      const card = document.createElement("div");
      card.className = "listing-card";

      // (1) 첫 번째 이미지가 있으면 사용, 없으면 기본 이미지
      const imageUrl = listing.firstImage || "/images/default-profile.png";

      // (2) favorite 상태에 따른 heart 아이콘
      const favClass = listing.isFavorite ? "active" : "";
      const iconClass = listing.isFavorite ? "fas fa-heart" : "far fa-heart";

      // (3) 상태별 라벨 텍스트 및 클래스 설정
      const statusText =
        listing.status === "open"
          ? "모집중"
          : listing.status === "living"
          ? "거주중"
          : "마감";
      const statusClass =
        listing.status === "open"
          ? "status-open"
          : listing.status === "living"
          ? "status-living"
          : "status-closed";

      // (4) 별점 표시 로직
      const ratingText =
        listing.avgRating !== undefined
          ? listing.avgRating === 0.0
            ? "⭐ 아직 리뷰가 없는 호스트입니다"
            : "⭐ " + listing.avgRating
          : "";

      // (5) 가격 포맷
      const formattedPrice = new Intl.NumberFormat().format(listing.price);

      // (6) 상세 페이지 링크
      const detailLink = `/share/selectOne?shareId=${listing.shareId}`;

      card.innerHTML = `
      <a href="${detailLink}" class="listing-link">
        <!-- 찜 버튼 -->
        ${
          listing.isLoggedIn
            ? `
              <button class="favorite-btn ${favClass}" data-id="${listing.shareId}">
                <i class="${iconClass}"></i>
              </button>
            `
            : `
              <button class="favorite-btn disabled" title="로그인 후 이용 가능합니다." disabled>
                <i class="far fa-heart"></i>
              </button>
            `
        }

        <!-- 이미지 -->
        <img src="${imageUrl}" class="roomImg" alt="이미지"/>

        <!-- 방 정보 -->
        <div class="listing-info">
          <h3>${listing.title || ""}</h3>
          
          <div class="host-info">
            <!-- 상태 라벨 -->
            <div class="status-label ${statusClass}">${statusText}</div>

            <!-- 호스트 프로필 -->
            <div class="host-image-wrapper">
              <img src="${
                listing.profileImage || "/images/default-profile.png"
              }" class="host-image" alt="hostImage"/>
            </div>

            <div class="host-detail">
              <span>${listing.nickname || ""}</span>
              <span>${ratingText}</span>
            </div>
          </div>

          <!-- 지역 -->
          <div class="detail-wrapper">
            <i class="fas fa-map-marker-alt icon-gray"></i>
            <p class="text-ellipsis">
              ${listing.regionName || ""} ${listing.cityName || ""} ${
        listing.townName || ""
      }
            </p>
          </div>

          <!-- 가격 -->
          <div class="detail-wrapper">
            <i class="fas fa-yen-sign icon-gray"></i>
            <p class="text-ellipsis">
              <span>${formattedPrice}</span> / 월
            </p>
          </div>

          <!-- 인원 -->
          <div class="detail-wrapper">
            <i class="fas fa-user icon-gray"></i>
            <p class="text-ellipsis">
              인원: ${listing.currentGuests || 0} / ${listing.maxGuests || 0}명
            </p>
          </div>
        </div>
      </a>
    `;

      listingsContainer.appendChild(card);
    });
  }

  //  마커 클릭 시 표시될 정보 창 생성
  function generateInfoWindowContent(share, fullAddress) {
    return `
    <div class="info-window">
      <img src="${share.fileList[0].fileUrl}" alt="이미지"/>
      <h4>${share.title}</h4>
      <p class="price">₩ ${new Intl.NumberFormat().format(share.price)} / 월</p>
      <p class="address">${fullAddress}</p>
      <a href="/share/selectOne?shareId=${
        share.shareId
      }" target="_blank" class="detail-link">
        상세 보기
      </a>
    </div>
        `;
  }

  // 메뉴 버튼
  const $menuButton = $("#menuButton");
  const $modalMenu = $("#modalMenu");

  // 메뉴 버튼 클릭 시 모달 열기
  $menuButton.on("click", function (event) {
    event.stopPropagation();
    if ($modalMenu.is(":visible")) {
      $modalMenu.hide();
    } else {
      $modalMenu.show();
    }
  });

  // 모달 바깥 클릭 시 모달 닫기
  $(document).on("click", function (event) {
    if (
      !$modalMenu.is(event.target) &&
      !$modalMenu.has(event.target).length &&
      !$menuButton.is(event.target)
    ) {
      $modalMenu.hide();
    }
  });

  // 좋아요(찜) 버튼 클릭 시 이벤트 처리 - 하트 아이콘 토글
  $(document).on("click", ".favorite-btn", function (e) {
    if ($(this).is("[disabled]")) {
      e.preventDefault();
      alert("로그인 후 이용 가능합니다.");
      return;
    }
    e.preventDefault();
    const button = $(this);
    const shareId = button.data("id");

    if (!button.hasClass("active")) {
      $.ajax({
        url: "/favorite/set",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({ shareId: Number(shareId) }),
        success: function () {
          button.addClass("active");
          button.find("i").removeClass("far").addClass("fas");
        },
        error: function () {
          console.error("찜하기 실패");
        },
      });
    } else {
      $.ajax({
        url: "/favorite/cancel?shareId=" + shareId,
        method: "DELETE",
        success: function () {
          button.removeClass("active");
          button.find("i").removeClass("fas").addClass("far");
        },
        error: function () {
          alert("찜 취소 실패!");
        },
      });
    }
  });
});

function updateRegionSelect() {
  const provinceSelect = document.getElementById("provinceId");
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedProvince = provinceSelect.value;

  // 하위 선택 박스 초기화
  regionSelect.innerHTML = '<option value="all">전체</option>';
  citySelect.innerHTML = '<option value="all">전체</option>';
  townSelect.innerHTML = '<option value="all">전체</option>';

  if (selectedProvince !== "all" && selectedProvince !== "") {
    fetch(`/api/location/regions?provinceId=${selectedProvince}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((regions) => {
        console.log("Fetched Regions:", regions);
        regions.forEach((region) => {
          const option = document.createElement("option");
          option.value = region.id;
          option.text = region.name;
          regionSelect.appendChild(option);
        });
      })
      .catch((error) => console.error("Error fetching regions:", error));
  }
}

function updateCitySelect() {
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedRegion = regionSelect.value;

  // 하위 선택 박스 초기화
  citySelect.innerHTML = '<option value="all">전체</option>';
  townSelect.innerHTML = '<option value="all">전체</option>';

  if (selectedRegion) {
    fetch(`/api/location/cities?regionId=${selectedRegion}`)
      .then((response) => response.json())
      .then((cities) => {
        cities.forEach((city) => {
          const option = document.createElement("option");
          option.value = city.id;
          option.text = city.name;
          citySelect.appendChild(option);
        });
      })
      .catch((error) => console.error("Error fetching cities:", error));
  }
}

function updateTownSelect() {
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedCity = citySelect.value;

  townSelect.innerHTML = '<option value="all">전체</option>';

  if (selectedCity !== "all") {
    fetch(`/api/location/towns?cityId=${selectedCity}`)
      .then((response) => response.json())
      .then((towns) => {
        towns.forEach((town) => {
          const option = document.createElement("option");
          option.value = town.id;
          option.text = town.name;
          townSelect.appendChild(option);
        });
      })
      .catch((error) => console.error("Error fetching towns:", error));
  }
}
