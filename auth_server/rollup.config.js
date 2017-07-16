import json from 'rollup-plugin-json'
import replace from 'rollup-plugin-replace'

const pkg = require('./package.json')

export default {
  banner: `// ${pkg.name} - ${pkg.version}`,
  entry: 'src/main.js',
  dest: 'dist/bundle.js',
  format: 'cjs',
  plugins: [
    json({
      exclude: 'node_modules/**',
      preferConst: true,
      indent: '  '
    }),
    replace({
      ENV: JSON.stringify(process.env.NODE_ENV || 'development'),
      PORT: Number(process.env.PORT || 3000),
      CLIENT_ID: JSON.stringify(process.env.CLIENT_ID),
      CLIENT_SECRET: JSON.stringify(process.env.CLIENT_SECRET),
    })
  ],
  external: [
    'connect-timeout', 'express', 'morgan', 'node-fetch'
  ]
}
