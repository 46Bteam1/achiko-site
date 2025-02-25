// Google Maps API가 로드된 후 initMap() 실행
document.addEventListener("DOMContentLoaded", function () {
    if (typeof google === 'object' && typeof google.maps === 'object') {
        console.log("Google Maps API가 정상적으로 로드되었습니다.");
        initMap();
    } else {
        console.error("Google Maps API가 로드되지 않았습니다. API Key 확인 필요.");
    }
});

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

    // 하위 선택 박스 초기화
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
            })
            .catch(error => console.error('Error fetching regions:', error));
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
        fetch(`/api/cities?regionId=${selectedRegion}`)
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
        fetch(`/api/towns?cityId=${selectedCity}`)
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
window.initMap = function () {
    console.log("Google Maps API가 정상적으로 로드되었습니다.");

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

    // 주소 입력 필드에 Autocomplete 적용 (일본어 주소 반환)
    const addressInput = document.getElementById('address');
    autocomplete = new google.maps.places.Autocomplete(addressInput, {
        componentRestrictions: { country: "JP" }, // 일본 지역에 한정
        fields: ["formatted_address", "geometry", "address_components", "name"],
        language: "ja" // 일본어 강제 설정
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

    // 일본어 주소 설정
    document.getElementById('address').value = place.formatted_address;

    // 우편번호 추출
    let postalCode = "";
    for (let component of place.address_components) {
        if (component.types.includes("postal_code")) {
            postalCode = component.long_name;
            break;
        }
    }
    document.getElementById('postalCode').value = postalCode;

    // 영어 주소 변환
    const lat = place.geometry.location.lat();
    const lng = place.geometry.location.lng();
    fetchEnglishAddress(lat, lng);
}

function fetchEnglishAddress(lat, lng) {
    // ★ 수정: response.json() 대신 response.text()를 사용하고, 이후 JSON.parse()로 파싱
    fetch(`/api/geocode?lat=${lat}&lng=${lng}`)
        .then(response => response.text()) // ★ 변경
        .then(text => {
            let data;
            try {
                data = JSON.parse(text); // ★ JSON 문자열을 파싱
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
// 폼 제출 전 전체 유효성 검사
// --------------------------------------------------------------------
function validateForm() {
    return validateAddress();
}
