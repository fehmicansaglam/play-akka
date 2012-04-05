package akka.manager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.NotificationSender;
import akka.actor.Props;
import akka.actor.NotificationWriter;
import akka.immutable.Notification;
import akka.routing.RoundRobinRouter;

public class NotificationManager {

	private static final ActorSystem ACTOR_SYSTEM;
	private static final ActorRef WORKER_ROUTER;
	private static final ActorRef SENDER_ROUTER;
	static {
		ACTOR_SYSTEM = ActorSystem.create("NotificationSystem");
		WORKER_ROUTER = ACTOR_SYSTEM.actorOf(
				new Props(NotificationWriter.class).withRouter(new RoundRobinRouter(4)),
				"workerRouter");
		SENDER_ROUTER = ACTOR_SYSTEM
				.actorOf(new Props(NotificationSender.class)
						.withRouter(new RoundRobinRouter(4)), "senderRouter");
	}

	private NotificationManager() {
	}

	public static void createNotification(final Notification notification) {
		WORKER_ROUTER.tell(notification);
	}

	public static void sendNotification(final String username) {
		SENDER_ROUTER.tell(username);
	}

	public static void awaitTermination() {
		ACTOR_SYSTEM.awaitTermination();
	}

	public static void terminate() {
		ACTOR_SYSTEM.shutdown();
	}

}
