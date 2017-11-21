FROM node:alpine

ENV NODE_ENV production

RUN mkdir /auth_server
WORKDIR /auth_server

COPY package.json /auth_server

RUN npm install --loglevel warn

COPY . /auth_server

EXPOSE 3000

CMD ["npm", "start"]