package cmsc433.p4.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import cmsc433.p4.messages.*;
import cmsc433.p4.support.IntegerCompute;

/**
 * Class of actors for processing map requests (i.e. requests for the creation
 * of a new list obtained by applying a function to each node in an existing
 * list).
 * 
 * You must complete the implementation of onReceive(). You also add additional
 * private fields if you wish, although you may not change the constructor.
 * 
 * @author Rance Cleaveland
 *
 */
public class MapProcessingActor extends UntypedActor
{

	static public Props props(ActorRef listManager, ActorRef replyTo,
			MapRequestMessage request)
	{
		return Props.create(MapProcessingActor.class, listManager, replyTo,
				request);
	}

	private ActorRef listManager; 		// Supervisor of cons, null actors
	private ActorRef replyTo; 			// Place to send result
	private MapRequestMessage request; 	// Original request, containing function
										 	// to apply

	private ActorRef tail = null; 				// Tail of list (initially null)
	private ActorRef next = null;				//Reference to the next MapRequestActor

	public MapProcessingActor(ActorRef listManager, ActorRef replyTo,
			MapRequestMessage request)
	{
		this.listManager = listManager;
		this.replyTo = replyTo;
		this.request = request;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception
	{
		if (msg instanceof MapProcessingStartMessage)
		{
			//Check if map is called on null
			
			ActorRef current = request.getArgumentListActor();
			current.tell(new NullStatusRequestMessage(), getSelf());
		}
		else if (msg instanceof NullStatusResultMessage)
		{
			if (((NullStatusResultMessage) msg).getStatus())
			{
				//Current node is a null node. Result is null
				listManager.tell(new NullActorRequestMessage(), getSelf());
			}
			else
			{
				//Current Node is a cons node. Get the tail of the cons node to recurse.
				ActorRef t = request.getArgumentListActor();
				t.tell(new TailRequestMessage(), getSelf());
			}
		}
		else if (msg instanceof NullActorResultMessage)
		{
			//Null node arrives, return it as result.
			replyTo.tell(new MapResultMessage(((NullActorResultMessage) msg).getNullActor()), getSelf());
		}
		else if (msg instanceof TailResultMessage)
		{
			//Generate new request based on the tail and starts it
			ActorRef t = ((TailResultMessage) msg).getTail();
			MapRequestMessage newRequest = new MapRequestMessage(request.getIntegerCompute(), t);
			Props p = props(listManager, getSelf(), newRequest);
			t.tell(new ReferenceDecrementRequestMessage(), getSelf());
			next = this.getContext().actorOf(p);
			next.tell(new MapProcessingStartMessage(), getSelf());
		}
		else if (msg instanceof MapResultMessage)
		{
			//When next is done. Tail is the result
			tail = ((MapResultMessage) msg).getResult();
			ActorRef current = request.getArgumentListActor();
			
			//Ask for head of current node
			current.tell(new HeadRequestMessage(), getSelf());
		}
		else if (msg instanceof HeadResultMessage)
		{
			//Compute head of current node and create new Cons
			IntegerCompute c = request.getIntegerCompute();
			int head = c.compute(((HeadResultMessage) msg).getHead());
			listManager.tell(new ConsActorRequestMessage(head, tail), getSelf());
		}
		else if (msg instanceof ConsActorResultMessage)
		{
			//Cons of this map operation is done. Send back
			ActorRef cons = ((ConsActorResultMessage) msg).getConsActor();
			replyTo.tell(new MapResultMessage(cons), getSelf());
			this.getContext().stop(getSelf());
		}
		else
		{
			System.out.printf("Bad message to MapProcessingActor:  %s%n",
					msg.toString());
			unhandled(msg); // Recommended practice
		}
	}

}
