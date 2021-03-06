package cmsc433.p2;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cmsc433.p2.Machine.MachineType;

/**
 * Simulation is the main class used to run the simulation. You may add any
 * fields (static or instance) or any methods you wish.
 */
/**
 * @author Me
 *
 */
public class Simulation
{
	private static Simulation instance = new Simulation();
	
	private Simulation()
	{
		
	}
	
	public static Simulation getInstance()
	{
		return instance;
	}
	
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;
	
	/**
	 * Used by other classes in the simulation to log events
	 * 
	 * @param event
	 */
	public synchronized static void logEvent(SimulationEvent event)
	{
		events.add(event);
		System.out.println(event);
	}
	
	private static Customer[] customers;
	private static Cook[] cooks;
	private static Machine fountain;
	private static Machine fryer;
	private static Machine grillpress;
	private static Machine oven;
	private static LinkedList<Integer> orders;
	private static HashSet<Integer> ready;
	private static HashMap<Integer, List<Food>> orderContents;
	private static int capacity;
	private static int tables;
	private static int occupied;
	
	/**
	 * @return
	 */
	public int occupied()
	{
		return occupied;
	}
	
	/**
	 * @return
	 */
	public int tables()
	{
		return tables;
	}
	
	/**
	 * @param c
	 * @throws InterruptedException
	 */
	public synchronized void sitDown(Customer c) throws InterruptedException
	{
		while (occupied >= tables)
		{
			wait();
		}
		logEvent(SimulationEvent.customerEnteredRatsies(c));
		occupied++;
	}
	
	/**
	 * @param c
	 */
	public synchronized void getUp(Customer c)
	{
		occupied--;
		logEvent(SimulationEvent.customerLeavingRatsies(c));
		notifyAll();
	}
	
	/**
	 * @param c
	 */
	public synchronized void placeOrder(Customer c)
	{
		orders.push(c.orderNum());
		orderContents.put(c.orderNum(), c.order());
		notifyAll();
		logEvent(SimulationEvent.customerPlacedOrder(c, c.order(), c.orderNum()));
	}
	
	/**
	 * @param c
	 */
	public synchronized void readyOrder(Cook c)
	{
		ready.add(c.orderNum());
		logEvent(SimulationEvent.cookCompletedOrder(c, c.orderNum()));
		notifyAll();
	}
	
	/**
	 * @param c
	 * @throws InterruptedException
	 */
	public synchronized void demandFood(Customer c) throws InterruptedException
	{
		while(!ready.contains(c.orderNum()))
		{
			wait();
		}
		ready.remove(c.orderNum());
		notifyAll();
		logEvent(SimulationEvent.customerReceivedOrder(c, c.order(), c.orderNum()));
	}
	
	/**
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized int pullOrder() throws InterruptedException
	{
		while (orders.size() <= 0)
		{
			wait();
		}
		notifyAll();
		int i = orders.poll();
		return i;
	}
	
	/**
	 * @param c
	 * @return
	 */
	public List<Food> lookUp(Cook c)
	{
		logEvent(SimulationEvent.cookReceivedOrder(c, orderContents.get(c.orderNum()), c.orderNum()));
		return orderContents.get(c.orderNum());
	}
	
