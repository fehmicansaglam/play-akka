package akka.actor;

import java.util.Collection;

import play.Logger;
import play.modules.hazelcast.HazelcastPlugin;
import akka.immutable.Notification;
import akka.immutable.QueueMetadata;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

public class NotificationSender extends UntypedActor {

	public void onReceive(Object message) {
		if (message instanceof String) {
			final MultiMap<String, Notification> notificationMap = HazelcastPlugin
					.getHazel().getMultiMap("notification-map");
			final IMap<String, QueueMetadata> notificationMetadataMap = HazelcastPlugin
					.getHazel().getMap("notification-metadata-map");

			final String key = (String) message;
			notificationMap.lock(key);
			notificationMetadataMap.lock(key);
			final Collection<Notification> notifications = notificationMap
					.get(key);
			notificationMap.remove(key);
			notificationMetadataMap.remove(key);
			notificationMetadataMap.unlock(key);
			notificationMap.unlock(key);

			if (notifications != null) {
				Logger.info("Sending mail to user: %s", key);
				for (Notification notification : notifications) {
					Logger.info("=> %s", notification.text);
				}
			}
		} else {
			unhandled(message);
		}
	}
}
