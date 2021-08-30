var stompClient = null;

let headers = {Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMTAxMTExMTExMSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAxNTgzNTJ9.64-fJxu9I1T8zLCkh_1197SRNS7Gf3WlpRCInghXNzGDg3M-ydK5rMDAuhUrvu5PY1D-jQ5VU1KT618kUr3Scg"};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);

    stompClient.connect(headers, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chat/room/1', function (message) {
            console.log(message);
            showGreeting(JSON.parse(message.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/pub/chat/message", {}, JSON.stringify({'roomId':'1','sender':'test_nickname1', 'type':'ENTER', 'message': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});