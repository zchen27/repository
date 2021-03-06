package cmsc433.p2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A Machine is used to make a particular Food. Each Machine makes just one kind
 * of Food. Each machine has a capacity: it can make that many food items in
 * parallel; if the machine is asked to produce a food item beyond its capacity,
 * the requester blocks. Each food item takes at least item.cookTimeS seconds to
 * produce.
 */

/**
 * @author Me
 *
 */
public class Machine
{

	// Types of machines used in Ratsie's. Recall that enum types are
	// effectively "static" and "final", so each instance of Machine
	// will use the same MachineType.
	

	public enum MachineType
	{
		fountain, fryer, grillPress, oven
	};

	// Converts Machine instances into strings based on MachineType.

	public String toString()
	{
		switch (machineType)
		{
			case fountain:
				return "Fountain";
			case fryer:
				return "Fryer";
			case grillPress:
				return "Grill Presss";
			case oven:
				return "Oven";
			default:
				return "INVALID MACHINE";
		}
	}

	public final MachineType machineType;
	public final Food machineFoodType;
	public final int capacity;	

	private LinkedList<String> waiting;
	private ArrayList<String> ready;
	private CookAnItem[] actors;
	
	// YOUR CODE GOES HERE...

	/**
	 * The constructor takes at least the type of the machine, the Food item it
	 * makes, and its capacity. You may extend it with other arguments, if you
	 * wish. Notice that the constructor currently does nothing with the
	 * capacity; you must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	/**
	 * @param machineType
	 * @param capacityIn
	 */
	public Machine(MachineType machineType, int capacityIn)
	{
		
		this.machineType = machineType;
		switch (this.machineType)
		{
		case fountain:
			this.machineFoodType = FoodType.soda;
			break;
		case fryer:
			this.machineFoodType = FoodType.wings;
			break;
		case grillPress:
			this.machineFoodType = FoodType.sub;
			break;
		case oven:
			this.machineFoodType = FoodType.pizza;
			break;
		default:
			//OMGWTFBBQ
			this.machineFoodType = null;
			break;
		}
		
		// YOUR CODE GOES HERE...
		capacity = capacityIn;
		
		waiting = new LinkedList<String>();
		ready = new ArrayList<String>();
		actors = new CookAnItem[capacity];
		for (int i = 0; i < actors.length; i++)
		{
			actors[i] = new CookAnItem(machineFoodType, this);
		}
	}
	
	/**
	 * 
	 */
	public void init()
	{
		for (int i = 0; i < actors.length; i++)
		{
			actors[i].start();
		}
		Simulation.logEvent(SimulationEvent.machineStarting(this, machineFoodType, capacity));
	}
	
	/**
	 * @throws InterruptedException
	 */
	public void halt() throws InterruptedException
	{
		for (int i = 0; i < actors.length; i++)
		{
			actors[i].interrupt();
		}
		for(int i = 0; i < actors.length; i++)
		{
			actors[i].join();
		}
		Simulation.logEvent(SimulationEvent.machineEnding(this));
	}

	/**
	 * This method is called by a Cook in order to make the Machine's food item.
	 * You can extend this method however you like, e.g., you can have it take
	 * extra parameters or return something other than Object. It should block
	 * if the machine is currently at full capacity. If not, the method should
	 * return, so the Cook making the call can proceed. You will need to
	 * implement some means to notify the calling Cook when the food item is
	 * finished.
	 */
	/**
	 * @param name
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized Object makeFood(String name) throws InterruptedException
	{
		// YOUR CODE GOES HERE...
		waiting.push(name);
		notifyAll();
		
		//TODO: Remove after implementation
		return machineFoodType;
	}
	
	/**
	 * @return
	 * @throws InterruptedException
	 */
	private synchronized String pullWaiting() throws InterruptedException
	{
		while (waiting.size() <= 0)
		{
			wait();
		}
		
		return waiting.poll();
	}
	
	/**
	 * @param name
	 * @throws InterruptedException
	 */
	public synchronized void grabFood(String name) throws InterruptedException
	{
		while (!ready.contains(name))
		{
			wait();
		}
		
		ready.remove(name);
	}
	
	/**
	 * @param name
	 */
	private synchronized void readyFood(String name)
	{
		ready.add(name);
		notifyAll();
	}
	
	// THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem extends Thread implements Runnable
	{
		public final Food food;
		public final Machine machine;
		private String cook;
		
		/**
		 * @param f
		 * @param m
		 */
		private CookAnItem(Food f, Machine m)
		{
			food = f;
			machine = m;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run()
		{
			try
			{
				// YOUR CODE GOES HERE...
				while (true)
				{
					cook = pullWaiting();
					Simulation.logEvent(SimulationEvent.machineCookingFood(machine, food));
					sleep(food.cookTimeS);
					Simulation.logEvent(SimulationEvent.machineDoneFood(machine, food));
					readyFood(cook);
				}
			}
			catch (InterruptedException e)
			{
				//Actor stopping
				//ystem.out.println("ACTOR STOPPIN!");
			}
		}
	}
}