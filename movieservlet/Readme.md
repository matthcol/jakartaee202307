
## Build Tomcat image with webapp
docker build -t movieservlet:1.0 .

## Manage image
docker image ls

docker image rm movieservlet:1.0

## Run container
docker run -d -p 8080:8080 --name movieservlet movieservlet:1.0

## Manage container
docker ps -a

docker logs movieservlet

docker stop|start|rm movieservlet

docker exec -it movieservlet bash
