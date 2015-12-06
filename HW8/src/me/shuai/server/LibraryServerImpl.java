package me.shuai.server;

import me.shuai.core.Book;
import me.shuai.core.LibraryServer;
import me.shuai.core.MemberData;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class LibraryServerImpl implements LibraryServer, Serializable {

    private final int numBooks;
    private final int copiesPerBook;
    private final int booksPerMember;

    private static AtomicInteger memberCounter = new AtomicInteger(1);

    // Key: Member name, value: member data
    private final ConcurrentHashMap<String, MemberData> members = new ConcurrentHashMap<String, MemberData>();

    // Key: Book name, value: Book object
    private final ConcurrentHashMap<String, Book> books = new ConcurrentHashMap<String, Book>();

    // Key: Book object, value: availability of the book
    private final ConcurrentHashMap<Book, Integer> bookAvailabilities = new ConcurrentHashMap<Book, Integer>();

    // Key: Member name, value: HashSet of books
    private final ConcurrentHashMap<String, HashSet<Book>> checkedOutBooks = new ConcurrentHashMap<String, HashSet<Book>>();

    /**
     * Constructor for the me.shuai.server server. It is given a number total books to have, number of
     * copies per book, and maximum books per client.
     * Creates a number of Book objects based on numBooks to give them to members when checking them out.
     * The server maintains the properties and enforces them for future transactions.
     *
     * @param numBooks
     * @param copiesPerBook
     * @param booksPerMember
     */
    public LibraryServerImpl(int numBooks, int copiesPerBook, int booksPerMember) {
        this.numBooks = numBooks;
        this.copiesPerBook = copiesPerBook;
        this.booksPerMember = booksPerMember;

        // initialize the library data
        for (int i = 1; i <= numBooks; i++) {
            Book book = new Book("Book" + i);
            books.put(book.getName(), book);
            bookAvailabilities.put(book, copiesPerBook);
        }
    }

    /* (non-Javadoc)
     * @see me.shuai.core.LibraryServer#registerMember(client.Member)
     */
    @Override
    public Integer registerMember(MemberData memberdata) throws RemoteException {
        String memberName = memberdata.getName();
        if (!members.containsKey(memberName)) {
            memberdata.setMemberId(memberCounter.incrementAndGet());
            memberdata.setBooksCurrCheckedOut(new CopyOnWriteArrayList<Book>());
            memberdata.setBooksRead(new CopyOnWriteArrayList<String>());
            members.put(memberName, memberdata);
            checkedOutBooks.put(memberdata.getName(), new HashSet<Book>());
            return memberdata.getMemberId();
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see me.shuai.core.LibraryServer#checkoutBook(java.lang.String, client.Member)
     */
    @Override
    public Book checkoutBook(String bookName, MemberData memberdata) throws RemoteException {
        if (!books.containsKey(bookName)) {
            return null;
        }

        Book book = books.get(bookName);

        String memberName = memberdata.getName();
        if (!members.containsKey(memberName)) {
            return null;
        }

        if (checkedOutBooks.get(memberName).size() >= booksPerMember) {
            return null;
        }

        if (checkedOutBooks.get(memberName).contains(book)) {
            // this user has already checked out this book
            return null;
        }

        if (bookAvailabilities.get(book) == 0) {
            return null;
        }

        bookAvailabilities.put(book, bookAvailabilities.get(book) - 1);
        checkedOutBooks.get(memberName).add(book);
        return book;
    }

    /* (non-Javadoc)
     * @see me.shuai.core.LibraryServer#returnBook(java.lang.String, client.Member)
     */
    @Override
    public boolean returnBook(String bookName, MemberData memberdata) throws RemoteException {
        if (!books.containsKey(bookName)) {
            return false;
        }

        Book book = books.get(bookName);
        String name = memberdata.getName();

        if (!members.containsKey(name)) {
            return false;
        }

        MemberData member = members.get(name);

        String memberName = member.getName();
        if (!checkedOutBooks.get(memberName).contains(book)) {
            return false;
        }

        bookAvailabilities.put(book, bookAvailabilities.get(book) + 1);
        checkedOutBooks.get(memberName).remove(book);
        return true;
    }

    /* (non-Javadoc)
     * @see me.shuai.core.LibraryServer#getBookListings()
     */
    @Override
    public List<String> getBookListings() throws RemoteException {
        return new ArrayList<String>(books.keySet());
    }

    /* (non-Javadoc)
     * @see me.shuai.core.LibraryServer#getAvailableBookListings()
     */
    @Override
    public List<String> getAvailableBookListings() throws RemoteException {
        List<String> availableBookListings = new ArrayList<String>();

        for (Book book : bookAvailabilities.keySet()) {
            if (bookAvailabilities.get(book) > 0) {
                availableBookListings.add(book.getName());
            }
        }

        return availableBookListings;
    }

    public static void main(String[] args) {
        try {
            String name = "Library";
            LibraryServer engine = new LibraryServerImpl(10, 10, 5);
            LibraryServer stub = (LibraryServer) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            registry.rebind(name, stub);
            System.out.println("LibraryServer ready!");
        } catch (Exception e) {
            System.err.println("LibraryServer exception:");
            e.printStackTrace();
        }
    }

}
