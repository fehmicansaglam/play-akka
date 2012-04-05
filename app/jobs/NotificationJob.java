package jobs;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.modules.hazelcast.HazelcastPlugin;
import akka.immutable.Notification;
import akka.immutable.QueueMetadata;
import akka.manager.NotificationManager;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

@On("0/10 * * * * ?")
public class NotificationJob extends Job {
	static final String[] keys = { "canavar", "abuzer", "haydar", "abuzittin",
			"cabbar" };
	static final Random random = new Random();

	@Override
	public void doJob() throws Exception {
		Logger.info("Generating messages");
		for (int i = 0; i < 5; i++) {
			final String username = keys[random.nextInt(keys.length)];
			final String text = UUID.randomUUID().toString();
			final Notification notification = new Notification(username, text);
			NotificationManager.createNotification(notification);
		}
		Logger.info("Generated messages");

		final IMap<String, QueueMetadata> notificationMetadataMap = HazelcastPlugin
				.getHazel().getMap("notification-metadata-map");
		final long timestamp = System.currentTimeMillis();
		Set<String> keys = notificationMetadataMap.keySet(new SqlPredicate(
				"entryCount > 6 or oldestEntryTimestamp < "
						+ (timestamp - 1000 * 60)));
		for (String key : keys) {
			NotificationManager.sendNotification(key);
		}
	}

}
