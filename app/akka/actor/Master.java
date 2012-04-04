package akka.actor;

import akka.pojo.immutable.Notification;
import akka.routing.RoundRobinRouter;

public class Master extends UntypedActor {

	private final ActorRef workerRouter;

	public static final ActorSystem ACTOR_SYSTEM;
	public static final ActorRef INSTANCE;
	static {
		ACTOR_SYSTEM = ActorSystem.create("NotificationSystem");
		INSTANCE = ACTOR_SYSTEM.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new Master(4);
			}
		}), "master");
	}

	private Master(final int nrOfWorkers) {
		workerRouter = this.getContext().actorOf(
				new Props(Worker.class).withRouter(new RoundRobinRouter(
						nrOfWorkers)), "workerRouter");
	}

	public void onReceive(Object message) {
		if (message instanceof Notification) {
			workerRouter.tell(message, getSelf());
		} else {
			unhandled(message);
		}
	}

	public static void awaitTermination() {
		ACTOR_SYSTEM.awaitTermination();
	}

	public static void terminate() {
		ACTOR_SYSTEM.shutdown();
	}

}
