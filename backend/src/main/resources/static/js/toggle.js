$(document).ready(function () {
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
