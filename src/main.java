import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;


public class main extends Application {
    private TableView<TestFile> table;
    private TextField accuracyFld;
    private TextField precisionFld;
    private TextField accuracyNFld;
    private TextField precisionNFld;
    private TextField timeFld;

    @Override
    public void start(Stage primaryStage) throws Exception{

        BorderPane layout;
        primaryStage.setTitle("Spam Master");

        //Directory Picker to let user navigate to data folder
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Data Directory");
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(primaryStage);

//        File mainDirectory = new File("/home/ibrahim/Desktop/csci2020u_NOOTNOOT/Assignment/Assignment 1/data");

        //Pad the other directory within different file
        File spamDir = new File(mainDirectory + "/train/spam/");
        File hamDir = new File(mainDirectory + "/train/ham/");
        File spamTest = new File(mainDirectory + "/test/spam/");
        File hamTest = new File(mainDirectory + "/test/ham/");

        //Creates a structure to hold data
        ObservableList<TestFile> fileList = FXCollections.observableArrayList();

        double startTime = System.currentTimeMillis();

        //Creates a spamFilters and will first train the spam and ham,
        //after, it will test the spam and ham, with their respective
        //directory
        Filter spamFilter = new Filter();
        spamFilter.trainSpam(spamDir);
        spamFilter.trainHam(hamDir);

        spamFilter.test(spamTest, fileList);
        spamFilter.test(hamTest, fileList);


        // Table columns for Results
        TableColumn<TestFile,String> fileCol = new TableColumn<>("File");
        fileCol.setMinWidth(350);
        fileCol.setCellValueFactory(new PropertyValueFactory<>("filename"));

        TableColumn<TestFile,String> classCol = new TableColumn<>("Actual Class");
        classCol.setMinWidth(200);
        classCol.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

        TableColumn<TestFile,String> spamCol = new TableColumn<>("Spam Probability");
        spamCol.setMinWidth(300);
        spamCol.setCellValueFactory(new PropertyValueFactory<>("spamProbRounded"));

        table = new TableView<>();
        table.getColumns().add(fileCol);
        table.getColumns().add(classCol);
        table.getColumns().add(spamCol);

        // Display Area Below Table
        GridPane bottom = new GridPane();
        bottom.setPadding(new Insets(10));
        bottom.setVgap(10);
        bottom.setHgap(10);

        String accuracy = Double.toString( (spamFilter.getAccuracyHam(fileList) + spamFilter.getAccuracySpam(fileList)) / 2);
        String precision = Double.toString( ((spamFilter.getPrecision(fileList, "ham") + spamFilter.getPrecision(fileList, "spam")) / 2) );


        //=========================================================
        // Enhancement Program, using N-Gram with N =2
        //=========================================================

        int n = 2;
        Filter nG = new Filter();

        nG.trainSpamN(spamDir,n);
        nG.trainHamN(hamDir,n);
        //nG.trainHamN(ham2Dir,n);

        nG.testNGram(spamTest, fileList, n);
        nG.testNGram(hamTest, fileList,n);

        String accuracyN = Double.toString( (nG.getAccuracyHam(fileList) + nG.getAccuracySpam(fileList)) / 2);
        String precisionN = Double.toString( ((nG.getPrecision(fileList, "ham") + nG.getPrecision(fileList, "spam")) / 2) );

        double endTime = System.currentTimeMillis();

        String time= Double.toString((endTime-startTime)/1000) + "s";
        System.out.println(endTime - startTime);


        //Outputting fields to the bottom form
        accuracyFld = new TextField();
        accuracyFld.setEditable(false);
        accuracyFld.setText(accuracy);

        precisionFld = new TextField();
        precisionFld.setEditable(false);
        precisionFld.setText(precision);

        bottom.add(new Label("Accuracy"), 0, 0);
        bottom.add(accuracyFld, 1, 0);

        bottom.add(new Label("Precision"), 0, 1);
        bottom.add(precisionFld, 1, 1);

        accuracyNFld = new TextField();
        accuracyNFld.setEditable(false);
        accuracyNFld.setText(accuracyN);

        precisionNFld = new TextField();
        precisionNFld.setEditable(false);
        precisionNFld.setText(precisionN);

        bottom.add(new Label("Accuracy N=" + n), 2, 0);
        bottom.add(accuracyNFld, 3, 0);

        bottom.add(new Label("Precision N=" + n), 2, 1);
        bottom.add(precisionNFld, 3, 1);

        timeFld = new TextField();
        timeFld.setEditable(false);
        timeFld.setText(time);

        bottom.add(new Label("Time"),4,0);
        bottom.add(timeFld,5,0);

        layout = new BorderPane();
        layout.setCenter(table);
        layout.setBottom(bottom);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        table.setItems(fileList);

    }

    public static void main(String[] args) {
        launch(args);
    }
}