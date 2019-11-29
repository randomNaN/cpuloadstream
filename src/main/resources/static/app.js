var stompClient = null;

function connect() {
    var sock = new SockJS('/websock');
    stompClient = Stomp.over(sock);
    stompClient.connect({}, function (params) {
        console.log("Connected");
        stompClient.subscribe('/exchange/amq.fanout', function(data) {
            console.log("Subscription is called");
            if (data) {
                alert("Cpu load limit reached");
            }
        });        
    });
}

$(function () {
    connect();   
});