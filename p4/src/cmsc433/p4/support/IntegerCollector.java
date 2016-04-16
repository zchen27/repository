package cmsc433.p4.support;

/**
 * Interface for collection objects that may be applied to lists. The idea is
 * that the collect() method is applied to each element in the list and an
 * internal field in the collector to produce a new value for the internal
 * field. The value in this internal field may be accessed via the getResult()
 * method.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 * @param <T>
 *        Type of the result
 */
public interface IntegerCollector<T>
{

	public abstract void collect(Integer i);

	T getResult();
}
