const express = require('express')
var request = require('request');
var morgan = require('morgan')

var port = process.env.PORT;
if (!port) {
    port = 3000;
}
var client_id = process.env.CLIENT_ID;
var client_secret = process.env.CLIENT_SECRET;
if (!client_id || !client_secret) {
    console.log("Please specify env : 'CLIENT_ID' and 'CLIENT_SECRET'")
    return
}


/*** SERVER ***/

const app = express()

app.use(morgan('combined'))

app.get('/', function (req, res) {
    res.send('Hello World!')
})

app.get('/auth', auth)

app.get('/galaxy', galaxy)

// catch 404 and forward to error handler
app.use(function (req, res) {
    res.status(404)
        .send();
});

app.listen(port, function () {
    console.log(`Listening on port ${port}!`)
})










function auth(req, res) {
    res.setHeader('Content-Type', 'application/json');

    var code = req.query.code;
    var redirect_uri = req.query.redirect_uri;

    if (code === undefined || code == null) {
        res.status(400).send("You need to specify 'code'")
        return
    }

     if (!redirect_uri) {
        redirect_uri= "http://localhost:3000/auth"
    }

    var json = {
        'code': code,
        'client_id': client_id,
        'client_secret': client_secret,
        'redirect_uri': redirect_uri,
        'grant_type': 'authorization_code'
    }
    // Set the headers
    var headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    // Configure the request
    var options = {
        url: 'https://api.intra.42.fr/oauth/token',
        method: 'POST',
        headers: headers,
        form: json
    }

    // Start the request
    request(options, function (error, response, body) {
        if (!error) {
            // Print out the response body
            res.send(body)
        } else {
            res.status(400)
                .send(error);
            console.log(error)
        }
    })
}

function galaxy(req, res) {
    res.status(200).send("Soon")
}