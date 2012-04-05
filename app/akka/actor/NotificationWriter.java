package akka.actor;

import play.modules.hazelcast.HazelcastPlugin;
import akka.immutable.Notification;
import akka.immutable.QueueMetadata;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

public class NotificationWriter extends UntypedActor {

	public void onReceive(Object message) {
		if (message instanceof Notification) {
			final MultiMap<String, Notification> notificationMap = HazelcastPlugin
					.getHazel().getMultiMap("notification-map");
			final IMap<String, QueueMetadata> notificationMetadataMap = HazelcastPlugin
					.getHazel().getMap("notification-metadata-map");
			notificationMetadataMap.addIndex("entryCount", true);
			Notification notification = (Notification) message;
			final String key = notification.username;
			notificationMap.lock(key);
			notificationMetadataMap.lock(key);

			notificationMap.put(key, notification);
			QueueMetadata metadata = notificationMetadataMap.get(key);
			if (metadata == null) {
				metadata = new QueueMetadata(1, notification.timestamp);
			} else {
				metadata = metadata.increment();
			}
			notificationMetadataMap.put(key, metadata);

			notificationMetadataMap.unlock(key);
			notificationMap.unlock(key);
		} else {
			unhandled(message);
		}
	}
}
