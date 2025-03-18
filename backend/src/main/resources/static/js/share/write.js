document.addEventListener("DOMContentLoaded", function () {
  // header 관련
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

  // (1) 구글 맵 초기화 검사
  if (typeof google === "object" && typeof google.maps === "object") {
    console.log("Google Maps API가 정상적으로 로드되었습니다.");
    initMap();
  } else {
    console.error("Google Maps API가 로드되지 않았습니다. API Key 확인 필요.");
  }

  // (2) 방 사진 영역을 드래그로 스크롤할 수 있게 만드는 코드
  //     .room-photos 요소에 마우스 다운/업/무브 이벤트를 걸어준다.
  const roomPhotos = document.querySelector(".room-photos");
  if (roomPhotos) {
    let isDown = false;
    let startX;
    let scrollLeft;

    // 마우스 눌렀을 때
    roomPhotos.addEventListener("mousedown", (e) => {
      isDown = true;
      roomPhotos.classList.add("active");
      startX = e.pageX - roomPhotos.offsetLeft;
      scrollLeft = roomPhotos.scrollLeft;
      // 누른 상태에서 손모양
      roomPhotos.style.cursor = "grabbing";
    });

    // 마우스가 영역 밖으로 나가거나 뗐을 때
    roomPhotos.addEventListener("mouseleave", () => {
      isDown = false;
      roomPhotos.classList.remove("active");
      roomPhotos.style.cursor = "grab";
    });
    roomPhotos.addEventListener("mouseup", () => {
      isDown = false;
      roomPhotos.classList.remove("active");
      roomPhotos.style.cursor = "grab";
    });

    // 드래그 중에 이동
    roomPhotos.addEventListener("mousemove", (e) => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - roomPhotos.offsetLeft;
      const walk = (x - startX) * 1; // 드래그 감도
      roomPhotos.scrollLeft = scrollLeft - walk;
    });
  }
});

// 전역 변수 선언
let selectedFiles = []; // { file: File, fileId: number|null }
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

  // 하위 선택 박스 초기화
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
      })
      .catch((error) => console.error("Error fetching cities:", error));
  }
}

function updateTownSelect() {
  const citySelect = document.getElementById("cityId");
  const townSelect = document.getElementById("townId");
  const selectedCity = citySelect.value;

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
      })
      .catch((error) => console.error("Error fetching towns:", error));
  }
}

// --------------------------------------------------------------------
// Google Maps API 관련 기능 구현
// --------------------------------------------------------------------
window.initMap = function () {
  console.log("Google Maps API가 정상적으로 로드되었습니다.");

  // 기본 중심 위치 (도쿄)
  const defaultLocation = { lat: 35.6895, lng: 139.6917 };

  // 지도 생성
  map = new google.maps.Map(document.getElementById("map"), {
    center: defaultLocation,
    zoom: 13,
  });

  // 마커 생성 (기본 위치 표시)
  marker = new google.maps.Marker({
    position: defaultLocation,
    map: map,
    draggable: false,
  });

  // 주소 입력 필드에 Autocomplete 적용 (일본어 주소 반환)
  const addressInput = document.getElementById("address");
  autocomplete = new google.maps.places.Autocomplete(addressInput, {
    componentRestrictions: { country: "JP" },
    fields: ["formatted_address", "geometry", "address_components", "name"],
    language: "ja",
  });

  // 주소 자동완성 선택 시 이벤트 처리
  autocomplete.addListener("place_changed", onPlaceChanged);
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

  // 일본어 주소 설정
  document.getElementById("address").value = place.formatted_address;

  // 우편번호 추출
  let postalCode = "";
  for (let component of place.address_components) {
    if (component.types.includes("postal_code")) {
      postalCode = component.long_name;
      break;
    }
  }
  document.getElementById("postalCode").value = postalCode;

  // 영어 주소 변환
  const lat = place.geometry.location.lat();
  const lng = place.geometry.location.lng();
  fetchEnglishAddress(lat, lng);
}

