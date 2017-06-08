
#### Сборка сервера

Структура базы данных лежит в resources/database/schema.sql

.proto файлы для протобафа лежат в resources/proto/*.proto

Настройки сервера в resources/config/server.xml

Перед сборкой нужно сгенерировать классы сообщений протобафа
```bash
./generate-proto.sh
```

Сервер собирается мавеном, требуется 8 версия java
```bash
mvn package
```

Jar файл сервера: target/chat-server.jar, запускается после сборки так:
```bash
java -jar target/chat-server.jar
```

#### Клиент

Главный файл клиента: src/main/java/wg_test/chat/client/Client.java

Клиент умеет:
* аутентификацию;
* регистрацию новых пользователей;
* отображение списка пользователей;
* отображение онлайн/оффлайн в момент аутентификации/выхода других пользователей;
* отправку сообщений в общий чат;
* отправку сообщений в приватные чаты;
* получение и отображение новых сообщений, пришедших с сервера;

Вообще клиент весьма багованый, так как его я писал только для тестирования сервера.

Хост и порт сервера в клиенте захардкожены: константы HOST и PORT в классе wg_test.chat.client.Client

### Протокол общения клиента и сервером

Сервер и клиент общаются с помощью протобафа.

Все сообщения с клиента имеют тип ClientMessage (request.proto), в поле type клиент прописывает тип текущего сообщения,
а в поле data в виде байт строки уже дописывается сообщение нужного типа. Сервер делает все аналогично, только
используются сообщения типа ServerMessage (resposne.proto), плюс в ответе от сервера есть поле success говорящее о
том, была ли операция выполена успешно.

После открытия соединения клиент отправляет один из трёх типов сообщений:
* type: ClientMessage.MessageType.AUTH - если пользователь указал логин и пароль;
* type: ClientMessage.MessageType.REGISTER - если это новый пользователь;
* type: ClientMessage.MessageType.RECONNECT - если клиент сохранил токен до этого и токен пока ещё не просрочен.

В ответ клиент получает одно из трёх типов сообщений:
* type: ServerMessage.MessageType.AUTH;
* type: ServerMessage.MessageType.REGISTER;
* type: ServerMessage.MessageType.VALIDATE_TOKEN;
Если клиент отправил валидные данные в ответ он получит токен.

После получения токена клиент может отправлять запросы типов:
* type: ClientMessage.MessageType.REGENERATE_TOKEN - для пересоздания токена, если срок его действия приближается к концу;
* type: ClientMessage.MessageType.USER_LIST - запрос на получение списка пользователей;
* type: ClientMessage.MessageType.SEND_MESSAGE - запрос на отправку сообщения в общий или приватный чат;
* type: ClientMessage.MessageType.UPDATE_MESSAGE - запрос на изменение текста сообщения;
* type: ClientMessage.MessageType.DELETE_MESSAGE - запрос на удаление сообщения;
* type: ClientMessage.MessageType.LIST_MESSAGES - запрос на получение списка сообщений в каком-либо чате.

В ответ на запросы клиента сервер отвечает сообщениями типов:
* type: ServerMessage.MessageType.REGENERATE_TOKEN;
* type: ServerMessage.MessageType.USER_LIST;
* type: ServerMessage.MessageType.SEND_MESSAGE;
* type: ServerMessage.MessageType.UPDATE_MESSAGE;
* type: ServerMessage.MessageType.DELETE_MESSAGE;
* type: ServerMessage.MessageType.LIST_MESSAGES.

Кроме того от сервера в процессе работы могут асинронно прилетать соощения типов:
* type: ServerMessage.MessageType.USER_STATUS_CHANGE - изменился онлайн статус одного из пользователей;
* type: ServerMessage.MessageType.NEW_USER - зарегестровался новый пользователь;
* type: ServerMessage.MessageType.NEW_MESSAGE - появилось новое сообщение (в общем чате или в приватном);
* type: ServerMessage.MessageType.UPDATED_MESSAGE - одно из сообщений изменилось (в общем чате или в приватном).

#### Сообщения, отправляемые с клиента:

##### ClientMessage.MessageType.AUTH

В поле data клиент записывает сообщение типа AuthMessage (auth_request.proto), в ответ, если все хорошо с сервера
придет сообщение ServerMessage.MessageType.AUTH в поле success будет true, а в поле data которого будет записано
сообщение AuthMessage (auth_response.proto) содержащее айди текущего пользователя, токен и таймштамп до которого
токен будет валиден.

Если было указан неверное имя пользователя или пароль в ответе от сервера в поле success придёт false а в поле data
придёт сообщение типа Error (error_info.proto) с описанием ошибки.

Если пользователь успешно получил токен, всем активным в данный момент пользователям разошлйтся сообщение типа
ServerMessage.MessageType.USER_STATUS_CHANGE, в поле data которого придёт сообщение типа UserStatusChangeMessage
с указанием айди пользователя и его текущего онлайн статуса.

##### ClientMessage.MessageType.REGISTER

В поле data клиент записывает сообщение типа RegisterMessage (auth_request.proto), в ответ, если все хорошо с сервера
придет сообщение ServerMessage.MessageType.REGISTER в поле success будет true, а в поле data которого будет записано
сообщение RegisterMessage (auth_response.proto) содержащее айди текущего пользователя, токен и таймштамп до которого
токен будет валиден.

Если было указан неверное имя пользователя или пароль в ответе от сервера в поле success придёт false а в поле data
придёт сообщение типа Error (error_info.proto) с описанием ошибки.

Если пользователь успешно получил токен, всем активным в данный момент пользователям разошлйтся сообщение типа
ServerMessage.MessageType.NEW_USER, в поле data которого придёт сообщение типа User с указанием айди нового
пользователя, его имени и текущего онлайн статуса.

##### ClientMessage.MessageType.VALIDATE_TOKEN (тестовый клиент не умеет)

В поле data клиент записывает сообщение типа ReconnectMessage (auth_request.proto), в ответ, если все хорошо с сервера
придет сообщение ServerMessage.MessageType.VALIDATE_TOKEN в поле success будет true, а в поле data будет
записано сообщение ReconnectMessage (auth_response.proto) содержащее токен пользователя.

Если был указан неверный токен или он уже просрочен в ответе от сервера в поле success придёт false а в поле data
придёт сообщение типа Error (error_info.proto) с описанием ошибки.

Если пользователь успешно обновил токен, всем активным в данный момент пользователям разошлётся сообщение типа
ServerMessage.MessageType.USER_STATUS_CHANGE, в поле data которого придёт сообщение типа UserStatusChangeMessage
с указанием айди пользователя и его текущего онлайн статуса.

##### ClientMessage.MessageType.USER_LIST

После получения токена клиент отправляет запрос на полчение списка пользователей, в поле data клиент устанавливает
сообщение типа ListMessage (user_request.proto).

В ответ с сервера приходит сообщение в поле data которого будет лежать сообщение типа ListMessage со списком всех
пользователей и указанием их онлайн статуса.

...
