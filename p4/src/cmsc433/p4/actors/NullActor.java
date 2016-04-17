package cmsc433.p4.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

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

	}

}