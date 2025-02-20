// WebSocket 연결
const socket = new WebSocket("ws://localhost:8080/ws-chat");

// 연결 성공시 처리
socket.onopen = function (event) {
  console.log("WebSocket 연결 성공:", event);
  // 서버에 메시지 보내기
  socket.send("안녕하세요, 서버!");
};

// 서버에서 메시지 받았을 때 처리
socket.onmessage = function (event) {
  console.log("서버에서 받은 메시지:", event.data);
};

// 연결 종료 처리
socket.onclose = function (event) {
  console.log("WebSocket 연결 종료:", event);
};

// 오류 처리
socket.onerror = function (error) {
  console.log("WebSocket 오류:", error);
};
