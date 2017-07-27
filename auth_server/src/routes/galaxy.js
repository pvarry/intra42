import fetch from 'node-fetch'
import FormData from 'form-data';

async function _auth_intra() {
  // const params = new FormData();
  // params.append('user[login]', INTRA_USERNAME);
  // params.append('user[password]', INTRA_PASSWORD);
  //
  // const res = await fetch('https://signin.intra.42.fr/users/sign_in', {
  //   method: 'POST',
  //   body: params,
  //   redirect: 'follow'
  // })
  //
  // const cookies = res.headers.get('set-cookie').split('; ').reduce((hash, cookie) => {
  //   const [key, value] = cookie.split('=')
  //   hash[key] = value
  //   return hash
  // }, {})
  //
  // global.INTRA_COOKIE = cookies['_intra_42_session_production']
}

async function _galaxy(cursus_id, campus_id, login) {
  // if (!global.INTRA_COOKIE) await _auth_intra()

  const res = await fetch(`https://projects.intra.42.fr/project_data.json?cursus_id=${cursus_id}&campus_id=${campus_id}&login=${login}`, {
    method: 'GET',
    headers: {
      'User-Agent': 'intra42/1.0 (+https://github.com/pvarry/intra42)',
      'Cookie': `_intra_42_session_production=${INTRA_COOKIE}`
    }
  })

  return res.json()
}

export default async (req, res) => {
  const cursus_id = req.query.cursus_id
  const campus_id = req.query.campus_id
  const login = req.query.login

  if (!cursus_id) {
    return res.status(400).json({
      error: 400,
      message: 'You need to specify \'cursus_id\''
    })
  } else if (!campus_id) {
    return res.status(400).json({
      error: 400,
      message: 'You need to specify \'campus_id\''
    })
  } else if (!login) {
    return res.status(400).json({
      error: 400,
      message: 'You need to specify \'login\''
    })
  }

  res.status(200).json(await _galaxy(cursus_id, campus_id, login))
}
