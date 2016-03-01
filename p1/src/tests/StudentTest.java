package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import cmsc433.p1.*;

public class StudentTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}
	

	@Test
	public void singel() throws InterruptedException
	{
		AuctionServer as = AuctionServer.getInstance();
		// No items have yet been added - check results of methods.
		assertEquals(-1, as.itemPrice(0));
		assertTrue(as.itemUnbid(-1));
		assertEquals(0, as.getItems().size());
		assertEquals(3, as.checkBidStatus("Wo", 0));
		assertEquals(0, as.soldItemsCount());
		assertEquals(0, as.revenue());
		
		//Adds one item and checks properties
		int id0 =  as.submitItem("Ha", "21\" Torpedo Early", 4, 100);
		assertNotEquals(-1, id0);
		assertEquals(1, as.getItems().size());
		assertEquals(2, as.checkBidStatus("Re", id0));
		assertEquals(1, as.getItems().size());
		assertEquals(4, as.itemPrice(id0));
		assertTrue(as.itemUnbid(id0));
		
		//Allows item to expire
		Thread.sleep(200);
		assertEquals(1, as.getItems().size());
		assertEquals(4, as.itemPrice(id0));
		assertTrue(as.itemUnbid(id0));
		
		//Item no longer on active bid
		assertEquals(3, as.checkBidStatus("Re", id0));
		//Price unchanged
		assertEquals(4, as.itemPrice(id0));
		//Remains in list
		assertEquals(1, as.getItems().size());
		
		//Can no longer bid on item
		assertFalse(as.submitBid("I", id0, 20));
		
		//No changes to sales information
		assertEquals(0, as.revenue());
		assertEquals(0, as.soldItemsCount());
		
		//Add new item
		int id1 = as.submitItem("To", "6\" Rapid Fire Twin Mount", 5, 100);
		assertNotEquals(-1, id1);
		assertEquals(2, as.getItems().size());
		assertEquals(2, as.checkBidStatus("Wo", id1));
		assertEquals(5, as.itemPrice(id1));
		assertTrue(as.itemUnbid(id1));
		
		//Can't bid under price
		assertFalse(as.submitBid("Wo", id1, 4));
		
		//Bid on opening price
		assertTrue(as.submitBid("Ka", id1, 5));
		assertFalse(as.itemUnbid(id1));
		assertEquals(5, as.itemPrice(id1));
		
		//Another bidder cannot big at same price
		assertFalse(as.submitBid("Chi", id1, 5));
		
		//Same bidder cannot bid higher
		assertFalse(as.submitBid("Ka", id1, 6));
		assertEquals(5, as.itemPrice(id1));
		
		//Another bidder can bid higher
		assertTrue(as.submitBid("Chi", id1, 6));
		assertEquals(6, as.itemPrice(id1));
		
		// Still alive.
		assertEquals(2, as.checkBidStatus("Chi", id1));
		
		//End the auction
		Thread.sleep(200);
		
		//No changes to sales information until someone checks
		assertEquals(0, as.revenue());
		assertEquals(0, as.soldItemsCount());
		
		//Failed the auction
		assertEquals(3, as.checkBidStatus("Ka", id1));
		//Did not bid
		assertEquals(3, as.checkBidStatus("Tsu", id1));
		//Won the action
		//System.out.println("MARK");
		assertEquals(1, as.checkBidStatus("Chi", id1));
		
		//Checks business records
		assertEquals(1, as.getItems().size());
		assertEquals(1, as.soldItemsCount());
		assertEquals(6, as.revenue());
		
		//Sanity check post-bid
		assertEquals(6, as.itemPrice(id1));
		assertFalse(as.itemUnbid(id1));
		
		//Basic sanity check
		assertTrue(AuctionServer.serverCapacity >= AuctionServer.maxSellerItems);
		
		// Test the limit for a single seller.
		int[] ids = new int[AuctionServer.maxSellerItems];
		for (int i = 0; i < AuctionServer.maxSellerItems; i++)
		{
			ids[i] = as.submitItem("Wo", "Helldiver-M" + i, 1, 1000);
			assertNotEquals(-1, ids[i]);
		}
		
		//Can't add one more
		assertEquals(-1, as.submitItem("Wo", "Avenger-M", 1, 1000));
		
		int leftOver = AuctionServer.serverCapacity - as.getItems().size();
		int[] newids = new int[leftOver];

		//Tests Server stresses
		for (int i = 0; i < leftOver; i++)
		{
			newids[i] = as.submitItem("Anchorage Demon" + i, "Deterioation AP Shell" + i, 1, 1000);
			//System.out.println(i);
			assertNotEquals(-1, newids[i]);
		}
		
		//Can't add one more
		assertEquals(-1, as.submitItem("Nu", "Hellcat-M", 1, 1000));
		
		//Basic snaity check
		assertTrue(AuctionServer.maxSellerItems > AuctionServer.maxBidCount);
		
		//Tests Buyer stresses
		for (int i = 0; i < AuctionServer.maxBidCount; i++)
		{
			assertTrue(as.submitBid("Armored Carrier Princess", newids[i], 2));
		}
		assertFalse(as.submitBid("Armored Carrier Princess", newids[0], 4));
		
		//Wait a while and then go resolve stuff.
		Thread.sleep(2000);
		for (int i = 0; i < AuctionServer.maxBidCount; i++)
		{
			assertEquals(1, as.checkBidStatus("Armored Carrier Princess", newids[i]));
		}
		
		//Now buyer and seller have space again
		int n = as.submitItem("Nu", "Hellcat-M", 1, 1000);
		assertNotEquals(-1, n);
		assertTrue(as.submitBid("Armored Carrier Princess", n, 4));
	}
	
	public void singleThreadedTest() throws InterruptedException
	{
		AuctionServer auctionServer = AuctionServer.getInstance();

		// No items have yet been added - check results of methods.
		assertEquals(-1, auctionServer.itemPrice(0));
		assertEquals(true, auctionServer.itemUnbid(-1));
		assertEquals(0, auctionServer.getItems().size());
		assertEquals(3, auctionServer.checkBidStatus("Johnny", 0));
		assertEquals(0, auctionServer.soldItemsCount());
		assertEquals(0, auctionServer.revenue());

		// An item is successfully added. Check results of methods.
		int toyCarId = auctionServer.submitItem("Bobby", "ToyCar", 4, 100);
		assertFalse(-1 == toyCarId);
		assertEquals(1, auctionServer.getItems().size());
		assertEquals(2, auctionServer.checkBidStatus("Johnny", toyCarId));
		assertEquals(1, auctionServer.getItems().size());
		assertEquals(4, auctionServer.itemPrice(toyCarId));
		assertEquals(true, auctionServer.itemUnbid(toyCarId));

		// The ToyCar auction expired without bids. Check the results.
		Thread.sleep(200);
		assertEquals(1, auctionServer.getItems().size());
		assertEquals(4, auctionServer.itemPrice(toyCarId));
		assertEquals(true, auctionServer.itemUnbid(toyCarId));

		// Bid status has now been checked, so item should be removed from the
		// list.
		assertEquals(3, auctionServer.checkBidStatus("Johnny", toyCarId));
		assertEquals(4, auctionServer.itemPrice(toyCarId));
		assertEquals(0, auctionServer.getItems().size());

		// Revenue and soldItemsCount should not have changed.
		assertEquals(0, auctionServer.revenue());
		assertEquals(0, auctionServer.soldItemsCount());

		// A bid on the ToyCar should fail
		assertFalse(auctionServer.submitBid("Johnny", toyCarId, 4));

		// Now add another item to the auction
		int toyTruckId = auctionServer.submitItem("Bobby", "ToyTruck", 5, 100);
		assertFalse(-1 == toyTruckId);
		assertEquals(1, auctionServer.getItems().size());
		assertEquals("ToyTruck", auctionServer.getItems().get(0).name());
		assertEquals(2, auctionServer.checkBidStatus("Johnny", toyTruckId));
		assertEquals(5, auctionServer.itemPrice(toyTruckId));
		assertEquals(true, auctionServer.itemUnbid(toyTruckId));

		// A bid below the opening price should be denied
		assertFalse(auctionServer.submitBid("Ellie", toyTruckId, 4));

		// Someone places a bid on the ToyTruck, at the opening price.
		assertTrue(auctionServer.submitBid("Johnny", toyTruckId, 5));
		assertEquals(false, auctionServer.itemUnbid(toyTruckId));
		assertEquals(5, auctionServer.itemPrice(toyTruckId));

		// If Johnny tries to bid again, his bid should be denied
		assertFalse(auctionServer.submitBid("Johnny", toyTruckId, 6));

		// Ellie cannot bid $5, since Johnny already did, so her request should
		// be denied
		assertFalse(auctionServer.submitBid("Ellie", toyTruckId, 5));

		// However, Ellie can bid $6, to outbid Johnny.
		assertTrue(auctionServer.submitBid("Ellie", toyTruckId, 6));
		assertEquals(6, auctionServer.itemPrice(toyTruckId));

		// Bidding should still be open (or your processor is slower than a
		// turtle).
		assertEquals(2, auctionServer.checkBidStatus("Ellie", toyTruckId));
		assertTrue(auctionServer.getItems().get(0).biddingOpen());

		// Wait for auction to end.
		Thread.sleep(200);
		assertFalse(auctionServer.getItems().get(0).biddingOpen());

		// Until checkBidStatus is called, the auctionServer should not reflect
		// the bidding is over.
		assertEquals(1, auctionServer.getItems().size());
		assertEquals(0, auctionServer.soldItemsCount());
		assertEquals(0, auctionServer.revenue());

		// Ellie has won the auction, so if she checks the bid status, it should
		// return success.
		assertEquals(1, auctionServer.checkBidStatus("Ellie", toyTruckId));

		// The auctionServer should now reflect that the ToyTruck was sold to
		// Ellie for $6.
		assertEquals(0, auctionServer.getItems().size());
		assertEquals(1, auctionServer.soldItemsCount());
		assertEquals(6, auctionServer.revenue());

		// Johnny lost the auction, so if he checks the bid status, it should
		// return failure.
		assertEquals(3, auctionServer.checkBidStatus("Johnny", toyTruckId));

		// Timmy didn't bid, so if he checks the bid status, it should also
		// return failure
		assertEquals(3, auctionServer.checkBidStatus("Timmy", toyTruckId));

		// Calls to itemPrice and itemUnbid should still report correctly
		assertEquals(6, auctionServer.itemPrice(toyTruckId));
		assertEquals(false, auctionServer.itemUnbid(toyTruckId));

		// The following test will cause an exception if this statement is false
		assertTrue(
				AuctionServer.serverCapacity >= AuctionServer.maxSellerItems);
		
		
		// Test the limit for a single seller.
		int[] itemIds = new int[AuctionServer.maxSellerItems];
		for (int i = 0; i < AuctionServer.maxSellerItems; i++)
		{
			itemIds[i] = auctionServer.submitItem("Derp", "TestItem" + i, 1,
					1000);
			assertFalse(-1 == itemIds[i]);
		}

		// If Bobby attempts to add another item, it should fail
		assertEquals(-1,
				auctionServer.submitItem("Derp", "OneItemTooMany", 1, 1000));

		// Test the state of the server
		assertEquals(AuctionServer.maxSellerItems,
				auctionServer.getItems().size());
		for (int i = 0; i < itemIds.length; i++)
		{
			assertEquals(true, auctionServer.itemUnbid(itemIds[i]));
			assertEquals(1, auctionServer.itemPrice(itemIds[i]));
			assertEquals(2, auctionServer.checkBidStatus("Johnny", itemIds[i]));
		}

		// The following test will cause an exception this statement is false.
		assertTrue(AuctionServer.maxSellerItems > AuctionServer.maxBidCount);

		// Test the limit for a single bidder
		for (int i = 0; i < AuctionServer.maxBidCount; i++)
		{
			assertTrue(auctionServer.submitBid("Johnny", itemIds[i], 1));
		}
		assertFalse(auctionServer.submitBid("Johnny",
				itemIds[AuctionServer.maxBidCount], 1));

		// However, if Johnny is outbid on an item, he should be able to place
		// another bid
		assertTrue(auctionServer.submitBid("Timmy", itemIds[0], 2));
		assertTrue(auctionServer.submitBid("Johnny",
				itemIds[AuctionServer.maxBidCount], 1));

		// Wait for bidding to close and cleanup the items
		Thread.sleep(1100);
		for (int i = 0; i < itemIds.length; i++)
		{
			assertEquals(3, auctionServer.checkBidStatus("Ellie", itemIds[i]));
		}
		
		assertEquals(1, auctionServer.getItems().size());
		System.out.println(auctionServer.soldItemsCount());
		assertEquals(1 + 1 + AuctionServer.maxBidCount,
				auctionServer.soldItemsCount());
		assertEquals(6 + 2 + AuctionServer.maxBidCount,
				auctionServer.revenue());
		
		// Bobby should now be permitted to list another item
		int toyBoatId = auctionServer.submitItem("Bobby", "ToyBoat", 7, 100);
		assertFalse(-1 == toyBoatId);
		// Johnny should also be permitted to place a bid again
		assertTrue(auctionServer.submitBid("Johnny", toyBoatId, 7));

		// Wait for the ToyBoat to be sold to Johnny
		Thread.sleep(200);
		assertEquals(1, auctionServer.checkBidStatus("Johnny", toyBoatId));
		assertEquals(2 + 1 + AuctionServer.maxBidCount,
				auctionServer.soldItemsCount());
		assertEquals(8 + 7 + AuctionServer.maxBidCount,
				auctionServer.revenue());

		// Max out the serverCapacity
		for (int i = 0; i < AuctionServer.serverCapacity; i++)
		{
			assertFalse(-1 == auctionServer.submitItem("TestSeller" + i,
					"ABrandNewCar!", 30000, 1000));
		}

		// This item should be rejected, since the server is at capacity.
		assertTrue(-1 == auctionServer.submitItem("Bobby", "ToyHelicopter", 8,
				100));

		// Test that messing with the returned list from getItems() does not
		// mess up the server
		List<Item> items = auctionServer.getItems();
		assertEquals(AuctionServer.serverCapacity, items.size());
		items.remove(0);
		assertEquals(AuctionServer.serverCapacity,
				auctionServer.getItems().size());
		assertTrue(-1 == auctionServer.submitItem("Bobby", "ToyHelicopter", 8,
				100));
	}

}
