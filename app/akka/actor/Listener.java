package akka.actor;

import akka.message.PiApproximation;

public class Listener extends UntypedActor {
	public void onReceive(Object message) {
		if (message instanceof PiApproximation) {
			PiApproximation approximation = (PiApproximation) message;
			System.out.println(String.format(
					"\n\tPi approximation: \t%s\n\tCalculation time: \t%s",
					approximation.getPi(), approximation.getDuration()));
			getContext().system().shutdown();
		} else {
			unhandled(message);
		}
	}
}