package spc.lottery.lottery;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;


import java.math.BigDecimal;
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
                    // Add a ChangeListener to the TextField
                    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (false == newValue) {
                            updatePrizeList();
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePrizeList() {
        List<Prize> newPrizeList = new ArrayList<>();
        String t = "name";
        for (Node node : gridPane.getChildren()) {

            if (node instanceof TextField) {
                if (t.equals("name") && !((TextField) node).getText().equals("")) {
                    t = "rate";
                    newPrizeList.add(new Prize(((TextField) node).getText(), 0));
                } else if (t.equals("rate")) {
                    t = "name";
                    try {
                        newPrizeList.get(newPrizeList.size() - 1).setRate(Float.parseFloat(((TextField) node).getText()));
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("有一个概率格式输入错误，取消更新");
                        return;
                    }
                }
            }
        }
        prizeList = newPrizeList;
    }

//    private void updatePrizeList() {
//        List<Prize> newPrizeList = new ArrayList<>();
//        int tfIndex = 0;
//        int pIndex = 0;
//        for (javafx.scene.Node node : gridPane.getChildren()) {
//            if (node instanceof TextField) {
//                TextField textField = (TextField) node;
//                if(textField.getText().equals("")){
//                    continue;
//                }
//                if (tfIndex % 2 == 0) {
//
//                    String oldName = prizeList.get(pIndex).getName();
//                    String newName = textField.getText();
//                    if (!oldName.equals(newName)) {
//                        prizeList.get(pIndex).setName(newName);
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setContentText("道具名: " + oldName + "修改为" + newName);
//                    }
//
//                } else {
//                    float oldRate = prizeList.get(pIndex).getRate();
//                    try {
//                        float newRate = Float.parseFloat(textField.getText());
//                        if (oldRate != newRate) {
//                            prizeList.get(pIndex).setRate(newRate);
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                            alert.setContentText("道具: " + prizeList.get(pIndex).getName() + "的概率修改为" + newRate);
//                        }
//                    } catch (Exception e) {
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setContentText("有一个概率格式输入错误, 已自动回复原值");
//                        textField.setText(oldRate + "");
//                    }
//                    //下一个prize
//                    pIndex++;
//                }
//            }
//
//        }
//    }

    public void saveRates() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("record.txt"))) {
            int colCount = gridPane.getColumnCount();
            for (Prize prize : prizeList) {
                writer.write(prize.getName() + " " + prize.getRate() + "\n");
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

    public void sum(ActionEvent actionEvent) {
        BigDecimal f = new BigDecimal("0.000000");
        for (Prize prize : prizeList) {
            f = f.add(new BigDecimal(""+prize.getRate()));
        }
        // 提示框
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText("提示");
        alert.setContentText("综合中奖率：\n" + f);
        alert.showAndWait();

    }
}