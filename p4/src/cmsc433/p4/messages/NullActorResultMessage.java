package cmsc433.p4.messages;

import akka.actor.ActorRef;

/**
 * Class of messages for returning a newly-constructed null actor.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class NullActorResultMessage
{
	private ActorRef nullActor;

	public NullActorResultMessage(ActorRef nullActor)
	{
		this.nullActor = nullActor;
	}

	public ActorRef getNullActor()
	{
		return nullActor;
	}
}
