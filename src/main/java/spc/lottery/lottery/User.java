package spc.lottery.lottery;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Data

public class User {
    private String id;
    private List<Draw> drawList = new ArrayList<>();
    private List<Prize> tmpPrizeList = new ArrayList<>();
    private PrizePool pp;
    private Map<String, Integer> map = new HashMap<>();
    private TextArea textArea;
    private int times;


    public String drawing(int times) {
        textArea.setText("");
//        tmpPrizeList.clear();
        this.times += times;
        Instant lastTime = Instant.now();
        for (int i = 1; i <= times; i++) {
            HelloApplication.progress = i;


            float random = new Random().nextFloat();
            Prize p = pp.getPrizes(random);
//            tmpPrizeList.add(p);
            if (p == null) {
                int finalI = i;
                continue;
            }
            if (map.containsKey(p.getName())) {
                map.put(p.getName(), map.get(p.getName()) + 1);
            } else {
                map.put(p.getName(), 1);
            }

        }

//        Draw draw = new Draw();
//        draw.setTimes(times);
//        draw.setPrizeList(tmpPrizeList);
//        drawList.add(draw);
        String r = detail();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            textArea.setText("获奖情况：\n" +
                    "-------------------\n" +
                    "本次共抽奖" + this.times + "次\n" +
                    "-------------------\n");
            textArea.appendText(r);

        });
        System.out.println(r);
        return r;
    }

    public String detail() {
//        for (Draw draw : draws) {
//            if (map.containsKey(draw.getRemark())) {
//                map.put(draw.getRemark(), map.get(draw.getPrize()) + 1);
//            } else {
//                map.put(draw.getRemark(), 1);
//            }
//        }
        String r = "";
        String rate = "";
        for (String key : map.keySet()) {
            rate = "(中奖率为" + pp.searchPrize(key).getRate().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%)";
            r += key + ": 共抽中" + map.get(key) + "次" + rate + "\n";
        }
        if (r.equals("")) {
            r = "一个也没中，再抽一次吧！";
        }
        return r;
    }



    public User() {
        this.id = Instant.now().toString();
    }
}
