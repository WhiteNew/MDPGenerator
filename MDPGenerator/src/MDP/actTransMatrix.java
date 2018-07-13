package cmdp;

import java.util.ArrayList;

public class actTransMatrix {
	private stateTransition st;
	private Action action;
	private double probability;
	
	actTransMatrix(stateTransition st, Action action, double prob){
		this.st = st;
		this.action = action;
		this.probability = prob;
	}
	public stateTransition getStateTransition() {
		return st;
	}
	public Action getAction() {
		return action;
	}
	public double getProbability() {
		return probability;
	}
}
