package shell;
import java.io.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    private static String textToShow;
    private static TextArea top = new TextArea();
    private static TextField bottom = new TextField();
    private static Button close;
    private static Button minimize;
    private static Button maximize;
    private PipedInputStream inp = new PipedInputStream();
    private PipedOutputStream out = new PipedOutputStream();
    private StringBuilder outStringBuilder = new StringBuilder();
    private OutputStream outStream;

    public static void main(String[] args) throws IOException {
        // Testiranje Assembler klase
        Assembler assembler = new Assembler();
        String sourceCode = "LOAD 10\nADD 20\nSTORE 30\nHALT\n";
        String machineCode = assembler.assemble(sourceCode);
        System.out.println(machineCode);  // Ispis mašinskog koda
        launch(args);
    }

    private void addTextToTop() {
        if (outStringBuilder.length() > 0) {
            top.appendText(outStringBuilder.toString());
            outStringBuilder = new StringBuilder();
        }
    }

    private Assembler assembler = new Assembler();

    @Override
    public void start(Stage primaryStage) throws Exception {

        inp.connect(out);
        textToShow = "";

        close = new Button("X");
        close.setPrefSize(5, 5);
        minimize = new Button("_");
        minimize.setPrefSize(5, 5);
        maximize = new Button("❐");

        HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.TOP_RIGHT);

        buttons.getChildren().addAll(minimize, maximize, close);

        top = new TextArea();
        top.setMinSize(900, 450);
        top.setEditable(false);
        top.setText("Welcome to the OS emulator!\n");

        bottom = new TextField();
        bottom.setMinSize(900, 62);

        close.setOnAction(e -> {
            System.exit(0);
        });

        minimize.setOnAction(e -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
            bottom.requestFocus();
        });

        maximize.setOnAction(e -> {
            Stage stage = (Stage) maximize.getScene().getWindow();
            if (!stage.isMaximized())
                stage.setMaximized(true);
            else
                stage.setMaximized(false);
            bottom.requestFocus();
        });

        bottom.setOnAction(e -> {
            String command = bottom.getText();
            String result = assembler.processCommand(command);
            top.appendText("> " + command + "\n" + result + "\n");
            bottom.clear();
        });

        VBox root = new VBox(15);
        root.setPadding(new Insets(10, 30, 30, 30));
        root.getChildren().setAll(buttons, top, bottom);
        VBox.setVgrow(top, Priority.ALWAYS);
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add("application.css");

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();

        bottom.requestFocus();
    }
}

