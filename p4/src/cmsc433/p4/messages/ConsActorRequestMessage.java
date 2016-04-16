package cmsc433.p4.messages;

import akka.actor.ActorRef;

/**
 * Class of message requesting the creation of a "cons actor". These actors
 * contain a data value in their "head" fields and a "rest of list" value
 * (another actor) in their tail field.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class ConsActorRequestMessage
{

	private final Integer head;
	private final ActorRef tail;

	public ConsActorRequestMessage(Integer head, ActorRef tail)
	{
		this.head = head;
		this.tail = tail;
	}

	public Integer getHead()
	{
		return head;
	}

	public ActorRef getTail()
	{
		return tail;
	}
}
