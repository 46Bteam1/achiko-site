$(document).ready(function () {
  getAllTests(); // 페이지 로딩 시 전체 데이터 조회
});

// 전체 데이터 조회 (RESTful API)
function getAllTests() {
  $.ajax({
    url: "/api/test/all", // API 경로 변경
    type: "GET",
    success: function (data) {
      let tableBody = $("#dataList");
      tableBody.empty();
      data.forEach(function (item) {
        tableBody.append(`
          <tr>
            <td>${item.id}</td>
            <td>${item.boolValue}</td>
            <td>${item.tinyintValue}</td>
            <td>${item.varcharValue}</td>
            <td>${item.charValue}</td>
            <td>${item.textValue}</td>
            <td>${item.decimalValue}</td>
            <td>${item.datetimeValue}</td>
            <td>${item.timestampValue}</td>
          </tr>
        `);
      });
    },
  });
}

// 단일 데이터 조회 (RESTful API)
function getTestById() {
  let id = $("#searchId").val();
  if (!id) {
    alert("ID를 입력하세요!");
    return;
  }

  $.ajax({
    url: `/api/test/${id}`, // API 경로 변경
    type: "GET",
    success: function (item) {
      alert(`조회가 잘됩니다.\n ID ${item.id} 데이터: ${JSON.stringify(item)}`);
    },
    error: function () {
      alert("해당 ID의 데이터를 찾을 수 없습니다.");
    },
  });
}

// 데이터 추가 (POST)
function createTest() {
  let data = collectFormData();

  $.ajax({
    url: "/api/test", // API 경로 변경
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function () {
      alert("데이터 추가 성공!");
      getAllTests();
    },
  });
}

// 데이터 전체 업데이트 (PUT)
function updateTest() {
  let id = $("#id").val();
  if (!id) {
    alert("수정할 ID를 입력하세요!");
    return;
  }

  let data = collectFormData();

  $.ajax({
    url: `/api/test/${id}`, // API 경로 변경
    type: "PUT",
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function () {
      alert("데이터 전체 수정 성공!");
      getAllTests();
    },
  });
}

// 데이터 일부 업데이트 (PATCH)
function patchTest() {
  let id = $("#id").val();
  if (!id) {
    alert("수정할 ID를 입력하세요!");
    return;
  }

  let data = collectFormData();

  $.ajax({
    url: `/api/test/${id}`, // API 경로 변경
    type: "PATCH",
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function () {
      alert("데이터 부분 수정 성공!");
      getAllTests();
    },
  });
}

// 데이터 삭제
function deleteTest() {
  let id = $("#id").val();
  if (!id) {
    alert("삭제할 ID를 입력하세요!");
    return;
  }

  $.ajax({
    url: `/api/test/${id}`, // API 경로 변경
    type: "DELETE",
    success: function () {
      alert("데이터 삭제 성공!");
      getAllTests();
    },
  });
}

// 폼 데이터 수집 함수
function collectFormData() {
  return {
    id: $("#id").val() ? parseInt($("#id").val()) : null,
    boolValue: $("#boolValue").prop("checked"),
    tinyintValue: $("#tinyintValue").val()
      ? parseInt($("#tinyintValue").val())
      : null,
    varcharValue: $("#varcharValue").val(),
    charValue: $("#charValue").val(),
    textValue: $("#textValue").val(),
    decimalValue: $("#decimalValue").val()
      ? parseFloat($("#decimalValue").val())
      : null,
    datetimeValue: $("#datetimeValue").val()
      ? new Date($("#datetimeValue").val()).toISOString()
      : null,
    timestampValue: $("#timestampValue").val()
      ? new Date($("#timestampValue").val()).toISOString()
      : null,
  };
}
