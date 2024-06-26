## Яндекс Практикум: проект "RESTful Канбан-доска"


Технологии: Java (no frameworks) + JUnit + GSON + RESTful API

Описание: Данный проект был создан на Java без использования фреймворков и направлен на ознакомление с основными принципами работы JUnit, GSON и RESTful API.

Основная идея проекта заключается в возможности создавать три вида задач:

обычные задачи - Task;
большие задачи, включающие в себя другие подзадачи, - Epic;
подзадачи, входящие в состав больших задач, - Subtask.
На текущий момент данные задачи можно: создавать, удалять, обновлять, хранить историю взаимодействия с ними, а также присутствует возможность сортировать данные задачи по приоритету и искать пересечения (когда одна задача начинается до окончания предыдущей).

Помимо вышеуказанного в проект был добавлен KVServer с доступом по API токену, который позволяет хранить задачи на удалённом хранилище.
