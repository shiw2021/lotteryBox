module spc.lottery.lottery {
    requires javafx.controls;
    requires javafx.fxml;

    requires lombok;


    opens spc.lottery.lottery to javafx.fxml;
    exports spc.lottery.lottery;
}