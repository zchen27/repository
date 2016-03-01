package cmsc433.p0.tests;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.LinkedList;

import org.junit.Test;

import cmsc433.p0.ParallelMaximizer;

public class StudentTests
{
	private int threadCount = 100;
	private ParallelMaximizer maximizer = new ParallelMaximizer(threadCount);
	
	@Test
	public void test0()
	{
		Random r = new Random();
		LinkedList l;
		IntStream s = r.ints(Short.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
		l = new LinkedList(s.boxed().collect(Collectors.toList()));
		int pM;
		try
		{
			pM = maximizer.max(l);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			fail("The test failed because the max procedure was interrupted unexpectedly.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("The test failed because the max procedure encountered a runtime error: " + e.getMessage());
		}
		
	}
}
