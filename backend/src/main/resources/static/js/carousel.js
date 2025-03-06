document.addEventListener("DOMContentLoaded", function () {
  console.log("Swiper 캐러셀 모달 스크립트 실행됨");

  // 1) Swiper 캐러셀 모달 요소 선택
  const carouselModal = document.getElementById("carouselModal");
  const carouselModalClose = document.getElementById("carouselModalClose");
  const swiperWrapper = document.querySelector(".myCarousel .swiper-wrapper");
  let myCarousel = null; // Swiper 인스턴스 저장 변수

  // 2) Swiper 슬라이드 동적 업데이트 함수
  function updateCarouselSlides() {
    console.log("Swiper 슬라이드 업데이트 시작");

    // 기존 Swiper 인스턴스 제거 (중복 방지)
    if (myCarousel !== null) {
      console.log("기존 Swiper 제거 중...");
      myCarousel.destroy(true, true);
      myCarousel = null;
      console.log("기존 Swiper 제거 완료");
    }

    swiperWrapper.innerHTML = ""; // 기존 슬라이드 삭제
    const thumbnails = document.querySelectorAll(".room-photos .photo-box img");

    console.log("썸네일 개수: " + thumbnails.length);

    thumbnails.forEach((img) => {
      const slideDiv = document.createElement("div");
      slideDiv.classList.add("swiper-slide");

      const newImg = document.createElement("img");
      newImg.src = img.src;
      newImg.alt = img.alt;

      slideDiv.appendChild(newImg);
      swiperWrapper.appendChild(slideDiv);
    });

    console.log("Swiper 슬라이드 추가 완료");

    // Swiper 다시 초기화 (순환 방식 활성화)
    myCarousel = new Swiper(".myCarousel", {
      loop: true, // Loop 기능 활성화: 마지막 슬라이드에서 첫 슬라이드로 순환
      speed: 800,
      autoplay: false,
      observer: true, // DOM 변경 감지 활성화
      observeParents: true, // 부모 요소 변경 감지
      navigation: {
        nextEl: ".swiper-button-next",
        prevEl: ".swiper-button-prev",
      },
      pagination: {
        el: ".swiper-pagination",
        clickable: true,
      },
    });

    console.log("Swiper 인스턴스 재초기화 완료:", myCarousel);

    myCarousel.update(); // 업데이트 수행
    console.log("Swiper 업데이트 완료, 슬라이드 개수: " + thumbnails.length);
  }

  // 3) 썸네일 클릭 시 모달 열기
  document.querySelectorAll(".room-photos .photo-box img").forEach((img, index) => {
    img.addEventListener("click", () => {
      console.log("썸네일 " + (index + 1) + " 클릭됨");

      updateCarouselSlides(); // Swiper 슬라이드 업데이트
      carouselModal.style.display = "flex"; // 모달 표시

      setTimeout(() => {
        // loop 모드 적용 시 slideToLoop를 사용하여 올바른 슬라이드로 이동
        myCarousel.slideToLoop(index, 0);
      }, 200);
    });
  });

  // 4) 모달 닫기 이벤트
  function closeModal() {
    carouselModal.style.display = "none";
    if (myCarousel !== null) {
      myCarousel.autoplay.stop(); // 자동 재생 중지
    }
  }

  carouselModalClose.addEventListener("click", closeModal);
  window.addEventListener("click", (e) => {
    if (e.target === carouselModal) {
      closeModal();
    }
  });
});
