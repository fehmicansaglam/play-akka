package akka.actor;

import play.modules.hazelcast.HazelcastPlugin;
import akka.pojo.immutable.Notification;
import akka.pojo.immutable.QueueMetadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

public class Worker extends UntypedActor {

	private static final Gson GSON = new GsonBuilder().create();

	public void onReceive(Object message) {
		if (message instanceof Notification) {
			final MultiMap<String, String> notificationMap = HazelcastPlugin
					.getHazel().getMultiMap("notification-map");
			final IMap<String, String> notificationMetadataMap = HazelcastPlugin
					.getHazel().getMap("notification-metadata-map");
			Notification notification = (Notification) message;
			final String key = notification.username;
			notificationMap.lock(key);
			notificationMetadataMap.lock(key);

			notificationMap.put(key, GSON.toJson(notification));
			QueueMetadata metadata = GSON.fromJson(
					notificationMetadataMap.get(key), QueueMetadata.class);
			if (metadata == null) {
				metadata = new QueueMetadata(1, notification.timestamp);
			} else {
				metadata = metadata.increment();
			}
			notificationMetadataMap.put(key, GSON.toJson(metadata));

			notificationMetadataMap.unlock(key);
			notificationMap.unlock(key);
			Master.terminate();
		} else {
			unhandled(message);
		}
	}
}
