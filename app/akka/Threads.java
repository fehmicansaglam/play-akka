package akka;

import java.util.concurrent.TimeUnit;

import akka.Messages.*;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;

public class Threads {

	public static class Worker extends UntypedActor {
		private double calculatePiFor(int start, int nrOfElements) {
			double acc = 0.0;
			for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
				acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
			}
			return acc;
		}

		public void onReceive(Object message) {
			if (message instanceof Work) {
				Work work = (Work) message;
				double result = calculatePiFor(work.getStart(),
						work.getNrOfElements());
				getSender().tell(new Result(result), getSelf());
			} else {
				unhandled(message);
			}
		}
	}

	public static class Master extends UntypedActor {
		private final int nrOfMessages;
		private final int nrOfElements;

		private double pi;
		private int nrOfResults;
		private final long start = System.currentTimeMillis();

		private final ActorRef listener;
		private final ActorRef workerRouter;

		public Master(final int nrOfWorkers, int nrOfMessages,
				int nrOfElements, ActorRef listener) {
			this.nrOfMessages = nrOfMessages;
			this.nrOfElements = nrOfElements;
			this.listener = listener;

			workerRouter = this.getContext().actorOf(
					new Props(Worker.class).withRouter(new RoundRobinRouter(
							nrOfWorkers)), "workerRouter");
		}

		public void onReceive(Object message) {
			if (message instanceof Calculate) {
				for (int start = 0; start < nrOfMessages; start++) {
					workerRouter.tell(new Work(start, nrOfElements), getSelf());
				}
			} else if (message instanceof Result) {
				Result result = (Result) message;
				pi += result.getValue();
				nrOfResults += 1;
				if (nrOfResults == nrOfMessages) {
					// Send the result to the listener
					Duration duration = Duration.create(
							System.currentTimeMillis() - start,
							TimeUnit.MILLISECONDS);
					listener.tell(new akka.Messages.PiApproximation(pi,
							duration), getSelf());
					// Stops this actor and all its supervised children
					getContext().stop(getSelf());
				}
			} else {
				unhandled(message);
			}
		}
	}

	public class Listener extends UntypedActor {
		public void onReceive(Object message) {
			if (message instanceof PiApproximation) {
				PiApproximation approximation = (PiApproximation) message;
				System.out
						.println(String
								.format("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s",
										approximation.getPi(),
										approximation.getDuration()));
				getContext().system().shutdown();
			} else {
				unhandled(message);
			}
		}
	}

}
