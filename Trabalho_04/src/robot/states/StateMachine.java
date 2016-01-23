package robot.states;

import robot.MyRobotLego;

public class StateMachine implements RobotNervousSystem {
	public final static int MAX_SCANNERS = 2;
	
	private MyRobotLego robot;
	private ActiveState activeState;
	private PassiveState passiveState;
	private Scanner[] scanners = new Scanner[MAX_SCANNERS];
	
	public MyRobotLego getRobot() { return this.robot; }
	
	public State getActiveState() { return this.activeState; }
	private void setActiveState(ActiveState newState) { this.activeState = newState; }
	
	public State getPassiveState() { return this.passiveState; }
	public void setPassiveState(PassiveState newState) { this.passiveState = newState; }
	
	public Scanner[] getScanners() { return this.scanners; }
	
	public int numberOfActiveScanners() {
		int i = 0;
		for(Scanner scanner : scanners)  i = (scanner != null) ? i++ : i;
		
		return i;
	}
	public boolean addScanner(Scanner newScanner) {
		int nScanners = numberOfActiveScanners();
		if(scanners.length == nScanners) return false;
		
		scanners[nScanners] = newScanner;
		
		return true;
	}
	public void rmScanner(int id) {
		for(Scanner scanner : scanners) {
			if(scanner.id == id) scanner = null;
		}
		
		for(int i = 0; i < scanners.length; i++) {
			if(scanners[i] == null && (i < scanners.length - 1)) {
				scanners[i] = scanners[i + 1];
			}
		}
	}
	@Override
	public void ObjectDetected(ActiveState newState) {
		if(activeState != null) {
			if(newState.weight > activeState.weight) activeState = newState;
		}	
		if(activeState == null) {
			activeState = newState;
		}
		if(passiveState != null && activeState != null) {
			passiveState.pause();
		}
	}
	@Override
	public void ObjectIsGone() {
		activeState = null;
		
		if(passiveState != null) {
			passiveState.unpause();
		}
	}
}