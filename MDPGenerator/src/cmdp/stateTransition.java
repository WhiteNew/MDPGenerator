package cmdp;

public class stateTransition {
	private int startState = 0;
	private int endState = 0;
	
	public void setStart(int start) {
		this.startState = start;
	}
	public void setEnd(int end) {
		this.endState = end;
	}
	public int getStart() {
		return startState;
	}
	public int getEnd() {
		return endState;
	}
	stateTransition(int start,int end){
		this.startState = start;
		this.endState = end;
	}
}
