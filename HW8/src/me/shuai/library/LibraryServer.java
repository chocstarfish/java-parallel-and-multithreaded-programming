package me.shuai.library;

import me.shuai.member.MemberData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LibraryServer extends Remote {
	
	/**
	 * Registers a new member. Returns a member id if successful, otherwise null.
	 * The server keeps track of the id and it is used to authenticate future 
	 * communication between the registered member and library.
	 * 
	 * @param memberdata
	 * @return Integer
	 * @throws RemoteException
	 */
	public Integer registerMember(MemberData memberdata) throws RemoteException;
	
	/**
	 * Checks out the given book and gives it to the member's account ensuring the member 
	 * is valid (member id), that there are sufficient copies of the book, that the member does 
	 * not have the book already, and that the member has not exceeded its book capacity. 
	 * Returns the Book object if successful, otherwise returns null.
	 * 
	 * @param bookName
	 * @param memberdata
	 * @return Book
	 * @throws RemoteException
	 */
	public Book checkoutBook(String bookName, MemberData memberdata) throws RemoteException;
	
	/**
	 * Returns the given book and removes it from the member's account ensuring the member 
	 * is valid (member id) and that the member currently has the book. Returns true if successful.
	 * 
	 * @param bookName
	 * @param memberdata
	 * @return boolean
	 * @throws RemoteException
	 */
	public boolean returnBook(String bookName, MemberData memberdata) throws RemoteException;
	
	/**
	 * Returns a listing of all the books.
	 * 
	 * @return List<String>
	 * @throws RemoteException
	 */
	public List<String> getBookListings() throws RemoteException;
	
	/**
	 * Returns a listing of all the books that have at least one available copy ready for checkout.
	 * 
	 * @return List<String>
	 * @throws RemoteException
	 */
	public List<String> getAvailableBookListings() throws RemoteException;
	
}
