import fetch from '../intra_auth'

async function _galaxy(cursus_id, campus_id, login) {
  const res = await fetch(`https://projects.intra.42.fr/project_data.json?cursus_id=${cursus_id}&campus_id=${campus_id}&login=${login}`)

  return [res.status, await res.json()]
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

  const [status, galaxy] = await _galaxy(cursus_id, campus_id, login)

  res.status(status).json(galaxy)
}
