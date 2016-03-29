
public interface BinarySemaphore
{
	public void acquire() throws InterruptedException;
	public void release() throws InterruptedException;
	public boolean tryAcquire();
	public boolean tryRelease();
}
