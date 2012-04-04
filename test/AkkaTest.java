import org.junit.Test;

import play.test.UnitTest;
import akka.Pi;

public class AkkaTest extends UnitTest {

	@Test
	public void testCalculatePi() {
		Pi c = new Pi();
		c.calculate(4, 10000, 10000);
		c.awaitTermination();
	}

}
