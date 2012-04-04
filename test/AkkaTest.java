import java.util.UUID;

import org.junit.Test;

import play.test.UnitTest;
import akka.actor.Master;
import akka.pojo.immutable.Notification;

public class AkkaTest extends UnitTest {

	@Test
	public void testHazelcast() {
		final String username = "canavar";
		final String text = UUID.randomUUID().toString();
		final Notification notification = new Notification(username, text);
		Master.INSTANCE.tell(notification);
		Master.awaitTermination();
	}

}
