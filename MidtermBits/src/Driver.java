
public class Driver
{	
	private static BinarySemaphoreThreadSafe b = new BinarySemaphoreThreadSafe();
	
	public static class A extends Thread
	{
		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					b.tryAcquire();
					sleep(1);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static class R extends Thread
	{
		@Override
		public void run()
		{
			while (true)
			{
				
				try
				{
					b.release();
					sleep(10);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] s)
	{
		A a = new A();
		R r = new R();
		
		a.start();
		r.start();
	}
}
