package ac.il.bgu.qa;
import ac.il.bgu.qa.errors.*;
import ac.il.bgu.qa.services.NotificationService;
import org.junit.jupiter.api.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import ac.il.bgu.qa.services.DatabaseService;
import ac.il.bgu.qa.services.ReviewService;
import org.mockito.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestLibrary {

    private Library library;
    @Mock
    private DatabaseService mockDatabaseService;
    @Mock
    private ReviewService mockReviewService;
    @Mock
    private NotificationService mockNotificationService;
    private User user1;
    private Book book1;
    private String isbn1;
    private String id1;
    @BeforeEach
    void setUp() {
        mockDatabaseService = mock(DatabaseService.class);
        mockReviewService = mock(ReviewService.class);
        mockNotificationService=mock(NotificationService.class);
        library = new Library(mockDatabaseService, mockReviewService);
        id1=generateID();
        isbn1=generateIsbn();
        user1=new User("Nadav Gigi",id1,mockNotificationService);
        when(mockDatabaseService.getUserById(id1)).thenReturn(user1);
        book1 = new Book(isbn1, "Book1", "Yohay Gabay");
        when(mockDatabaseService.getBookByISBN(isbn1)).thenReturn(book1);


    }

    //region AddBook Tests
    @Test
    void givenValidBook_whenAddBook_thenBookAddedToDatabase() {
        Book validBook = new Book("978-0-596-52068-7", "Sample Title", "Sample Author");

        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        library.addBook(validBook);

        verify(mockDatabaseService).addBook(eq("978-0-596-52068-7"), eq(validBook));
    }
    @Test
    void givenValidBook_whenAddBookloop_thenBookAddedToDatabase() {
        for(int i=0;i<500;i++){
            String isbn=generateIsbn();
            Book validBook = new Book(isbn, "Sample Title", "Sample Author");

            when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

            library.addBook(validBook);

            verify(mockDatabaseService).addBook(eq(isbn), eq(validBook));
        }

    }
    @Test
    void givenInvalidBook_whenAddBook_thenIsntAddedToDatabase() {
        Book InvalidBook = new Book("978-0-596-5206", "Sample Title", "Sample Author");

        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->library.addBook(InvalidBook),"Invalid ISBN.");

        verify(mockDatabaseService,never()).addBook(any(), eq(InvalidBook));
    }
    @Test
    void givenInvalidBookNullIsbn_whenAddBook_thenIsntAddedToDatabase() {
        Book InvalidBook = new Book(null, "Sample Title", "Sample Author");

        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->library.addBook(InvalidBook),"Invalid ISBN.");

        verify(mockDatabaseService,never()).addBook(any(), eq(InvalidBook));
    }
    @Test
    void givenNullBook_whenAddBook_thenIsntAddedToDatabase() {
        Book InvalidBook = null;

        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->library.addBook(InvalidBook),"Invalid book.");

        verify(mockDatabaseService,never()).addBook(any(), eq(InvalidBook));
    }
    @Test
    void givenInvalidBookWithInvalidTitle_whenAddBook_thenIsntAddedToDatabase() {
        Book InvalidBook = new Book("978-0-596-52068-7", "", "Sample Author");

        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->library.addBook(InvalidBook),"Invalid title.");

        verify(mockDatabaseService,never()).addBook(any(), eq(InvalidBook));
    }
    @Test
    void givenInvalidBookWithNullTitle_whenAddBook_thenIsntAddedToDatabase() {
        Book InvalidBook = new Book("978-0-596-52068-7", null, "Sample Author");


        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->library.addBook(InvalidBook),"Invalid title.");

        verify(mockDatabaseService,never()).addBook(any(), eq(InvalidBook));
    }
    @Test
    void givenInvalidBookWithNullAuthor_whenAddBook_thenIsntAddedToDatabase() {
        Book InvalidBook = new Book("978-0-596-52068-7", "Sample Title", null);

        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->library.addBook(InvalidBook),"Invalid author.");

        verify(mockDatabaseService,never()).addBook(any(), eq(InvalidBook));
    }
    @Test
    void givenInvalidBookWithNullAuthorNullIsbn_whenAddBook_thenIsntAddedToDatabase() {
        Book InvalidBook = new Book(null, "Sample Title", null);

        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->library.addBook(InvalidBook),"Invalid ISBN.");

        verify(mockDatabaseService,never()).addBook(any(), eq(InvalidBook));
    }
    @Test
    void givenBookThatInTheDatabase_whenAddBook_thenIsntAddedToDatabase() {
        Book validBook = new Book("978-0-596-52068-7", "Sample Title", "Sample Author");
        when(mockDatabaseService.getBookByISBN("978-0-596-52068-7")).thenReturn(null);
        library.addBook(validBook);
        when(mockDatabaseService.getBookByISBN("978-0-596-52068-7")).thenReturn(new Book("978-0-596-52068-7", "Sample Title", "Sample Author"));
        assertThrows(IllegalArgumentException.class, () -> library.addBook(validBook), "Book already exists.");

        verify(mockDatabaseService, times(1)).addBook(any(), eq(validBook));

    }
    //endregion

    //region registerUser Tests
    @Test
    void givenValidUser_whenRegisterUser_thenUserHaveBeenAddedSuccesfully() {
        User validUser = new User("Nadav Gigi","313268831123",mockNotificationService);

        when(mockDatabaseService.getUserById("313268831123")).thenReturn(null);

        library.registerUser(validUser);

        verify(mockDatabaseService,times(1)).registerUser(eq("313268831123"), eq(validUser));
    }
    @Test
    void givenNullUserObject_whenRegisterUser_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.registerUser(null));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }

    @Test
    void givenUserWithExistingId_whenRegisterUser_thenThrowIllegalArgumentException() {
        User existingUser = new User("John Doe", "987654321098", mockNotificationService);

        when(mockDatabaseService.getUserById("987654321098")).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(existingUser));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }

    @Test
    void givenUserWithInvalidIdFormat_whenRegisterUser_thenThrowIllegalArgumentException() {
        User userWithInvalidId = new User("John Doe", "InvalidId", mockNotificationService);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(userWithInvalidId));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }

    @Test
    void givenUserWithNullId_whenRegisterUser_thenThrowIllegalArgumentException() {
        User userWithNullId = new User("John Doe", null, mockNotificationService);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(userWithNullId));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }

    @Test
    void givenUserWithEmptyName_whenRegisterUser_thenThrowIllegalArgumentException() {
        User userWithEmptyName = new User("", "123456789012", mockNotificationService);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(userWithEmptyName));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }

    @Test
    void givenValidUser_whenRegisterUserAndDatabaseReturnsExistingUser_thenThrowIllegalArgumentException() {
        User existingUser = new User("John Doe", "123456789012", mockNotificationService);

        when(mockDatabaseService.getUserById("123456789012")).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(existingUser));

        verify(mockDatabaseService, never()).registerUser(any(), any());
    }

    @Test
    void givenNullUser_whenRegisterUser_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.registerUser(null));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }

    @Test
    void givenInvalidUserId_whenRegisterUser_thenThrowIllegalArgumentException() {
        User invalidUser = new User("John Doe", "invalidUserId", mockNotificationService);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(invalidUser));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }

    @Test
    void givenExistingUser_whenRegisterUser_thenThrowIllegalArgumentException() {
        User existingUser = new User("John Doe", "123456789012", mockNotificationService);

        when(mockDatabaseService.getUserById("123456789012")).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(existingUser));

        verify(mockDatabaseService, never()).registerUser(any(), any());

    }
    @Test
    void givenUserWithNullInvalidIdAndName_whenRegisterUser_thenThrowIllegalArgumentException() {
        User invalidUser = new User(null, null, mockNotificationService);
        assertThrows(IllegalArgumentException.class, () -> library.registerUser(invalidUser),"Invalid user Id.");
        verify(mockDatabaseService, never()).registerUser(any(), any());
    }
    @Test
    void givenUserWithNotification_whenRegisterUser_thenThrowIllegalArgumentException() {
        User invalidUser = new User("John Doe", "123456789012", null);
        assertThrows(IllegalArgumentException.class, () -> library.registerUser(invalidUser),"Invalid notification service.");
        verify(mockDatabaseService, never()).registerUser(any(), any());
    }
    @Test
    void givenUserWithInvalidId_whenRegisterUser_thenThrowIllegalArgumentException() {
        User invalidUser = new User("John Doe", "", null);
        assertThrows(IllegalArgumentException.class, () -> library.registerUser(invalidUser),"Invalid user Id.");
        verify(mockDatabaseService, never()).registerUser(any(), any());
    }
    @Test
    void givenUserWithInvalidIdChar_whenRegisterUser_thenThrowIllegalArgumentException() {
        User invalidUser = new User("John Doe", "123456g89012", null);
        assertThrows(IllegalArgumentException.class, () -> library.registerUser(invalidUser),"Invalid user Id.");
        verify(mockDatabaseService, never()).registerUser(any(), any());
    }

    //endregion

    //region borrowBook
    @Test
    void givenInvalidISBN_whenBorrowBookWithEmptyDatabase_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook("invalidISBN", "123456789012"));
    }

    @Test
    void givenNonexistentBook_whenBorrowBook_thenThrowBookNotFoundException() {
        when(mockDatabaseService.getBookByISBN("9780596520687")).thenReturn(null);

        assertThrows(BookNotFoundException.class, () -> library.borrowBook("9780596520687", "123456789012"));
    }

    @Test
    void givenInvalidUserId_whenBorrowbook_thenThrowIllegalArgumentException() {
        Book validBook = new Book("9780596520687", "Sample Title", "Sample Author");
        when(mockDatabaseService.getBookByISBN("9780596520687")).thenReturn(validBook);

        assertThrows(IllegalArgumentException.class, () -> library.borrowBook("9780596520687", "invalidUserId"));
    }

    @Test
    void givenUnregisteredUser_whenBorrowBook_thenThrowUserNotRegisteredException() {
        Book validBook = new Book("9780596520687", "Sample Title", "Sample Author");
        when(mockDatabaseService.getBookByISBN("9780596520687")).thenReturn(validBook);
        when(mockDatabaseService.getUserById("9780596520687")).thenReturn(null);

        assertThrows(UserNotRegisteredException.class, () -> library.borrowBook("9780596520687", "123456789012"));
    }
    @Test
    void givenregisteredUser_whenBorrowBook_thenSuccess() {
        Book validBook = new Book("9780596520687", "Sample Title", "Sample Author");

        when(mockDatabaseService.getBookByISBN("9780596520687")).thenReturn(validBook);
        when(mockDatabaseService.getUserById("9780596520687")).thenReturn(null);

        assertThrows(UserNotRegisteredException.class, () -> library.borrowBook("9780596520687", "123456789012"));
    }

    @Test
    void givenAlreadyBorrowedBook_whenBorrowBook_thenThrowBookAlreadyBorrowedException() {
        Book borrowedBook = new Book(isbn1, "Sample Title", "Sample Author");
        borrowedBook.borrow();
        when(mockDatabaseService.getBookByISBN(isbn1)).thenReturn(borrowedBook);
        when(mockDatabaseService.getUserById("123456789012")).thenReturn(new User("John Doe", "123456789012", mock(NotificationService.class)));

        assertThrows(BookAlreadyBorrowedException.class, () -> library.borrowBook(isbn1, "123456789012"));
    }

    @Test
    void givenValidBorrowRequest_whenBorrowBook_thenBookMarkedAsBorrowed() {
        Book validBook = new Book(isbn1, "Sample Title", "Sample Author");
        when(mockDatabaseService.getBookByISBN(isbn1)).thenReturn(validBook);
        when(mockDatabaseService.getUserById("123456789012")).thenReturn(new User("John Doe", "123456789012", mock(NotificationService.class)));

        library.borrowBook(isbn1, "123456789012");

        assertTrue(validBook.isBorrowed());
    }

    @Test
    void givenValidBorrowRequest_whenBorrowBook_thenBorrowTransactionRecordedInDatabase() {
        Book validBook = new Book(isbn1, "Sample Title", "Sample Author");
        User validUser = new User("John Doe", "123456789012", mock(NotificationService.class));
        when(mockDatabaseService.getBookByISBN(isbn1)).thenReturn(validBook);
        when(mockDatabaseService.getUserById("123456789012")).thenReturn(validUser);

        library.borrowBook(isbn1, "123456789012");

        verify(mockDatabaseService, times(1)).borrowBook(isbn1, "123456789012");
    }
    @Test
    void givenValidBookAndValidUser_whenBorrowBook_thenBorrowTransactionRecordedInDatabase() {

        library.borrowBook(isbn1, id1);

        verify(mockDatabaseService, times(1)).borrowBook(isbn1, id1);
        assertTrue(book1.isBorrowed());
    }
    //endregion

    //region returnBook

    @Test
    void givenBorrowedBook_whenReturnBook_thenBookMarkedAsNotBorrowed() {
        book1.borrow(); // Mark the book as borrowed
        library.returnBook(isbn1);

        assertFalse(book1.isBorrowed());
    }

    @Test
    void givenNotBorrowedBook_whenReturnBook_thenExceptionThrown() {
        assertThrows(BookNotBorrowedException.class, () -> library.returnBook(isbn1));
    }


    @Test
    void givenInvalidISBN_whenReturnBook_thenExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> library.returnBook("InvalidISBN"));
    }


    @Test
    void givenNullISBN_whenReturnBook_thenExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(null));
    }


    @Test
    void givenEmptyISBN_whenReturnBook_thenExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(""));
    }

    @Test
    void givenValidBookAndValidUser_whenReturnBook_thenDatabaseUpdated() {
        library.borrowBook(isbn1,id1);
        assertTrue(book1.isBorrowed());
        library.returnBook(isbn1);


        verify(mockDatabaseService, times(1)).returnBook(isbn1);
        assertFalse(book1.isBorrowed());
    }


    @Test
    void givenValidBookAndInvalidUser_whenManyReturnBook_thenDatabaseUpdated() {
        for(int i=0;i<100;i++){
            id1=generateID();
            isbn1=generateIsbn();
            user1=new User("Nadav Gigi",id1,mockNotificationService);
            when(mockDatabaseService.getUserById(id1)).thenReturn(user1);
            book1 = new Book(isbn1, "Book1", "Yohay Gabay");
            when(mockDatabaseService.getBookByISBN(isbn1)).thenReturn(book1);
            library.borrowBook(isbn1,id1);
            library.returnBook(isbn1);
        }
        verify(mockDatabaseService, times(100)).returnBook(anyString());
        assertFalse(book1.isBorrowed());
    }
    @Test
    void givenInValidBookAndValidUser_whenReturnBook_thenExceptionThrown() {
        assertFalse(book1.isBorrowed());

        assertThrows(BookNotBorrowedException.class, () -> library.returnBook(isbn1));

        verify(mockDatabaseService, never()).returnBook(isbn1);
        assertFalse(book1.isBorrowed());
    }
    @Test
    void givenInValidBookAndValidUser2_whenReturnBook_thenExceptionThrown() {
        String isbn=generateIsbn();
        assertThrows(BookNotFoundException.class, () -> library.returnBook(isbn));

        verify(mockDatabaseService, never()).returnBook(isbn);
    }
    @Test
    void givenManyInValidBookAndValidUser2_whenReturnBook_thenExceptionThrown() {
        for(int i=0;i<100;i++) {
            String isbn = generateIsbn();
            assertThrows(BookNotFoundException.class, () -> library.returnBook(isbn));

            verify(mockDatabaseService, never()).returnBook(isbn);
        }
    }

    //endregion

    //region getBookByISBN
    @Test
    void givenValidISBNAndValidUserId_whenGetBookByISBN_thenReturnBook() {
        String validUserId = "123456789012";
        Book result = library.getBookByISBN(isbn1, validUserId);
        assertEquals(book1, result);
    }

    @Test
    void givenInvalidISBNAndValidUserId_whenGetBookByISBN_thenThrowIllegalArgumentException() {
        String validUserId = "123456789012";
        assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN("invalidISBN", validUserId));
    }

    @Test
    void givenValidISBNAndInvalidUserId_whenGetBookByISBN_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(isbn1, "invalidUserId"));
    }

    @Test
    void givenNullISBNAndValidUserId_whenGetBookByISBN_thenThrowIllegalArgumentException() {
        String validUserId = "123456789012";
        assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(null, validUserId));
    }

    @Test
    void givenValidISBNAndNullUserId_whenGetBookByISBN_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(isbn1, null));
    }

    @Test
    void givenValidISBNAndValidUserIdAndBorrowedBook_whenGetBookByISBN_thenThrowBookAlreadyBorrowedException() {
        book1.borrow();
        String validUserId = "123456789012";
        assertThrows(BookAlreadyBorrowedException.class, () -> library.getBookByISBN(isbn1, validUserId));
    }

    @Test
    void givenValidISBNAndValidUserIdAndNonexistentBook_whenGetBookByISBN_thenThrowBookNotFoundException() {
        when(mockDatabaseService.getBookByISBN(anyString())).thenReturn(null);
        String validUserId = "123456789012";
        assertThrows(BookNotFoundException.class, () -> library.getBookByISBN(isbn1, validUserId));
    }


    @Test
    void givenValidISBNAndValidUserId_whenGetBookByISBN_thenNotBorrowed() {
        String validUserId = "123456789012";
        Book result = library.getBookByISBN(isbn1, validUserId);
        assertFalse(result.isBorrowed());
    }

    @Test
    void givenValidISBNAndValidUserId_whenGetBookByISBN_thenNotReserved() {
        String validUserId = "123456789012";
        Book result = library.getBookByISBN(isbn1, validUserId);
        assertFalse(result.isBorrowed());
    }



    @Test
    void givenValidISBNAndValidUserIdAndExceptionThrown_whenGetBookByISBN_thenNotCatchException() {
        when(mockDatabaseService.getBookByISBN(isbn1)).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> library.getBookByISBN(isbn1, "123456789012"),"Database error");
    }
    @Test
    void givenValidISBNAndValidUserIdNoNotifation_whenGetBookByISBN_thenGoodBookReturned() {
        assertSame(book1,library.getBookByISBN(isbn1,id1));
    }
    @Test
    void givenInValidISBNAndValidUserId_whenGetBookByISBN_thenThrows() {
        isbn1="blalbla";
        assertThrows(IllegalArgumentException.class,()->library.getBookByISBN(isbn1, id1),"Invalid ISBN.");
        verify(mockDatabaseService,never()).getBookByISBN(anyString());
    }
    @Test
    void givenValidISBNAndInValidUserId_whenGetBookByISBN_thenThrows() {
        id1="blalbla";
        assertThrows(IllegalArgumentException.class,()->library.getBookByISBN(isbn1, id1),"Invalid user Id.");
        verify(mockDatabaseService,never()).getBookByISBN(anyString());
    }
    @Test
    void givenValidISBNAndNullUserId_whenGetBookByISBN_thenThrows() {
        id1=null;
        assertThrows(IllegalArgumentException.class,()->library.getBookByISBN(isbn1, id1),"Invalid user Id.");
        verify(mockDatabaseService,never()).getBookByISBN(anyString());
    }
    @Test
    void givenValidISBNAndValidUserIdInvalidDatabase_whenGetBookByISBN_thenThrows() {
        when(mockDatabaseService.getBookByISBN(isbn1)).thenThrow(new IllegalArgumentException("Database down."));
        assertThrows(IllegalArgumentException.class,()->library.getBookByISBN(isbn1, id1),"Database down.");
    }
    @Test
    void givenValidISBNAndValidUserButNoBook_whenGetBookByISBN_thenThrows() {
        when(mockDatabaseService.getBookByISBN(isbn1)).thenReturn(null);

        assertThrows(BookNotFoundException.class,()->library.getBookByISBN(isbn1, id1),"Book not found!");
    }
    @Test
    void givenValidISBNAndValidUserBookBorrowed_whenGetBookByISBN_thenThrows() {
        book1.borrow();
        assertThrows(BookAlreadyBorrowedException.class,()->library.getBookByISBN(isbn1, id1),"Book was already borrowed!");
    }
    @Test
    void givenValidISBNAndValidUserNotfiIsDown_whenGetBookByISBN_thenWorksFine() {
        Library spyLib=spy(library);
        doThrow(new IllegalArgumentException("Testing")).when(spyLib).notifyUserWithBookReviews(anyString(),anyString());
        assertSame(book1,library.getBookByISBN(isbn1,id1));
    }

    //endregion

    //region notifyUserWithBookReviews
    @Test
    void givenValidISBNAndInValidUser_whenNotifyUserWithBookReviews_thenThrows() {
        id1="blalbla5";
        assertThrows(IllegalArgumentException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Invalid user Id.");
    }
    @Test
    void givenValidISBNAndnullUser_whenNotifyUserWithBookReviews_thenThrows() {
        id1=null;
        assertThrows(IllegalArgumentException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Invalid user Id.");
    }
    @Test
    void givenInvalidISBNAndInValidUser_whenNotifyUserWithBookReviews_thenThrows() {
        isbn1="fsfsfs";
        assertThrows(IllegalArgumentException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Invalid ISBN.");
    }
    @Test
    void givenInvalidISBNAndnulldUser_whenNotifyUserWithBookReviews_thenThrows() {
        isbn1=null;
        assertThrows(IllegalArgumentException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Invalid ISBN.");
    }
    @Test
    void givenInvalidISBNAndValiddUserBrokenDatabase_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockDatabaseService.getBookByISBN(isbn1)).thenThrow(new IllegalArgumentException("Database down."));
        assertThrows(IllegalArgumentException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Database down.");
    }
    @Test
    void givenInvalidISBNAndValiddUserDatabaseReturnBookNUll_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockDatabaseService.getBookByISBN(isbn1)).thenReturn(null);
        assertThrows(BookNotFoundException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Book not found!");
    }
    @Test
    void givenInvalidISBNAndValiddUserDatabaseReturnUserNUll_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockDatabaseService.getUserById(id1)).thenReturn(null);
        assertThrows(UserNotRegisteredException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"User not found!");
    }
    @Test
    void givenInvalidISBNAndValiddUserDatabaseBreak_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockDatabaseService.getUserById(id1)).thenThrow(new IllegalArgumentException("Database down."));
        assertThrows(IllegalArgumentException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Database down.");
    }
    @Test
    void givenInvalidISBNAndValiddUserDatabaseBreakInReviews_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockReviewService.getReviewsForBook(isbn1)).thenThrow(new ReviewException("for testing."));
        assertThrows(ReviewServiceUnavailableException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"Review service unavailable!");
        verify(mockReviewService,times(1)).close();
    }
    @Test
    void givenInvalidISBNAndValiddUserDatabaseBreakInReviews2_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockReviewService.getReviewsForBook(isbn1)).thenThrow(new IllegalArgumentException("for testing."));
        assertThrows(IllegalArgumentException.class,()->library.notifyUserWithBookReviews(isbn1, id1),"for testing.");
        verify(mockReviewService,times(1)).close();
    }
    @Test
    void givenInvalidISBNAndValiddUserDatabaseBreakInReviews3_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockReviewService.getReviewsForBook(isbn1)).thenReturn(new ArrayList<>());
        assertThrows(NoReviewsFoundException.class,()->library.notifyUserWithBookReviews(isbn1, id1));
        verify(mockReviewService,times(1)).close();
    }
    @Test
    void givenInvalidISBNAndValiddUserDatabaseBreakInReviews4_whenNotifyUserWithBookReviews_thenThrows() {
        when(mockReviewService.getReviewsForBook(isbn1)).thenReturn(null);
        assertThrows(NoReviewsFoundException.class,()->library.notifyUserWithBookReviews(isbn1, id1));
        verify(mockReviewService,times(1)).close();
    }
    @Test
    void givenInvalidISBNAndValiddUser_whenNotifyUserWithBookReviews_thenSucces() {
        ArrayList<String> reviews= new ArrayList<>(Arrays.asList("abasdas", "fsfsfs"));
        String notificationMessage = "Reviews for '" + book1.getTitle() + "':\n" + String.join("\n", reviews);
        when(mockReviewService.getReviewsForBook(isbn1)).thenReturn(reviews);
        User mockUser=mock(User.class);
        when(mockDatabaseService.getUserById(id1)).thenReturn(mockUser);
        library.notifyUserWithBookReviews(isbn1,id1);
        verify(mockReviewService,times(1)).close();
        verify(mockUser,times(1)).sendNotification(notificationMessage);

    }
    @Test
    void givenInvalidISBNAndValiddUserInvalidSendNotification_whenNotifyUserWithBookReviews_thenSucces() {
        ArrayList<String> reviews= new ArrayList<>(Arrays.asList("abasdas", "fsfsfs"));
        String notificationMessage = "Reviews for '" + book1.getTitle() + "':\n" + String.join("\n", reviews);
        when(mockReviewService.getReviewsForBook(isbn1)).thenReturn(reviews);
        User mockUser=mock(User.class);
        when(mockDatabaseService.getUserById(id1)).thenReturn(mockUser);
        library.notifyUserWithBookReviews(isbn1,id1);
        verify(mockReviewService,times(1)).close();
        verify(mockUser,times(1)).sendNotification(notificationMessage);

    }
    @Test
    void givenValidUserValidBook_whenNotifyUserWithBookReviewsSendNotificationDontWorkMoreThen5Times_throws3() {
        when(mockDatabaseService.getUserById("102030405060")).thenReturn(null);
        when(mockDatabaseService.getBookByISBN("9780140449266")).thenReturn(new Book("9780140449266", "title", "author"));

        UserNotRegisteredException userNotRegisteredException =
                assertThrows(UserNotRegisteredException.class, () -> library.notifyUserWithBookReviews("9780140449266", "102030405060"));
        assertEquals(userNotRegisteredException.getMessage(), "User not found!");
    }
    @Test
    void givenValidUserValidBook_whenNotifyUserWithBookReviewsSendNotificationDontWorkMoreThen5Times_throws2() {
        when(mockReviewService.getReviewsForBook(isbn1)).thenThrow(new ReviewException("error"));

        ReviewServiceUnavailableException reviewServiceUnavailableException =
                assertThrows(ReviewServiceUnavailableException.class, () -> library.notifyUserWithBookReviews(isbn1, id1));
        assertEquals(reviewServiceUnavailableException.getMessage(), "Review service unavailable!");
    }
    @Test
    void givenValidUserValidBook_whenNotifyUserWithBookReviewsSendNotificationDontWorkMoreThen5Times_throws() {
        String id=generateID();
        String isbn=generateIsbn();
        Book book = new Book(isbn, "TitleTest", "AuthorTest");
        User user = Mockito.spy(new User("guy", id, mockNotificationService));

        doThrow(new NotificationException("error message")).when(user).sendNotification(anyString());
        when(mockDatabaseService.getBookByISBN(isbn)).thenReturn(book);
        when(mockDatabaseService.getUserById(id)).thenReturn(user);
        when(mockReviewService.getReviewsForBook(isbn)).thenReturn(Arrays.asList("Review 1", "Review 2"));

        NotificationException exception = assertThrows(NotificationException.class, () -> library.notifyUserWithBookReviews(isbn, id));
        assertEquals(exception.getMessage(), "Notification failed!");
        verify(user, times(5)).sendNotification(anyString());
    }

    //endregion

    //region private methoods
    private static String generateIsbn() {
        StringBuilder isbnBuilder = new StringBuilder("978"); // Prefix for ISBN-13

        // Generate the next 9 digits randomly
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            isbnBuilder.append(random.nextInt(10));
        }

        // Calculate the check digit
        int checkDigit = calculateIsbn13CheckDigit(isbnBuilder.toString());

        // Append the check digit to complete the ISBN-13
        isbnBuilder.append(checkDigit);

        return isbnBuilder.toString();
    }

    private static int calculateIsbn13CheckDigit(String isbnWithoutCheckDigit) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbnWithoutCheckDigit.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }


        return (10 - (sum % 10)) % 10;
    }
    private static String generateID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            int randomDigit = random.nextInt(10); // Generate a random digit (0 to 9)
            sb.append(randomDigit);
        }

        return sb.toString();
    }
    //endregion
}
