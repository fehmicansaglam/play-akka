import java.util.UUID;

import org.junit.Test;

import play.test.UnitTest;
import akka.immutable.Notification;
import akka.manager.NotificationManager;

public class AkkaTest extends UnitTest {

	@Test
	public void testHazelcast() {
		for (int i = 0; i < 10000; i++) {
			final String username = "canavar";
			final String text = UUID.randomUUID().toString();
			final Notification notification = new Notification(username, text);
			NotificationManager.createNotification(notification);
		}
		NotificationManager.awaitTermination();
	}

}
