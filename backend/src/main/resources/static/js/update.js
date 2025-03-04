// ================================
// update.js
// ================================

// (A) 전역 변수 선언
let selectedFiles = [];
let map, marker, autocomplete, autocompleteService;
let facilityMap;
let facilityMarkers = [];
let firstKeyPressed = false;  // ★ 전역 변수로 선언하여 매번 재설정 가능
window.isMapReady = false;      // ★ 지도 초기화 완료 여부 플래그
// ★ 주소 수정 후 지도 업데이트 버튼을 누른 상태 여부 (true: 갱신됨)
let isMapUpdated = false;
// ★ 주소가 수정되었는지 여부 (사용자가 "주소 수정" 버튼을 눌렀을 때 true)
let addressModified = false;

// (A-추가) ★ 지도 및 우편번호, 영문 주소 업데이트 함수
function updateMapAndPostalCode() {
  const addressInput = document.getElementById("address");
  const currentAddress = addressInput.value.trim();
  if (currentAddress === "") return;
  const geocoder = new google.maps.Geocoder();
  geocoder.geocode({ address: currentAddress }, function (results, status) {
    if (status === "OK" && results.length > 0) {
      const location = results[0].geometry.location;
      if (map && marker) {
        map.setCenter(location);
        map.setZoom(15); // zoom 15로 설정
        marker.setPosition(location);
      }
      // 우편번호 추출
      let postalCode = "";
      for (let component of results[0].address_components) {
        if (component.types.includes("postal_code")) {
          postalCode = component.long_name;
          break;
        }
      }
      document.getElementById("postalCode").value = postalCode;
      // 영문 주소 업데이트 (fetchEnglishAddress()를 호출하여 영문 변환)
      fetchEnglishAddress(location.lat(), location.lng());
      // 지도 업데이트 성공 시 flag 설정
      isMapUpdated = true;
    } else if (status === "ZERO_RESULTS") {
      console.warn("입력하신 주소로 검색 결과가 없습니다. 유효한 주소를 입력해 주세요.");
    } else {
      console.error("Geocode 실패: " + status);
    }
  });
}

/*
  (B) 전역 함수 정의  
  HTML 인라인 이벤트(예: onchange="updateRegionSelect()")에서 호출할 수 있도록,
  모든 함수는 window 객체에 할당합니다.
*/

// 1) 드롭다운 업데이트 함수: province 변경 시 region 목록 업데이트
window.updateRegionSelect = function (callback) {
  const provinceSelect = document.getElementById("provinceId");
  const regionSelect = document.getElementById("regionId");
  // ★ 추가: city와 town 드롭다운 초기화
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");

  const selectedProvince = provinceSelect ? provinceSelect.value : "";
  regionSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  citySelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  if (selectedProvince) {
    fetch(`/api/regions?provinceId=${selectedProvince}`)
      .then(response => response.json())
      .then(regions => {
        regions.forEach(region => {
          const option = document.createElement("option");
          option.value = region.id;
          option.text = region.name;
          regionSelect.appendChild(option);
        });
        if (callback) callback();
      })
      .catch(error => console.error("Error fetching regions:", error));
  } else {
    if (callback) callback();
  }
};

// 2) 도시 업데이트 함수: region 변경 시 city 목록 업데이트
window.updateCitySelect = function (callback) {
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedRegion = regionSelect ? regionSelect.value : "";
  citySelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  if (selectedRegion) {
    fetch(`/api/cities?regionId=${selectedRegion}`)
      .then(response => response.json())
      .then(cities => {
        cities.forEach(city => {
          const option = document.createElement("option");
          option.value = city.id;
          option.text = city.name;
          citySelect.appendChild(option);
        });
        if (callback) callback();
      })
      .catch(error => console.error("Error fetching cities:", error));
  } else {
    if (callback) callback();
  }
};

