const mongoose = require('mongoose')

var MessageSchema = mongoose.Schema({
    text:String,
    user_name:String,
    image:String,
    date:Number
})

module.exports= mongoose.model('Messages',MessageSchema,'Messages')

