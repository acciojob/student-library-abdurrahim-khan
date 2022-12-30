package com.example.library.studentlibrary.services;

import com.example.library.studentlibrary.models.*;
import com.example.library.studentlibrary.repositories.BookRepository;
import com.example.library.studentlibrary.repositories.CardRepository;
import com.example.library.studentlibrary.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    int max_allowed_books;

    @Value("${books.max_allowed_days}")
    int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        //conditions required for successful transaction of issue book:
        //1. book is present and available
        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");
        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");
        //If the transaction is successful, save the transaction to the list of transactions and return the id

        //Note that the error message should match exactly in all cases

        if((!bookRepository5.existsById(bookId)) || (bookRepository5.findById(bookId).get().isAvailable()==false))
        {
            throw new Exception("Book is either unavailable or not present");
        }
        if((!cardRepository5.existsById(cardId)) || (cardRepository5.findById(cardId).get().getCardStatus().toString().equals("DEACTIVATED")))
        {
            throw new Exception("Card is invalid");
        }
        if(cardRepository5.findById(cardId).get().getBooks().size() >= max_allowed_books)
        {
            throw new Exception("Book limit has reached for this card");
        }
        Book book = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();
        Transaction transaction = Transaction.builder()
                .book(book)
                .card(card)
                .isIssueOperation(true)
                .transactionStatus(TransactionStatus.SUCCESSFUL)
                .fineAmount(0)
                .transactionId(UUID.randomUUID().toString())
                .build();


        List<Book> bookList = card.getBooks();
        if(bookList == null)
        {
            bookList = new ArrayList<>();
        }
        bookList.add(book);

        book.setAvailable(false);
        book.setCard(card);
        bookRepository5.updateBook(book);
        List<Transaction> transactionList = book.getTransactions();
        if(transactionList == null)
        {
            transactionList = new ArrayList<>();
        }
        transactionList.add(transaction);


        Transaction myTransaction = transactionRepository5.save(transaction);
       return myTransaction.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId,TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        //make the book available for other users
        //make a new transaction for return book which contains the fine amount as well

        Book book = transaction.getBook();
        book.setAvailable(true);

        Card card = cardRepository5.findById(cardId).get();

        Date issueDate = transaction.getTransactionDate();
        Date returnDate = new Date();

        long dateBeforeInMs = issueDate.getTime();
        long dateAfterInMs = returnDate.getTime();

        long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);

        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

        int delay = (int)daysDiff - getMax_allowed_days;
        int fine =0;
        if(delay > 0)
        {
            fine = delay*fine_per_day;
        }


        Transaction returnBookTransaction  = null;
        returnBookTransaction = Transaction.builder()
                .fineAmount(fine)
                .transactionStatus(TransactionStatus.SUCCESSFUL)
                .book(book)
                .isIssueOperation(false)
                .card(card)
                .transactionId(UUID.randomUUID().toString())
                .build();

        removeBookFromCard(cardId,bookId);
        book.setCard(null);
        book.getTransactions().add(transaction);
        bookRepository5.updateBook(book);

        transactionRepository5.save(returnBookTransaction);
        return returnBookTransaction; //return the transaction after updating all details
    }
    public void removeBookFromCard(int card_id, int book_id)
    {
        Card card = cardRepository5.findById(card_id).get();
        int index = -1;
        List<Book> bookList = card.getBooks();
        if(bookList.size() == 0) return;
        for(index=0;index<bookList.size();index++)
        {
            if(bookList.get(index).getId() == book_id)
            {

                break;
            }
        }
        bookList.remove(index);
    }
}