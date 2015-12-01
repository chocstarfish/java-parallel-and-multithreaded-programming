package me.shuai.member;

import me.shuai.library.Book;
import me.shuai.library.LibraryServer;

import java.rmi.RemoteException;
import java.util.List;


public class MemberImpl implements Member {

    /**
     * Default constructor of the member client. Initializes variables.
     * You may add other constructors if you need.
     */
    public MemberImpl() {
        // IMPLEMENT THIS
    }

    /* (non-Javadoc)
     * @see member.Member#getName()
     */
    public String getName() throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    /* (non-Javadoc)
     * @see member.Member#register()
     */
    public boolean register() throws RemoteException {
        // IMPLEMENT THIS
        return false;
    }

    /* (non-Javadoc)
     * @see member.Member#checkoutBook(java.lang.String)
     */
    public boolean checkoutBook(String bookName) throws RemoteException {
        // IMPLEMENT THIS
        return false;
    }

    /* (non-Javadoc)
     * @see member.Member#returnBook(java.lang.String)
     */
    public boolean returnBook(String bookName) throws RemoteException {
        // IMPLEMENT THIS
        return false;
    }

    /* (non-Javadoc)
     * @see member.Member#getServer()
     */
    public LibraryServer getServer() throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    /* (non-Javadoc)
     * @see member.Member#setServer(library.LibraryServer)
     */
    public void setServer(LibraryServer server) throws RemoteException {
        // IMPLEMENT THIS
    }

    /* (non-Javadoc)
     * @see member.Member#getBooksCheckedOut()
     */
    public List<Book> getBooksCurrCheckedOut() throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    /* (non-Javadoc)
     * @see member.Member#getBooksRead()
     */
    public List<String> getBooksRead() throws RemoteException {
        // IMPLEMENT THIS
        return null;
    }

    public static void main(String[] args) {

    }

}
