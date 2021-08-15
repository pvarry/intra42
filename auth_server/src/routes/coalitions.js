import fetch from '../intra_auth'

async function _coalitions(id) {
  const res = await fetch(`https://profile.intra.42.fr/blocs/${id}/stats`)

  return [res.status, await res.json()]
}

export default async (req, res) => {
  const id = req.query.blocs_id

  if (!id) {
    return res.status(400).json({
      error: 400,
      message: 'You need to specify \'blocs_id\''
    })
  }

  const [status, coalition] = await _coalitions(id)

  res.status(status).json(coalition)
}
