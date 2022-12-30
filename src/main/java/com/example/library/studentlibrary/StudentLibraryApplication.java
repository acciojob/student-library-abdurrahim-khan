package com.example.library.studentlibrary;

import com.example.library.studentlibrary.models.*;
import com.example.library.studentlibrary.repositories.AuthorRepository;
import com.example.library.studentlibrary.repositories.BookRepository;
import com.example.library.studentlibrary.repositories.StudentRepository;
import com.example.library.studentlibrary.repositories.TransactionRepository;
import com.example.library.studentlibrary.services.AuthorService;
import com.example.library.studentlibrary.services.BookService;
import com.example.library.studentlibrary.services.StudentService;
import com.example.library.studentlibrary.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class StudentLibraryApplication implements CommandLineRunner {

	@Autowired
	AuthorService authorService;
	@Autowired
	BookService bookService;

	@Autowired
	StudentService studentService;
	@Autowired
	TransactionService transactionService;
	public static void main(String[] args) {
		SpringApplication.run(StudentLibraryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*Author author = new Author();
		author.setAge(34);
		author.setName("abd");
		author.setEmail("abd@gmail.com");
		author.setCountry("india");
		authorService.create(author);

		Book book = new Book();
		book.setAvailable(true);
		book.setName("merchant of venice");
		book.setGenre(Genre.FICTIONAL);
		book.setAuthor(author);
		bookService.createBook(book);

		Student student = new Student();
		student.setAge(19);
		student.setCountry("India");
		student.setName("abdurrahim");
		student.setEmailId("abdurrahimalig@gmail.com");
		studentService.createStudent(student);*/

		//transactionService.issueBook(1,1);
		//transactionService.returnBook(1,1);
		//studentService.deleteStudent(1);






	}
}
