"use strict";

// (A) 전역 변수 선언
let selectedFiles = []; // 각 요소: { file: File|null, fileId: number|null, fileUrl: string, isNew: boolean }
let map, marker, autocomplete, autocompleteService;
let facilityMap;
let facilityMarkers = [];
let firstKeyPressed = false;
window.isMapReady = false;
let isMapUpdated = false;
let addressModified = false;

// (B) DOMContentLoaded 초기화
document.addEventListener("DOMContentLoaded", function () {
  // header
  // Fragment가 동적으로 로드된 후 이벤트 바인딩
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

  const updateForm = document.getElementById("updateForm");
  // 인라인 onsubmit 속성이 있다면 제거하여 중복 제출을 방지
  updateForm.removeAttribute("onsubmit");

  // 캡처링 단계에서 제출 이벤트 가로채기
  updateForm.addEventListener(
    "submit",
    async function (e) {
      e.preventDefault();
      e.stopImmediatePropagation();
      if (!validateForm()) {
        // 유효성 검사 실패 시 해당 입력창에 포커스를 주고, 제출을 중단
        return false;
      }
      // 유효성 검사 통과 시 파일 저장 작업 실행
      await saveUpdatedFiles();
      // 유효성 검사 통과 후 실제 제출 (테스트 중에는 여기서 updateForm.submit() 호출을 주석 처리하면 제출이 차단됩니다)
      updateForm.submit();
    },
    true
  ); // 캡처링 단계에서 실행

  // 드롭다운, 지도 등 초기화
  initProvinceRegionCityTown();

  const updateMapBtn = document.getElementById("updateMapBtn");
  if (updateMapBtn) {
    updateMapBtn.addEventListener("click", function () {
      updateMapAndPostalCode();
    });
  }

  // update.html에 숨은 필드로 전달된 sessionId (또는 shareId) 사용
  let sessionId = document.getElementById("sessionId")
    ? document.getElementById("sessionId").value
    : "";
  let existing = window.existingFiles;
  if (typeof existing === "string") {
    try {
      existing = JSON.parse(existing);
    } catch (e) {
      existing = [];
    }
  }
  if (existing && Array.isArray(existing)) {
    selectedFiles = existing.map((file) => {
      return {
        file: null,
        fileId: file.fileId,
        fileUrl: file.fileUrl,
        isNew: false,
      };
    });
    renderPhotoPreview();
  }
});

// (C) 이미지 미리보기 렌더링 함수
function renderPhotoPreview() {
  const previewContainer = document.getElementById("photoPreview");
  previewContainer.innerHTML = "";
  previewContainer.style.display = "flex";
  previewContainer.style.flexWrap = "wrap";
  previewContainer.style.justifyContent = "center";
  previewContainer.style.gap = "10px";

  selectedFiles.forEach((item, idx) => {
    const container = document.createElement("div");
    container.style.textAlign = "center";

    // 이미지 미리보기
    const img = document.createElement("img");
    if (item.file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        img.src = e.target.result;
      };
      reader.readAsDataURL(item.file);
    } else if (item.fileUrl) {
      img.src = item.fileUrl;
    }
    img.style.width = "150px";
    img.style.display = "block";
    img.style.marginBottom = "5px";
    container.appendChild(img);

    // 순번 표시
    const fileNumberDiv = document.createElement("div");
    fileNumberDiv.textContent = (idx + 1).toString();
    fileNumberDiv.style.fontSize = "0.9em";
    fileNumberDiv.style.color = "#555";
    container.appendChild(fileNumberDiv);

    // "수정" 버튼
    const modifyBtn = document.createElement("button");
    modifyBtn.type = "button";
    modifyBtn.textContent = "수정";
    modifyBtn.style.marginRight = "5px";
    modifyBtn.onclick = () => modifyFile(idx);
    container.appendChild(modifyBtn);

    // "삭제" 버튼
    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.textContent = "삭제";
    deleteBtn.onclick = () => deleteFile(idx);
    container.appendChild(deleteBtn);

    previewContainer.appendChild(container);
  });
}

