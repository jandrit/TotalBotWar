package com.codingame.game;

import java.util.ArrayList;
import java.util.List;

public class Action {
    public static int id;
    public static int x;
    public static int y;
    public Player player;
    
    public Action(int id, int x, int y) {
        //this.player = player;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public static Action parse (String data) throws InvalidAction {
        String[] str = data.split(" ", 3);

        int Id = Integer.parseInt(str[0]);
        int X = Integer.parseInt(str[1]);
        int Y = Integer.parseInt(str[2]);

        return new Action (Id, X, Y);
    }
    
    @Override
    public String toString() {
        return id + " " + x + " " + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Action) {
            Action other = (Action) obj;
            return id == other.id && x == other.x && y == other.y;
        } else {
            return false;
        }
    }
}