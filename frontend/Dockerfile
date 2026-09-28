# Étape 1 : build Angular (Angular 22 demande Node >= 22.22.3 ou 24)
FROM node:24-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --no-audit --no-fund
COPY . .
RUN npx ng build

# Étape 2 : servir les fichiers statiques avec Nginx
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
