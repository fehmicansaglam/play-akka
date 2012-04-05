package akka.immutable;

import java.io.Serializable;

/**
 *
 * @author fehmicansaglam
 */
public final class QueueMetadata implements Serializable {

    public final int entryCount;
    public final long oldestEntryTimestamp;

    public QueueMetadata(int entryCount, long oldestEntryTimestamp) {
        this.entryCount = entryCount;
        this.oldestEntryTimestamp = oldestEntryTimestamp;
    }

    public QueueMetadata increment() {
        return new QueueMetadata(entryCount + 1, oldestEntryTimestamp);
    }
}
