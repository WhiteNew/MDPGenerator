package cmdp;

import javafx.scene.control.Label;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.*;

public class MyController implements Initializable {
	@FXML
	private TextField myTextField1;
	@FXML
	private TextField myTextField2;
	@FXML
	private TextField inputFeature;

	@FXML
	private TextField genderfld;
	@FXML
	private TextField agefld;
	@FXML
	private TextField careerfld;
	@FXML
	private TextField edtingfld;
	@FXML
	private TextField callingfld;
	@FXML
	private TextField drivingtimefld;
	@FXML
	private TextField posefld;
	@FXML
	private TextField eyefld;
	@FXML
	private TextField handfld;

	@FXML
	private Button openDataFileButton;
	@FXML
	private Button openTestDataButton;
	@FXML
	private Button traningButton;
	@FXML
	private Button evaluatingButton;
	@FXML
	private Button testingButton;
	@FXML
	private Button showGraph;
	@FXML
	private Button showPrismCode;
	@FXML
	private Button predictOne;
	@FXML
	private Button detail;
	@FXML
	private Button saveSubmit;
	@FXML
	private Button closeDetails;
	@FXML
	private Button updateTrainSet;
	@FXML
	private Button helpButton;
	@FXML
	private Button quitButton;
	@FXML
	private TextArea showwindow;
	@FXML
	private ImageView myImageView;
	
	private Stage DeatilsWindow = new Stage();

	// user choose .txt file
	public String trainfilepath;
	public String testfilepath;
	// output target .arff file
	public String outFilePathTrain;
	public String outFilePathEvaluate;
	public String outFilePathTest;

	public BufferedReader traind;
	public Instances train;
	public NaiveBayes nb;
	public Evaluation eval;

	public int numOfTestInstance = 0;
	public double act[][];
	public double predict[];

	public ArrayList<double[][]> actStateTransPro;
	public ArrayList<State> stateList;
	public ArrayList<Action> actionList;
	public ArrayList<stateTransition> stateTransList;
	public ArrayList<actTransMatrix> actStateTransList;

