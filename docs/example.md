# Ejemplo de ejecucion de un sp

## ORACLE

1) SP

    ```sql
    CREATE OR REPLACE
    PROCEDURE prueba.sp_prueba(
        entrada IN INT,
        salida OUT VARCHAR2
    )
    IS
    BEGIN
        SELECT USER INTO salida from dual;
    END;
    ```

    Entrada

    ```json
    {
        "schema": "prueba",
        "name": "sp_prueba",
        "inParams": {
            "entrada": 1
        },
        "outParams": [
            "salida"
        ]
    }
    ```

    Salida

    ```json
    {
        "resultset": [],
        "outs": {
            "salida": "asd"
        }
    }

    ```

2) SP

    ```sql
    CREATE OR REPLACE
    PROCEDURE prueba.myprocedure(
        mycursor OUT SYS_REFCURSOR 
    )
    IS 
    BEGIN
        OPEN mycursor FOR SELECT 1, '23', 'asd' FROM dual;
    END;

    ```

    Entrada

    ```json
    {
        "schema": "prueba",
        "name": "myprocedure",
        "outParams": [
            "mycursor"
        ],
        "cursorParam": "mycursor"
    }
    ```

    Salida

    ```json
    {
        "resultset": [
            {
                "1": 1,
                "23": 23,
                "asd": "asd",
            }
        ],
        "outs": {}
    }
    ```
