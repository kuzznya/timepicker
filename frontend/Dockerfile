FROM node:16-alpine as build
COPY package.json yarn.lock babel.config.js ./
RUN yarn install --frozen-lockfile
COPY public public
COPY src src
RUN yarn build

FROM node:16-alpine
RUN yarn add express
COPY server.js .
COPY --from=build /dist /dist
ENV PORT 8080
# using @ as delimiter instead of / because URL contains / too
ENTRYPOINT sed -i "s@ENV_API_PATH@$ENV_API_PATH@" /dist/js/app*.js &&
  sed -i "s@ENV_AUTH_PATH@$ENV_AUTH_PATH@" /dist/js/app*.js &&
  node server.js
