# Configuracion de application properties 

## Configuraciones del proyecto

Se puede ver que se toman variables de ambiente para configurar el archivo en caso de que se corra en docker por ejemplo

```yaml
project:
  version: ${VERSION:local}

server:
  port: ${PORT:8080}

logging:
  file: 
    name: ${LOG_PATH:/tmp/java.log}
  level:
    org:
      springframework: ${LOG_LEVEL:INFO}

```

## Configuracion de los datasources

La variable **name** es elegido por quien use la herramienta y luego se usa cuando se quiera ejecutar un SP

```yaml
datasources:

    # todos los parametros
  - name: postgres
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
    driverClassName: org.postgresql.Driver
    initialSize: 1
    maxTotal: 2
    maxIdle: 2

    # parametros basicos
  - name: oracle
    url: jdbc:oracle:thin:@localhost:1521/oracle
    username: system
    password: oracle

```

Dentro de la api rest existe un endpoint para recargar estos datasources en runtime de ser necesario. El endpoint en cuestion es [localhost:8080/api/v1/datasources](localhost:8080/api/v1/datasources), el metodo es un GET y devuelve los datasources que se pudieron conectar de forma exitosa, en caso de error con sultar los logs