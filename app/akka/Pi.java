package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Listener;
import akka.actor.Master;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.message.Calculate;

public class Pi {
	private ActorSystem system;

	public void calculate(final int nrOfWorkers, final int nrOfElements,
			final int nrOfMessages) {
		// Create an Akka system
		system = ActorSystem.create("PiSystem");

		// create the result listener, which will print the result and shutdown
		// the system
		final ActorRef listener = system.actorOf(new Props(Listener.class),
				"listener");

		// create the master
		ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new Master(nrOfWorkers, nrOfMessages, nrOfElements,
						listener);
			}
		}), "master");

		// start the calculation
		master.tell(new Calculate());
	}

	public void awaitTermination(){
		system.awaitTermination();
	}
}