	/**
	 * @param c
	 */
	public void makeSoda(Cook c)
	{
		try
		{
			logEvent(SimulationEvent.cookStartedFood(c, FoodType.soda, c.orderNum()));
			fountain.makeFood(c.name());
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param c
	 */
	public void makeWings(Cook c)
	{
		try
		{
			logEvent(SimulationEvent.cookStartedFood(c, FoodType.wings, c.orderNum()));
			fryer.makeFood(c.name());
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param c
	 */
	public void makeSub(Cook c)
	{
		try
		{
			logEvent(SimulationEvent.cookStartedFood(c, FoodType.sub, c.orderNum()));
			grillpress.makeFood(c.name());
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param c
	 */
	public void makePizza(Cook c)
	{
		try
		{
			logEvent(SimulationEvent.cookStartedFood(c, FoodType.pizza, c.orderNum()));
			oven.makeFood(c.name());
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param c
	 */
	public void grabSoda(Cook c)
	{
		try
		{
			fountain.grabFood(c.name());
			logEvent(SimulationEvent.cookFinishedFood(c, FoodType.soda, c.orderNum()));
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param c
	 */
	public void grabWings(Cook c)
	{
		try
		{
			fryer.grabFood(c.name());
			logEvent(SimulationEvent.cookFinishedFood(c, FoodType.wings, c.orderNum()));
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param c
	 */
	public void grabSub(Cook c)
	{
		try
		{
			grillpress.grabFood(c.name());
			logEvent(SimulationEvent.cookFinishedFood(c, FoodType.sub, c.orderNum()));

		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param c
	 */
	public void grabPizza(Cook c)
	{
		try
		{
			oven.grabFood(c.name());
			logEvent(SimulationEvent.cookFinishedFood(c, FoodType.pizza, c.orderNum()));

		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function responsible for performing the simulation. Returns a List of
	 * SimulationEvent objects, constructed any way you see fit. This List will
	 * be validated by a call to Validate.validateSimulation. This method is
	 * called from Simulation.main(). We should be able to test your code by
	 * only calling runSimulation.
	 * 
	 * Parameters:
	 * 
	 * @param numCustomers
	 *        the number of customers wanting to enter Ratsie's
	 * @param numCooks
	 *        the number of cooks in the simulation
	 * @param numTables
	 *        the number of tables in Ratsie's (i.e. Ratsie's capacity)
	 * @param machineCapacity
	 *        the capacity of all machines in Ratsie's
	 * @param randomOrders
	 *        a flag say whether or not to give each customer a random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(int numCustomers,
			int numCooks, int numTables, int machineCapacity,
			boolean randomOrders)
	{

		// This method's signature MUST NOT CHANGE.

		// We are providing this events list object for you.
		// It is the ONLY PLACE where a concurrent collection object is
		// allowed to be used.
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());

		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers, numCooks,
				numTables, machineCapacity));

		// Set things up you might need
		customers = new Customer[numCustomers];
		cooks = new Cook[numCooks];
		tables = numTables;
		capacity = machineCapacity;
		occupied = 0;
		orders = new LinkedList<Integer>();
		orderContents = new HashMap<Integer, List<Food>>();
		ready = new HashSet<Integer>();
		
		// Start up machines
		fountain = new Machine(MachineType.fountain, machineCapacity);
		fryer = new Machine(MachineType.fryer, machineCapacity);
		grillpress = new Machine(MachineType.grillPress, machineCapacity);
		oven = new Machine(MachineType.oven, machineCapacity);
		capacity = machineCapacity;

		fountain.init();
		fryer.init();
		grillpress.init();
		oven.init();
		
		// Let cooks in
		for (int i = 0; i < cooks.length; i++)
		{
			cooks[i] = new Cook("Cook" + i);
			cooks[i].start();
			logEvent(SimulationEvent.cookStarting(cooks[i]));
		}
		
		
		// Build the customers.						
		//Thread[] customers = new Thread[numCustomers];
		LinkedList<Food> order;
		if (!randomOrders)
		{
			order = new LinkedList<Food>();
			order.add(FoodType.wings);
			order.add(FoodType.pizza);
			order.add(FoodType.sub);
			order.add(FoodType.soda);
			for (int i = 0; i < customers.length; i++)
			{
				customers[i] =
						new Customer("Customer " + (i + 1), order);
			}
		}
		else
		{
			for (int i = 0; i < customers.length; i++)
			{
				Random rnd = new Random();
				int wingsCount = rnd.nextInt(4);
				int pizzaCount = rnd.nextInt(4);
				int subCount = rnd.nextInt(4);
				int sodaCount = rnd.nextInt(4);
				order = new LinkedList<Food>();
				for (int b = 0; b < wingsCount; b++)
				{
					order.add(FoodType.wings);
				}
				for (int f = 0; f < pizzaCount; f++)
				{
					order.add(FoodType.pizza);
				}
				for (int f = 0; f < subCount; f++)
				{
					order.add(FoodType.sub);
				}
				for (int c = 0; c < sodaCount; c++)
				{
					order.add(FoodType.soda);
				}
				customers[i] =
						new Customer("Customer " + (i + 1), order);
			}
		}

		// Now "let the customers know the shop is open" by
		// starting them running in their own thread.
		for (int i = 0; i < customers.length; i++)
		{
			customers[i].start();
			logEvent(SimulationEvent.customerStarting(customers[i]));
			// NOTE: Starting the customer does NOT mean they get to go
			// right into the shop. There has to be a table for
			// them. The Customer class' run method has many jobs
			// to do - one of these is waiting for an available
			// table...
		}

		try
		{
			// Wait for customers to finish
			// -- you need to add some code here...
			for (int i = 0; i < customers.length; i++)
			{
				customers[i].join();
			}
			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads. There are other approaches
			// though, so you can change this if you want to.
			for (int i = 0; i < cooks.length; i++)
				cooks[i].interrupt();
			for (int i = 0; i < cooks.length; i++)
				cooks[i].join();
			
			fountain.halt();
			fryer.halt();
			grillpress.halt();
			oven.halt();
			

		}
		catch (InterruptedException e)
		{
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines

		// Done with simulation

		logEvent(SimulationEvent.endSimulation());
		return events;
	}

	

	
	/**
	 * Entry point for the simulation.
	 *
	 * @param args
	 *        the command-line arguments for the simulation. There should be
	 *        exactly four arguments: the first is the number of customers, the
	 *        second is the number of cooks, the third is the number of tables
	 *        in Ratsie's, and the fourth is the number of items each machine
	 *        can make at the same time.
	 */
	public static void main(String args[]) throws InterruptedException
	{

		/*if (args.length != 4)
		{
			System.err.println(
					"usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders");
			System.exit(1);
		}*/
		
		/*int numCustomers = new Integer(args[0]).intValue();
		int numCooks = new Integer(args[1]).intValue();
		int numTables = new Integer(args[2]).intValue();
		int machineCapacity = new Integer(args[3]).intValue();
		boolean randomOrders = new Boolean(args[4]);*/
		Random r = new Random();
		int numCustomers = 10;
		int numCooks = 2;
		int numTables = 5;
		int machineCapacity = 4;
		boolean randomOrders = true;
		
		/*numCustomers = r.nextInt(100) + 1;
		numCooks = r.nextInt(10) + 1;
		numTables = r.nextInt(50) + 1;
		machineCapacity = r.nextInt(50) + 1;*/
		//machineCapacity = 1;
		
		/*for (int i = 0; i < 200; i++)
		{
			numCustomers = r.nextInt(100) + 1;
			numCooks = r.nextInt(10) + 1;
			numTables = r.nextInt(50) + 1;
			machineCapacity = r.nextInt(50) + 1;
			
			if (!Validate.validateSimulation(runSimulation(numCustomers,
							numCooks, numTables, machineCapacity, randomOrders)))
			{
				System.err.println("Failed");
			}
		}*/
		


		// Run the simulation and then
		// feed the result into the method to validate simulation.
		System.out.println("Did it work? "
				+ Validate.validateSimulation(runSimulation(numCustomers,
						numCooks, numTables, machineCapacity, randomOrders)));
	}

}
