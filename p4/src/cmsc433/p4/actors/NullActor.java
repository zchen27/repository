package cmsc433.p4.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import cmsc433.p4.messages.*;

/**
 * Class of actors implementing null nodes in applicative lists. Despite the
 * name, these actors have a role to play, specifically in returning results of
 * collect operations and others.
 * 
 * You must complete the implementation of onReceive(). You also add additional
 * private fields if you wish, although you may not change the constructor.
 * 
 * @author Rance Cleaveland
 *
 */
public class NullActor extends UntypedActor
{

	static public Props props = Props.create(NullActor.class);

	private int referenceCount = 0;

	@Override
	public void onReceive(Object msg) throws Exception
	{

		// Fill in your implementation here.

		if (msg instanceof CollectRequestMessage)
		{
			// Sends Forwards back the collect request
			ActorRef reply = ((CollectRequestMessage<Integer>) msg).getReplyTo();
			CollectResultMessage result = new CollectResultMessage((CollectRequestMessage<Integer>) msg);
			if (reply == null)
			{
				reply = getSender();
			}
			reply.tell(result, getSelf());
		}
		else if (msg instanceof NullStatusRequestMessage)
		{
			// Is a null node
			ActorRef reply = getSender();
			reply.tell(new NullStatusResultMessage(true), getSelf());
		}
		else if (msg instanceof ReferenceCountRequestMessage)
		{
			// Returns reference count
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
			// Decrements Reference
			referenceCount--;
			if (referenceCount < 0)
			{
				getContext().stop(getSelf());
			}
		}
		else
		{
			System.out.printf("Bad message to NullActor:  %s%n",
					msg.toString());
			unhandled(msg); // Recommended practice
		}
	}

}
