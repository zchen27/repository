package cmsc433.p2;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list of
 * Food items that constitute the Customer's order. When running, an customer
 * attempts to enter the Ratsie's (only successful if the Ratsie's has a free
 * table), place its order, and then leave the Ratsie's when the order is
 * complete.
 */
/**
 * @author Me
 *
 */
public class Customer extends Thread implements Runnable
{
	// JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private Simulation sim = Simulation.getInstance();

	private int orderNum;

	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor. It must take at least the name
	 * and order but may take other parameters if you would find adding them
	 * useful.
	 * @param name
	 * @param order
	 */
	public Customer(String name, List<Food> order)
	{
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#toString()
	 */
	public String toString()
	{
		return name;
	}
	
	/**
	 * @return
	 */
	public List<Food> order()
	{
		return order;
	}
	
	/**
	 * @return
	 */
	public int orderNum()
	{
		return orderNum;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to enter
	 * the Ratsie's (only successful when the Ratsie's has a free table), place
	 * its order, and then leave the Ratsie's when the order is complete.
	 */
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		// YOUR CODE GOES HERE...
		try
		{
			sim.sitDown(this);
			sim.placeOrder(this);
			sim.demandFood(this);
			sim.getUp(this);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Customer ends
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Customer))
		{
			return false;
		}
		else
		{
			if (((Customer) o).toString().equals(this.name))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}