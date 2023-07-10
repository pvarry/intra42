import fetch from 'node-fetch'

async function _oauth_token(code, redirect_uri) {
  const response = await fetch('https://api.intra.42.fr/oauth/token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      'code': code,
      'client_id': CLIENT_ID,
      'client_secret': CLIENT_SECRET,
      'redirect_uri': redirect_uri,
      'grant_type': 'authorization_code'
    })
  })

  return response.json()
}

export default async (req, res) => {
  const client_id = req.params.uid
  const code = req.query.code
  const redirect_uri = req.query.redirect_uri || 'com.paulvarry.intra42://oauth2redirect'

  if (!code || client_id !== CLIENT_ID) {
    return res.status(400).json({
      error: 400,
      message: 'You need to specify \'code\''
    })
  }

  const json = await _oauth_token(code, redirect_uri)

  if (json.error) {
    res.status(401).json({
      error: 'invalid grant'
    })
  } else {
    res.status(200).json(json)
  }
}
