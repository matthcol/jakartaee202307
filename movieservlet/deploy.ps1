mvn clean package
docker stop movieservlet
docker rm movieservlet
docker image rm movieservlet:1.0
docker build -t movieservlet:1.0 .
docker run -d -p 8080:8080 --name movieservlet movieservlet:1.0
