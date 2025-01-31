# spa-multi-tenant-demo

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Customize configuration

See [Vite Configuration Reference](https://vitejs.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev -- --port 8888
```

### Compile and Minify for Production

```sh
npm run build
```

### Test in Docker
```
dos2unix  *
docker build -t f01 .
docker run --net=host -it -d --env-file dockerenv.iisi --name f01 f01:latest

```
see https://localhost:1443/  or  https://dukes.rock:1443/

#### in SIT
* Using HTTPS
```bash
docker run --net=host -it -d -v .\nginx.sit:/etc/nginx/nginx.conf --env-file dockerenv.iisi --name f01 f01:latest
```
* Using HTTP
```bash
docker run --net=host -it -d -v ./nginx.sit:/etc/nginx/nginx.conf --env-file dockerenv.iisi-2 --name f01 f01:latest
```

### Test multi-tenant
```
http://dukes.rock:3000/
```

