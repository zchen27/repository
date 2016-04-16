package cmsc433.p4.messages;

/**
 * Class of messages for returning the null status of an actor. The boolean is
 * "true" if the actor is a null actor and "false" otherwise.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class NullStatusResultMessage
{

	private boolean status;

	public NullStatusResultMessage(boolean status)
	{
		this.status = status;
	}

	public boolean getStatus()
	{
		return status;
	}
}
