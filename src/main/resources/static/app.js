var stompClient = null;

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

    let headers = {Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMTAxMTExMTExMSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAwNzQxODR9.aukE4zCSf8uCH_Bwl8vl2xC2qt0D_164YPR_-xFT-xeLIgFkjkEnVS7JTOtn3T_8w0-nYibm7K_YPxndOv3U0A"};

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
    let headers = {Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMTAxMTExMTExMSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAwNzYxNzZ9.11p5t1hfCYZw5zyrvwbSnGow-J3PIrqFB0Ov5FKng4pIQ728AEvZ415kIcxAWWDJFhaI3RcYlslROcdzU_sMaw"};

    stompClient.send("/pub/chat/message", headers, JSON.stringify({'roomId':'1','sender':'test_nickname1', 'type':'TALK', 'message': $("#name").val()}));
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