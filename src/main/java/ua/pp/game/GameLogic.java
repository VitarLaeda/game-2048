package ua.pp.game;

import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ua.pp.game.Main.TILE_COUNT;

public abstract class GameLogic extends JPanel {

    @Getter
    public int myScore = 0;

    private static final double NEW_VALUE_PERCENTAGE = 0.9;
    private static final int ANGLE_RIGHT = 180;
    private static final int ANGLE_DOWN = 270;
    private static final int ANGLE_UP = 90;
    Tile[] myTiles;
    boolean win = false;
    boolean lose = false;

    void resetGame() {
        myScore = 0;
        win = false;
        lose = false;
        myTiles = new Tile[TILE_COUNT * TILE_COUNT];
        for (int i = 0; i < myTiles.length; i++) {
            myTiles[i] = new Tile();
        }
        addTile();
        addTile();
    }

    public void left() {
        boolean needAddTile = false;
        for (int i = 0; i < TILE_COUNT; i++) {
            Tile[] line = getLine(i);
            Tile[] merged = mergeLine(moveLine(line));
            setLine(i, merged);
            if (!needAddTile && !compare(line, merged)) {
                needAddTile = true;
            }
        }

        if (needAddTile) {
            addTile();
        }
    }

    public void right() {
        myTiles = rotate(ANGLE_RIGHT);
        left();
        myTiles = rotate(ANGLE_RIGHT);
    }

    public void up() {
        myTiles = rotate(ANGLE_DOWN);
        left();
        myTiles = rotate(ANGLE_UP);
    }

    public void down() {
        myTiles = rotate(ANGLE_UP);
        left();
        myTiles = rotate(ANGLE_DOWN);
    }

    private void addTile() {
        List<Tile> list = availableSpace();
        if (!availableSpace().isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            Tile emptyTile = list.get(index);
            emptyTile.value = Math.random() < NEW_VALUE_PERCENTAGE ? 2 : 4;
        }
    }

    private List<Tile> availableSpace() {
        final List<Tile> list = new ArrayList<Tile>(TILE_COUNT * TILE_COUNT);
        for (Tile t : myTiles) {
            if (t.isEmpty()) {
                list.add(t);
            }
        }
        return list;
    }

    private boolean isFull() {
        return availableSpace().size() == 0;
    }

    boolean canMove() {
        if (!isFull()) {
            return false;
        }
        for (int x = 0; x < TILE_COUNT; x++) {
            for (int y = 0; y < TILE_COUNT; y++) {
                Tile t = tileAt(x, y);
                if ((x < TILE_COUNT - 1 && t.value == tileAt(x + 1, y).value)
                        || ((y < TILE_COUNT - 1) && t.value == tileAt(x, y + 1).value)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compare(Tile[] line1, Tile[] line2) {
        if (line1 == line2) {
            return true;
        } else if (line1.length != line2.length) {
            return false;
        }

        for (int i = 0; i < line1.length; i++) {
            if (line1[i].value != line2[i].value) {
                return false;
            }
        }
        return true;
    }

    private Tile[] rotate(int angle) {
        Tile[] newTiles = new Tile[TILE_COUNT * TILE_COUNT];
        int offsetX = TILE_COUNT - 1, offsetY = TILE_COUNT - 1;
        if (angle == 90) {
            offsetY = 0;
        } else if (angle == 270) {
            offsetX = 0;
        }

        double rad = Math.toRadians(angle);
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);
        for (int x = 0; x < TILE_COUNT; x++) {
            for (int y = 0; y < TILE_COUNT; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                newTiles[(newX) + (newY) * TILE_COUNT] = tileAt(x, y);
            }
        }
        return newTiles;
    }

    private Tile[] moveLine(Tile[] oldLine) {
        LinkedList<Tile> l = new LinkedList<Tile>();
        for (int i = 0; i < TILE_COUNT; i++) {
            if (!oldLine[i].isEmpty())
                l.addLast(oldLine[i]);
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[TILE_COUNT];
            ensureSize(l);
            for (int i = 0; i < TILE_COUNT; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    private Tile[] mergeLine(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        for (int i = 0; i < TILE_COUNT && !oldLine[i].isEmpty(); i++) {
            int num = oldLine[i].value;
            if (i < TILE_COUNT - 1 && oldLine[i].value == oldLine[i + 1].value) {
                num *= 2;
                myScore += num;
                int ourTarget = 2048;
                if (num == ourTarget) {
                    win = true;
                }
                i++;
            }
            list.add(new Tile(num));
        }
        if (list.size() == 0) {
            return oldLine;
        } else {
            ensureSize(list);
            return list.toArray(new Tile[TILE_COUNT]);
        }
    }

    private static void ensureSize(List<Tile> l) {
        while (l.size() != Main.TILE_COUNT) {
            l.add(new Tile());
        }
    }

    private Tile[] getLine(int index) {
        Tile[] result = new Tile[TILE_COUNT];
        for (int i = 0; i < TILE_COUNT; i++) {
            result[i] = tileAt(i, index);
        }
        return result;
    }

    private void setLine(int index, Tile[] re) {
        System.arraycopy(re, 0, myTiles, index * TILE_COUNT, TILE_COUNT);
    }
    private Tile tileAt(int x, int y) {
        return myTiles[x + y * TILE_COUNT];
    }

}
