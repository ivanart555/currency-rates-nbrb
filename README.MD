# Currency rates - NBRB

## Описание

Currency rates - NBRB - это Java проект для получения курсов валют из внешнего API(сайт Национального банка РБ) и сохранения их в локальную базу данных H2. Проект предоставляет два эндпоинта: один для загрузки курсов на определенную дату из банка, и другой для получения курса валюты из локальной базы данных по дате и коду валюты, переданным пользователем.

## Требования

- Java 17 или выше

## Установка

### Ссылка на Docker Hub

Образ доступен на [Docker Hub](https://hub.docker.com/r/ivanart555/currency-rates-nbrb).

Запускать на порту 8080

ИЛИ

1. Склонируйте репозиторий:
    ```sh
    git clone https://github.com/ivanart555/currency-rates-nbrb.git
    cd currency-rates-nbrb
    ```

    - Параметры подключения к базе данных H2 уже настроены в файле `src/main/resources/application.properties`.

    ```properties
    spring.datasource.url=jdbc:h2:mem:currency_rates_db
    spring.datasource.driverClassName=org.h2.Driver
    spring.datasource.username=root
    spring.datasource.password=
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.h2.console.enabled=true
    spring.h2.console.path=/h2-console
    spring.jpa.hibernate.ddl-auto=create-drop
    ```

2. Соберите и запустите проект:
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```

## Использование

### Обновление курсов валют

**Endpoint:** `/api/v1/rates/update`

**Метод:** `GET`

**Параметры:**
- `date` (required): Дата для обновления курсов в формате `YYYY-MM-DD`.

**Пример запроса:**
```sh
curl -X GET "http://localhost:8080/api/v1/rates/update?date=2024-06-29"
```

Ответ:
```
Currency rates loaded successfully for date: 2024-06-29
```

### Получение курса валюты

**Endpoint:** /api/v1/rates/

**Метод:** `GET`

**Параметры:**
- `date` (required): Дата для получения курса в формате `YYYY-MM-DD`.
- `currencyCode` (required): Код валюты (3 цифры).

**Пример запроса:**

```sh
curl -X GET "http://localhost:8080/api/v1/rates/?date=2024-06-29&currencyCode=643"
```

Ответ:

```
{
  "Cur_ID": 456,
  "Date": "2024-06-29",
  "Cur_OfficialRate": 3.68,
  "Cur_Scale": 100,
  "Cur_Abbreviation": "RUB",
  "Cur_Name": "Российский рубль"
}
```

**Swagger**

Для доступа к Swagger документации используйте следующий URL:
```
http://localhost:8080/swagger-ui/index.html
```

**Консоль H2**

Для доступа к консоли H2 используйте следующий URL:

```
http://localhost:8080/h2-console
```

```
JDBC URL: jdbc:h2:mem:currency_rates_db
User Name: root
Password: (оставьте пустым)
```