	public static String updatedata;
	public static String detailData;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	// Convert txt training file to two arff file:training & evaluate
	public void traintxt2arff(String filepath) throws IOException {
		outFilePathTrain = filepath.substring(0, filepath.length() - 4) + "Training.arff";
		outFilePathEvaluate = filepath.substring(0, filepath.length() - 4) + "Evaluate.arff";

		int numOfInstance;
		FileReader reader = new FileReader(filepath);
		BufferedReader br = new BufferedReader(reader);

		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filepath)));
		lnr.skip(Long.MAX_VALUE);
		numOfInstance = lnr.getLineNumber() + 1 - 1;
		lnr.close();
		// System.out.println(numOfInstance);
		FileWriter writer1 = new FileWriter(outFilePathTrain);
		BufferedWriter bw1 = new BufferedWriter(writer1);
		FileWriter writer2 = new FileWriter(outFilePathEvaluate);
		BufferedWriter bw2 = new BufferedWriter(writer2);
		// first line are the attributes
		String str = null;
		str = br.readLine();
		String[] attributes = str.split(" ");
		// write head information for training file and evaluate file
		bw1.write("@relation" + " " + "TrainingData" + "\r\n");
		for (int i = 0; i < attributes.length - 1; i++) {
			bw1.write("@attribute" + " " + attributes[i] + " " + "numeric" + "\r\n");
		}
		bw1.write("@attribute" + " " + attributes[attributes.length - 1] + " " + "{1, 2, 3}" + "\r\n");
		bw1.write("@data" + "\r\n");
		bw2.write("@relation" + " " + "TrainingData" + "\r\n");
		for (int i = 0; i < attributes.length - 1; i++) {
			bw2.write("@attribute" + " " + attributes[i] + " " + "numeric" + "\r\n");
		}
		bw2.write("@attribute" + " " + attributes[attributes.length - 1] + " " + "{1, 2, 3}" + "\r\n");
		bw2.write("@data" + "\r\n");
		// split 0.8 data to train file and 0.2 to evaluate file
		while ((str = br.readLine()) != null) {
			int i;
			for (i = 0; i < numOfInstance * 0.8; i++) {
				bw1.write(str + "\r\n");
				str = br.readLine();
			}
			for (; i < numOfInstance; i++) {
				bw2.write(str + "\r\n");
				str = br.readLine();
			}
		}

		br.close();
		reader.close();
		bw1.close();
		bw2.close();
		writer1.close();
		writer2.close();
	}

	// transfer test.txt to test.arff,every fourth and fifth columns are
	// actions,generate the action two-dimensional vector
	public double[][] testtxt2arff(String filepath) throws IOException {
		outFilePathTest = filepath.substring(0, filepath.length() - 4) + "Test.arff";

		FileReader reader = new FileReader(filepath);
		BufferedReader br = new BufferedReader(reader);
		FileWriter writer = new FileWriter(outFilePathTest);
		BufferedWriter bw = new BufferedWriter(writer);

		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filepath)));
		lnr.skip(Long.MAX_VALUE);
		numOfTestInstance = lnr.getLineNumber() + 1 - 1;
		lnr.close();

		// initialize two-dimensional vector
		double tmp[][] = new double[numOfTestInstance][2];

		// first line are the attributes
		String str = null;
		str = br.readLine();
		String[] attributes = str.split(" ");

		// write head information for training file and evaluate file
		bw.write("@relation" + " " + "TestingData" + "\r\n");
		for (int i = 0; i < attributes.length - 1; i++) {
			bw.write("@attribute" + " " + attributes[i] + " " + "numeric" + "\r\n");
		}
		bw.write("@attribute" + " " + attributes[attributes.length - 1] + " " + "{1, 2, 3}" + "\r\n");
		bw.write("@data" + "\r\n");

		String[] vector;
		while ((str = br.readLine()) != null) {
			for (int i = 0; i < numOfTestInstance; i++) {
				vector = null;
				vector = str.split(" ");
				bw.write(str + "\r\n");
				tmp[i][0] = Double.valueOf(vector[4]);
				tmp[i][1] = Double.valueOf(vector[5]);
				str = br.readLine();
			}
		}

		br.close();
		reader.close();
		bw.close();
		writer.close();

		return tmp;
	}

	// Through FileChooser to open the training data file.
	public void openTrainingDataFile(ActionEvent event) {
		System.out.println("Please open the trainging data file,format like .txt");
		Stage Filestage = null;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Training Data File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(Filestage);
		trainfilepath = selectedFile.getPath();
		System.out.println(trainfilepath);
		myTextField1.setText(trainfilepath);
	}

	public void openTestingDataFile(ActionEvent event) {
		System.out.println("Please open the testing data file,format like .txt");
		Stage Filestage = null;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Testing Data File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(Filestage);
		testfilepath = selectedFile.getPath();
		System.out.println(testfilepath);
		myTextField2.setText(testfilepath);
	}

	public void Traingingdata(ActionEvent event) throws Exception {
		// 将。txt文件转化为。arff文件;
		traintxt2arff(trainfilepath);
		// 为读入文件建立文件流，构建训练实例train
		traind = new BufferedReader(new FileReader(outFilePathTrain));
		train = new Instances(traind);

		// 将最后一列的状态值作为训练的目标属性
		train.setClassIndex(train.numAttributes() - 1);
		traind.close();

		// 使用朴素贝叶斯方法训练分类器
		nb = new NaiveBayes();
		nb.buildClassifier(train);

		showwindow.clear();
		showwindow.appendText("恭喜，状态预测模型训练完成！！！！");
	}

	public double evaluatingClassifier() throws Exception {
		// build stream for read file and construct Instance for evaluating
		BufferedReader evaluated = new BufferedReader(new FileReader(outFilePathEvaluate));
		System.out.println(outFilePathEvaluate);
		Instances evaluate = new Instances(evaluated);
		// uses the last attribute as class attribute
		evaluate.setClassIndex(evaluate.numAttributes() - 1);
		evaluated.close();

		// use evaluateModel() to evaluate the classifier
		eval = new Evaluation(train);
		eval.evaluateModel(nb, evaluate);
		// calculate the accurate of the classifier
		double accurate = 1 - eval.errorRate();

		showwindow.clear();
		showwindow.appendText(eval.toSummaryString("\nResult\n==========\n", true));
		showwindow.appendText(eval.toClassDetailsString());
		showwindow.appendText(eval.toMatrixString());
		showwindow.appendText(eval.correct() + "");
		showwindow.appendText(1 - eval.errorRate() + "");
		showwindow.appendText(accurate + "");

		return 1 - eval.errorRate();
	}

	public void TestingData(ActionEvent event) throws Exception {
		System.out.println("Now,We begin to test Driver Data!");
		act = testtxt2arff(testfilepath);

		for (int i = 0; i < act.length; i++) {
			for (int j = 0; j < act[0].length; j++) {
				System.out.print(act[i][j] + " ");
			}
			System.out.println(" ");
		}
		// 为驾驶员行为模型的训练数据集文件建立文件流，并生成训练实例
		BufferedReader testd = new BufferedReader(new FileReader(outFilePathTest));
		Instances test = new Instances(testd);
		testd.close();
		// uses the last attribute as class attribute
		test.setClassIndex(test.numAttributes() - 1);

		// 初始化Evaluation类对象eval
		eval = new Evaluation(train);
		// 利用分类器对训练集test进行预测，将结果保存在predict数组中
		predict = eval.evaluateModel(nb, test);

		for (int i = 0; i < predict.length; i++) {
			System.out.print(predict[i] + 1 + " ");
		}
		System.out.println(" ");
		// for 4 action,generate 4 two-dimensional vectors;
		double[][] act1transpro = new double[3][3];
		double[][] act2transpro = new double[3][3];
		double[][] act3transpro = new double[3][3];
		double[][] act4transpro = new double[3][3];

		int act1count = 0;
		int act2count = 0;
		int act3count = 0;
		int act4count = 0;

		int act1st1count = 0;
		int act1st2count = 0;
		int act1st3count = 0;
		int act2st1count = 0;
		int act2st2count = 0;
		int act2st3count = 0;
		int act3st1count = 0;
		int act3st2count = 0;
		int act3st3count = 0;
		int act4st1count = 0;
		int act4st2count = 0;
		int act4st3count = 0;

		for (int i = 0; i < act.length - 1; i++) {
			if (act[i][0] == 0 && act[i][1] == 0) {
				act1count += 1;
				if (predict[i] == 0 && predict[i + 1] == 0) {
					act1transpro[0][0] += 1.0;
					act1st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 1) {
					act1transpro[0][1] += 1.0;
					act1st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 2) {
					act1transpro[0][2] += 1.0;
					act1st1count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 0) {
					act1transpro[1][0] += 1.0;
					act1st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 1) {
					act1transpro[1][1] += 1.0;
					act1st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 2) {
					act1transpro[1][2] += 1.0;
					act1st2count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 0) {
					act1transpro[2][0] += 1.0;
					act1st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 1) {
					act1transpro[2][1] += 1.0;
					act1st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 2) {
					act1transpro[2][2] += 1.0;
					act1st3count += 1;
				}
			} else if (act[i][0] == 0 && act[i][1] == 1) {
				act2count += 1;
				if (predict[i] == 0 && predict[i + 1] == 0) {
					act2transpro[0][0] += 1.0;
					act2st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 1) {
					act2transpro[0][1] += 1.0;
					act2st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 2) {
					act2transpro[0][2] += 1.0;
					act2st1count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 0) {
					act2transpro[1][0] += 1.0;
					act2st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 1) {
					act2transpro[1][1] += 1.0;
					act2st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 2) {
					act2transpro[1][2] += 1.0;
					act2st2count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 0) {
					act2transpro[2][0] += 1.0;
					act2st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 1) {
					act2transpro[2][1] += 1.0;
					act2st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 2) {
					act2transpro[2][2] += 1.0;
					act2st3count += 1;
				}
			} else if (act[i][0] == 1 && act[i][1] == 0) {
				act3count += 1;
				if (predict[i] == 0 && predict[i + 1] == 0) {
					act3transpro[0][0] += 1.0;
					act3st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 1) {
					act3transpro[0][1] += 1.0;
					act3st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 2) {
					act3transpro[0][2] += 1.0;
					act3st1count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 0) {
					act3transpro[1][0] += 1.0;
					act3st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 1) {
					act3transpro[1][1] += 1.0;
					act3st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 2) {
					act3transpro[1][2] += 1.0;
					act3st2count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 0) {
					act3transpro[2][0] += 1.0;
					act3st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 1) {
					act3transpro[2][1] += 1.0;
					act3st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 2) {
					act3transpro[2][2] += 1.0;
					act3st3count += 1;
				}
			} else if (act[i][0] == 1 && act[i][1] == 1) {
				act4count += 1;
				if (predict[i] == 0 && predict[i + 1] == 0) {
					act4transpro[0][0] += 1.0;
					act4st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 1) {
					act4transpro[0][1] += 1.0;
					act4st1count += 1;
				} else if (predict[i] == 0 && predict[i + 1] == 2) {
					act4transpro[0][2] += 1.0;
					act4st1count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 0) {
					act4transpro[1][0] += 1.0;
					act4st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 1) {
					act4transpro[1][1] += 1.0;
					act4st2count += 1;
				} else if (predict[i] == 1 && predict[i + 1] == 2) {
					act4transpro[1][2] += 1.0;
					act4st2count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 0) {
					act4transpro[2][0] += 1.0;
					act4st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 1) {
					act4transpro[2][1] += 1.0;
					act4st3count += 1;
				} else if (predict[i] == 2 && predict[i + 1] == 2) {
					act4transpro[2][2] += 1.0;
					act4st3count += 1;
				}
			}
		}
		if (act1st1count != 0) {
			act1transpro[0][0] /= act1st1count;
			act1transpro[0][1] /= act1st1count;
			act1transpro[0][2] /= act1st1count;
		}
		if (act1st2count != 0) {
			act1transpro[1][0] /= act1st2count;
			act1transpro[1][1] /= act1st2count;
			act1transpro[1][2] /= act1st2count;
		}
		if (act1st3count != 0) {
			act1transpro[2][0] /= act1st3count;
			act1transpro[2][1] /= act1st3count;
			act1transpro[2][2] /= act1st3count;
		}
		if (act2st1count != 0) {
			act2transpro[0][0] /= act2st1count;
			act2transpro[0][1] /= act2st1count;
			act2transpro[0][2] /= act2st1count;
		}
		if (act2st2count != 0) {
			act2transpro[1][0] /= act2st2count;
			act2transpro[1][1] /= act2st2count;
			act2transpro[1][2] /= act2st2count;
		}
		if (act2st3count != 0) {
			act2transpro[2][0] /= act2st3count;
			act2transpro[2][1] /= act2st3count;
			act2transpro[2][2] /= act2st3count;
		}
		if (act3st1count != 0) {
			act3transpro[0][0] /= act3st1count;
			act3transpro[0][1] /= act3st1count;
			act3transpro[0][2] /= act3st1count;
		}
		if (act3st2count != 0) {
			act3transpro[1][0] /= act3st2count;
			act3transpro[1][1] /= act3st2count;
			act3transpro[1][2] /= act3st2count;
		}
		if (act3st3count != 0) {
			act3transpro[2][0] /= act3st3count;
			act3transpro[2][1] /= act3st3count;
			act3transpro[2][2] /= act3st3count;
		}
		if (act4st1count != 0) {
			act4transpro[0][0] /= act4st1count;
			act4transpro[0][1] /= act4st1count;
			act4transpro[0][2] /= act4st1count;
		}
		if (act4st2count != 0) {
			act4transpro[1][0] /= act4st2count;
			act4transpro[1][1] /= act4st2count;
			act4transpro[1][2] /= act4st2count;
		}
		if (act4st3count != 0) {
			act4transpro[2][0] /= act4st3count;
			act4transpro[2][1] /= act4st3count;
			act4transpro[2][2] /= act4st3count;
		}
		remainTwoDigit(act1transpro);
		remainTwoDigit(act2transpro);
		remainTwoDigit(act3transpro);
		remainTwoDigit(act4transpro);
		actStateTransPro = new ArrayList<double[][]>();
		actStateTransPro.add(act1transpro);
		actStateTransPro.add(act2transpro);
		actStateTransPro.add(act3transpro);
		actStateTransPro.add(act4transpro);

		showwindow.clear();
		showwindow.appendText("MDP model have been constructed successfully!\n");
		
		getFeatureList(actStateTransPro);
		
		showwindow.appendText("state: ");
		for(int i=0;i<stateList.size();i++) {
			showwindow.appendText(" "+stateList.get(i).getStateName());
		}
		showwindow.appendText("\n");
		
		showwindow.appendText("act: ");
		for(int i=0;i<actionList.size();i++) {
			showwindow.appendText(" "+actionList.get(i).getActName());
		}
		showwindow.appendText("\n");
		
		showwindow.appendText("==========act1transpro==========\n");
		for (int i = 0; i < 3; i++) {
			showwindow.appendText("          ");
			for (int j = 0; j < 3; j++) {
				showwindow.appendText("        "+act1transpro[i][j] + " ");
			}
			showwindow.appendText("\n");
		}
		
		showwindow.appendText("==========act2transpro==========\n");
		for (int i = 0; i < 3; i++) {
			showwindow.appendText("          ");
			for (int j = 0; j < 3; j++) {
				showwindow.appendText("        "+act2transpro[i][j] + " ");
			}
			showwindow.appendText("\n");
		}
		
		showwindow.appendText("==========act3transpro==========\n");
		for (int i = 0; i < 3; i++) {
			showwindow.appendText("          ");
			for (int j = 0; j < 3; j++) {
				showwindow.appendText("        "+act3transpro[i][j] + " ");
			}
			showwindow.appendText("\n");
		}
		
		showwindow.appendText("==========act4transpro==========\n");
		for (int i = 0; i < 3; i++) {
			showwindow.appendText("          ");
			for (int j = 0; j < 3; j++) {
				showwindow.appendText("        "+act4transpro[i][j] + " ");
			}
			showwindow.appendText("\n");
		}
		
		// outputPrismFile(testfilepath,actStateTransList);
		Graphviz gv = new Graphviz("graph", "Graphviz2.38\\bin\\dot.exe");
		String tmp = gv.generateDotString(actStateTransList);
		gv.start_graph();
		gv.add(tmp);
		gv.end_graph();
		try {
			gv.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showMDPGraph() throws FileNotFoundException {
		File file = new File("E:\\研究学习\\cmdp\\cmdp\\graph\\dotPng.png");
		String url = file.toURI().toString();
		System.out.println(url);
		Image image = new Image(url, true);
		// myImageView = new ImageView(image);
		myImageView.setImage(image);
	}

	public void showPrismCode() throws IOException {
		String outputPrismFilePath = testfilepath.substring(0, testfilepath.length() - 4) + "Prism.txt";
		try {
			// read file content from file

			FileReader reader = new FileReader(outputPrismFilePath);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			showwindow.clear();
			while ((str = br.readLine()) != null) {
				showwindow.appendText(str + "\n");
			}
			br.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void display() {
		Stage window = new Stage();
		window.setTitle("状态转移图");
		// modality要使用Modality.APPLICATION_MODEL
		window.initModality(Modality.APPLICATION_MODAL);
		window.setMinWidth(300);
		window.setMinHeight(150);

		ImageView iv = new ImageView();
		File file = new File("E:\\研究学习\\cmdp\\cmdp\\graph\\dotPng.png");
		String url = file.toURI().toString();
		System.out.println(url);
		Image image = new Image(url, true);
		iv.setImage(image);

		VBox vb = new VBox(10);
		vb.getChildren().addAll(iv);
		vb.setAlignment(Pos.CENTER);

		Scene scene = new Scene(vb);
		window.setScene(scene);
		// 使用showAndWait()先处理这个窗口，而如果不处理，main中的那个窗口不能响应
		window.showAndWait();

	}

	// **************************************************填写详细信息***********************************************************************************************************
	public void fillDetails() throws IOException {
	//	Stage DeatilsWindow = new Stage();
		URL url;
		try {
			url = new File("src/cmdp/detailsData.fxml").toURL();
			Parent root = FXMLLoader.load(url);
			DeatilsWindow.setTitle("输入驾驶员详细行为信息");
			DeatilsWindow.setScene(new Scene(root));
			DeatilsWindow.show();
			
			

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public void helpButton() {
    	showwindow.clear();
    	showwindow.appendText("帮助手册：\n");
    	showwindow.appendText("1.实现步骤\n"+
    	"1.1实现状态预测模型\n"+"1.2实现MDP模型\n"+"1.3生成MDP模型后，可以生成MDP图和相应的prism代码\n");
    	showwindow.appendText("2.点击生成的图形，可以看放大 的MDP图。");
    	
    	
    }
    public void ExitButton() {
    	quitButton.getScene().getWindow().hide();
    }
	public void handleSubmit() {
		String s1=genderfld.getText();
		String s2=agefld.getText();
		String s3=careerfld.getText();
		String s4=edtingfld.getText();
		String s5=callingfld.getText();
		String s6=drivingtimefld.getText();
		String s7=posefld.getText();
		String s8=eyefld.getText();
		String s9=handfld.getText();
		detailData=s1+" "+s2+" "+s3+" "+s4+" "+s5+" "+s6+" "+s7+" "+s8+" "+s9+" "+"1";

		System.out.println(detailData);
		saveSubmit.getScene().getWindow().hide();
		DeatilsWindow.close();
//		handfld.setText(detailData);
//		showwindow.appendText(detailData);
//		inputFeature.setText(detailData);
	}
	
	public void HandDetailClose() {
		closeDetails.getScene().getWindow().hide();
	}

	// Stage window = new Stage();
	// window.setTitle("特征信息");
	// BorderPane root = new BorderPane();
	// Scene scene = new Scene(root, 380, 150, Color.WHITE);
	// GridPane gridpane = new GridPane();

	//
	// //modality要使用Modality.APPLICATION_MODEL
	// window.initModality(Modality.APPLICATION_MODAL);
	// window.setMinWidth(300);
	// window.setMinHeight(300);
	// Label genderlb = new Label("性别:0表示男性，1表示女性");
	// TextField genderfld=new TextField();
	//
	// Label agelb = new Label("年龄:0代表20~30岁人群，1代表35~45岁人群，2代表大于50岁的人群");
	// TextField agefld=new TextField();
	//
	// Label careerlb = new Label("职业：0代表商人，1代表白领，2代表无业人群");
	// TextField careerfld=new TextField();
	//
	// Label edtinglb = new Label("编辑短信：0表示未编辑短信，1表示编辑短信");
	// TextField edtingfld=new TextField();
	//
	// Label callinglb = new Label("打电话：0表示未接听电话，1表示接听电话");
	// TextField callingfld=new TextField();
	//
	// Label drivingtimelb = new
	// Label("驾驶时间：0代表驾驶时间小于1个小时，1代表驾驶时间为1到2个小时，2表示驾驶时间大于2个小时，");
	// TextField drivingtimefld=new TextField();
	//
	// Label poselb = new Label("坐姿是否端正：0代表挺直，1代表弯腰");
	// TextField posefld=new TextField();
	//
	// Label eyelb = new Label("眼睛是否注视前方：0代表直视前方，1代表斜视");
	// TextField eyefld=new TextField();
	//
	// Label handlb = new Label("手是否放在方向盘：0代表双手在方向盘上，1代表单手在方向盘上，2表示双手离开方向盘");
	// TextField handfld=new TextField();
	// // TextField state=new TextField();
	// Button submit=new Button();
	//

	// vb.getChildren().addAll(genderlb,genderfld,agelb,agefld,careerlb,careerfld,edtinglb,edtingfld,callinglb,callingfld,
	// drivingtimelb,drivingtimefld,poselb,posefld,eyelb,eyefld,handlb,handfld);
	// vb.setAlignment(Pos.CENTER);

	// Scene scene = new Scene();
	// window.setScene(scene);
	// 使用showAndWait()先处理这个窗口，而如果不处理，main中的那个窗口不能响应
	// window.showAndWait();


//	@FXML
//	public void getFeatureData() {
//		inputFeature.textProperty().addListener((observable, oldValue, newValue) -> {
//			// outputTextArea.appendText("TextField Text Changed (newValue: " + newValue +
//			// ")\n");
//			showwindow.appendText(inputFeature.getText());
//			// System.out.println(newValue);
//		});
//	}
	
	public void getFeatureDataFromDetailWin() {
		if(detailData!=null) {
			inputFeature.setText(detailData);
		}
	}

	public void predictOne() throws Exception {
		String onedata = inputFeature.getText();

		File oneDataFile = new File("oneData.txt");
		if (!oneDataFile.exists()) {
			oneDataFile.createNewFile();
		}

		FileReader reader = new FileReader(trainfilepath);
		BufferedReader br = new BufferedReader(reader);
		FileWriter writer1 = new FileWriter("oneData.txt");

		String str = null;
		str = br.readLine();
		String[] attributes = str.split(" ");

		FileOutputStream out = new FileOutputStream(oneDataFile, false); // 如果追加方式用true
		StringBuffer sb = new StringBuffer();
		sb.append("@relation" + " " + "TrainingData" + "\r\n");
		for (int i = 0; i < attributes.length - 1; i++) {
			sb.append("@attribute" + " " + attributes[i] + " " + "numeric" + "\r\n");
		}
		sb.append("@attribute" + " " + attributes[attributes.length - 1] + " " + "{1, 2, 3}" + "\r\n");
		sb.append("@data" + "\r\n");
		sb.append(onedata);
		out.write(sb.toString().getBytes("utf-8"));// 注意需要转换对应的字符集
		out.close();

		BufferedReader testd = new BufferedReader(new FileReader("oneData.txt"));
		Instances test = new Instances(testd);
		testd.close();
		// uses the last attribute as class attribute
		test.setClassIndex(test.numAttributes() - 1);

		// 初始化Evaluation类对象eval
		eval = new Evaluation(train);
		double[] result;
		// 利用分类器对训练集test进行预测，将结果保存在predict数组中
		result = eval.evaluateModel(nb, test);

		showwindow.clear();
		showwindow.appendText("状态预测完成");
		showwindow.appendText((int) result[0] + "");

		updatedata = onedata.substring(0, onedata.length() - 1) + (int) result[0] + "";
	}

	public void updateTrainSet() throws IOException {
		FileWriter writer = new FileWriter(trainfilepath, true);
		BufferedWriter bw = new BufferedWriter(writer);
		String str = inputFeature.getText();
		// bw.write("\\r\\n");
		bw.write("\r\n" + updatedata);
		bw.close();
		writer.close();
		showwindow.clear();
		showwindow.appendText("新数据\n" + updatedata + "\t"+"更新完成");
	}

	public void getFeatureList(ArrayList<double[][]> actStateTransPro) {
		int actnum = actStateTransPro.size();
		Action act1 = new Action(1, "OnlyDriving");
		Action act2 = new Action(2, "OnlyEditing");
		Action act3 = new Action(3, "OnlyCalling");
		Action act4 = new Action(4, "Both");
		actionList = new ArrayList<Action>();
		actionList.add(act1);
		actionList.add(act2);
		actionList.add(act3);
		actionList.add(act4);

		int statesize = actStateTransPro.get(0).length;
		State st1 = new State(1, "Focus");
		State st2 = new State(2, "SemiFocus");
		State st3 = new State(3, "Distracted");
		stateList = new ArrayList<State>();
		stateList.add(st1);
		stateList.add(st2);
		stateList.add(st3);

		stateTransList = new ArrayList<stateTransition>();
		actStateTransList = new ArrayList<actTransMatrix>();
		for (int i = 0; i < actnum; i++) {
			double tmp[][] = new double[statesize][statesize];
			tmp = actStateTransPro.get(i);
			for (int j = 0; j < tmp.length; j++) {
				for (int k = 0; k < tmp[0].length; k++) {
					if (tmp[j][k] != 0) {
						stateTransition stt = new stateTransition(j, k);
						actTransMatrix atm = new actTransMatrix(stt, actionList.get(i), tmp[j][k]);
						stateTransList.add(stt);
						actStateTransList.add(atm);
					}
				}
			}
		}
		System.out.println("-----------------dfasdfadsf-----------");
		System.out.println(stateList.size());
		System.out.println(actionList.size());
		System.out.println(stateTransList.size());
		System.out.println(actStateTransList.size());
		// actStateTransList.get(0).getStateTransition().getStart();
	}

	public void outputPrismFile(String filepath, ArrayList<actTransMatrix> actStateTransList) throws IOException {
		String outputPrismFilePath = filepath.substring(0, filepath.length() - 4) + "Prism.txt";

		FileWriter writer = new FileWriter(outputPrismFilePath);
		BufferedWriter bw = new BufferedWriter(writer);

		bw.write("mdp" + "\r\n");
		bw.write("module" + " Driver_behavior" + "\r\n");
		bw.write("state" + " : " + "[0..2]" + ";" + "\r\n");
		// bw.write("init"+":"+actStateTransList.get(0).getStateTransition().getStart());

		System.out.println("Does program run this?");

		int oldStartState = 0;
		int oldAction = 0;
		int currentStartState = actStateTransList.get(0).getStateTransition().getStart();
		int currentEndState = actStateTransList.get(0).getStateTransition().getEnd();
		int currentAction = actStateTransList.get(0).getAction().getActID();
		double probability = actStateTransList.get(0).getProbability();

		// write firstline
		bw.write("[]" + "state" + "=" + currentStartState + " & " + "act" + "=" + currentAction);
		bw.write("->");
		bw.write(probability + ":" + "(state'=" + currentEndState + ")");
		for (int i = 1; i < actStateTransList.size(); i++) {
			currentStartState = actStateTransList.get(i).getStateTransition().getStart();
			currentEndState = actStateTransList.get(i).getStateTransition().getEnd();
			currentAction = actStateTransList.get(i).getAction().getActID();
			probability = actStateTransList.get(i).getProbability();

			if (currentStartState != oldStartState || currentAction != oldAction) {
				bw.write(";" + "\r\n");
				bw.write("[]" + "states" + "=" + currentStartState + " & " + "act" + "=" + currentAction);
				bw.write("->");
				bw.write(probability + ":" + "(state'=" + currentEndState + ")");
			} else {
				bw.write("+");
				bw.write(probability + ":" + "(state'=" + currentEndState + ")");
			}
			oldStartState = currentStartState;
			oldAction = currentAction;
		}

		bw.write("\r\n" + "endmodule" + "\r\n");

		bw.close();
		writer.close();
	}

	public void remainTwoDigit(double num[][]) {
		for (int i = 0; i < num.length; i++) {
			for (int j = 0; j < num[0].length; j++) {
				if (num[i][j] != 0) {
					BigDecimal tmp = new BigDecimal(num[i][j]);
					num[i][j] = tmp.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
			}
		}
	}
}
