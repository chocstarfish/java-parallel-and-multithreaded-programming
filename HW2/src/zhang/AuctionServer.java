package zhang;

/**
 * @author YOUR NAME SHOULD GO HERE
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AuctionServer {
    /**
     * Singleton: the following code makes the server a Singleton. You should
     * not edit the code in the following noted section.
     * <p/>
     * For test purposes, we made the constructor protected.
     */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
    protected AuctionServer() {
    }

    private static AuctionServer instance = new AuctionServer();

    public static AuctionServer getInstance() {
        return instance;
    }

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


    /**
     * Server statistic variables and access methods:
     */
    private int soldItemsCount = 0;
    private int revenue = 0;

    public int soldItemsCount() {
        return this.soldItemsCount;
    }

    public int revenue() {
        return this.revenue;
    }


    /**
     * Server restriction constants:
     */
    public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
    public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
    public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */


    /**
     * Some variables we think will be of potential use as you implement the server...
     */

    // List of items currently up for bidding (will eventually remove things that have expired).
    private List<Item> itemsUpForBidding = new ArrayList<Item>();


    // The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
    private int lastListingID = -1;

    // List of item IDs and actual items.  This is a running list with everything ever added to the auction.
    private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

    // List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
    private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

    // List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
    private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>();


    // List of sellers and how many items they have currently up for bidding.
    private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

    // List of buyers and how many items on which they are currently bidding.
    private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();


    // Object used for instance synchronization if you need to do it at some point
    // since as a good practice we don't use synchronized (this) if we are doing internal
    // synchronization.
    //
    private final Object instanceLock = new Object();



	
	
	



	/*
     *  The code from this point forward can and should be changed to correctly and safely
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


    /**
     * Attempt to submit an <code>Item</code> to the auction
     *
     * @param sellerName         Name of the <code>Seller</code>
     * @param itemName           Name of the <code>Item</code>
     * @param lowestBiddingPrice Opening price
     * @param biddingDurationMs  Bidding duration in milliseconds
     * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
     */
    public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs) {
        // TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //   Make sure there's room in the auction site.
        //   If the seller is a new one, add them to the list of sellers.
        //   If the seller has too many items up for bidding, don't let them add this one.
        //   Don't forget to increment the number of things the seller has currently listed.
        synchronized (instanceLock) {
            int result = -1;

            // check if the server capacity has been reached
            if (itemsUpForBidding.size() < serverCapacity) {
                Integer sellerItemCount = 0;
                if (itemsPerSeller.containsKey(sellerName)) {
                    // existing seller
                    sellerItemCount = itemsPerSeller.get(sellerName);
                } else {
                    // create a new seller if not existed
                    itemsPerSeller.put(sellerName, 0);
                }

                // check if maxSellerItems has been reached
                if (sellerItemCount < maxSellerItems) {
                    // increase the seller item count by 1
                    itemsPerSeller.put(sellerName, sellerItemCount + 1);

                    // increase the lastListingID by 1
                    Integer newId = ++lastListingID;

                    // create a new item and add it into the list/hashmap
                    Item item = new Item(sellerName, itemName, newId, lowestBiddingPrice, biddingDurationMs);
                    itemsUpForBidding.add(item);
                    itemsAndIDs.put(newId, item);
                    result = newId;
                }
            }

            return result;
        }
    }


    /**
     * Get all <code>Items</code> active in the auction
     *
     * @return A copy of the <code>List</code> of <code>Items</code>
     */
    public List<Item> getItems() {
        // TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //    Don't forget that whatever you return is now outside of your control.
        synchronized (itemsUpForBidding) {
            // make a copy of the itemsUpForBidding ArrayList for others to play with
            return new ArrayList<Item>(itemsUpForBidding);
        }
    }


    /**
     * Attempt to submit a bid for an <code>Item</code>
     *
     * @param bidderName    Name of the <code>Bidder</code>
     * @param listingID     Unique ID of the <code>Item</code>
     * @param biddingAmount Total amount to bid
     * @return True if successfully bid, false otherwise
     */
    public boolean submitBid(String bidderName, int listingID, int biddingAmount) {
        // TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //   See if the item exists.
        //   See if it can be bid upon.
        //   See if this bidder has too many items in their bidding list.
        //   Get current bidding info.
        //   See if they already hold the highest bid.
        //   See if the new bid isn't better than the existing/opening bid floor.
        //   Decrement the former winning bidder's count
        //   Put your bid in place
        synchronized (instanceLock) {
            // check if the item exists
            if (itemsAndIDs.containsKey(listingID)) {
                Item item = itemsAndIDs.get(listingID);

                // check if the item is still open
                if (item.biddingOpen()) {
                    int currentBidCount = 0;

                    // check if the buyer has ever bidded for any items
                    if (itemsPerBuyer.containsKey(bidderName)) {
                        currentBidCount = itemsPerBuyer.get(bidderName);
                        // check if the bidder has too many items in the bidding list
                        if (currentBidCount >= maxBidCount) {
                            return false;
                        }
                    }

                    if (highestBids.containsKey(listingID)) {
                        // item already been bidded
                        int currentHighestBid = highestBids.get(listingID);

                        // check if the current bidding amount exceeds the current highest bid
                        if (biddingAmount <= currentHighestBid) {
                            return false;
                        }

                        if (highestBidders.containsKey(listingID)) {
                            String highestBidderName = highestBidders.get(listingID);
                            // check if the buyer already holds the highest bid
                            if (highestBidderName.equals(bidderName)) {
                                return false;
                            }

                            // decrease the number of bids for the former highest bidder
                            itemsPerBuyer.put(highestBidderName, itemsPerBuyer.get(highestBidderName) - 1);
                        }
                    }

                    itemsPerBuyer.put(bidderName, ++currentBidCount);

                    // update the highest bids and bidder with current buyer
                    highestBids.put(listingID, biddingAmount);
                    highestBidders.put(listingID, bidderName);
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
     *
     * @param bidderName Name of <code>Bidder</code>
     * @param listingID  Unique ID of the <code>Item</code>
     * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
     * 2 (open) if this <code>Item</code> is still up for auction<br>
     * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
     */

    public int checkBidStatus(String bidderName, int listingID) {
        // TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //   If the bidding is closed, clean up for that item.
        //     Remove item from the list of things up for bidding.
        //     Decrease the count of items being bid on by the winning bidder if there was any...
        //     Update the number of open bids for this seller
        synchronized (instanceLock) {
            int result = 3;
            // check if the item and the buyer exist in the lists
            if (!itemsAndIDs.containsKey(listingID) || !itemsPerBuyer.containsKey(bidderName)) {
                return result;
            }

            Item item = itemsAndIDs.get(listingID);

            // check if the item is still open for bidding
            if (item.biddingOpen()) {
                result = 2;
            } else if (itemsUpForBidding.contains(item)) {
                // remove item from server
                itemsUpForBidding.remove(item);

                // check if the item has ever been bidded
                if (highestBidders.containsKey(listingID)) {
                    String highestBidder = highestBidders.get(listingID);

                    // update the highest bidder's item count
                    itemsPerBuyer.put(highestBidder, itemsPerBuyer.get(highestBidder) - 1);

                    String seller = item.seller();

                    // update the seller's item count
                    itemsPerSeller.put(seller, itemsPerSeller.get(seller) - 1);

                    // check if the highest bidder is the current bidder
                    if (highestBidder.equals(bidderName)) {
                        result = 1;
                    }

                    // increase the sold items count
                    soldItemsCount++;

                    // increase the revenue
                    revenue += highestBids.get(listingID);
                }

            }
            return result;
        }
    }

    /**
     * Check the current bid for an <code>Item</code>
     *
     * @param listingID Unique ID of the <code>Item</code>
     * @return The highest bid so far or the opening price if no bid has been made,
     * -1 if no <code>Item</code> exists
     */
    public int itemPrice(int listingID) {
        // TODO: IMPLEMENT CODE HERE
        synchronized (instanceLock) {
            int result = -1;
            // check if the listingID exists in the list
            if (!itemsAndIDs.containsKey(listingID)) {
                return result;
            }

            // check if the item has been bidded
            if (highestBids.containsKey(listingID)) {
                result = highestBids.get(listingID);
            } else {
                // get the opening price of the item
                for (Item item : itemsUpForBidding) {
                    if (item.listingID() == listingID) {
                        return item.lowestBiddingPrice();
                    }
                }
            }

            return result;
        }
    }

    /**
     * Check whether an <code>Item</code> has been bid upon yet
     *
     * @param listingID Unique ID of the <code>Item</code>
     * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
     */
    public Boolean itemUnbid(int listingID) {
        // TODO: IMPLEMENT CODE HERE
        // check if an item exists and has not been bidded
        return !itemsAndIDs.containsKey(listingID) || !highestBids.containsKey(listingID);
    }


}
 