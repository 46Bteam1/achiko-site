$(document).ready(function () {
  $(window).on("scroll", function () {
    if ($(window).scrollTop() > 0) {
      $("header").addClass("sticky");
    } else {
      $("header").removeClass("sticky");
    }
  });
});
