package cmsc433.p4.actors;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import cmsc433.p4.messages.*;
import cmsc433.p4.support.*;

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
	private static int counter = 0;

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

		Props consProps = props(head, tail);
		ActorRef consRef = context.actorOf(consProps, counter + "");
		counter++;
		tail.tell(new ReferenceIncrementRequestMessage(), consRef);
		destination.tell(new ConsActorResultMessage(consRef), context.self());
	}

	@Override
	public void onReceive(Object msg) throws Exception
	{

		// Fill in your implementation here

		if (msg instanceof CollectRequestMessage)
		{
			IntegerCollector<Integer> collect = ((CollectRequestMessage<Integer>) msg)
					.getIntegerCollector();
			ActorRef reply = ((CollectRequestMessage<Integer>) msg).getReplyTo();
			collect.collect(head);
			if (reply == null)
			{
				reply = getSender();
			}
			tail.tell(new CollectRequestMessage<Integer>(
					new CollectRequestMessage<Integer>(collect), reply, reply),
					getSelf());
		}
		else if (msg instanceof HeadRequestMessage)
		{
			ActorRef reply = getSender();
			reply.tell(new HeadResultMessage(head), this.getSelf());
		}
		else if (msg instanceof NullStatusRequestMessage)
		{
			ActorRef reply = getSender();
			reply.tell(new NullStatusResultMessage(false), this.getSelf());
		}
		else if (msg instanceof ReferenceCountRequestMessage)
		{
			ActorRef reply = getSender();
			reply.tell(new ReferenceCountResultMessage(referenceCount),
					this.getSelf());
		}
		else if (msg instanceof ReferenceIncrementRequestMessage)
		{
			referenceCount++;
		}
		else if (msg instanceof ReferenceDecrementRequestMessage)
		{
			referenceCount--;
			if (referenceCount < 0)
			{
				tail.tell(new ReferenceDecrementRequestMessage(), getSelf());
				getContext().stop(getSelf());
			}
		}
		else if (msg instanceof TailRequestMessage)
		{
			ActorRef reply = getSender();
			tail.tell(new ReferenceIncrementRequestMessage(), this.getSelf());
			reply.tell(new TailResultMessage(tail), this.getSelf());
		}
		else
		{
			System.out.printf("Bad message to ConsActor:  %s%n",
					msg.toString());
			unhandled(msg); // Recommended practice
		}

	}

}
