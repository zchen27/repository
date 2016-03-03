package cmsc433.p2;

import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name. When
 * running, a cook attempts to retrieve outstanding orders placed by Eaters and
 * process them.
 */
public class Cook extends Thread implements Runnable 
{
	private final String name;
	private int num;
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

	public String toString()
	{
		return name;
	}
	
	private void processOrder()
	{
		for (Food f: order)
		{
			Simulation.logEvent(SimulationEvent.cookStartedFood(this, f, num));
			switch (f.name)
			{
				case "soda":
					sim.makeSoda();
					break;
				case "wings":
					sim.makeWings();
					break;
				case "sub":
					sim.makeSub();
					break;
				case "pizza":
					sim.makePizza();
					break;
				default:
					break;
			}
			Simulation.logEvent(SimulationEvent.cookFinishedFood(this, f, num));
		}
		sim.readyOrder(num);
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
	public void run()
	{
		try
		{
			while (true)
			{
				// YOUR CODE GOES HERE...
				num = sim.pullOrder();
				Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, order, num));
				order = sim.lookUp(num);
				processOrder();
				sim.readyOrder(num);
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, num));
				//TODO: remove after implementation
			}
		}
		catch (InterruptedException e)
		{
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}