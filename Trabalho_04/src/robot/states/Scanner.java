package robot.states;

import java.util.ArrayList;
import java.util.List;

import robot.MyRobotLego;

public abstract class Scanner extends Thread {
	public final static int DEFAULT_DELAY = 500;
	
	protected final MyRobotLego robot;
	protected final int id;
	protected final int port;
	
	protected boolean active = true;
	protected int objectDistance = 0;
	protected boolean objectDetected = false;
	protected int delay = DEFAULT_DELAY;
	protected List<RobotNervousSystem> listeners = new ArrayList<RobotNervousSystem>();
	
	public int getDelay() { return this.delay; }
	
	public void setDelay(int delay) { this.delay = delay; }
	
	public boolean isActive() { return this.active; }
	
	public void deactivate() { this.active = false; }
	
	public int getPort() { return this.port; }
	
	protected void addListener(RobotNervousSystem toAdd) { this.listeners.add(toAdd); }
	
	protected abstract void objectDetected();
	protected abstract void objectIsGone();
	protected abstract void setPort(int port);
	public abstract int scan();
	
	public Scanner(StateMachine machine, int id, int port) {
		this.robot = machine.getRobot();
		this.id = id;
		setPort(port);
		this.port = port;
		this.addListener(machine);
		this.start();
	}
	
	@Override
	public void run() {		
		if(!active) this.interrupt();
	}
}

interface RobotNervousSystem {
	void ObjectDetected(ActiveState state);
	void ObjectIsGone();
}

class BackScanner extends Scanner {
	public static final int ID = 1;
	
	private MyRobotLego robot;
	private int[] trigger = new int[2];
	private int objectDistance = 0;

	public BackScanner(StateMachine machine, int id, int port) {
		super(machine, id, port);
	}

	@Override
	protected void objectDetected() {
		objectDetected = true;
		for(RobotNervousSystem listener : listeners)  listener.ObjectDetected(new Escape(trigger));	
	}

	@Override
	protected void objectIsGone() {
		objectDetected = false;
		for(RobotNervousSystem listener : listeners)  listener.ObjectIsGone();	
	}

	@Override
	protected void setPort(int port) { robot.SetSensorLowspeed(port); }

	@Override
	public int scan() { return robot.SensorUS(port); }
	
	@Override
	public void run() { 
		while(active) {
			objectDistance = scan();
			
			if(!objectDetected && (objectDistance > trigger[0] && objectDistance < trigger[1])) {
				objectDetected();
			}			
			else if(objectDetected && (objectDistance < trigger[0] || objectDistance > trigger[1])) {
				objectIsGone();
			}
			
				
			try { Thread.sleep(delay); } 
			catch (InterruptedException e) { }
		}
	}

}

class FrontScanner extends Scanner {
	public static final int ID = 2;
	
	public FrontScanner(StateMachine machine, int id, int port) {
		super(machine, id, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void objectDetected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void objectIsGone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setPort(int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int scan() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}