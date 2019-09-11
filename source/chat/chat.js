var app=require('express');
var http=require('http').Server(app);
var io=require('socket.io')(http);
io.on('connection',function (socket) {

    console.log('connect user id = '+socket.id);
    socket.on('message',function (dataapp) {
        console.log(dataapp);
        socket.broadcast.emit('message',dataapp);
    });

    socket.on('disconnect',function () {
        console.log('disconnect user id = '+socket.id)
    })


});
http.listen(3000,function () {
    console.log("recive to port 3000")
});

