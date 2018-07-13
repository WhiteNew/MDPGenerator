package cmdp;

public class Action {
	private int a_id;
	private String a_name;
	
	public int getActID() {
		return a_id;
	}
	public String getActName() {
		return a_name;
	}
	public void SetActID(int id) {
		a_id = id;
	}
	public void SetActName(String name) {
		a_name = name;
	}
	Action(int id,String name){
		this.a_id = id;
		this.a_name = name;
	}
}