function fetchEnglishAddress(lat, lng) {
  // response.json() 대신 response.text() 후 JSON.parse()
  fetch(`/api/geocode?lat=${lat}&lng=${lng}`)
    .then((response) => response.text())
    .then((text) => {
      let data;
      try {
        data = JSON.parse(text);
      } catch (e) {
        console.error("JSON 파싱 오류:", e);
        document.getElementById("englishAddress").innerHTML =
          "영문 주소 로드 실패";
        return;
      }
      console.log("Geocode API 응답:", data);
      if (data.status === "OK" && data.results.length > 0) {
        document.getElementById("englishAddress").innerHTML =
          data.results[0].formatted_address;
      } else {
        document.getElementById("englishAddress").innerHTML = "Not Found";
      }
    })
    .catch((error) => {
      console.error("Error fetching English address:", error);
      document.getElementById("englishAddress").innerHTML =
        "영문 주소 로드 실패";
    });
}

// --------------------------------------------------------------------
// 파일 미리보기 및 누적 추가 기능 (수정 및 삭제 기능 추가)
// --------------------------------------------------------------------

// 미리보기 영역을 업데이트하는 함수
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
    const reader = new FileReader();

    reader.onload = function (e) {
      img.src = e.target.result;
    };
    reader.readAsDataURL(item.file);
    img.style.width = "150px";
    img.style.display = "block";
    img.style.marginBottom = "5px";
    container.appendChild(img);

    // 순번 표시
    const fileNumberDiv = document.createElement("div");
    fileNumberDiv.textContent = `${idx + 1}`;
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

// 기존 파일 업로드 핸들러 (여러 파일 추가)
// 변경된 사항: 파일 선택 시 총 첨부 개수가 5개를 초과하면 바로 alert를 띄우고 추가 업로드를 중단함
function handlePhotoUpload(event) {
  const newFiles = Array.from(event.target.files);
  const currentCount = selectedFiles.length;

  if (currentCount + newFiles.length > 5) {
    alert("사진은 5개까지 첨부 가능합니다.");
    event.target.value = "";
    return;
  }

  newFiles.forEach((file, index) => {
    if (!file.type.match("image.*")) {
      return;
    }
    selectedFiles.push({ file: file, fileId: null });

    const formData = new FormData();
    formData.append("file", file);
    const sessionIdValue = document.getElementById("sessionId").value;
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
        console.log("파일 업로드 성공:", data);
        selectedFiles[currentCount + index].fileId = data.fileId;
        renderPhotoPreview();
      })
      .catch((err) => {
        console.error("파일 업로드 에러:", err);
      });
  });
  event.target.value = "";
  renderPhotoPreview();
}

// 수정 기능: 숨겨진 파일 입력을 통해 대체 파일 선택 후 업로드
function modifyFile(index) {
  const tempInput = document.createElement("input");
  tempInput.type = "file";
  tempInput.accept = "image/*";
  tempInput.style.display = "none";

  tempInput.onchange = (event) => {
    const files = event.target.files;
    if (files.length === 0) return;
    const newFile = files[0];

    // 기존 파일이 있다면 삭제 후 새 파일 업로드
    if (selectedFiles[index].fileId) {
      const oldFileId = selectedFiles[index].fileId;
      const params = new URLSearchParams();
      params.append("fileId", oldFileId);
      fetch("/share-files/delete", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: params.toString(),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(
              "기존 파일 삭제 실패. 상태코드: " + response.status
            );
          }
          console.log("기존 파일 삭제 성공");
          selectedFiles[index].file = newFile;
          selectedFiles[index].fileId = null;
          uploadModifiedFile(index, newFile);
        })
        .catch((err) => {
          console.error("기존 파일 삭제 에러:", err);
        });
    } else {
      selectedFiles[index].file = newFile;
      selectedFiles[index].fileId = null;
      uploadModifiedFile(index, newFile);
    }
  };

  document.body.appendChild(tempInput);
  tempInput.click();
  tempInput.remove();
}

