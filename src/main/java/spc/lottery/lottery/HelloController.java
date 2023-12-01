package spc.lottery.lottery;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    int numRows = 20; // Set the number of rows as needed
    int numColumns = 4; // Set the number of columns as needed

    public TextArea textArea = new TextArea();
    @FXML
    private GridPane gridPane;
    private TextField[][] textFields;

    private PrizePool pp = new PrizePool();
    private List<Prize> prizeList = new ArrayList<>();


    //初始化：
    public void initialize() {

        FileTestAndCreate();
        generateTextFields();
    }

    public void drawClick(ActionEvent actionEvent) {
        // 获取点击的按钮
        Button clickedButton = (Button) actionEvent.getSource();
        String buttonText = clickedButton.getText();


        // 将按钮的名称追加到TextArea中
        textArea.appendText("Clicked button: " + buttonText + "\n");

    }

    public void reset(ActionEvent actionEvent) {
        // 获取点击的按钮
        Button clickedButton = (Button) actionEvent.getSource();
        String buttonText = clickedButton.getText();


        // 将按钮的名称追加到TextArea中
        textArea.appendText("Clicked button: " + buttonText + "\n");

    }

    public void initPrizePool(ActionEvent actionEvent) {
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            if (gridPane.getChildren().get(i) instanceof TextField) {
                TextField textField = (TextField) gridPane.getChildren().get(i);
                String content = textField.getText();
                System.out.println("TextField " + i + " content: " + content);
                // You can store or process the content as needed
            }
        }

    }

    public void generateTextFields() {
        // read prize record from localfile
        /*
        道具1 0.001
        道具2 0.002
         */
        // new prize pool
        // new prizelist


        try (BufferedReader reader = new BufferedReader(new FileReader("record.txt"))) {
            String line;


            while ((line = reader.readLine()) != null) {
                String[] values = line.split(" ");
                Prize p = new Prize(values[0], Float.parseFloat(values[1]));
                prizeList.add(p);
            }
            pp.setPrizes(prizeList);

            int pIndex = 0;
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numColumns; col++) {
                    String text = "";
                    if (prizeList.size() == pIndex) {
                        text = "";
                    } else {
                        text = col % 2 == 0 ? prizeList.get(pIndex).getName() : prizeList.get(pIndex++).getRate() + "";
                    }
                    TextField textField = new TextField();
                    textField.setText(text);
                    gridPane.add(textField, col, row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateValuesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("yourfile.txt"))) {
            int colCount = gridPane.getColumnCount();
            for (TextField[] row : textFields) {
                for (TextField textField : row) {
                    writer.write(textField.getText() + " ");
                    colCount++;
                    if (colCount % gridPane.getColumnCount() == 0)
                        writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void FileTestAndCreate() {
        String filePath = "record.txt"; // 指定文件路径

        // 创建Path对象
        Path path = Paths.get(filePath);

        // 检查文件是否存在
        if (Files.exists(path)) {
            System.out.println("文件已经存在");
        } else {
            try {
                // 创建文件
                Files.createFile(path);
                System.out.println("文件创建成功");
            } catch (IOException e) {
                System.err.println("无法创建文件: " + e.getMessage());
            }
        }
    }

}