// (D) 신규 파일 업로드 핸들러
window.handlePhotoUpload = function (event) {
  const newFiles = Array.from(event.target.files);
  const currentCount = selectedFiles.length;
  if (currentCount + newFiles.length > 5) {
    alert("사진은 5개까지 첨부 가능합니다.");
    event.target.value = "";
    return;
  }
  newFiles.forEach((file, index) => {
    if (!file.type.match("image.*")) return;
    selectedFiles.push({ file: file, fileId: null, fileUrl: "", isNew: true });
    const formData = new FormData();
    formData.append("file", file);
    const sessionIdValue = document.getElementById("sessionId")
      ? document.getElementById("sessionId").value
      : "";
    formData.append("sessionId", sessionIdValue);
    formData.append("displayOrder", (currentCount + index).toString());

    fetch("/share-files/upload", {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("파일 업로드 실패. 상태코드: " + response.status);
        }
        return response.json();
      })
      .then((data) => {
        selectedFiles[currentCount + index].fileId = data.fileId;
        selectedFiles[currentCount + index].fileUrl = data.fileUrl;
        renderPhotoPreview();
      })
  });
  event.target.value = "";
  renderPhotoPreview();
};

// (E) 개별 이미지 수정 함수 (기존 항목은 삭제하지 않고 업데이트)
function modifyFile(index) {
  const tempInput = document.createElement("input");
  tempInput.type = "file";
  tempInput.accept = "image/*";
  tempInput.style.display = "none";

  tempInput.onchange = (event) => {
    const files = event.target.files;
    if (files.length === 0) return;
    const newFile = files[0];
    const sessionIdValue = document.getElementById("sessionId")
      ? document.getElementById("sessionId").value
      : "";
    if (selectedFiles[index].fileId) {
      const oldFileId = selectedFiles[index].fileId;
      const params = new URLSearchParams();
      params.append("fileId", oldFileId);
      fetch("/share-files/delete", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params.toString(),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(
              "기존 파일 삭제 실패. 상태코드: " + response.status
            );
          }
          selectedFiles[index].file = newFile;
          selectedFiles[index].fileId = null;
          selectedFiles[index].isNew = true;
          uploadModifiedFile(index, newFile);
        })
    } else {
      selectedFiles[index].file = newFile;
      selectedFiles[index].isNew = true;
      uploadModifiedFile(index, newFile);
    }
  };

  document.body.appendChild(tempInput);
  tempInput.click();
  document.body.removeChild(tempInput);
}

// (F) 수정된 파일 업로드 함수
function uploadModifiedFile(index, file) {
  const formData = new FormData();
  formData.append("file", file);
  const sessionIdValue = document.getElementById("sessionId")
    ? document.getElementById("sessionId").value
    : "";
  formData.append("sessionId", sessionIdValue);
  formData.append("displayOrder", index.toString());

  fetch("/share-files/upload", {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("수정 파일 업로드 실패. 상태코드: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      selectedFiles[index].fileId = data.fileId;
      selectedFiles[index].fileUrl = data.fileUrl;
      renderPhotoPreview();
    })
}

// (G) 이미지 삭제 함수
function deleteFile(index) {
  const item = selectedFiles[index];
  if (item.fileId) {
    const params = new URLSearchParams();
    params.append("fileId", item.fileId);
    fetch("/share-files/delete", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params.toString(),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("파일 삭제 실패. 상태코드: " + response.status);
        }
        return response.text();
      })
      .then((result) => {
        selectedFiles.splice(index, 1);
        renderPhotoPreview();
      })
  } else {
    selectedFiles.splice(index, 1);
    renderPhotoPreview();
  }
}

// (H) 드롭다운 업데이트 및 지도/주소 관련 함수들
window.updateRegionSelect = function (callback) {
  const provinceSelect = document.getElementById("provinceId");
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedProvince = provinceSelect ? provinceSelect.value : "";
  regionSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  citySelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  if (selectedProvince) {
    fetch(`/api/location/regions?provinceId=${selectedProvince}`)
      .then((response) => response.json())
      .then((regions) => {
        regions.forEach((region) => {
          const option = document.createElement("option");
          option.value = region.id;
          option.text = region.name;
          regionSelect.appendChild(option);
        });
        if (callback) callback();
      })
  } else {
    if (callback) callback();
  }
};

window.updateCitySelect = function (callback) {
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedRegion = regionSelect ? regionSelect.value : "";
  citySelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
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
        if (callback) callback();
      })
  } else {
    if (callback) callback();
  }
};

window.updateTownSelect = function (callback) {
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedCity = citySelect ? citySelect.value : "";
  townSelect.innerHTML = '<option value="">-- 선택하세요 --</option>';
  if (selectedCity) {
    fetch(`/api/location/towns?cityId=${selectedCity}`)
      .then((response) => response.json())
      .then((towns) => {
        towns.forEach((town) => {
          const option = document.createElement("option");
          option.value = town.id;
          option.text = town.name;
          townSelect.appendChild(option);
        });
        if (callback) callback();
      })
  } else {
    if (callback) callback();
  }
};

