package akka.pojo.immutable;

import java.io.Serializable;

/**
 *
 * @author fehmicansaglam
 */
public final class Notification implements Serializable {

    public final long timestamp;
    public final String username;
    public final String text;

    public Notification(final String username, final String text) {
        this.timestamp = System.currentTimeMillis();
        this.username = username;
        this.text = text;
    }
}
