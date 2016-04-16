package cmsc433.p4.messages;

/**
 * Class of messages for carrying reference-count information from actors.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class ReferenceCountResultMessage
{
	private int referenceCount;

	public ReferenceCountResultMessage(int c)
	{
		referenceCount = c;
	}

	public int getReferenceCount()
	{
		return referenceCount;
	}
}
