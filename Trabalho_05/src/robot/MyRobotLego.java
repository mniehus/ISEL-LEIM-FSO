package robot;

import javax.swing.JTextField;

import RobotLego.RobotLego;
import gui.RobotPlayer;
import robot.automate.*;

public class MyRobotLego implements RobotNervousSystem {
	private final JTextField l;
	
	private final static int FRONT_SCANNER_PORT = RobotLego.S_2;
	private final static int BACK_SCANNER_PORT = RobotLego.S_1;
	private final boolean liveMode;
	
	private RobotLego robot;
	private RobotPlayer player;
	
	private BackScanner bScanner;
	private FrontScanner fScanner;
	private Roam roam;
	private Escape escape;
	private Avoid avoid;


	public MyRobotLego(JTextField l, boolean liveMode, RobotPlayer player) {
		this.l = l;
		this.liveMode = liveMode;

		this.player = player;
		if(liveMode) robot = new RobotLego();
	}

	public boolean OpenNXT(String name) {
		return liveMode ? robot.OpenNXT(name) : !liveMode;
	}

	public boolean CloseNXT() {
		if(liveMode) robot.CloseNXT();
		return true;
	}

	public void Reta(int units) {
		l.setText("Moving forward " + units + " units");
		//0:units
		if(player.isRecording()) player.addCommand("0:"+units);
		if(liveMode) robot.Reta(units);
	}

	public void CurvarDireita(int radius, int angle) {
		l.setText("Turning left " + radius + " radius " + angle + " angle");
		//1:radius:angle
		if(player.isRecording()) player.addCommand("1:"+radius+":"+angle);
		if (liveMode) robot.CurvarDireita(radius, angle);
	}

	public void CurvarEsquerda(int radius, int angle) {
		l.setText("Turning right " + radius + " radius " + angle + " angle");
		//2:radius:angle
		if(player.isRecording()) player.addCommand("2:"+radius+":"+angle);
		if (liveMode) robot.CurvarEsquerda(radius, angle);
	}

	public void AjustarVMD(int offset) {
		if (liveMode) robot.AjustarVMD(offset);
	}

	public void AjustarVME(int offset) {
		if (liveMode) robot.AjustarVME(offset);
	}

	public void Parar(boolean trueStop) {
		l.setText("Robot stop");
		//3
		if(player.isRecording()) player.addCommand("3");
		if (liveMode) robot.Parar(trueStop);
	}
	
	public void SetSpeed(int speed) {
		if (liveMode) robot.SetSpeed(speed);
	}
	
	public void SetSensorLowspeed(int port) {
		if (liveMode) robot.SetSensorLowspeed(port);
	}
	
	public int SensorUS(int port) {
		if (liveMode) return robot.SensorUS(port);
		
		return 0;
	}
	
	public void SetSensorTouch(int port) {
		if (liveMode) robot.SetSensorTouch(port);
	}
	
	public int Sensor(int port) {
		if (liveMode) return robot.Sensor(port);
		
		return 0;
	}
	
	@Override
	public void roam() {
		if(roam == null || (roam != null && !roam.isActive())) roam = new Roam(this);
		else roam.deactivate();
	}
	@Override
	public void escape(int minDistance, int maxDistance) { 
		if(bScanner == null || (bScanner != null && !bScanner.isActive())) bScanner = new BackScanner(this, BACK_SCANNER_PORT, minDistance, maxDistance);
		else {
			bScanner.deactivate();
			if(escape != null) escape.deactivate();
		}
	}
	@Override
	public void avoid() {
		if(fScanner == null || (fScanner != null && !fScanner.isActive())) fScanner = new FrontScanner(this, FRONT_SCANNER_PORT);
		else {
			fScanner.deactivate();
			if(avoid != null) avoid.deactivate();
		}
		
	}

	public static void sleep(int ms) {	  
		try { Thread.sleep(ms); }
		catch (InterruptedException e) { }
	}

	@Override
	public void frontObjectDetected(int distance) {
		System.out.println("Front object detected");

		if(bScanner != null && bScanner.isActive()) {
			bScanner.pause();
			System.out.println("BackScanner paused");
		}
		
		if(roam != null && roam.isActive()) {
			roam.pause();
			System.out.println("Roam paused");
		}
		
		if(escape != null && escape.isActive()) {
			escape.deactivate();
			escape = null;
			System.out.println("Escape deactivated");
		}
		
		robot.Parar(true);
		avoid = new Avoid(this, fScanner);
	}

	@Override
	public void rearObjectDetected(int distance) {
		System.out.println("Rear object detected");
		
		if(roam != null && roam.isActive()) {
			roam.pause();
			System.out.println("Roam paused");
		}
		
		if(bScanner != null && bScanner.isActive()) {
			bScanner.pause();
			System.out.println("BackScanner paused");
		}
		
		System.out.println("New Escape");
		escape = new Escape(this, bScanner);	
	}
	
	@Override
	public void frontObjectIsGone() {
		System.out.println("Front object is gone");

		avoid = null;
		if(roam != null && roam.isPaused()) {
			roam.unpause();
			System.out.println("Roam unpaused");
		}
		
		if(bScanner != null && bScanner.isActive() && bScanner.isPaused()) {
			bScanner.unpause();
			System.out.println("BackScanner unpaused");
		}
		
	}

	@Override
	public void rearObjectIsGone() {
		System.out.println("Rear object is gone");
		
		if(escape != null && escape.isActive()) {
			escape.deactivate();
			escape = null;
			System.out.println("Escape deactivated");
		}
		
		if(bScanner != null && bScanner.isActive() && bScanner.isPaused()) {
			bScanner.unpause();
			System.out.println("BackScanner unpaused");
		}
		
		if(roam != null && roam.isPaused()) {
			roam.unpause();	
			System.out.println("Roam paused");
		}
	}
}