package io.tankgo.tankserver.gameobject.map;

import io.tankgo.tankserver.gameobject.GameObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameGrid {
    private String a;
    private List<GameObject> gameObjects=new ArrayList<>();
}