// 3) 하위 지역 업데이트 함수: city 변경 시 town 목록 업데이트
window.updateTownSelect = function (callback) {
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedCity = citySelect ? citySelect.value : "";
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  if (selectedCity) {
    fetch(`/api/towns?cityId=${selectedCity}`)
      .then(response => response.json())
      .then(towns => {
        towns.forEach(town => {
          const option = document.createElement("option");
          option.value = town.id;
          option.text = town.name;
          townSelect.appendChild(option);
        });
        if (callback) callback();
      })
      .catch(error => console.error("Error fetching towns:", error));
  } else {
    if (callback) callback();
  }
};

// 4) 현재 인원 유효성 검사
window.validateGuestCount = function () {
  const currentGuestsInput = document.getElementById("currentGuests");
  const maxGuestsInput = document.getElementById("maxGuests");
  const currentValue = parseInt(currentGuestsInput.value);
  const maxValue = parseInt(maxGuestsInput.value);
  if (!isNaN(currentValue) && !isNaN(maxValue) && currentValue > maxValue) {
    console.warn("현재 인원이 최대 인원을 초과합니다.");
    currentGuestsInput.value = "";
    currentGuestsInput.focus();
  }
};

// 5) 주소 유효성 검사
window.validateAddress = function () {
  const addressInput = document.getElementById("address");
  const regionSelect = document.getElementById("regionId");
  const townSelect = document.getElementById("townId");
  const selectedRegionText = regionSelect.options[regionSelect.selectedIndex]?.text || "";
  const selectedTownText = townSelect.options[townSelect.selectedIndex]?.text || "";
  const addressValue = addressInput.value;
  if (selectedRegionText && !addressValue.includes(selectedRegionText)) {
    console.warn("입력된 주소에 선택된 지역명이 포함되어 있지 않습니다.");
    addressInput.value = "";
    addressInput.focus();
    document.getElementById("englishAddress").innerHTML = "";
    return false;
  }
  if (selectedTownText && !addressValue.includes(selectedTownText)) {
    console.warn("입력된 주소에 선택된 하위 시/군/구명이 포함되어 있지 않습니다.");
    addressInput.value = "";
    addressInput.focus();
    document.getElementById("englishAddress").innerHTML = "";
    return false;
  }
  return true;
};

// 6) 요금 입력 유효성 검사
window.validatePrice = function () {
  const priceInput = document.getElementById("price");
  const priceValue = priceInput.value.trim();
  const pricePattern = /^[0-9]+$/;
  if (!pricePattern.test(priceValue)) {
    console.warn("요금은 정수(숫자)만 입력 가능합니다.");
    priceInput.focus();
    return false;
  }
  return true;
};

// 7) 폼 유효성 검사 (최종)
// 만약 사용자가 주소를 수정한 경우(addressModified==true) && 지도가 업데이트되지 않았다(isMapUpdated==false)
// alert: "지도 업데이트 버튼을 눌러 갱신된 위치 정보를 확인해주세요."를 띄우고 제출 차단.
window.validateForm = function () {
  const titleInput = document.getElementById("title");
  const descriptionInput = document.getElementById("description");
  if (titleInput.value.trim().length < 5) {
    console.warn("제목은 5글자 이상이어야 합니다.");
    titleInput.focus();
    return false;
  }
  if (descriptionInput.value.trim().length < 10) {
    console.warn("본문은 10자 이상이어야 합니다.");
    descriptionInput.focus();
    return false;
  }
  if (!validatePrice()) {
    return false;
  }
  if (addressModified && !isMapUpdated) {
    alert("지도 업데이트 버튼을 눌러 갱신된 위치 정보를 확인해주세요.");
    return false;
  }
  return validateAddress();
};

