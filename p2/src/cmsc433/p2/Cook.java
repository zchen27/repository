package cmsc433.p2;

import java.util.Collections;
import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name. When
 * running, a cook attempts to retrieve outstanding orders placed by Eaters and
 * process them.
 */
/**
 * @author Me
 *
 */
public class Cook extends Thread implements Runnable 
{
	private final String name;
	private int orderNum;
	
	private List<Food> order;
	private Simulation sim = Simulation.getInstance();
	
	/**
	 * You can feel free to modify this constructor. It must take at least the
	 * name, but may take other parameters if you would find adding them useful.
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#toString()
	 */
	public String toString()
	{
		return name;
	}
	
	
	/**
	 * 
	 */
	private void processOrder()
	{
		for (Food f: order)
		{
			switch(f.name)
			{
				case "soda":
					sim.makeSoda(this);
					break;
				case "wings":
					sim.makeWings(this);
					break;
				case "sub":
					sim.makeSub(this);
					break;
				case "pizza":
					sim.makePizza(this);
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * 
	 */
	private void grabOrder()
	{
		for (Food f: order)
		{
			switch (f.name)
			{
				case "soda":
					sim.grabSoda(this);
					break;
				case "wings":
					sim.grabWings(this);
					break;
				case "sub":
					sim.grabSub(this);
					break;
				case "pizza":
					sim.grabPizza(this);
					break;
				default:
					break;
			}
		}
	}
	

	/**
	 * This method executes as follows. The cook tries to retrieve orders placed
	 * by Customers. For each order, a List<Food>, the cook submits each Food
	 * item in the List to an appropriate Machine, by calling makeFood(). Once
	 * all machines have produced the desired Food, the order is complete, and
	 * the Customer is notified. The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some other thread
	 * calls the interrupt() method on it, which could raise
	 * InterruptedException if the cook is blocking), then it terminates.
	 */
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		try
		{
			while (true)
			{
				// YOUR CODE GOES HERE...
				//System.out.println("live");
				orderNum = sim.pullOrder();
				order = sim.lookUp(this);
				processOrder();
				grabOrder();
				sim.readyOrder(this);
				//TODO: remove after implementation
			}
		}
		catch (InterruptedException e)
		{
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			//System.out.println("Cook stopping!");
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
	
	/**
	 * @return
	 */
	public String name()
	{
		return name;
	}
	
	/**
	 * @return
	 */
	public int orderNum()
	{
		return orderNum;
	}
}