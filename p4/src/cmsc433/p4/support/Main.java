package cmsc433.p4.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import cmsc433.p4.actors.ListManagerActor;
import cmsc433.p4.messages.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * A class containing a main() function that tests some actor-implementation
 * routines. Feel free to modify for your own testing purposes. Please do not
 * include any code that other classes may depend on, however; this is strictly
 * for testing your code.
 * 
 * @author Rance Cleaveland
 *
 */
public class Main
{

	public static String getName(ActorRef actor)
	{
		return (actor.path().name());
	}

	public static void main(String[] args)
	{

		ActorSystem listSystem = ActorSystem.create("list_system");
		ActorRef lm = listSystem.actorOf(Props.create(ListManagerActor.class));

		// Create list operations object.

		IntegerListOperations lops = new IntegerListOperations(lm);

		// Create null actor. "[]" should be printed; reference count should be
		// 0.
		ActorRef nil = lops.nil();
		System.out.printf("nil is %s; reference count is %d%n",
				lops.exportList(nil), lops.getReferenceCount(nil));

		// Cons 3 onto null actor. "[3]" should be printed; reference count for
		// list3 should be 0.
		ActorRef list3 = lops.cons(3, nil);
		System.out.printf("list3 is %s; reference count is %d%n",
				lops.exportList(list3), lops.getReferenceCount(list3));

		// Get reference count of nil (= tail of list3). "1" should be printed.
		System.out.printf("Reference count for nil is %d%n",
				lops.getReferenceCount(nil));

		// Get tail of list3. "[]" and "2" (reference count) should be printed.
		// Then print reference count of nil; should be 2.

		ActorRef tailList3 = lops.tail(list3);
		System.out.printf(
				"Tail for list3 is %s; reference count for tail is %d%n",
				lops.exportList(tailList3), lops.getReferenceCount(tailList3));
		System.out.printf("Reference count for nil is %d%n",
				lops.getReferenceCount(nil));

		// Decrement reference count of nil and print it. "1" should be printed.

		lops.decrementReferenceCount(nil);
		System.out.printf("Reference count for nil is %d%n",
				lops.getReferenceCount(nil));

		// // Decrement reference count for list3, then print reference counts
		// for list3, nil. Should get an error,
		// // since list3 and nil should have been shutdown.
		//
		// lops.decrementReferenceCount(list3);
		// System.out.printf("Reference count for list is %d%n, for nil is
		// %d%n", lops.getReferenceCount(list3), lops.getReferenceCount(nil));

		// Get head value. "3" should be printed.
		System.out.printf("Head of list3 is %d%n", lops.head(list3));

		// Cons 2 onto list3. "[2,3]" should be printed. Then print reference
		// counts of list23 (0) and list3 (1).
		ActorRef list23 = lops.cons(2, list3);
		System.out.printf("List23 is %s%n", lops.exportList(list23));
		System.out.printf(
				"Reference count for list23 is %d; reference count for list3 is %d%n",
				lops.getReferenceCount(list23), lops.getReferenceCount(list3));

		// Get head, tail of list23. "2", then "[3]", should be printed.
		// Reference count of list3 should be "2"
		System.out.printf("Head of list 23 is %s; tail of list23 is %s%n",
				lops.head(list23), lops.exportList(lops.tail(list23)));
		System.out.printf("Reference count for list3 is %d%n",
				lops.getReferenceCount(list3));

		// Now do list import / export and compare. "[1,2,3]" should be printed
		// twice. Then print reference count of
		// list123; should be 0. Finally print reference count of tail of
		// list123; should be "2".

		List<Integer> l123 = Arrays.asList(1, 2, 3);
		System.out.printf("Imported l123 is %s%n", l123);
		ActorRef list123 = lops.importList(l123);
		System.out.printf("Exported list123 is %s%n", lops.exportList(list123));
		System.out.printf("Reference count for list123 is %d%n",
				lops.getReferenceCount(list123));
		System.out.printf("Reference count for tail(list123) is %d%n",
				lops.getReferenceCount(lops.tail(list123)));

		// Now try map. "[]", "[4]" and "[2,3,4]" should be printed. Printed
		// reference counts should all be 0

		ActorRef mappedNil = lops.add1(nil);
		System.out.printf("mappedNil is %s; reference count is %d%n",
				lops.exportList(mappedNil), lops.getReferenceCount(mappedNil));
		ActorRef mappedList3 = lops.add1(list3);
		System.out.printf("mappedList3 is %s; reference count is %d%n",
				lops.exportList(mappedList3),
				lops.getReferenceCount(mappedList3));
		ActorRef mappedList123 = lops.add1(list123);
		System.out.printf("mappedList123 is %s; reference count is %d%n",
				lops.exportList(mappedList123),
				lops.getReferenceCount(mappedList123));

		// Compute reference count of tail(mappedList3); should be 2 (1 for
		// being tail + 1 for new tail message)

		System.out.printf("Reference count of tail(mappedList3) is %d%n",
				lops.getReferenceCount(lops.tail(mappedList123)));

		// Compute reference count of tail (list3); should be 2 (1 for being
		// tail + 1 for new tail message)

		System.out.printf("Reference count of tail(list3) is %d%n",
				lops.getReferenceCount(lops.tail(list3)));

		// Now try sum. "0" and "6" should be printed.

		System.out.printf("The sum of nil is %d%n", lops.sum(nil));
		System.out.printf("The sum of list123 is %d%n", lops.sum(list123));

		// Now print reference count of list123; should be 3 (1 for reference
		// from head node + 1 for earlier tail message + 1 for new tail message)

		System.out.printf("The reference count for tail(list123) is %d%n",
				lops.getReferenceCount(lops.tail(list123)));

		listSystem.terminate();
	}

}
