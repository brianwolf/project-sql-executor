# Sql executor

> Herramienta para ejecutar SPs de diferentes base de datos y convertirlas en una API REST

 ![alt](docs/img/java.png)

## :gear: Requisitos

* openjdk 17
* Apache Maven 3.8.4

## :package: Compilacion

```bash

# usando maven
mvn spring-boot:run

# usando java
mvn clean install
cp application.yml target/
java -jar target/sql-executor-1.0.0-SNAPSHOT
```

## :tada: Uso

1) Para levantar la aplicacion es necesario un archivo llamado **application.yml** con la configuracion de la app dejandolo en la raiz del jar, justo al lado

2) Una vez levantada la api dirigirse a [localhost:8080/docs](localhost:8080/docs) para usar la API mediante swagger

3) Ejemplos de la API REST [aca](docs/example.md)

---

## :grin: Autor

> **Brian Lobo**

* Github: [brianwolf](https://github.com/brianwolf)
* Docker Hub:  [brianwolf94](https://hub.docker.com/u/brianwolf94)
