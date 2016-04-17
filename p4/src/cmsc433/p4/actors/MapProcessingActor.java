package cmsc433.p4.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import cmsc433.p4.messages.MapRequestMessage;

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

	private ActorRef tail; 				// Tail of list (initially null)

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

		// Fill in your implementation here.

	}

}