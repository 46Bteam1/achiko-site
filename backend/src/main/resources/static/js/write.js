// write.js

// 전역 변수 선언
let selectedFiles = [];
let map, marker, autocomplete;

// --------------------------------------------------------------------
// 지방/지역/도시/하위 시/군/구 선택 업데이트 함수들
// --------------------------------------------------------------------
function updateRegionSelect() {
  const provinceSelect = document.getElementById("provinceId");
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedProvince = provinceSelect.value;
  
  // 하위 select 초기화
  regionSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  citySelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  
  if (selectedProvince) {
    fetch('/api/regions?provinceId=' + selectedProvince)
      .then(response => response.json())
      .then(regions => {
        regions.forEach(region => {
          const option = document.createElement("option");
          option.value = region.id; // RegionDTO의 id
          option.text = region.name; // RegionDTO의 name (예: nameKanji)
          regionSelect.appendChild(option);
        });
      })
      .catch(error => console.error('Error fetching regions:', error));
  }
}

function updateCitySelect() {
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedRegion = regionSelect.value;
  
  citySelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  
  if (selectedRegion) {
    fetch('/api/cities?regionId=' + selectedRegion)
      .then(response => response.json())
      .then(cities => {
        cities.forEach(city => {
          const option = document.createElement("option");
          option.value = city.id;
          option.text = city.name;
          citySelect.appendChild(option);
        });
      })
      .catch(error => console.error('Error fetching cities:', error));
  }
}

function updateTownSelect() {
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedCity = citySelect.value;
  
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  
  if (selectedCity) {
    fetch('/api/towns?cityId=' + selectedCity)
      .then(response => response.json())
      .then(towns => {
        towns.forEach(town => {
          const option = document.createElement("option");
          option.value = town.id;
          option.text = town.name;
          townSelect.appendChild(option);
        });
      })
      .catch(error => console.error('Error fetching towns:', error));
  }
}

// --------------------------------------------------------------------
// Google Maps API 관련 기능 구현
// --------------------------------------------------------------------

// 전역 함수: Google Maps API 스크립트의 콜백용 (window.initMap으로 전역 등록)
window.initMap = function() {
  // 기본 중심 위치 (도쿄)
  const defaultLocation = { lat: 35.6895, lng: 139.6917 };

  // 지도 생성
  map = new google.maps.Map(document.getElementById('map'), {
    center: defaultLocation,
    zoom: 13
  });

  // 마커 생성 (기본 위치 표시)
  marker = new google.maps.Marker({
    position: defaultLocation,
    map: map,
    draggable: false
  });

  // 주소 입력 필드에 Autocomplete 적용 (language=ja로 일본어 주소 반환)
  const addressInput = document.getElementById('address');
  autocomplete = new google.maps.places.Autocomplete(addressInput, {
    componentRestrictions: { country: "jp" },
    fields: ["formatted_address", "geometry", "address_components", "name"]
  });

  // 주소 자동완성 선택 시 이벤트 처리
  autocomplete.addListener('place_changed', onPlaceChanged);
};

function onPlaceChanged() {
  const place = autocomplete.getPlace();

  if (!place.geometry) {
    alert("선택한 주소에 대한 정보를 찾을 수 없습니다. 다시 선택해 주세요.");
    return;
  }

  // 지도 중심 및 마커 업데이트
  map.setCenter(place.geometry.location);
  map.setZoom(15);
  marker.setPosition(place.geometry.location);

  // 주소 입력 필드 업데이트 (일본어 주소)
  document.getElementById('address').value = place.formatted_address;

  // address_components에서 우편번호 추출 후 postalCode 필드에 채움
  if (place.address_components) {
    let postalCode = null;
    for (let i = 0; i < place.address_components.length; i++) {
      const component = place.address_components[i];
      if (component.types.indexOf("postal_code") !== -1) {
        postalCode = component.long_name;
        break;
      }
    }
    if (postalCode) {
      document.getElementById('postalCode').value = postalCode;
    }
  }

  // 영어 주소를 별도 영역에 표시 (Geocoding API 호출)
  const lat = place.geometry.location.lat();
  const lng = place.geometry.location.lng();
  fetchEnglishAddress(lat, lng);
}

function fetchEnglishAddress(lat, lng) {
	const apiKey = window.googleApiKeyFromServer; // 혹은 인자로 받아오기
	
	  if (!apiKey) {
	    console.warn("No Google API Key .env 설정 및 주입 여부 확인 필요");
	    return;
	  }
  const url = `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&language=en&key=${apiKey}`;

  fetch(url)
    .then(response => response.json())
    .then(data => {
      if (data.status === "OK" && data.results.length > 0) {
        document.getElementById("englishAddress").innerHTML = data.results[0].formatted_address;
      } else {
        document.getElementById("englishAddress").innerHTML = "영문 주소를 찾을 수 없습니다.";
      }
    })
    .catch(error => {
      console.error("Error fetching English address:", error);
      document.getElementById("englishAddress").innerHTML = "영문 주소 로드 실패";
    });
}

