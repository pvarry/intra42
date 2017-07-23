import fetch from 'node-fetch'

function _oauth_token(code, redirect_uri) {
  return fetch('https://api.intra.42.fr/oauth/token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      'code': code,
      'client_id': 'CLIENT_ID',
      'client_secret': 'CLIENT_SECRET',
      'redirect_uri': redirect_uri,
      'grant_type': 'authorization_code'
    })
  }).then(res => {
    return res.json()
  })
}

export default (req, res) => {
  const code = req.query.code
  const redirect_uri = req.query.redirect_uri || 'com.paulvarry.intra42://oauth2redirect'

  if (!code) {
    return res.status(400).json({
      error: 400,
      message: 'You need to specify \'code\''
    })
  }

  _oauth_token(code, redirect_uri).then(json => {
    if (json.error) {
      res.status(401).json({
        error: 401,
        message: json.error_description
      })
    } else {
      res.status(200).json(json)
    }
  })
}
