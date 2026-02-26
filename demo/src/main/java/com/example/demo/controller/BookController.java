package com.example.demo.controller;

import com.example.demo.entity.Book;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BookController {

    private List<Book> books = new ArrayList<>();

    private Long nextId = 1L;

    public BookController() {
        // Add 15 books with varied data for testing
        books.add(new Book(nextId++, "Spring Boot in Action", "Craig Walls", 39.99));
        books.add(new Book(nextId++, "Effective Java", "Joshua Bloch", 45.00));
        books.add(new Book(nextId++, "Clean Code", "Robert Martin", 42.50));
        books.add(new Book(nextId++, "Java Concurrency in Practice", "Brian Goetz", 49.99));
        books.add(new Book(nextId++, "Design Patterns", "Gang of Four", 54.99));
        books.add(new Book(nextId++, "Head First Java", "Kathy Sierra", 35.00));
        books.add(new Book(nextId++, "Spring in Action", "Craig Walls", 44.99));
        books.add(new Book(nextId++, "Clean Architecture", "Robert Martin", 39.99));
        books.add(new Book(nextId++, "Refactoring", "Martin Fowler", 47.50));
        books.add(new Book(nextId++, "The Pragmatic Programmer", "Andrew Hunt", 41.99));
        books.add(new Book(nextId++, "You Don't Know JS", "Kyle Simpson", 29.99));
        books.add(new Book(nextId++, "JavaScript: The Good Parts", "Douglas Crockford", 32.50));
        books.add(new Book(nextId++, "Eloquent JavaScript", "Marijn Haverbeke", 27.99));
        books.add(new Book(nextId++, "Python Crash Course", "Eric Matthes", 38.00));
        books.add(new Book(nextId++, "Automate the Boring Stuff", "Al Sweigart", 33.50));
    }

    // get all books - /api/books
    @GetMapping("/books")
    public List<Book> getBooks() {
        return books;
    }

    // get ADVANCED all books - /api/books
    @GetMapping("/books/advanced/{page}/{size}")
    public List<Book> getBooksAdvanced(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @PathVariable int page,
            @PathVariable int size
    ) {
        //filtering
        List<Book> result;
        result = books.stream().filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());

        //sorting
        Comparator<Book> comparator;

        switch(sortBy.toLowerCase()) {
            case "author":
                comparator = Comparator.comparing(Book::getAuthor);
                break;
            case "title":
                comparator = Comparator.comparing(Book::getTitle);
            default:
                comparator = Comparator.comparing(Book::getTitle);
                break;
        }

        if("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        result = result.stream().sorted(comparator)
                .collect(Collectors.toList());

        // pagination
        int start = page * size;
        int end = start + size;

        return result.subList(start, end);

    }

    // get book by id
    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable Long id) {
        return books.stream().filter(book -> book.getId().equals(id))
                .findFirst().orElse(null);
    }

    // create a new book
    @PostMapping("/books")
    public List<Book> createBook(@RequestBody Book book) {
        books.add(book);
        return books;
    }

    // search by title
    @GetMapping("/books/search")
    public List<Book> searchByTitle(
            @RequestParam(required = false, defaultValue = "") String title
    ) {
        if(title.isEmpty()) {
            return books;
        }

        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    // price range
    @GetMapping("/books/price-range")
    public List<Book> getBooksByPrice(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return books.stream()
                .filter(book -> {
                    boolean min = minPrice == null || book.getPrice() >= minPrice;
                    boolean max = maxPrice == null || book.getPrice() <= maxPrice;

                    return min && max;
                }).collect(Collectors.toList());
    }

    // sort
    @GetMapping("/books/sorted")
    public List<Book> getSortedBooks(
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order
    ){
        Comparator<Book> comparator;

        switch(sortBy.toLowerCase()) {
            case "author":
                comparator = Comparator.comparing(Book::getAuthor);
                break;
            case "title":
                comparator = Comparator.comparing(Book::getTitle);
            default:
                comparator = Comparator.comparing(Book::getTitle);
                break;
        }

        if("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return books.stream().sorted(comparator)
                .collect(Collectors.toList());
    }

    // PUT
    // {
    //      "title": "New Title",
    //      "author": "New Author",
    //      "price": 19.99
    // }
    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book newBook) {
        // find the book with the matching id
        for (int i = 0; i < books.size(); i++){
            // if found, update book information, book id will remain the same
            if (books.get(i).getId().equals(id)){
                newBook.setId(id);
                books.set(i, newBook);
                return newBook;
            }
        }
        // if id not found, make new book with new id??
        newBook.setId(id);
        books.add(newBook);
        return newBook;
    }

    // PATCH
    @PatchMapping("/books/{id}")
    public Book modifyBook(@PathVariable Long id, @RequestBody Book modifiedBook) {
        // find the book with the matching id
        for (int i = 0; i < books.size(); i++){
            // if found, update book information, book id will remain the same
            if (books.get(i).getId().equals(id)){
                Book currentBook = books.get(i);
                if (modifiedBook.getTitle() != null) {
                    currentBook.setTitle(modifiedBook.getTitle());
                }
                if (modifiedBook.getAuthor() != null) {
                    currentBook.setAuthor(modifiedBook.getAuthor());
                }
                if (modifiedBook.getPrice() != null) {
                    currentBook.setPrice(modifiedBook.getPrice());
                }
                return currentBook;
            }
        }
        return null;
    }

    // DELETE
    @DeleteMapping("/books/{id}")
    public Book deleteBook(@PathVariable Long id) {
        // find the book with the matching id
        for (int i = 0; i < books.size(); i++){
            // if found, update book information, book id will remain the same
            if (books.get(i).getId().equals(id)){

                Book deletedBook =books.get(i);
                books.remove(i);
                return deletedBook;
            }
        }
        return null;
    }

    // Get Pagination
    @GetMapping("/books/pagination/{page}/{size}")
    public List<Book> getPagination(@PathVariable int page, @PathVariable int size) {
        int start = page * size;
        int end = start + size;

        return books.subList(start, end);
    }


}