package cmsc433.p1;

/**
 * @author Zhehao Chen (zchen27)
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected.
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */

	/*
	 * Statistic variables and server constants: Begin code you should likely
	 * leave alone.
	 */

	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
		return this.revenue;
	}

	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at
												 // any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items
												 // that a seller can submit at
												 // any given time.
	public static final int serverCapacity = 80; // The maximum number of active
												 // items at a given time.

	/*
	 * Statistic variables and server constants: End code you should likely
	 * leave alone.
	 */

	/**
	 * Some variables we think will be of potential use as you implement the
	 * server...
	 */

	// List of items currently up for bidding (will eventually remove things
	// that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();

	// The last value used as a listing ID. We'll assume the first thing added
	// gets a listing ID of 0.
	private int lastListingID = -1;

	// List of item IDs and actual items. instanceLock is a running list with
	// everything
	// ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item. instanceLock is a
	// running list
	// with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.
	// instanceLock is a running list with everything ever bid upon.
	private volatile HashMap<Integer, String> highestBidders = new HashMap<Integer, String>();

	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

	// Object used for instance synchronization if you need to do it at some
	// point
	// since as a good practice we don't use synchronized (instanceLock) if we
	// are doing
	// internal
	// synchronization.
	//
	private Object instanceLock = new Object();

	/*
	 * The code from instanceLock point forward can and should be changed to
	 * correctly and safely implement the methods as needed to create a working
	 * multi-threaded server for the system. If you need to add Object instances
	 * here to use for locking, place a comment with them saying what they
	 * represent. Note that if they just represent one structure then you should
	 * probably be using that structure's intrinsic lock.
	 */

	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * 
	 * @param sellerName
	 *        Name of the <code>Seller</code>
	 * @param itemName
	 *        Name of the <code>Item</code>
	 * @param lowestBiddingPrice
	 *        Opening price
	 * @param biddingDurationMs
	 *        Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed
	 *         successfully, otherwise -1
	 */
	public synchronized int submitItem(String sellerName, String itemName,
			int lowestBiddingPrice, int biddingDurationMs)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		// Make sure there's room in the auction site.
		// If the seller is a new one, add them to the list of sellers.
		// If the seller has too many items up for bidding, don't let them add
		// instanceLock one.
		// Don't forget to increment the number of things the seller has
		// currently listed.

		Item it;
		int numsells;

		// Checks how many items a seller has
		if (itemsPerSeller.containsKey(sellerName))
		{
			numsells = itemsPerSeller.get(sellerName);
		}
		else
		{
			numsells = 0;
		}

		// Checks for server capacity
		if (itemsUpForBidding.size() >= serverCapacity
				|| numsells >= maxSellerItems)
		{
			
			//System.out.println("Adding " + itemName + " FAILED");
			return -1;
		}

		// Seller is new
		if (itemsPerSeller.get(sellerName) == null)
		{
			itemsPerSeller.putIfAbsent(sellerName, 1);
		}

		numsells++;
		lastListingID++;
		it = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice,
				biddingDurationMs);
		itemsAndIDs.put(lastListingID, it);
		itemsUpForBidding.add(it);
		itemsPerSeller.put(sellerName, numsells);

		return lastListingID;
	}

	/**
	 * Get all <code>Items</code> active in the auction
	 * 
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	public List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		// Don't forget that whatever you return is now outside of your control.
		List<Item> l = new ArrayList<Item>(itemsUpForBidding);
		// System.out.println(l.size());
		return l;
	}

	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * 
	 * @param bidderName
	 *        Name of the <code>Bidder</code>
	 * @param listingID
	 *        Unique ID of the <code>Item</code>
	 * @param biddingAmount
	 *        Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public synchronized boolean submitBid(String bidderName, int listingID,
			int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		// See if the item exists.
		// See if it can be bid upon.
		// See if instanceLock bidder has too many items in their bidding list.
		// Get current bidding info.
		// See if they already hold the highest bid.
		// See if the new bid isn't better than the existing/opening bid floor.
		// Decrement the former winning bidder's count
		// Put your bid in place

		// No Item or closed
		if (!itemsAndIDs.containsKey(listingID)
				|| !itemsAndIDs.get(listingID).biddingOpen())
		{
			return false;
		}

		int numbids;
		int currbid;
		String oldBuyer;
		int oldbids;

		if (!itemsPerBuyer.containsKey(bidderName))
		{
			numbids = 0;
		}
		else
		{
			numbids = itemsPerBuyer.get(bidderName);
		}

		// Max bids reached
		if (numbids >= maxBidCount)
		{
			return false;
		}

		if (highestBidders.containsKey(listingID))
		{
			// Already have highest bid
			if (highestBidders.get(listingID).equals(bidderName))
			{
				return false;
			}
		}

		if (highestBids.containsKey(listingID))
		{
			currbid = highestBids.get(listingID);

			// Insufficiently high bid
			if (biddingAmount <= currbid)
			{
				return false;
			}
		}
		else
		{
			currbid = itemsAndIDs.get(listingID).lowestBiddingPrice();

			// Can bid at opening price
			if (biddingAmount < currbid)
			{
				return false;
			}
		}

		// Decrement old buyer
		if (highestBidders.containsKey(listingID))
		{
			oldBuyer = highestBidders.get(listingID);
			if (itemsPerBuyer.containsKey(oldBuyer))
			{
				oldbids = itemsPerBuyer.get(oldBuyer);
				oldbids--;
			}
			else
			{
				oldbids = 0;
			}
			itemsPerBuyer.put(oldBuyer, oldbids);
		}

		// Update to new buyer
		numbids++;
		highestBids.put(listingID, biddingAmount);
		highestBidders.put(listingID, bidderName);
		itemsPerBuyer.put(bidderName, numbids);
		
		//System.out.println("HightestBidder " + highestBidders.get(listingID) + System.currentTimeMillis());
		return true;
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * 
	 * @param bidderName
	 *        Name of <code>Bidder</code>
	 * @param listingID
	 *        Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and instanceLock <code>Bidder</code>
	 *         has won <br>
	 *         2 (open) if instanceLock <code>Item</code> is still up for
	 *         auction<br>
	 *         3 (failed) If instanceLock <code>Bidder</code> did not win or the
	 *         <code>Item</code> does not exist
	 */
	public synchronized int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		// If the bidding is closed, clean up for that item.
		// Remove item from the list of things up for bidding.
		// Decrease the count of items being bid on by the winning bidder if
		// there was any...
		// Update the number of open bids for instanceLock seller

		// No Item
		if (!itemsAndIDs.containsKey(listingID))
		{
			return 3;
		}

		// Item no longer open
		if (!itemsAndIDs.get(listingID).biddingOpen())
		{
			// Close the item and lowers seller count
			String seller = itemsAndIDs.get(listingID).seller();
			int sellernum = itemsPerSeller.get(seller);
			itemsPerSeller.put(seller, sellernum - 1);
			
			// If this item was bid upon
			if (highestBidders.containsKey(listingID))
			{
				
				//Lowers buyer's utilization count
				String buyer = highestBidders.get(listingID);
				int buyernum = itemsPerBuyer.get(buyer);
				buyernum--;
				System.out.println(buyer + " " + buyernum);
				itemsPerBuyer.put(buyer, buyernum);
				itemsUpForBidding.remove(itemsAndIDs.get(listingID));

				
				// Check if bidder actually placed the highest bid
				//System.out.println(bidderName + " " + highestBidders.get(listingID) + " is Actual Highest  " + System.currentTimeMillis());
				if (highestBidders.get(listingID).equals(bidderName))
				{
					// Congratulations, you won!
					// Decrements buyer count
					//System.out.println("SOLD" + itemsAndIDs.get(listingID).name());
					
					//Adds revenue and soldItems
					revenue += itemPrice(listingID);
					soldItemsCount++;
					return 1;
				}
			}

			// Bid failed
			return 3;
		}

		// still open
		return 2;
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * 
	 * @param listingID
	 *        Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been
	 *         made, -1 if no <code>Item</code> exists
	 */
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE

		if (!itemsAndIDs.containsKey(listingID))
		{
			return -1;
		}

		if (highestBids.containsKey(listingID))
		{
			return highestBids.get(listingID);
		}
		else
		{
			return itemsAndIDs.get(listingID).lowestBiddingPrice();
		}
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * 
	 * @param listingID
	 *        Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist,
	 *         false otherwise
	 */
	public Boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE

		return !highestBids.containsKey(listingID);
	}

}
