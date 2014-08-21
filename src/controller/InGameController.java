package controller;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import stages.Stage;
import view.InGameScene;
import view.TimeLabel;
import model.GameObject;

/**
 * @author furkan
 *
 */

public class InGameController implements EventHandler<ActionEvent> {
	
	private InGameScene scene;
	private Stage stage;
	private TimeLabel view;
	private int min;
	private int sec;
	private int ms;
	private float timeStep = 1.0f / 60.f;
	private int velocityIterations = 8;
	private int positionIterations = 3;

	public InGameController(InGameScene inGameScene, Stage st, TimeLabel view)
	{
		stage = st;
		this.scene = inGameScene;
		this.view = view;
		min = 0;
		sec = 0;
		ms = 0;
	}
	
	// Add object
	public void addObject(GameObject obj)
	{
		stage.getWorldController();
		WorldController.addObject(obj);
	}
	


	
	public void incrementTime() {
		//increment second and then check if necessary to increment label
		ms += 1;

		if ( ms == 60) {
			sec++;
			ms = 0;
		}
		if (sec == 60){
			min++;
			sec = 0;
		}
	}
	
	public void resetTime() {
		min = 0;
		sec = 0;
	}

	// Simulates the world and checks for required conditions
	@Override
	public void handle(ActionEvent event) {
		boolean isMainReplaced = false;

		// JBox2D calculations

		stage.getWorldController().getWorld().step(timeStep, velocityIterations, positionIterations);
		for (GameObject obj: stage.getWorldController().getObjects()) {
			obj.setPos(obj.body.getPosition());
			obj.rotate(obj.body.getAngle());
			if ( obj.isMain() && !obj.isInside())
				isMainReplaced = true;
		}
		
		// Destroy objects outside of the screen
		stage.destroyOutside();
		// add listeners to new main ball
		if (isMainReplaced){
			scene.restoreMain();
			stage.getMainBall().gShape().setOnMouseClicked(new BallController(stage.getMainBall()));

		}
		// Checks finish condition		
		if(stage.getFlag().isFinished(stage.getMainBall().getPos())){
			scene.won();
		}
		
		// Checks if main ball captured by launcher
		if(stage.haveLauncher() != null ) {
				if(stage.haveLauncher().isCaptured(stage.getMainBall().getPos())){
					scene.pause();
					scene.launch();
				}
		}
		//update the time view
		if( !view.isPaused()) {
			incrementTime();
			view.setTime(min, sec);
		}
	}

}