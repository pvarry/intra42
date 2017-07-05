FROM node:alpine

ENV NODE_ENV production

RUN mkdir /auth_server
WORKDIR /auth_server

COPY package.json package-lock.json

RUN npm install --loglevel warn

COPY .

EXPOSE 3000

RUN ["npm", "start"]