package io.tankgo.tankserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireVec {
    //开炮中心点x
    private double x;
    //开炮中心点y
    private double y;
    //炮管角度
    private double r;

    //炮管长度比例
    private double lenRatio;

    //炮弹速度
    private double v;

}
