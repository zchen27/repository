package cmsc433.p4.support;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import cmsc433.p4.messages.CollectRequestMessage;
import cmsc433.p4.messages.CollectResultMessage;
import cmsc433.p4.messages.ConsActorRequestMessage;
import cmsc433.p4.messages.ConsActorResultMessage;
import cmsc433.p4.messages.HeadRequestMessage;
import cmsc433.p4.messages.HeadResultMessage;
import cmsc433.p4.messages.ImportListRequestMessage;
import cmsc433.p4.messages.ImportListResultMessage;
import cmsc433.p4.messages.MapRequestMessage;
import cmsc433.p4.messages.MapResultMessage;
import cmsc433.p4.messages.NullActorRequestMessage;
import cmsc433.p4.messages.NullActorResultMessage;
import cmsc433.p4.messages.NullStatusRequestMessage;
import cmsc433.p4.messages.NullStatusResultMessage;
import cmsc433.p4.messages.ReferenceCountRequestMessage;
import cmsc433.p4.messages.ReferenceCountResultMessage;
import cmsc433.p4.messages.ReferenceDecrementRequestMessage;
import cmsc433.p4.messages.TailRequestMessage;
import cmsc433.p4.messages.TailResultMessage;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * List operations supported by actor implementation. These are useful in
 * testing actor list code and will be used in our grading.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class IntegerListOperations
{

	private final ActorRef listManager;

	private long futureDelay = 1000; // milliseconds
	private Duration awaitDelay = Duration.Inf();

	public IntegerListOperations(ActorRef listManager)
	{
		this.listManager = listManager;
	}

	/**
	 * Returns empty list
	 * 
	 * @return null actor corresponding to empty list
	 */
	public ActorRef nil()
	{
		Future<Object> fmsg = Patterns.ask(listManager,
				new NullActorRequestMessage(), futureDelay);
		try
		{
			NullActorResultMessage msg = (NullActorResultMessage) Await
					.result(fmsg, awaitDelay);
			return (msg.getNullActor());
		}
		catch (Exception e)
		{
			System.out.println("Error in nil()");
			e.printStackTrace(System.out);
			
			return null;
		}
	}

	/**
	 * Return list obtained by glueing head onto front of tail.
	 * 
	 * @param head
	 *        Integer to put on front
	 * @param tail
	 *        Existing list actor
	 * @return Actor representing new list
	 */
	public ActorRef cons(Integer head, ActorRef tail)
	{
		Future<Object> fmsg = Patterns.ask(listManager,
				new ConsActorRequestMessage(head, tail), futureDelay);
		try
		{
			ConsActorResultMessage msg = (ConsActorResultMessage) Await
					.result(fmsg, awaitDelay);
			return (msg.getConsActor());
		}
		catch (Exception e)
		{
			
			System.out.println("Error in cons()");
			e.printStackTrace(System.out);
			return null;
		}
	}

	/**
	 * Function determining whether or not list is empty.
	 * 
	 * @param list
	 *        Actor representing list to test
	 * @return Boolean representing whether list is empty ("true") or not
	 *         ("false")
	 */
	public Boolean isNull(ActorRef list)
	{
		Future<Object> fmsg = Patterns.ask(list, new NullStatusRequestMessage(),
				futureDelay);
		try
		{
			NullStatusResultMessage msg = (NullStatusResultMessage) Await
					.result(fmsg, awaitDelay);
			return (msg.getStatus());
		}
		catch (Exception e)
		{
			System.out.println("Error in isNull()");
			e.printStackTrace(System.out);;
			return null;
		}
	}

	/**
	 * Function returning first element of non-empty list
	 * 
	 * @param list
	 *        Actor representing list
	 * @return First element. Note that list is not modified.
	 */
	public Integer head(ActorRef list)
	{
		Future<Object> fmsg = Patterns.ask(list, new HeadRequestMessage(),
				futureDelay);
		try
		{
			HeadResultMessage msg = (HeadResultMessage) Await.result(fmsg,
					awaitDelay);
			return (msg.getHead());
		}
		catch (Exception e)
		{
			System.out.println("Error in head()");
			e.printStackTrace(System.out);;
			return null;
		}

	}

	/**
	 * Returns tail of non-empty list (i.e. returns list with first element
	 * removed).
	 * 
	 * @param list
	 *        List to return tail of
	 * @return Actor representing list after first element
	 */
	public ActorRef tail(ActorRef list)
	{
		Future<Object> fmsg = Patterns.ask(list, new TailRequestMessage(),
				futureDelay);
		try
		{
			TailResultMessage msg = (TailResultMessage) Await.result(fmsg,
					awaitDelay);
			return (msg.getTail());
		}
		catch (Exception e)
		{
			System.out.println("Error in tail()");
			e.printStackTrace(System.out);;
			return null;
		}
	}

	/**
	 * Returns reference count of given list node
	 * 
	 * @param list
	 *        Node whose reference count should be returned
	 * @return Reference count
	 */
	public Integer getReferenceCount(ActorRef list)
	{
		Future<Object> fmsg = Patterns.ask(list,
				new ReferenceCountRequestMessage(), futureDelay);
		try
		{
			ReferenceCountResultMessage msg = (ReferenceCountResultMessage) Await
					.result(fmsg, awaitDelay);
			return (msg.getReferenceCount());
		}
		catch (Exception e)
		{
			System.out.println("Error in getReferenceCount()");
			e.printStackTrace(System.out);;
			return null;
		}
	}

	/**
	 * Decrements reference count of given list node.
	 * 
	 * @param list
	 *        Node whose reference count should be decremented.
	 */
	public void decrementReferenceCount(ActorRef list)
	{
		list.tell(new ReferenceDecrementRequestMessage(), null);
	}

	/**
	 * Returns actor corresponding to list obtained from Java list.
	 * 
	 * @param list
	 *        Java list to import
	 * @return Actor corresponding to list
	 */
	public ActorRef importList(List<Integer> list)
	{
		ImportListRequestMessage req = new ImportListRequestMessage(list);
		Future<Object> fmsg = Patterns.ask(listManager, req, futureDelay);
		try
		{
			ImportListResultMessage msg = (ImportListResultMessage) Await
					.result(fmsg, awaitDelay);
			return (msg.getListActor());
		}
		catch (Exception e)
		{
			System.out.println("Error in tail()");
			e.printStackTrace(System.out);;
			return null;
		}
	}

	/**
	 * Returns result of applying collector to list. A collector is an object
	 * containing a method that is applied to each element in a list, updating
	 * an internal result field each time. When the end of the list is reached,
	 * the result is returned.
	 * 
	 * @param collector
	 *        Collector object to apply to each element in list
	 * @param list
	 *        Actor for list to which collector is applied
	 * @return Result of collection
	 */
	public <T> T collect(IntegerCollector<T> collector, ActorRef list)
	{
		CollectRequestMessage<T> req = new CollectRequestMessage<T>(collector);
		Future<Object> fmsg = Patterns.ask(list, req, futureDelay);
		try
		{
			CollectResultMessage<T> msg = (CollectResultMessage<T>) Await
					.result(fmsg, awaitDelay);
			return (msg.getIntegerCollector().getResult());
		}
		catch (Exception e)
		{
			System.out.println("Error in collect()");
			e.printStackTrace(System.out);;
			return null;

		}
	}

	/**
	 * Function that returns list containing the results of applying a given
	 * function to every element in a list.
	 * 
	 * @param f
	 *        Function to apply to every element in a list
	 * @param list
	 *        Actor representing list to whose elements f is to be applied
	 * @return Actor representing list of results
	 */
	public ActorRef map(IntegerCompute f, ActorRef list)
	{
		MapRequestMessage req = new MapRequestMessage(f, list);
		Future<Object> fmsg = Patterns.ask(listManager, req, futureDelay);
		try
		{
			MapResultMessage msg = (MapResultMessage) Await.result(fmsg,
					awaitDelay);
			return (msg.getResult());
		}
		catch (Exception e)
		{
			System.out.println("Error in map()");
			e.printStackTrace(System.out);;
			return null;

		}
	}

	/**
	 * Function for summing all elements in a list. It is implemented using
	 * collect().
	 * 
	 * @param list
	 *        Actor representing list whose elements are to be summed
	 * @return The sum of the elements in list
	 */
	public Integer sum(ActorRef list)
	{
		IntegerCollector<Integer> sumCollector = new IntegerCollector<Integer>()
		{

			private Integer result = 0;

			@Override
			public void collect(Integer i)
			{
				result += i;
			}

			@Override
			public Integer getResult()
			{
				return result;
			}

		};
		return (collect(sumCollector, list));
	}

	/**
	 * Function that builds Java ArrayList containing same elements as list
	 * represented as an actor. Implemented using collect().
	 * 
	 * @param list
	 *        Actor representing list to be converted into Java ArrayList
	 * @return Array list
	 */
	public ArrayList<Integer> exportList(ActorRef list)
	{
		IntegerCollector<ArrayList<Integer>> listCollector = new IntegerCollector<ArrayList<Integer>>()
		{

			private final ArrayList<Integer> arrayList = new ArrayList<Integer>();

			@Override
			public void collect(Integer i)
			{
				arrayList.add(i);
			}

			@Override
			public ArrayList<Integer> getResult()
			{
				return arrayList;
			}
		};
		return (collect(listCollector, list));
	}

	/**
	 * Function that adds one to every element in a list, returning the list of
	 * results. Implemented using map().
	 * 
	 * @param list
	 *        Actor corresponding to list
	 * @return List obtained by adding one to every element in list.
	 */
	public ActorRef add1(ActorRef list)
	{
		IntegerCompute addOneFunction = new IntegerCompute()
		{

			@Override
			public Integer compute(Integer i)
			{
				return (i + 1);
			}

		};
		return (map(addOneFunction, list));
	}
}
