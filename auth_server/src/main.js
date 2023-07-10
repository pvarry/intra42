import express from 'express'
import morgan from 'morgan'
import timeout from 'connect-timeout'
import route_root from './routes/root'
import route_auth from './routes/auth'
import route_galaxy from './routes/galaxy'
import route_coalitions from './routes/coalitions'

if (!CLIENT_ID || !CLIENT_SECRET) {
  console.log('Please specify env : \'CLIENT ID\' and \'CLIENT SECRET\'')
  process.exit(1)
}

/*** SERVER ***/

const app = express()
app.set('trust proxy', true)
app.use(morgan('combined'))
app.use(timeout('30s'))
app.disable('x-powered-by')

app.get('*', (req, res, next) => {
  res.header('Content-Type', 'application/json')
  next()
})

app.get('/', route_root)
app.get('/oauth/authorize/:uid', route_auth)
app.get('/galaxy', route_galaxy)
app.get('/coalitions', route_coalitions)

// catch 404 and forward to error handler
app.use((req, res) => {
  res.status(404).json({
    error: 404,
    message: 'Not Found'
  })
})

// catch 505 and forward to error handler
app.use(function (err, req, res, next) {
  if (req.timedout) {
    res.status(504).json({
      error: 504,
      message: 'Gateway Timeout'
    })
  } else {
    res.status(500).json({
      error: 500,
      message: 'Internal Server Error'
    })
  }
})

app.listen(PORT, () => {
  console.log(`Listening on port ${PORT}!`)
})
