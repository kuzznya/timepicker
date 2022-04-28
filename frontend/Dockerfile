FROM node:17-alpine as build
COPY package.json yarn.lock babel.config.js ./
RUN yarn install --frozen-lockfile
COPY public public
COPY src src
RUN yarn build

FROM node:17-alpine
RUN yarn add express
COPY server.js .
COPY --from=build /dist /dist
ENV PORT 8080
# using @ as delimiter instead of / because URL contains / too
ENTRYPOINT sed -i "s@API_PATH@$API_PATH@" /dist/js/app*.js && node server.js
