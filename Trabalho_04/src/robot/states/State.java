package robot.states;

import robot.MyRobotLego;

public abstract class State extends Thread {
	public final static int DEFAULT_DELAY = 500;
	
	protected final MyRobotLego robot;
	protected boolean active = true;
	protected int delay = DEFAULT_DELAY;

	public int getDelay() { return this.delay; }
	
	public void setDelay(int delay) { this.delay = delay; }
	
	public boolean isActive() { return this.active; }
	
	public void deactivate() { this.active = false; }
	
	public abstract void action();
	
	public State(MyRobotLego robot) {
		this.robot = robot;
		this.start();
	}
	
	@Override
	public void run() {		
		if(!active) this.interrupt();
	}
	
}

abstract class ActiveState extends State {
	protected int weight = 0;
	
	public int getWeight() { return this.weight; }

	
	public ActiveState(MyRobotLego robot, Scanner scanner) {
		super(robot);
	}
	
	public void run() {
		super.run();
		
		try { Thread.sleep(delay); }
		catch (InterruptedException e) { }
	}
}

abstract class PassiveState extends State {
	protected boolean pause = false;
	
	public boolean isPaused() { return this.pause; }
	
	public void pause() { this.pause = true; }
	
	public void unpause() {
		synchronized(robot) {
			this.pause = false;
			robot.notify();
		}
	}
	
	public PassiveState(MyRobotLego robot) {
		super(robot);
	}
	
	public void run() {
		super.run();
		
		if(active && pause) {
			synchronized(robot) {
				try { robot.wait(); }
				catch (InterruptedException e) { }
			}
		}
		
		try { Thread.sleep(delay); } 
		catch (InterruptedException e) { }
	}
}

class Roam extends PassiveState {

	public Roam(MyRobotLego robot) {
		super(robot);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}
	
}

class Escape extends ActiveState {

	public Escape(MyRobotLego robot, Scanner scanner) {
		super(robot, scanner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}
	
}

class Avoid extends ActiveState {

	public Avoid(MyRobotLego robot, Scanner scanner) {
		super(robot, scanner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}
	
}