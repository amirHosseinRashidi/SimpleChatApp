
const express = require('express')
const http = require('http').Server(express)
const io = require('socket.io')(http)
const User = require('../models/User')
const Message = require('../models/Message')
const app = express()

////////////// MONGO DB ///////////////
const mongo = require('mongoose');
mongo.connect('mongodb://localhost:27017/local', { useNewUrlParser: true });
mongo.Promise=global.Promise;
let db = mongo.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));
///////////////////////////////////////

const mongoose = require('mongoose')

io.on('connection',function(socket){
    console.log('User connected with id '+socket.id)

    socket.on('user-joined',function(user){
        console.log('joined ' +user.user_name);
        socket.broadcast.emit('user-joined',user)
        User.insertMany(user)
        socket.on('typing',function(user){
            socket.broadcast.emit('typing',user)
        })
        
        socket.on('message',function(data){
            console.log(data)
            socket.broadcast.emit('message',data)
            Message.insertMany(data, function(err, res){
                if(err)console.log(err)
                else console.log(res)
            })
        })
     
        socket.on('user-disconnected',function(){
            console.log('disconnected user :' +socket.id)
            socket.broadcast.emit('user-disconnected',user)
        })

    })
})


http.listen(3000,function(){
    console.log('receiving messages!')
})


app.post('/read-messages',(req,res)=> {
    
    (async ()=>{
        res.send(await readDataMessages())
    })
    ()

       })

       app.listen(8080, () => {
        console.log("We are Listening")
    })

async function readDataMessages(){
 var messages = await Message.find({})
 console.log(messages)
 return messages;   
}
