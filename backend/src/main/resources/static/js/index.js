$(document).ready(function () {
  let lastScrollTop = 0; // 마지막 스크롤 위치 저장
  let isStickyDisabled = false; // sticky가 해제되었는지 상태 확인
  let map, geocoder;
  let markers = [];

  // 스크롤 이벤트: 일정 부분 이상 스크롤되면 sticky 추가
  $(window).on("scroll", function () {
    let scrollTop = $(window).scrollTop();

    if (scrollTop > 100 && !isStickyDisabled) {
      $("header").addClass("sticky");
    } else if (scrollTop <= 100) {
      $("header").removeClass("sticky");
      isStickyDisabled = false; // 스크롤이 최상단이면 다시 sticky 허용
    }

    lastScrollTop = scrollTop; // 마지막 스크롤 위치 저장
  });

  // simple-query-form 클릭 시 sticky 해제
  $(".simple-query-form-btn").on("click", function () {
    if ($("header").hasClass("sticky")) {
      $("header").removeClass("sticky");
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
        isStickyDisabled = false; // 다시 sticky 허용
      }
    }
  });

  // 지역 검색 버튼 클릭 시 쉐어하우스 검색 실행
  $(".query-submit-btn").on("click", function (event) {
    event.preventDefault(); // 폼 제출 방지
    searchShares();
  });

  let mapVisible = false; // 지도 표시 여부 상태

  $("#mapButton").click(function () {
    if (!mapVisible) {
      $(".near-trip").hide(); // 기존 여행지 숨기기
      $("#mapContainer").show(); // 지도 컨테이너 표시
      $(this).text("목록 보기"); // 버튼 텍스트 변경
      loadGoogleMap(); // 구글 맵 로드
    } else {
      $(".near-trip").show(); // 기존 여행지 다시 표시
      $("#mapContainer").hide(); // 지도 숨기기
      $(this).text("지도 표시하기"); // 버튼 텍스트 변경
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
            infoWindow.open(map, marker);
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

          // 검색 결과에서도 첫 번째 이미지 반영
          const imageUrl = listing.firstImage ? listing.firstImage : "/images/no-image.png";

          card.innerHTML = `
              <a href="/share/selectOne?shareId=${listing.id}" class="listing-link">
                  <button class="favorite-btn"><i class="far fa-heart"></i></button>
                  <img src="${imageUrl}" alt="숙소 이미지">
                  <div class="listing-info">
                      <h3>${listing.title}</h3>
                      <p>${listing.regionName} ${listing.cityName} ${listing.townName}</p>
                      <p>₩${new Intl.NumberFormat().format(listing.price)}/박</p>
                      <p>최대 인원: ${listing.maxGuests}명</p>
                  </div>
              </a>
          `;

          listingsContainer.appendChild(card);
      });
  }


  //  마커 클릭 시 표시될 정보 창 생성
  function generateInfoWindowContent(share, fullAddress) {
    return `
            <div style="max-width: 250px;">
                <h4>${share.title}</h4>
                <p><strong>가격:</strong> ₩${new Intl.NumberFormat().format(
                  share.price
                )}/박</p>
                <p><strong>위치:</strong> ${fullAddress}</p>
                <a href="/share/selectOne?shareId=${
                  share.shareId
                }" target="_blank">상세 보기</a>
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
});

function updateRegionSelect() {
  const provinceSelect = document.getElementById("provinceId");
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedProvince = provinceSelect.value;

  // 하위 선택 박스 초기화
  regionSelect.innerHTML = '<option value="all">전체</option>';
  citySelect.innerHTML = '<option value="all" >전체</option>';
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
  citySelect.innerHTML = '<option value="all" >전체</option>';
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

  townSelect.innerHTML = '<option value="all" >전체</option>';

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
