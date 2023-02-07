# Ejemplo de ejecucion de un sp

## ORACLE

1)

### SP 1

```sql
CREATE OR REPLACE
PROCEDURE prueba.sp_prueba(
    entrada IN INT,
    salida OUT VARCHAR2
)
IS
BEGIN
    select USER INTO salida from dual;
END;
```

### Body 1

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

### Json Salida 1

```json
{
    "table": [],
    "out": {
        "salida": "asd"
    }
}

```

2)

### SP 2

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

### Body 2

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

### Json Salida 2

```json
{
    "table": [
        {
            "1": 1,
            "23": 23,
            "asd": "asd",
        }
    ],
    "out": {}
}

```
