package controllers;

import akka.Pi;
import play.Logger;
import play.mvc.Controller;

public class Application extends Controller {

	public static void index() { 
		Logger.info("Started");
		Pi c = new Pi();
		c.calculate(4, 50000, 50000);
		Logger.info("Finished");
		render();
	}

}