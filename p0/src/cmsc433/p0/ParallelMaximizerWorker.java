package cmsc433.p0;

import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the maximum over a
 * subset of its <code>Integers</code>.
 */
public class ParallelMaximizerWorker extends Thread
{

	protected LinkedList<Integer> list;
	protected int partialMax = Integer.MIN_VALUE; // initialize
													 // to
													 // lowest
													 // value

	public ParallelMaximizerWorker(LinkedList<Integer> list)
	{
		this.list = list;
	}

	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run()
	{
		while (true)
		{
			int number;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized (list)
			{
				if (list.isEmpty()) return; // list is empty
				number = list.remove();
			}
			if (number > partialMax)
			{
				partialMax = number;
			}
			// update partialMax according to new value
			// TODO: IMPLEMENT CODE HERE
		}
	}

	public int getPartialMax()
	{
		return partialMax;
	}

}
