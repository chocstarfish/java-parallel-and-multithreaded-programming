package me.shuai.client;

import me.shuai.core.Book;
import me.shuai.core.LibraryServer;
import me.shuai.core.Member;
import me.shuai.core.MemberData;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MemberImpl implements Member {

    private LibraryServer libraryServer;
    private static AtomicInteger memberCounter = new AtomicInteger(1);
    private MemberData member;

    /**
     * Default constructor of the me.shuai.server.client client. Initializes variables.
     * You may add other constructors if you need.
     */
    public MemberImpl() {
        int memberId = memberCounter.getAndIncrement();
        member = new MemberData("Member" + memberId, new ArrayList<Book>(), null, new ArrayList<String>());
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#getName()
     */
    public String getName() throws RemoteException {
        return member.getName();
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#register()
     */
    public boolean register() throws RemoteException {
        int id = libraryServer.registerMember(member);
        if (id != 0) {
            member.setMemberId(id);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#checkoutBook(java.lang.String)
     */
    public boolean checkoutBook(String bookName) throws RemoteException {
        Book request = libraryServer.checkoutBook(bookName, member);
        if (request != null) {
            member.getBooksCurrCheckedOut().add(request);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#returnBook(java.lang.String)
     */
    public boolean returnBook(String bookName) throws RemoteException {
        if (libraryServer.returnBook(bookName, member)) {
            for (Book book : member.getBooksCurrCheckedOut()) {
                if (book.getName().equals(bookName)) {
                    member.getBooksCurrCheckedOut().remove(book);
                }
            }

            member.getBooksRead().add(bookName);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#getServer()
     */
    public LibraryServer getServer() throws RemoteException {
        return libraryServer;
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#setServer(me.shuai.core.LibraryServer)
     */
    public void setServer(LibraryServer server) throws RemoteException {
        libraryServer = server;
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#getBooksCheckedOut()
     */
    public List<Book> getBooksCurrCheckedOut() throws RemoteException {
        return member.getBooksCurrCheckedOut();
    }

    /* (non-Javadoc)
     * @see me.shuai.server.client.Member#getBooksRead()
     */
    public List<String> getBooksRead() throws RemoteException {
        return member.getBooksRead();
    }

    public static void main(String[] args) {
        try {
            String name = "Library";
            Registry registry = LocateRegistry.getRegistry();
            LibraryServer libraryServer = (LibraryServer) registry.lookup(name);

            List<String> bookListings = libraryServer.getBookListings();
            System.out.println(bookListings);

            List<String> availableBooks = libraryServer.getAvailableBookListings();
            System.out.println(availableBooks);
        } catch (Exception e) {
            System.err.println("Library exception:");
            e.printStackTrace();
        }
    }

}
