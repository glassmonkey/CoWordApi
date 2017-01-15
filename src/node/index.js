/* 1. expressモジュールをロードし、インスタンス化してappに代入。*/
var express = require("express");
var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var upload = multer();
var app = express();
app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

/* 2. listen()メソッドを実行して3000番ポートで待ち受け。*/
var server = app.listen(3000, function(){
    console.log("Node.js is listening to PORT:" + server.address().port);
});


// ツイッターデータポスト用
app.post("/api/twitter/", function(req, res, next){
    console.log(req.body);
    res.json(req.body);
});

// ツイッターデータget用
app.get("/api/twitter/", function(req, res, next){
    res.send('Admin Homepage');
});
