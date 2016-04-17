package cmsc433.p4.actors;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Class of actors corresponding to cons ("internal") nodes in applicative
 * lists. Each such actor stores a head field and a tail field; the later is a
 * reference to an actor that manages the rest of the list.
 * 
 * You must complete the implementations of makeAndDeliver() and onReceive().
 * You may add new private fields and methods if you wish.
 * 
 * @author Rance Cleaveland
 *
 */
public class ConsActor extends UntypedActor
{

	/**
	 * Props structure-generator for this class.
	 * 
	 * @param head
	 *        head of the list the new node will manage
	 * @param tail
	 *        rest of the list
	 * @return Props structure for creating actor
	 */
	static Props props(Integer head, ActorRef tail)
	{
		return Props.create(ConsActor.class, head, tail);
	}

	private final Integer head; // Head of list being managed by cons actor
	private final ActorRef tail; // Actor managing tail of this list

	private int referenceCount = 0; // Reference count

	private ConsActor(Integer head, ActorRef tail)
	{
		this.head = head;
		this.tail = tail;
	}

	/**
	 * Factory method for making ConsActors. Ensures update of reference count
	 * of tail.
	 * 
	 * @param head
	 *        Integer to store in new node
	 * @param tail
	 *        Tail node
	 * @param destination
	 *        Actor to deliver constructed node to
	 * @param context
	 *        Actor context used to "start" new node
	 */
	public static void makeAndDeliver(Integer head, ActorRef tail,
			ActorRef destination, ActorContext context)
	{

		// Fill in your implementation here

	}

	@Override
	public void onReceive(Object msg) throws Exception
	{

		// Fill in your implementation here

	}

}