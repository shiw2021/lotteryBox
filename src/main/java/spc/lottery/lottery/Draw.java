package spc.lottery.lottery;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class Draw {

    private String id;
    private String times;
    private Prize prize;
    private String remark;
    private String detail;


}
