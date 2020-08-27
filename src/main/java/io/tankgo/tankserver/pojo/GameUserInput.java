package io.tankgo.tankserver.pojo;

import lombok.Data;

@Data
public class GameUserInput {
    private String playId;
    private String button;
    private Double addx;
    private Double addy;
    private Double bodyAngle;
    private Double towerAngle;
}
