package cmdp;

public class State {
	private int s_id;
	private String s_name;
	
	public int getStateID() {
		return s_id;
	}
	public String getStateName() {
		return s_name;
	}
	public void setStateID(int id) {
		s_id = id;
	}
	public void setStateName(String name) {
		s_name = name;
	}
	State(int id,String name){
		this.s_id = id;
		this.s_name = name;
	}
}
