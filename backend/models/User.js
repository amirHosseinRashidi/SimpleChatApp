const mongoose = require('mongoose')
var UserSchema = mongoose.Schema({
    userName: String,
    userPass:String
})

module.exports = mongoose.model('Users',UserSchema,'Users')