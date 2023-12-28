package spc.lottery.lottery;

import javafx.application.Platform;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class HelloController {
    public ProgressBar progressBar;
    int numRows = 20; // Set the number of rows as needed
    int numColumns = 4; // Set the number of columns as needed

    private User user = new User();
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

    public void drawClick(ActionEvent actionEvent) throws InterruptedException {

        if (HelloApplication.lock) {
            alert(false, "正在抽奖中，请稍后再试");
            return;
        }
        // 获取点击的按钮
        Button clickedButton = (Button) actionEvent.getSource();
        String buttonText = clickedButton.getText();
        int times = 0;
        switch (buttonText) {
            case "抽奖一次":
                times = 1;
                break;
            case "抽奖五次":
                times = 5;
                break;
            case "抽奖十次":
                times = 10;
                break;
            case "抽奖一百次":
                times = 100;
                break;
            case "抽奖一千次":
                times = 1000;
                break;
            case "抽奖一万次":
                times = 10000;
                break;
            case "抽奖十万次":
                times = 100000;
                break;
            case "抽奖一百万次":
                times = 1000000;
                break;
            case "抽奖一千万次":
                times = 10000000;
                break;
            case "抽奖一亿次":
                times = 100000000;
                break;
            case "抽奖十亿次":
                times = 1000000000;
                break;
            default:
                alert(false, "抽奖次数错误");
                return;
        }
        user.setTextArea(textArea);
        user.setPp(pp);
        int finalTimes = times;
        Thread thread = new Thread(() -> {
            HelloApplication.lock = true;
            user.drawing(finalTimes);
            HelloApplication.lock = false;
        });
        thread.start();
        HelloApplication.progress = 0;
        int finalTimes1 = times;
        Thread thread1 = new Thread(() -> {
            Instant lastTime = Instant.now();
            while (HelloApplication.progress < finalTimes1) {
                System.out.println("progress: " + HelloApplication.progress + "/" + finalTimes1);
                Platform.runLater(() -> progressBar.setProgress((double) HelloApplication.progress / finalTimes1));
                lastTime = Instant.now();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Platform.runLater(() -> progressBar.setProgress(1));
        });
        thread1.start();

    }

    public void reset(ActionEvent actionEvent) {
        user = new User();
        textArea.setText("重置成功");
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
                BigDecimal rate;
                if (values[1].contains("%")) {
                    rate = new BigDecimal(values[1].replace("%", ""));
                    rate = rate.divide(new BigDecimal("100"));
                } else {
                    rate = new BigDecimal(values[1]);
                }
                Prize p = new Prize(values[0], rate);
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
                        text = col % 2 == 0 ? prizeList.get(pIndex).getName() : prizeList.get(pIndex++).getRate().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + "%";
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

    public void updatePrizeList() {
        List<Prize> newPrizeList = new ArrayList<>();
        String t = "name";
        for (Node node : gridPane.getChildren()) {

            if (node instanceof TextField) {
                if (t.equals("name") && !((TextField) node).getText().equals("")) {
                    t = "rate";
                    newPrizeList.add(new Prize(((TextField) node).getText(), null));
                } else if (t.equals("rate")) {
                    t = "name";
                    try {
                        if (((TextField) node).getText().contains("%")) {
                            newPrizeList.get(newPrizeList.size() - 1).setRate(new BigDecimal((((TextField) node).getText().replace("%", ""))).divide(BigDecimal.valueOf(100)));
                        } else {
                            newPrizeList.get(newPrizeList.size() - 1).setRate(new BigDecimal(((TextField) node).getText()));
                        }
                    } catch (Exception e) {
                        alert(true, "概率输入错误, 已自动回复原值");
                        return;
                    }
                }
            }
        }
        alert(true, "更新成功");
        prizeList = newPrizeList;
        pp.setPrizes(prizeList);
    }

    public Alert alert(Boolean hid, String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(s);
        if (hid) {
            System.out.println(s);
        } else {
            System.out.println(s);
            alert.show();
        }
        return alert;
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
            for (Prize prize : prizeList) {
                writer.write(prize.getName() + " " + (prize.getRate()).multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%\n");
            }
            alert(false, "保存成功");
        } catch (IOException e) {
            alert(false, "保存失败");
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
        BigDecimal f = new BigDecimal("0.0");
        for (Prize prize : prizeList) {
            f = f.add( prize.getRate());
        }
        // 提示框
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText("提示");
        alert.setContentText("综合中奖率：\n" + f);
        alert.showAndWait();

    }
}