// --------------------------------------------------------------------
// 파일 미리보기 및 누적 추가 기능 구현 (복수 파일 추가, 순서대로 왼쪽부터 추가)
// --------------------------------------------------------------------
function handlePhotoUpload(event) {
  const previewContainer = document.getElementById("photoPreview");
  
  // 미리보기 영역 flex 컨테이너 설정 (가로 정렬, 가운데 정렬)
  previewContainer.style.display = "flex";
  previewContainer.style.flexWrap = "wrap";
  previewContainer.style.justifyContent = "center";
  previewContainer.style.gap = "10px";

  // 새로 선택한 파일들을 배열로 변환 후 기존 배열에 누적
  const newFiles = Array.from(event.target.files);
  selectedFiles = selectedFiles.concat(newFiles);

  // 전체 미리보기 영역 초기화
  previewContainer.innerHTML = "";

  // 파일들을 순서대로 읽어 미리보기 생성 (순서 보장)
  const readPromises = selectedFiles.map((file, index) => {
    return new Promise((resolve, reject) => {
      if (!file.type.match("image.*")) {
        resolve(null);
      } else {
        const reader = new FileReader();
        reader.onload = function(e) {
          resolve({ index, src: e.target.result, name: file.name });
        };
        reader.onerror = reject;
        reader.readAsDataURL(file);
      }
    });
  });

  Promise.all(readPromises)
    .then(results => {
      results.forEach((result, idx) => {
        if (!result) return;
        
        // 이미지 미리보기 생성
        const img = document.createElement("img");
        img.src = result.src;
        img.style.width = "150px";
        img.style.height = "auto";
        img.style.display = "block";
        img.style.marginBottom = "5px";

        // 파일 번호만 표시 (순서)
        const fileNumberDiv = document.createElement("div");
        fileNumberDiv.textContent = `${idx + 1}`;
        fileNumberDiv.style.fontSize = "0.9em";
        fileNumberDiv.style.color = "#555";

        // 개별 컨테이너 생성 (중앙 정렬)
        const container = document.createElement("div");
        container.style.textAlign = "center";
        container.appendChild(img);
        container.appendChild(fileNumberDiv);

        // 미리보기 영역에 순서대로 추가 (왼쪽부터 오른쪽으로)
        previewContainer.appendChild(container);
      });
    })
    .catch(error => console.error("Error reading files:", error));

  // 파일 입력 필드 초기화 (같은 파일 재선택 가능)
  event.target.value = "";
}

// --------------------------------------------------------------------
// 현재 인원 유효성 검사: currentGuests가 maxGuests보다 작아야 함
// --------------------------------------------------------------------
function validateGuestCount() {
  const currentGuestsInput = document.getElementById("currentGuests");
  const maxGuestsInput = document.getElementById("maxGuests");
  const currentValue = parseInt(currentGuestsInput.value);
  const maxValue = parseInt(maxGuestsInput.value);
  
  if (!isNaN(currentValue) && !isNaN(maxValue)) {
    if (currentValue >= maxValue) {
      alert("현재 인원이 수용 가능 인원보다 많습니다.\n다시 확인해주세요.");
      currentGuestsInput.value = "";
      currentGuestsInput.focus();
    }
  }
}

// --------------------------------------------------------------------
// 주소 유효성 검사: address에 선택된 region의 이름이 포함되어야 함
// (미포함 시 주소와 영어 주소 영역 초기화)
// --------------------------------------------------------------------
function validateAddress() {
  const addressInput = document.getElementById("address");
  const regionSelect = document.getElementById("regionId");
  
  // regionSelect의 선택된 옵션 텍스트 (예: "東京都")
  const selectedRegionText = regionSelect.options[regionSelect.selectedIndex]?.text || "";
  const addressValue = addressInput.value;
  
  if (selectedRegionText && !addressValue.includes(selectedRegionText)) {
    alert("지역을 다시 한번 확인해주세요.");
    addressInput.value = "";
    addressInput.focus();
    document.getElementById("englishAddress").innerHTML = "";
    return false;
  }
  return true;
}

// --------------------------------------------------------------------
// 폼 제출 전 전체 유효성 검사: 제목, 본문, 주소
// --------------------------------------------------------------------
function validateForm() {
  const titleInput = document.getElementById("title");
  const descriptionInput = document.getElementById("description");

  // 제목은 5글자 이상 (공백 제거 후 검사)
  if (titleInput.value.trim().length < 5) {
    alert("제목은 5글자 이상 입력해주세요.");
    titleInput.focus();
    return false;
  }
  
  // 본문은 10글자 이상 (공백 제거 후 검사)
  if (descriptionInput.value.trim().length < 10) {
    alert("본문은 10글자 이상 입력해주세요.");
    descriptionInput.focus();
    return false;
  }
  
  // 주소 유효성 검사
  return validateAddress();
}