window.validateGuestCount = function () {
  const currentGuestsInput = document.getElementById("currentGuests");
  const maxGuestsInput = document.getElementById("maxGuests");
  const currentValue = parseInt(currentGuestsInput.value);
  const maxValue = parseInt(maxGuestsInput.value);
  if (!isNaN(currentValue) && !isNaN(maxValue) && currentValue >= maxValue) {
    alert("현재 인원이 수용 가능 인원보다 많습니다.");
    currentGuestsInput.value = "";
    currentGuestsInput.focus();
  }
};

window.validateAddress = function () {
  const addressInput = document.getElementById("address");
  const regionSelect = document.getElementById("regionId");
  const townSelect = document.getElementById("townId");
  const selectedRegionText =
    regionSelect.options[regionSelect.selectedIndex]?.text || "";
  const selectedTownText =
    townSelect.options[townSelect.selectedIndex]?.text || "";
  const addressValue = addressInput.value;
  if (selectedRegionText && !addressValue.includes(selectedRegionText)) {
    alert("지역을 다시 한번 확인해주세요.");
    addressInput.value = "";
    addressInput.focus();
    document.getElementById("englishAddress").innerHTML = "";
    return false;
  }
  if (selectedTownText && !addressValue.includes(selectedTownText)) {
    alert("하위 시/군/구를 다시 한번 확인해주세요.");
    addressInput.value = "";
    addressInput.focus();
    document.getElementById("englishAddress").innerHTML = "";
    return false;
  }
  return true;
};

window.validatePrice = function () {
  const priceInput = document.getElementById("price");
  const priceValue = priceInput.value.trim();
  const pricePattern = /^[0-9]+$/;
  if (!pricePattern.test(priceValue)) {
    alert("요금은 정수(숫자)만 입력 가능합니다.");
    priceInput.focus();
    return false;
  }
  return true;
};

window.validateForm = function () {
  const titleInput = document.getElementById("title");
  const descriptionInput = document.getElementById("description");
  if (titleInput.value.trim().length < 5) {
    alert("제목은 5글자 이상 입력해주세요.");
    titleInput.focus();
    return false;
  }
  if (descriptionInput.value.trim().length < 10) {
    alert("본문은 10글자 이상 입력해주세요.");
    descriptionInput.focus();
    return false;
  }
  if (selectedFiles.length < 1) {
    alert("사진을 최소 1장 이상 첨부해주세요.");
    return false;
  }
  if (!validatePrice()) {
    return false;
  }
  if (!validateAddress()) {
    return false;
  }
  return true;
};

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
        map.setZoom(15);
        marker.setPosition(location);
      }
      let postalCode = "";
      for (let component of results[0].address_components) {
        if (component.types.includes("postal_code")) {
          postalCode = component.long_name;
          break;
        }
      }
      document.getElementById("postalCode").value = postalCode;
      fetchEnglishAddress(location.lat(), location.lng());
      isMapUpdated = true;
    } else if (status === "ZERO_RESULTS") {
      alert(
        "입력하신 주소로 검색 결과가 없습니다. 유효한 주소를 입력해 주세요."
      );
    } 
  });
}

function resetAddress() {
  const addressInput = document.getElementById("address");
  addressInput.value = "";
  addressInput.removeAttribute("readonly");
  const englishAddressElem = document.getElementById("englishAddress");
  if (englishAddressElem) {
    englishAddressElem.innerHTML = "";
  }
  addressInput.focus();
  addressModified = true;
  isMapUpdated = false;
  autocomplete = new google.maps.places.Autocomplete(addressInput, {
    componentRestrictions: { country: "JP" },
    fields: ["formatted_address", "geometry", "address_components", "name"],
    language: "ja",
  });
  autocomplete.addListener("place_changed", function () {
    if (!window.isMapReady) {
      alert("지도 초기화가 완료되지 않았습니다. 잠시 후 다시 시도해주세요.");
      setTimeout(onPlaceChanged, 500);
    } else {
      onPlaceChanged();
    }
  });
}

