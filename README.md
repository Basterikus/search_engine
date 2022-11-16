# search_engine
Дипломный проект поискового движка.

Сайт запускается на порту 8010 и также доступна небольшая документация Swagger по ссылке - /swagger-ui.html

## Dashboard
- На главной странице расположенна статистика по индексации - количество индексированных ссылок, слов и т.д.
- Индексированные слова приведены к общей форме и указаны как Lemmas.
![image](https://user-images.githubusercontent.com/102787942/202157673-671bf5b6-acf3-418b-9883-9dd9d4e69b33.png)

## Management
- На странице Management при нажатии на кнопку Start indexing - начинается полная переиндексация всех ссылок.
- Кнопками ADD/UPDATE - можно добавить новые ссылки для индексации.
- Ссылки указываются в формате - https://dombulgakova.ru
- Индексация производится по всему сайту, от указанного уровня ссылки.
- Индексация сайта не произойдет, если он не добавлен в список индексируемых сайтов.
- Добавить сайты в список индексируемых можно в application.yml.
![image](https://user-images.githubusercontent.com/102787942/202158328-91606ffa-1ca0-43af-a244-11929c3f0fd1.png)

## Search
- На странице Search доступен поиск по индексированным сайтам.
- Также доступен выбор на каких сайтах производить поиск.
![image](https://user-images.githubusercontent.com/102787942/202159738-ae20aa55-358a-45ac-9abc-7830c596f785.png)

# Используемый стэк
В программе использовалось:
- Spring Boot
- База данных MySQL
- Для создания сущностей и связей использовался JPA
- Для поиска всех ссылок на сайте использовался jsoup + ForkJoinPool
- Для приведения всех слов к одной форме использовалась библиотека LuceneMorphology
- RandomUserAgent - утилита рандомайзер юзер агента при коннекте
- Lombok для упрощения и чистоты кода
