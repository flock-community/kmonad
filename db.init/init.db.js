client = new Mongo()

starWars = client.getDB("StarWars");

starWars.createCollection("jedi", {capped: false});
starWars.createCollection("sith", {capped: false});

starWars.jedi.insertMany([
    {id: "f04e5e8a-7b8d-4519-9a8b-8d04101086f7", name: "Luke", age: 19},
    {id: "f89444e7-189c-4bba-b48c-e291146c2f5a", name: "Yoda", age: 942},
    {id: "93a1d159-eb4d-4a61-abf6-b8eab94bc9cc", name: "Obi Wan", age: 67},
]);

starWars.sith.insertMany([
    {id: "c1cd2cc7-1fa8-47de-b528-33b090654fdf", name: "Palpatine", age: 340},
    {id: "dda88970-6730-400a-861c-08f992970f80", name: "Anakin", age: 29},
]);

starWars.createCollection("todo", {capped: false})

starWars.todo.insertMany([
    {
        id: "e2c1281f-ad63-44bf-920f-fe78bd5c896c",
        title: "Title",
        description: "Description",
        completed: false,
        createdAt: "2020-01-01",
        dueDate: "2020-02-02"
    },
    {
        id: "670c04cf-9359-419a-9e69-093e07b4620f",
        title: "Title2",
        description: "Description2",
        completed: true,
        createdAt: "2020-02-02",
        dueDate: "2020-03-03"
    }
])