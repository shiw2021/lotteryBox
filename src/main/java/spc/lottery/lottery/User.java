package spc.lottery.lottery;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Data

public class User {
    private String id;
    private List<Draw> draws;
    private PrizePool pp;
    private Map<String,Integer> map=new HashMap<>();

    private String drawing( int times) {
        for (int i = 0; i < times; i++) {
            //0.0-1.0
            float random = new Random().nextFloat();
            Prize p = pp.getPrizes(random);
            if (p == null) {
                continue;
            }
            if (map.containsKey(p.getName())) {
                map.put(p.getName(), map.get(p.getName()) + 1);
            } else {
                map.put(p.getName(), 1);
            }
            //todo 进度加一


        }
        String r=detail();
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
        String r="";
        String rate="";
        for (String key : map.keySet()) {
            rate="(中奖率为"+pp.searchPrize(key).getRate()+")";
            r+=key+": 共抽中"+map.get(key)+"次"+rate+"\n";
        }
        return r;
    }
}