// 8) 파일 미리보기
window.handlePhotoUpload = function (event) {
  const previewContainer = document.getElementById("photoPreview");
  if (!previewContainer) return;
  previewContainer.style.display = "flex";
  previewContainer.style.flexWrap = "wrap";
  previewContainer.style.justifyContent = "center";
  previewContainer.style.gap = "10px";
  const newFiles = Array.from(event.target.files);
  selectedFiles = selectedFiles.concat(newFiles);
  previewContainer.innerHTML = "";
  const readPromises = selectedFiles.map((file, index) => {
    return new Promise((resolve, reject) => {
      if (!file.type.match("image.*")) {
        resolve(null);
      } else {
        const reader = new FileReader();
        reader.onload = function (e) {
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
        const img = document.createElement("img");
        img.src = result.src;
        img.style.width = "150px";
        img.style.height = "auto";
        img.style.display = "block";
        img.style.marginBottom = "5px";
        const fileNumberDiv = document.createElement("div");
        fileNumberDiv.textContent = `${idx + 1}`;
        fileNumberDiv.style.fontSize = "0.9em";
        fileNumberDiv.style.color = "#555";
        const container = document.createElement("div");
        container.style.textAlign = "center";
        container.appendChild(img);
        container.appendChild(fileNumberDiv);
        previewContainer.appendChild(container);
      });
    })
    .catch(error => console.error("Error reading files:", error));
  event.target.value = "";
};

// (C) DOMContentLoaded 이후, 추가 초기화 로직
document.addEventListener("DOMContentLoaded", function () {
  // ★ 기존 DB 값으로 드롭다운 초기화
  initProvinceRegionCityTown();
  // 조건부 이벤트 등록 (필요한 경우)
  const shareButton = document.getElementById("shareButton");
  if (shareButton) {
    shareButton.addEventListener("click", function () {
      console.info("URL 복사 또는 SNS 공유 기능 호출");
    });
  }
  // ★ '지도 업데이트' 버튼 이벤트 등록
  const updateMapBtn = document.getElementById("updateMapBtn");
  if (updateMapBtn) {
    updateMapBtn.addEventListener("click", function () {
      updateMapAndPostalCode();
    });
  }
});

// (D) DB에 저장된 기존 province/region/city/town 값을 드롭다운에 설정
function initProvinceRegionCityTown() {
  const provinceSelect = document.getElementById("provinceId");
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  if (provinceSelect && window.provinceId && window.provinceId !== "0" && window.provinceId !== 0) {
    provinceSelect.value = window.provinceId;
  }
  updateRegionSelect(function () {
    if (regionSelect && window.regionId && window.regionId !== "0" && window.regionId !== 0) {
      regionSelect.value = window.regionId;
    }
    updateCitySelect(function () {
      if (citySelect && window.cityId && window.cityId !== "0" && window.cityId !== 0) {
        citySelect.value = window.cityId;
      }
      updateTownSelect(function () {
        if (townSelect && window.townId && window.townId !== "0" && window.townId !== 0) {
          townSelect.value = window.townId;
        }
      });
    });
  });
}

// (E) Google Maps API 관련 기능 구현 (수정된 부분)
// ★ 핵심 수정: 초기 로드시 저장된 주소(window.fullAddress)가 있다면 해당 주소로 지도를 설정
window.initMap = function () {
  console.log("Google Maps API가 정상적으로 로드되었습니다.");
  const defaultLocation = { lat: 35.6895, lng: 139.6917 };
  const mapContainer = document.getElementById("map");

  // 만약 저장된 주소가 있다면 geocode하여 초기 위치 설정, 없으면 기본값 사용
  if (window.fullAddress && window.fullAddress.trim() !== "" && window.fullAddress !== "Default Address, Default Detail") {
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({ address: window.fullAddress }, function (results, status) {
      if (status === "OK" && results.length > 0) {
        const location = results[0].geometry.location;
        map = new google.maps.Map(mapContainer, {
          center: location,
          zoom: 13
        });
        marker = new google.maps.Marker({
          position: location,
          map: map,
          draggable: false
        });
      } else {
        console.error("초기 주소 geocode 실패: " + status);
        map = new google.maps.Map(mapContainer, {
          center: defaultLocation,
          zoom: 13
        });
        marker = new google.maps.Marker({
          position: defaultLocation,
          map: map,
          draggable: false
        });
      }
      window.isMapReady = true;
      // ★ 페이지 로드시 영어주소도 갱신 (updateMapAndPostalCode 호출)
      updateMapAndPostalCode();
      initializeAddressListeners();
    });
  } else {
    // 저장된 주소가 없으면 기본값 사용
    map = new google.maps.Map(mapContainer, {
      center: defaultLocation,
      zoom: 13
    });
    marker = new google.maps.Marker({
      position: defaultLocation,
      map: map,
      draggable: false
    });
    window.isMapReady = true;
    initializeAddressListeners();
  }
};

// ★ 초기 주소 입력 필드 관련 이벤트 등록 함수
function initializeAddressListeners() {
  const addressInput = document.getElementById("address");
  // ★ AutocompleteService 초기화
  autocompleteService = new google.maps.places.AutocompleteService();

  // ★ "keydown" 이벤트 리스너 추가  
  // 사용자가 주소 입력 필드에 포커스한 후 처음 키를 누르면,
  // 강제로 공백(" ") 입력을 통해 예측 검색을 트리거하고,
  // 첫 번째 추천 주소를 자동 입력한 뒤 input 이벤트를 강제 발생시킵니다.
  firstKeyPressed = false; // ★ 매번 초기화
  addressInput.addEventListener("focus", function () {
    firstKeyPressed = false;
  });
  addressInput.addEventListener("keydown", function (event) {
    if (!firstKeyPressed) {
      firstKeyPressed = true;
      autocompleteService.getPlacePredictions({ input: " " }, function (predictions, status) {
        if (status === google.maps.places.PlacesServiceStatus.OK &&
            predictions && predictions.length > 0) {
          addressInput.value = predictions[0].description; // 첫 번째 추천 주소 자동 입력
          addressInput.dispatchEvent(new Event("input", { bubbles: true })); // ★ input 이벤트 강제 발생
        }
      });
    }
  });

  // ★ 기존 Autocomplete 위젯 초기화 및 리스너 등록
  autocomplete = new google.maps.places.Autocomplete(addressInput, {
    componentRestrictions: { country: "JP" },
    fields: ["formatted_address", "geometry", "address_components", "name"],
    language: "ja"
  });
  // ★ "place_changed" 이벤트 리스너는 지연을 주어 실행 (500ms 후 시도)
  autocomplete.addListener("place_changed", function () {
    if (!window.isMapReady) {
      console.warn("지도 초기화가 완료되지 않았습니다. 500ms 후 다시 시도합니다.");
      setTimeout(onPlaceChanged, 500);
    } else {
      onPlaceChanged();
    }
  });

  // ★ "input" 이벤트 리스너 등록: AutocompleteService를 통해 예측 검색 수행 및 지도/마커 업데이트
  addressInput.addEventListener("input", function () {
    const currentValue = this.value;
    console.log("Input event fired, current value:", currentValue);
    if (currentValue.length >= 1) {
      const acService = new google.maps.places.AutocompleteService();
      acService.getPlacePredictions({ input: currentValue }, function (predictions, status) {
        if (status === google.maps.places.PlacesServiceStatus.OK &&
            predictions && predictions.length > 0) {
          const query = predictions[0].description;
          const service = new google.maps.places.PlacesService(map);
          service.textSearch({ query: query }, function (results, status) {
            if (status === google.maps.places.PlacesServiceStatus.OK &&
                results && results.length > 0) {
              const placeResult = results[0];
              map.setCenter(placeResult.geometry.location);
              marker.setPosition(placeResult.geometry.location);
              const englishAddressElem = document.getElementById("englishAddress");
              if (englishAddressElem) {
                englishAddressElem.innerHTML = placeResult.formatted_address;
              }
            }
          });
        }
      });
    }
  });
}

function onPlaceChanged() {
  // ★ 방어 코드: map 또는 marker가 초기화되지 않은 경우 강제로 initMap() 호출
  if (!map || !marker) {
    console.error("onPlaceChanged 호출 시 map이 초기화되지 않았습니다. 강제로 initMap()을 호출합니다.");
    window.initMap();
    return;
  }
  const place = autocomplete.getPlace();
  if (!place.geometry) {
    console.warn("선택한 주소에 대한 정보를 찾을 수 없습니다. 다시 선택해 주세요.");
    return;
  }
  const location = place.geometry.location;
  map.setCenter(location);
  map.setZoom(15);
  marker.setPosition(location);
  document.getElementById("address").value = place.formatted_address;
  // 우편번호 업데이트
  let postalCode = "";
  for (let component of place.address_components) {
    if (component.types.includes("postal_code")) {
      postalCode = component.long_name;
      break;
    }
  }
  document.getElementById("postalCode").value = postalCode;
  fetchEnglishAddress(location.lat(), location.lng());
}

function fetchEnglishAddress(lat, lng) {
  fetch(`/api/geocode?lat=${lat}&lng=${lng}`)
    .then(response => response.text())
    .then(text => {
      let data;
      try {
        data = JSON.parse(text);
      } catch (e) {
        console.error("JSON 파싱 오류:", e);
        document.getElementById("englishAddress").innerHTML = "영문 주소 로드 실패";
        return;
      }
      console.log("Geocode API 응답:", data);
      if (data.status === "OK" && data.results.length > 0) {
        document.getElementById("englishAddress").innerHTML = data.results[0].formatted_address;
      } else {
        document.getElementById("englishAddress").innerHTML = "Not Found";
      }
    })
    .catch(error => {
      console.error("Error fetching English address:", error);
      document.getElementById("englishAddress").innerHTML = "영문 주소 로드 실패";
    });
}

// (F) "주소 수정" 기능: 주소 입력 필드 초기화 및 readonly 속성 제거, 그리고 Autocomplete 재초기화
window.resetAddress = function () {
  const addressInput = document.getElementById("address");
  addressInput.value = "";                         // 주소 초기화
  addressInput.removeAttribute("readonly");        // readonly 속성 제거 ★
  const englishAddressElem = document.getElementById("englishAddress");
  if (englishAddressElem) {
    englishAddressElem.innerHTML = "";
  }
  addressInput.focus();
  // 주소가 수정되었음을 flag 설정
  addressModified = true;
  isMapUpdated = false; // 갱신 전이므로 false로 초기화
  // ★ 재초기화: Autocomplete 위젯 재부착 (새 주소 입력 대비)
  autocomplete = new google.maps.places.Autocomplete(addressInput, {
    componentRestrictions: { country: "JP" },
    fields: ["formatted_address", "geometry", "address_components", "name"],
    language: "ja"
  });
  autocomplete.addListener("place_changed", function () {
    if (!window.isMapReady) {
      console.warn("지도 초기화가 완료되지 않았습니다. 500ms 후 다시 시도합니다.");
      setTimeout(onPlaceChanged, 500);
    } else {
      onPlaceChanged();
    }
  });
};

// (G) 폼 제출 전 전체 유효성 검사 (기존과 동일, 단 주소가 수정되었으면 지도 업데이트 여부도 확인)
function validateForm() {
  const titleInput = document.getElementById("title");
  const descriptionInput = document.getElementById("description");
  if (titleInput.value.trim().length < 5) {
    console.warn("제목은 5글자 이상이어야 합니다.");
    titleInput.focus();
    return false;
  }
  if (descriptionInput.value.trim().length < 10) {
    console.warn("본문은 10자 이상이어야 합니다.");
    descriptionInput.focus();
    return false;
  }
  if (!validatePrice()) {
    return false;
  }
  /*
  if (addressModified && !isMapUpdated) {
    alert("지도 업데이트 버튼을 눌러 갱신된 위치 정보를 확인해주세요.");
    return false;
  }
  */
  return validateAddress();
}
