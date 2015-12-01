package me.shuai.library;

import me.shuai.member.MemberData;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


public class LibraryServerImpl implements LibraryServer, Serializable {

    /**
     * Constructor for the library server. It is given a number total books to have, number of
     * copies per book, and maximum books per member.
     * Creates a number of Book objects based on numBooks to give them to members when checking them out.
     * The server maintains the properties and enforces them for future transactions.
     *
     * @param numBooks
     * @param copiesPerBook
     * @param booksPerMember
     */
    public LibraryServerImpl(int numBooks, int copiesPerBook, int booksPerMember) {
        // IMPLEMENT THIS
    }

    /* (non-Javadoc)
     * @see library.LibraryServer#registerMember(member.Member)
     */
    @Override
    public Integer registerMember(MemberData memberdata) throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    /* (non-Javadoc)
     * @see library.LibraryServer#checkoutBook(java.lang.String, member.Member)
     */
    @Override
    public Book checkoutBook(String bookName, MemberData memberdata) throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    /* (non-Javadoc)
     * @see library.LibraryServer#returnBook(java.lang.String, member.Member)
     */
    @Override
    public boolean returnBook(String bookName, MemberData memberdata) throws RemoteException {
        // IMPLEMENT THIS
        return false;
    }

    /* (non-Javadoc)
     * @see library.LibraryServer#getBookListings()
     */
    @Override
    public List<String> getBookListings() throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    /* (non-Javadoc)
     * @see library.LibraryServer#getAvailableBookListings()
     */
    @Override
    public List<String> getAvailableBookListings() throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "LibraryServer";
            LibraryServer engine = new LibraryServerImpl(10, 10, 5);
            LibraryServer stub = (LibraryServer) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("LibraryServer bound");
        } catch (Exception e) {
            System.err.println("LibraryServer exception:");
            e.printStackTrace();
        }
    }

}
