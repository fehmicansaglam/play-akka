package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import antlr.Utils;

import models.*;
import Utils.Pi;

public class Application extends Controller {

	public static void index() {
		Logger.info("Started");
		Pi c = new Pi();
		c.calculate(4, 50000, 50000);
		Logger.info("Finished");
		render();
	}

}