function initMap() {
  const defaultLocation = { lat: 35.6895, lng: 139.6917 };
  const mapContainer = document.getElementById("map");
  if (
    window.fullAddress &&
    window.fullAddress.trim() !== "" &&
    window.fullAddress !== "Default Address, Default Detail"
  ) {
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode(
      { address: window.fullAddress },
      function (results, status) {
        if (status === "OK" && results.length > 0) {
          const location = results[0].geometry.location;
          map = new google.maps.Map(mapContainer, {
            center: location,
            zoom: 13,
          });
          marker = new google.maps.Marker({
            position: location,
            map: map,
            draggable: false,
          });
        } else {
          map = new google.maps.Map(mapContainer, {
            center: defaultLocation,
            zoom: 13,
          });
          marker = new google.maps.Marker({
            position: defaultLocation,
            map: map,
            draggable: false,
          });
        }
        window.isMapReady = true;
        updateMapAndPostalCode();
        initializeAddressListeners();
      }
    );
  } else {
    map = new google.maps.Map(mapContainer, {
      center: defaultLocation,
      zoom: 13,
    });
    marker = new google.maps.Marker({
      position: defaultLocation,
      map: map,
      draggable: false,
    });
    window.isMapReady = true;
    initializeAddressListeners();
  }
}

function onPlaceChanged() {
  if (!map || !marker) {
    initMap();
    return;
  }
  const place = autocomplete.getPlace();
  if (!place.geometry) {
    alert("선택한 주소에 대한 정보를 찾을 수 없습니다. 다시 선택해 주세요.");
    return;
  }
  const location = place.geometry.location;
  map.setCenter(location);
  map.setZoom(15);
  marker.setPosition(location);
  document.getElementById("address").value = place.formatted_address;
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
    .then((response) => response.text())
    .then((text) => {
      let data;
      try {
        data = JSON.parse(text);
      } catch (e) {
        document.getElementById("englishAddress").innerHTML =
          "영문 주소 로드 실패";
        return;
      }
      if (data.status === "OK" && data.results.length > 0) {
        document.getElementById("englishAddress").innerHTML =
          data.results[0].formatted_address;
      } else {
        document.getElementById("englishAddress").innerHTML = "Not Found";
      }
    })
    .catch((error) => {
      document.getElementById("englishAddress").innerHTML =
        "영문 주소 로드 실패";
    });
}

function initializeAddressListeners() {
  const addressInput = document.getElementById("address");
  autocompleteService = new google.maps.places.AutocompleteService();
  firstKeyPressed = false;
  addressInput.addEventListener("focus", function () {
    firstKeyPressed = false;
  });
  addressInput.addEventListener("keydown", function (event) {
    if (!firstKeyPressed) {
      firstKeyPressed = true;
      autocompleteService.getPlacePredictions(
        { input: " " },
        function (predictions, status) {
          if (
            status === google.maps.places.PlacesServiceStatus.OK &&
            predictions &&
            predictions.length > 0
          ) {
            addressInput.value = predictions[0].description;
            addressInput.dispatchEvent(new Event("input", { bubbles: true }));
          }
        }
      );
    }
  });
  autocomplete = new google.maps.places.Autocomplete(addressInput, {
    componentRestrictions: { country: "JP" },
    fields: ["formatted_address", "geometry", "address_components", "name"],
    language: "ja",
  });
  autocomplete.addListener("place_changed", function () {
    if (!window.isMapReady) {
      alert("지도 초기화가 완료되지 않았습니다. 잠시 후 다시 시도해주세요.");
      setTimeout(onPlaceChanged, 500);
    } else {
      onPlaceChanged();
    }
  });
}

// (J) 초기 드롭다운 설정 함수 (DB 값으로 초기화)
window.initProvinceRegionCityTown = function () {
  const provinceSelect = document.getElementById("provinceId");
  const regionSelect = document.getElementById("regionId");
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  if (
    provinceSelect &&
    window.provinceId &&
    window.provinceId !== "0" &&
    window.provinceId !== 0
  ) {
    provinceSelect.value = window.provinceId;
  }
  window.updateRegionSelect(function () {
    if (
      regionSelect &&
      window.regionId &&
      window.regionId !== "0" &&
      window.regionId !== 0
    ) {
      regionSelect.value = window.regionId;
    }
    window.updateCitySelect(function () {
      if (
        citySelect &&
        window.cityId &&
        window.cityId !== "0" &&
        window.cityId !== 0
      ) {
        citySelect.value = window.cityId;
      }
      window.updateTownSelect(function () {
        if (
          townSelect &&
          window.townId &&
          window.townId !== "0" &&
          window.townId !== 0
        ) {
          townSelect.value = window.townId;
        }
      });
    });
  });
};

// 전역 함수 initMap을 노출
window.initMap = initMap;
