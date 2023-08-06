$(document).ready(function() {
    // Pobierz listę książek po załadowaniu strony
    getAllBooks();

    // Obsługa formularza dodawania nowej książki (okno modalne)
    $("#addBookForm").submit(function(event) {
        event.preventDefault();

        var title = $("#title").val();
        var author = $("#author").val();
        var year = $("#year").val();

        var newBook = {
            title: title,
            author: author,
            year: parseInt(year)
        };

        axios.post("http://localhost:8080/api/books", newBook)
            .then(function(response) {
                getAllBooks();
                $("#addBookModal").modal('hide');
                $("#addBookForm")[0].reset();
            })
            .catch(function(error) {
                console.error(error);
            });
    });

    // Funkcja pobierająca listę książek
    function getAllBooks() {
        axios.get("http://localhost:8080/api/books")
            .then(function(response) {
                var tableBody = $("#bookTableBody");
                tableBody.empty();

                $.each(response.data, function(index, book) {
                    var row = "<tr><td>" + book.title + "</td><td>" + book.author + "</td><td>" + book.year + "</td>" +
                        "<td><button data-id='" + book.id + "' class='btn btn-primary editBtn'>Edytuj</button>" +
                        "<button data-id='" + book.id + "' class='btn btn-danger deleteBtn'>Usuń</button></td></tr>";

                    tableBody.append(row);
                });
            })
            .catch(function(error) {
                console.error(error);
            });
    }

    // Obsługa kliknięcia na przycisk Edytuj
    $("#bookTableBody").on("click", ".editBtn", function() {
        var id = $(this).data("id");
        editBook(id);
    });

    // Obsługa formularza edycji książki (okno modalne)
    $("#updateBookForm").submit(function(event) {
        event.preventDefault();

        var id = $("#editBookId").val();
        var title = $("#editTitle").val();
        var author = $("#editAuthor").val();
        var year = $("#editYear").val();

        var updatedBook = {
            title: title,
            author: author,
            year: parseInt(year)
        };

        axios.put("http://localhost:8080/api/books/" + id, updatedBook)
            .then(function(response) {
                getAllBooks();
                $("#editBookModal").modal('hide');
            })
            .catch(function(error) {
                console.error(error);
            });
    });

    // Obsługa przycisku Anuluj przy edycji książki (okno modalne)
    $("#cancelEdit").click(function() {
        $("#editBookModal").modal('hide');
    });

    // Obsługa kliknięcia na przycisk Usuń
    $("#bookTableBody").on("click", ".deleteBtn", function() {
        var id = $(this).data("id");
        deleteBook(id);
    });

    // Funkcja obsługi edycji książki (okno modalne)
    function editBook(id) {
        axios.get("http://localhost:8080/api/books/" + id)
            .then(function(response) {
                var book = response.data;
                $("#editBookId").val(book.id);
                $("#editTitle").val(book.title);
                $("#editAuthor").val(book.author);
                $("#editYear").val(book.year);

                $("#editBookModal").modal('show');
            })
            .catch(function(error) {
                console.error(error);
            });
    }

    // Funkcja obsługi usuwania książki
    function deleteBook(id) {
        // Wyświetlenie potwierdzenia usuwania
        if (confirm("Czy na pewno chcesz usunąć tę książkę?")) {
            axios.delete("http://localhost:8080/api/books/" + id)
                .then(function(response) {
                    getAllBooks();
                })
                .catch(function(error) {
                    console.error(error);
                });
        }
    }
});