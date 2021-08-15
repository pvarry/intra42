import fetch from 'node-fetch'
import FormData from 'form-data';

function _parse_cookies(cookies) {
  return cookies.reduce((hash, cookie) => {
    const [key, value] = cookie.split(';')[0].split('=')
    hash[key] = value
    return hash
  }, {})
}

async function _intra_auth() {
  const csrf_res = await fetch('https://signin.intra.42.fr/users/sign_in', {
    method: 'GET',
    headers: {
      'User-Agent': 'intra42/1.0 (+https://github.com/pvarry/intra42)',
    }
  })

  const csrf_body = await csrf_res.text()
  const csrf_param = csrf_body.match(/<meta.*?name=["']csrf-param["'].*?content=["'](.+)["']/)[1]
  const csrf_token = csrf_body.match(/<meta.*?name=["']csrf-token["'].*?content=["'](.+)["']/)[1]
  const csrf_cookies = _parse_cookies(csrf_res.headers.raw()['set-cookie'])

  const params = new FormData();
  params.append('user[login]', INTRA_USERNAME);
  params.append('user[password]', INTRA_PASSWORD);
  params.append(csrf_param, csrf_token);

  const signin_res = await fetch('https://signin.intra.42.fr/users/sign_in', {
    method: 'POST',
    body: params,
    headers: {
      'User-Agent': 'intra42/1.0 (+https://github.com/pvarry/intra42)',
      'Cookie': `_intra_42_session_production=${csrf_cookies['_intra_42_session_production']}`
    },
    redirect: 'nofollow'
  })

  const signin_cookies = _parse_cookies(signin_res.headers.raw()['set-cookie'])

  if (signin_res.status != 302) return
  if (signin_res.headers.get('location').search('/intra_otp_sessions/new') != -1) return console.log('OTP is required')

  if (signin_res.headers.get('location').search('profile.intra.42.fr') != -1) global.INTRA_COOKIE = signin_cookies['_intra_42_session_production']
}

async function _get(url, opts = {}) {
  return await fetch(url, {
    ...opts,
    headers: {
      'User-Agent': 'intra42/1.0 (+https://github.com/pvarry/intra42)',
      'Cookie': `_intra_42_session_production=${global.INTRA_COOKIE}`,
      ...opts['headers']
    }
  })
}

export default async (url, opts = {}) => {
  if (!global.INTRA_COOKIE) await _intra_auth()

  let res = await _get(url, opts)

  if (res.status == 401) {
    _intra_auth()

    res = await _get(url, opts)
  }

  return res
}
