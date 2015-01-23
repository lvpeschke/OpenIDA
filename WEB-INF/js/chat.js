$(document).ready(function() {
    // connect the socket
    var connection = new WebSocket('ws://' + document.location.host + '/');

    connection.onopen = function() {
        console.log('Connection open!');
    }
    connection.onmessage = function(msg) {
        if (msg.data !== 'keep-alive') {
            $(".conversation").append(msg.data);
            $('.conversation-window').scrollTop($('.conversation-window')[0].scrollHeight);
        }
    }
    connection.onclose = function() {
        console.log('Connection closed!');
    }

    // wire up the button
    $('.message').keypress(function(event) {
        if (event.keyCode == '13') {
        if ($('.message').val() != '') {
            try {
                connection.send($('.message').val());
                $('.message').val('');
            } catch (exception) {
                console.log('Websocket error: ' + exception.message);
            }
        }
    }
    });
});