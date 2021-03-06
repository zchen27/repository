package cmsc433.p3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This file needs to hold your solver to be tested. You can alter the class to
 * extend any class that extends MazeSolver. It must have a constructor that
 * takes in a Maze. It must have a solve() method that returns the datatype List
 * <Direction> which will either be a reference to a list of steps to take or
 * will be null if the maze cannot be solved.
 */
/**
 * @author Zhehao Chen
 *
 */
public class StudentMTMazeSolver extends SkippingMazeSolver
{

	private ExecutorService pool;

	/**
	 * @param maze
	 */
	public StudentMTMazeSolver(Maze maze)
	{
		super(maze);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see cmsc433.p3.MazeSolver#solve()
	 */
	@Override
	public List<Direction> solve()
	{
		// TODO Auto-generated method stub

		// Creates a list of tasks, a list of futures, and the final answer
		LinkedList<DFSTask> tasks = new LinkedList<DFSTask>();
		List<Future<List<Direction>>> futures = new LinkedList<Future<List<Direction>>>();
		List<Direction> answer = null;
		
		//Create a pool naively of core+1
		int nThreads = Runtime.getRuntime().availableProcessors() + 1;
		pool = Executors.newFixedThreadPool(nThreads);

		try
		{
			Choice start = firstChoice(maze.getStart());
			int size = start.choices.size();

			// Spawn tasks for each direction of the choice
			for (int i = 0; i < size; i++)
			{
				Choice c = follow(start.at, start.choices.peek());
				tasks.add(new DFSTask(c, start.choices.pop()));
			}
		}
		catch (SolutionFound e)
		{
			// Well, that was quick.
			System.out.println("Solution Found!");
		}

		// Now time to fetch the futures
		try
		{
			futures = pool.invokeAll(tasks);
		}
		catch (InterruptedException e)
		{
			// Something went wrong
			e.printStackTrace();
		}

		// End the pool
		pool.shutdown();

		// Sift through the futures to see if any solution pops up
		for (Future<List<Direction>> a : futures)
		{
			try
			{
				// null answer means unsolvable
				if (a.get() != null)
				{
					answer = a.get();
				}
			}
			catch (InterruptedException | ExecutionException e)
			{
				// Something went wrong
				e.printStackTrace();
			}
		}
		return answer;
	}

	/**
	 * @author Zhehao Chen
	 *
	 *         A class abstraction of an individual depth-first search task.
	 */
	private class DFSTask implements Callable<List<Direction>>
	{
		private Choice source;
		private Direction dir;

		/**
		 * Constructs a DFSTask starting at a so
		 * 
		 * @param source
		 */
		public DFSTask(Choice source, Direction dir)
		{
			this.source = source;
			this.dir = dir;
		}

		/**
		 * Basically a shameless rip-off of STMazeSolverDFS's solve algorithm
		 * 
		 * @see java.util.concurrent.Callable#call()
		 * @see cmsc433.p3.STMazeSolverDFS#solve()
		 */
		@Override
		public List<Direction> call() throws Exception
		{
			LinkedList<Choice> choiceStack = new LinkedList<Choice>();
			Choice ch;

			try
			{
				choiceStack.push(source);
				while (!choiceStack.isEmpty())
				{
					ch = choiceStack.peek();
					if (ch.isDeadend())
					{
						// backtrack.
						choiceStack.pop();
						if (!choiceStack.isEmpty())
							choiceStack.peek().choices.pop();
						continue;
					}
					choiceStack.push(follow(ch.at, ch.choices.peek()));
				}
				// No solution found.
				return null;
			}
			catch (SolutionFound e)
			{
				Iterator<Choice> iter = choiceStack.iterator();
				LinkedList<Direction> solutionPath = new LinkedList<Direction>();
				while (iter.hasNext())
				{
					ch = iter.next();
					solutionPath.push(ch.choices.peek());
				}
				// Add the last direction to make up for jump from start
				solutionPath.push(dir);
				if (maze.display != null) maze.display.updateDisplay();
				// System.out.println(solutionPath);
				return pathToFullPath(solutionPath);
			}
		}

	}

}
