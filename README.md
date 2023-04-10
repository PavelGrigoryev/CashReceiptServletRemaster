# Cash Receipt Servlet Remaster

## Автор: [Grigoryev Pavel](https://pavelgrigoryev.github.io/GrigoryevPavel/)

### Оригинальный проект находится по ссылке [CashReceipt](https://github.com/PavelGrigoryev/CashReceipt)

### Разница ремастера и оригинала:

* Java 17
* Gradle 7.6
* Spring-boot 2.7.6 -> заменён на jakarta-servlet-api 5.0.0
* Spring-boot-starter-data-jpa -> заменён на чистый jdbc
* Spring-boot-starter-web (Apache Tomcat) -> приложение развёртывается на Apache Tomcat 10.1
* Lombok
* Mapstruct -> заменён на самописный маппер, который имплементит функциональный интерфейс Function
* Postgresql
* Liquibase
* Добавлен Snakeyaml 2.0 для работы с Liquibase и для настроек бд
  в [application.yaml](src/main/resources/application.yaml)
* Заменён @ControllerAdvice
  на [ExceptionHandlerServlet](src/main/java/ru/clevertec/cashreceipt/servletremaster/exception/handler/ExceptionHandlerServlet.java)
  на который идёт перенаправление при выбросе моих исключений
* Добавлены фильтры для кодировки и валидации данных

### Инструкция для запуска приложения локально:

1. У вас должна быть
   установлена [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html),
   [Tomcat 10.1](https://tomcat.apache.org/download-10.cgi), [Intellij IDEA Ultimate](https://www.jetbrains.com/idea/download/)
   и [Postgresql](https://www.postgresql.org/download/) (P.S:
   Postgresql можно развернуть в докере)
2. В Postgresql нужно создать базу данных. Как пример: "cash_receipt" . Sql: CREATE DATABASE cash_receipt
3. В [application.yaml](src/main/resources/application.yaml) в строчке №3 введите ваш username для Postgresql, в строчке
   №4 введите ваш password для Postgresql
4. В настройках идеи Run -> Edit Configurations... вы должны поставить Tomcat 10.1. И в графе Deployment
   очистить Application context
5. При запуске приложения Liquibase сам создаст таблицы и наполнит их дефолтными значениями
6. Приложение готово к использованию

## Функциональность

В сумме приложение умеет:
***
***CashReceiptServlet [cashReceipts.http](src/main/resources/cashReceipts.http)***
***

* **GET createCashReceipt**
    * Пример request:
      ````
      http://localhost:8080/cashReceipts?idAndQuantity=3-6 2-6 1-7&discountCardNumber=9875
      ````
    * Получаем чек, где idAndQuantity=3-6 2-6 1-7 discountCardNumber=9875 должны формировать чек
      содержащий продукт с id=3 и quantity из 6 продуктов, то же самое с id=2 и quantity из 6 продуктов,
      id=1 и quantity из 7 продуктов, и так далее. discountCardNumber=9875 означает, что карта с номером 9875
      представлена
    * Чек так же сохраняется в .pdf file CashReceipt.pdf. Путь : Apache Software Foundation\Tomcat
      10.1\webapps\ROOT\WEB-INF\classes\pdf\CashReceipt.pdf. Пишется в логах приложения
    * Пример response:
      ````
      Cash Receipt
      DATE: 2023-04-09 TIME: 21: 12: 40
      ----------------------------------------
      QTY    DESCRIPTION      PRICE    TOTAL
      6 | Woolen gloves | 30.89 | 185.34
      6 | Golden samovar | 100.99 | 605.94
      7 | Rock-drill Bosh | 575.25 | 4026.75
      ========================================
      TOTAL: 4818.03
      DiscountCard -10%: -481.803
      PromoDiscount -10%: "Woolen gloves"
      more than 5 items: -18.534
      PromoDiscount -10% : "Rock-drill Bosh"
      more than 5 items: -402.675
      TOTAL PAID: 3915.02
      ````
    * Пример exception response:
      ````
      {
        "error": "Product with ID 0 does not exist"
      }
      ````

***
***ProductServlet [products.http](src/main/resources/products.http)***
***

* **GET findAll**
    * Пример request:
      ````
      http://localhost:8080/products?pageNumber=1&pageSize=5
      ````
    * Находит все продукты с пагинацией, где pageNumber это номер страницы, а pageSize - размер страницы
    * Пример response:
      ````
      [
        {
          "id": 1,
          "quantity": 7,
          "name": "Rock-drill Bosh",
          "price": 575.25,
          "total": 4026.75,
          "promotion": true
        },
        {
          "id": 2,
          "quantity": 6,
          "name": "Golden samovar",
          "price": 100.99,
          "total": 605.94,
          "promotion": false
        },
        {
          "id": 3,
          "quantity": 6,
          "name": "Woolen gloves",
          "price": 30.89,
          "total": 185.34,
          "promotion": true
        },
        {
          "id": 4,
          "quantity": 1,
          "name": "Edible noodles",
          "price": 7.41,
          "total": 7.41,
          "promotion": true
        },
        {
          "id": 5,
          "quantity": 1,
          "name": "Revision hatch",
          "price": 50.73,
          "total": 50.73,
          "promotion": true
        }
      ]
      ````
    * Пример exception response:
      ````
      {
        "error": "Your page number is 0! Value must be greater than zero!"
      }
      ````
* **GET findById**
    * Пример request:
      ````
      http://localhost:8080/products?id=2
      ````
    * Находит продукт по id
    * Пример response:
      ````
      {
        "id": 2,
        "quantity": 6,
        "name": "Golden samovar",
        "price": 100.99,
        "total": 605.94,
        "promotion": false
      }
      ````
    * Пример exception response:
      ````
      {
        "error": "Product with ID 9 does not exist"
      }
      ````
* **POST save**
    * Пример request:
      ````
      http://localhost:8080/products
      ````
      Тело:
      ````
      {
        "quantity": 8,
        "name": "Golden samovar",
        "price": "100.99",
        "promotion": false
      }
      ````
    * Сохраняет новый продукт
    * Пример response:
      ````
      {
        "id": 6,
        "quantity": 8,
        "name": "Golden samovar",
        "price": 100.99,
        "total": 807.92,
        "promotion": false
      }
      ````
* **PUT update**
    * Пример request:
      ````
      http://localhost:8080/products?id=2&quantity=15
      ````
    * Обновляет quantity продукта по id
    * Пример response:
      ````
      {
        "id": 2,
        "quantity": 15,
        "name": "Golden samovar",
        "price": 100.99,
        "total": 1514.85,
        "promotion": false
      }
      ````
    * Пример exception response:
      ````
      {
        "error": "Product with ID 0 does not exist"
      }
      ````
* **DELETE delete**
    * Пример request:
      ````
      http://localhost:8080/products?id=6
      ````
    * Удаляет продукт по id
    * Пример response:
      ````
      {
        "info": "Product with ID 6 successfully deleted"
      }
      ````
    * Пример exception response:
      ````
      {
        "error": "No product with ID 6 to delete"
      }
      ````

***
***DiscountCardServlet [discountCards.http](src/main/resources/discountCards.http)***
***

* **GET findAll**
    * Пример request:
      ````
      http://localhost:8080/discountCards?pageNumber=1&pageSize=3
      ````
    * Находит все дисконтные карты с пагинацией, где pageNumber это номер страницы, а pageSize - размер страницы
    * Пример response:
      ````
      [
        {
          "id": 1,
          "discountCardNumber": "1234",
          "discountPercentage": 7
        },
        {
          "id": 2,
          "discountCardNumber": "5678",
          "discountPercentage": 3.5
        },
        {
          "id": 3,
          "discountCardNumber": "9875",
          "discountPercentage": 10
        }
      ]
      ````
    * Пример exception response:
      ````
      {
        "error": "Your page size is 0! Value must be greater than zero!"
      }
      ````
* **GET findById**
    * Пример request:
      ````
      http://localhost:8080/discountCards?id=2
      ````
    * Находит дисконтную карту по id
    * Пример response:
      ````
      {
        "id": 2,
        "discountCardNumber": "5678",
        "discountPercentage": 3.5
      }
      ````
    * Пример exception response:
      ````
      {
        "error": "DiscountCard with ID 7 does not exist"
      }
      ````
* **POST save**
    * Пример request:
      ````
      http://localhost:8080/discountCards
      ````
      Тело:
      ````
      {
        "discountCardNumber": "5689",
        "discountPercentage": 6
      }
      ````
    * Сохраняет новую дисконтную карту
    * Пример response:
      ````
      {
        "id": 4,
        "discountCardNumber": "5689",
        "discountPercentage": 6
      }
      ````
* **GET findByDiscountCardNumber**
    * Пример request:
      ````
      http://localhost:8080/discountCards?discountCardNumber=5689
      ````
    * Находит дисконтную карту по discountCardNumber
    * Пример response:
      ````
      {
        "id": 4,
        "discountCardNumber": "5689",
        "discountPercentage": 6
      }
      ````
    * Пример exception response:
      ````
      {
        "error": "DiscountCard with card number 568 does not exist"
      }
      ````
* **DELETE delete**
    * Пример request:
      ````
      http://localhost:8080/discountCards?id=4
      ````
    * Удаляет дисконтную карту по id
    * Пример response:
      ````
      {
        "info": "Discount card with ID 4 successfully deleted"
      }
      ````
    * Пример exception response:
      ````
      {
        "error": "No discount card with ID 4 to delete"
      }
      ````