function uploadModifiedFile(index, file) {
  const formData = new FormData();
  formData.append("file", file);
  const sessionIdValue = document.getElementById("sessionId").value;
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
      console.log("수정 파일 업로드 성공:", data);
      selectedFiles[index].fileId = data.fileId;
      renderPhotoPreview();
    })
    .catch((err) => {
      console.error("수정 파일 업로드 에러:", err);
    });
}

// 삭제 기능: 해당 파일을 배열에서 제거하고 서버에 삭제 요청
function deleteFile(index) {
  const item = selectedFiles[index];
  if (item.fileId) {
    const params = new URLSearchParams();
    params.append("fileId", item.fileId);
    fetch("/share-files/delete", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: params.toString(),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("파일 삭제 실패. 상태코드: " + response.status);
        }
        return response.text();
      })
      .then((result) => {
        console.log("파일 삭제 성공:", result);
        selectedFiles.splice(index, 1);
        renderPhotoPreview();
      })
      .catch((err) => {
        console.error("파일 삭제 에러:", err);
      });
  } else {
    selectedFiles.splice(index, 1);
    renderPhotoPreview();
  }
}

// --------------------------------------------------------------------
// 현재 인원 유효성 검사
// --------------------------------------------------------------------
function validateGuestCount() {
  const currentGuestsInput = document.getElementById("currentGuests");
  const maxGuestsInput = document.getElementById("maxGuests");
  const currentValue = parseInt(currentGuestsInput.value);
  const maxValue = parseInt(maxGuestsInput.value);

  if (!isNaN(currentValue) && !isNaN(maxValue) && currentValue >= maxValue) {
    alert("현재 인원이 수용 가능 인원보다 많습니다.");
    currentGuestsInput.value = "";
    currentGuestsInput.focus();
  }
}

// --------------------------------------------------------------------
// 주소 유효성 검사: address에 선택된 region과 town의 이름이 포함되어야 함
// (미포함 시 주소와 영어 주소 영역 초기화)
// --------------------------------------------------------------------
function validateAddress() {
  const addressInput = document.getElementById("address");
  const regionSelect = document.getElementById("regionId");
  const townSelect = document.getElementById("townId");

  // regionSelect의 선택된 옵션 텍스트
  const selectedRegionText =
    regionSelect.options[regionSelect.selectedIndex]?.text || "";
  // townSelect의 선택된 옵션 텍스트
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
}

// --------------------------------------------------------------------
// 요금 입력 유효성 검사: 정수만 허용 (숫자만 입력 가능)
// --------------------------------------------------------------------
function validatePrice() {
  const priceInput = document.getElementById("price");
  const priceValue = priceInput.value.trim();
  // 정규식: 하나 이상의 숫자만 허용 (소수점 미포함)
  const pricePattern = /^[0-9]+$/;

  if (!pricePattern.test(priceValue)) {
    alert("요금은 정수(숫자)만 입력 가능합니다.");
    priceInput.focus();
    return false;
  }
  return true;
}

// --------------------------------------------------------------------
// 폼 제출 전 전체 유효성 검사: 제목, 본문, 주소, 사진
// --------------------------------------------------------------------
function validateForm() {
  const canRegistShare = document.getElementById("canRegistShare");
  if (!canRegistShare) {
    alert("이미 진행 중인 숙소가 등록되어있습니다.");
    return false;
  }

  const titleInput = document.getElementById("title");
  const descriptionInput = document.getElementById("description");

  // 제목은 5글자 이상
  if (titleInput.value.trim().length < 5) {
    alert("제목은 5글자 이상 입력해주세요.");
    titleInput.focus();
    return false;
  }

  // 본문은 10글자 이상
  if (descriptionInput.value.trim().length < 10) {
    alert("본문은 10글자 이상 입력해주세요.");
    descriptionInput.focus();
    return false;
  }

  // 요금 정수 체크
  if (!validatePrice()) {
    return false;
  }

  // 주소 검사
  if (!validateAddress()) {
    return false;
  }

  // 사진 첨부 유효성 검사 (이제 파일 선택 시 미리 체크하므로 여기서는 생략 가능)
  if (selectedFiles.length < 1) {
    alert("사진을 최소 1장 이상 첨부해주세요.");
    return false;
  }

  return true;
}
