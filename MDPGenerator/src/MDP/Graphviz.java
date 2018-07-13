package cmdp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Graphviz {
	private String runPath = "";
    private String dotPath = ""; 
    private String runOrder="";
    private String dotCodeFile="dotcode.txt";
    private String resultPng="dotPng";
    private StringBuilder graph = new StringBuilder();

    Runtime runtime=Runtime.getRuntime();
    
    Graphviz(){
    	
    }
    Graphviz(String runPath,String dotPath){
    	this.runPath = runPath;
    	this.dotPath = dotPath;
    }
    
    public void run() {
        File file=new File(runPath);
        file.mkdirs();
        writeGraphToFile(graph.toString(), runPath);
        creatOrder();
        try {
            runtime.exec(runOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void creatOrder(){
        runOrder+=dotPath+" ";
        runOrder+=runPath;
        runOrder+="\\"+dotCodeFile+" ";
        runOrder+="-T png ";
        runOrder+="-o ";
        runOrder+=runPath;
        runOrder+="\\"+resultPng+".png";
        System.out.println(runOrder);
    }

    public void writeGraphToFile(String dotcode, String filename) {
        try {
            File file = new File(filename+"\\"+dotCodeFile);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(dotcode.getBytes());
            fos.close();
        } catch (java.io.IOException ioe) { 
            ioe.printStackTrace();
        }
     }  

    public void add(String line) {
        graph.append("\t"+line);
    }

    public void addln(String line) {
        graph.append("\t"+line + "\n");
    }

    public void addln() {
        graph.append('\n');
    }

    public void start_graph() {
        graph.append("digraph G {\n") ;
    }

    public void end_graph() {
        graph.append("}") ;
    }   
    
    public String generateDotString(ArrayList<actTransMatrix>arrayList) {
    	String temp = "";
    	System.out.println("this sentence shows that you're generating Dot Gragh");
    	for(int i=0;i<arrayList.size();i++) {
    		temp += ""+arrayList.get(i).getStateTransition().getStart();
    		temp += "->";
    		temp += ""+arrayList.get(i).getStateTransition().getEnd();
    		temp += "[label="+""+arrayList.get(i).getAction().getActName()+"]";
    		temp += "[taillabel="+""+arrayList.get(i).getProbability()+"]";
    		temp += ";";	
    	}
    	return temp;
    }
}
