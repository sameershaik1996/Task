FROM java:8
EXPOSE 8080
ADD /target/orderservice.jar orderservice.jar
ENTRYPOINT ["java","-jar","orderservice.jar"]