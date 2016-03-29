
public class BinarySemaphoreThreadSafe implements BinarySemaphore
{
	
	private volatile boolean a = false;
	
	@Override
	public synchronized void acquire() throws InterruptedException
	{
		while (!a)
		{
			System.out.println("ACQUIRE BALKING");
			wait();
		}
		a = false;
		System.out.println("ACQUIRED");
		notifyAll();
	}

	@Override
	public synchronized void release() throws InterruptedException
	{
		while (a)
		{
			System.out.println("RELEASE BALKING");
			wait();
		}
		a = true;
		System.out.println("RELEASED");
		notifyAll();
	}

	@Override
	public boolean tryAcquire()
	{
		if (!a)
		{
			System.out.println("TRY TO ACQUIRE FAILED");
			return false;
		}
		else
		{
			System.out.println("TRY TO ACQUIRE SUCEEDED");
			a = false;
			synchronized (this) {notifyAll();}
			return true;
		}
			
	}

	@Override
	public boolean tryRelease()
	{
		if (a)
		{
			System.out.println("TRY TO RELEASE FAILED");
			return false;
		}
		else
		{
			System.out.println("TRY TO RELEASE SUCEEDED");
			a = true;
			synchronized (this) {notifyAll();}
			return true;
		}
	}

}
