import java.util.Random;
import java.util.Scanner;

public class NewAgent {
    static class Unit {
        int id;
        int posX;
        int posY;
        int direction;
        int life;
        int type;
        int moving;
        String team;
        Section section;

        public Unit(int id, int posX, int posY, int direction, int life, int type, int moving, String team) {
            this.id = id;
            this.posX = posX;
            this.posY = posY;
            this.direction = direction;
            this.life = life;
            this.type = type;
            this.moving = moving;
            this.team = team;
        }
    }

    static class Section {
        int x;
        int y;
        int XSize;
        int YSize;
        Unit[] units;
        int pos;

        public Section (int x, int y, int nUnits) {
            this.x = x;
            this.y = y;
            XSize = 192;
            YSize = 180;
            units = new Unit[nUnits];
            pos = 0;
        }
    }

    static class UnitCheck {
        Unit unit;
        Section section;
        float heuristic;

        public UnitCheck (Unit unit, Section section, float heuristic) {
            this.unit = unit;
            this.section = section;
            this.heuristic = heuristic;
        }
    }

    static class heuristicValue {
        public Unit ally;
        public Unit enemy;
        public float value;

        public heuristicValue(Unit ally,Unit enemy, float value) {
            this.ally = ally;
            this.enemy = enemy;
            this.value = value;
        }
    }

    static class position {
        public int x;
        public int y;

        public position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static Section[][] mapSections;
    private static Unit[] allyUnits;
    private static Unit[] enemyUnits;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = 0, turn = 0, size;
        long TInicio, TFin, tiempo;
        boolean draft = false;

        int numberUnits = in.nextInt();
        if (numberUnits != 4)
            draft = true;

        if (numberUnits == 30)
            size = 75;
        else
            size = 150;

        mapSections = new Section[6][10];
        divideMap(numberUnits);

        while (true) {
            TInicio = System.currentTimeMillis();
            allyUnits = new Unit[numberUnits + 1];

            for (int i = +1; i < numberUnits + 1; i++) {
                int allyUnitId = in.nextInt();
                int allyUnitPosX = in.nextInt();
                int allyUnitPosY = in.nextInt();
                int allyUnitDirection = in.nextInt();
                int allyUnitLife = in.nextInt();
                int allyUnitType = in.nextInt();
                int allyUnitMoving = in.nextInt();
                int allyUnitNextX = in.nextInt();
                int allyUnitNextY = in.nextInt();

                if (allyUnitId != -1)
                    allyUnits[i] = new Unit(allyUnitId, allyUnitPosX, allyUnitPosY, allyUnitDirection, allyUnitLife, allyUnitType, allyUnitMoving, "ally");
                else {
                    allyUnits[i] = null;
                }
            }

            enemyUnits = new Unit[numberUnits + 1];

            for (int i = 1; i < numberUnits + 1; i++) {
                int enemyUnitId = in.nextInt();
                int enemyUnitPosX = in.nextInt();
                int enemyUnitPosY = in.nextInt();
                int enemyUnitDirection = in.nextInt();
                int enemyUnitLife = in.nextInt();
                int enemyUnitType = in.nextInt();
                int enemyUnitMoving = in.nextInt();

                if (enemyUnitId != -1)
                    enemyUnits[i] = new Unit(enemyUnitId, enemyUnitPosX, enemyUnitPosY, enemyUnitDirection, enemyUnitLife, enemyUnitType, enemyUnitMoving, "enemy");
                else
                    enemyUnits[i] = null;
            }

            if (draft) { //Draft
                int x = 0, y = 0, type = 0;
                if (numberUnits == 9 && turn < 2) {
                    type = 3;
                    y = 50;
                    x = 785 + 200 * turn;
                } else if (numberUnits == 30 && turn < 8) {
                    type = 3;
                    y = 50;
                    x = 548 + 100 * turn;
                } else {
                    type = selectUnitDraft(turn);
                    y = 250;
                    if (numberUnits == 9) {
                        if (type == 2) {
                            x = 173;
                            y = 150;
                        } else {
                            x = 173;
                        }
                    }
                }
                turn++;
                if (turn >= numberUnits)
                    draft = false;
                System.out.println(type + " " + x + " " + y);
            } else {

                heuristicValue maxValue = new heuristicValue(null, null, 0);
                heuristicValue newValue = null;

                heuristicValue[] unitsValues = new heuristicValue[allyUnits.length];

                for (int i = 1; i < allyUnits.length; i++) {
                    if (allyUnits[i] != null) {
                        newValue = calculateFirstHeuristic(allyUnits[i], numberUnits);
                        if (newValue.value > maxValue.value)
                            maxValue = newValue;
                    }

                    if (maxValue.ally != null)
                        unitsValues[i] = maxValue;
                    else
                        unitsValues[i] = null;

                    maxValue = new heuristicValue(null, null, 0);
                }

                cleanMap();
                assignUnits();
                Section[][] futureMap = createFutureMap(numberUnits);
                UnitCheck[] checks = new UnitCheck[allyUnits.length];
                Random r = new Random();
                int randomX, randomY, randomUnit;
                float maxHeuristic = 0, actualHeuristic, newHeuristic;
                for (int i = 1; i < checks.length; i++) {
                    if (allyUnits[i] != null) {
                        randomX = r.nextInt(10);
                        randomY = r.nextInt(6);
                        if (allyUnits[i].type != 3) {
                            actualHeuristic = calculateHeuristic(allyUnits[i], mapSections[randomY][randomX], unitsValues[i], futureMap);
                            checks[i] = new UnitCheck(allyUnits[i], mapSections[randomY][randomX], actualHeuristic);
                        } else {
                            actualHeuristic = calculateHeuristic(allyUnits[i], allyUnits[i].section, unitsValues[i], futureMap);
                            checks[i] = new UnitCheck(allyUnits[i], allyUnits[i].section, actualHeuristic);
                        }
                        maxHeuristic += actualHeuristic;
                    }
                }

                TFin = System.currentTimeMillis();
                while (TFin - TInicio < 5) {
                    r = new Random();
                    randomUnit = r.nextInt(allyUnits.length - 1) + 1; //Ya que el 0 no tiene nada
                    if (allyUnits[randomUnit] != null) {
                        randomX = r.nextInt(10);
                        randomY = r.nextInt(6);
                        actualHeuristic = calculateHeuristic(allyUnits[randomUnit], mapSections[randomY][randomX], unitsValues[randomUnit], futureMap);
                        newHeuristic = maxHeuristic - checks[randomUnit].heuristic + actualHeuristic;
                        if (newHeuristic >= maxHeuristic)
                            checks[randomUnit] = new UnitCheck(allyUnits[randomUnit], mapSections[randomY][randomX], actualHeuristic);
                    }
                    TFin = System.currentTimeMillis();
                }

                String finalString = "";
                int finalX, finalY;
                for (int i = 1; i < checks.length; i++) {
                    if (checks[i] != null) {
                        if (finalString != "")
                            finalString += ";";
                        finalX = checks[i].section.x + 96 - checks[i].unit.posX;
                        finalY = checks[i].section.y + 90 - checks[i].unit.posY;
                        finalString += checks[i].unit.id + " " + finalX + " " + finalY;
                    }
                }

                if (finalString == "")
                    System.out.println("0 0 0");
                else
                    System.out.println(finalString);
            }
        }
    }

    private static int selectUnitDraft(int n) {
        int archersAndSoldiers = 0, cavalry = 0, spears = 0;
        for (int i = 1; i < n + 1; i++) {
            if (enemyUnits[i].type == 0 || enemyUnits[i].type == 3) {
                archersAndSoldiers++;
            } else if (enemyUnits[i].type == 1) {
                spears++;
            } else {
                cavalry++;
            }
        }

        if (archersAndSoldiers > cavalry && archersAndSoldiers > spears) {
            return 2;
        } else if (spears > cavalry && spears > archersAndSoldiers) {
            return 1;
        } else {
            return 0;
        }
    }

    private static void divideMap(int nUnits) {
        int x;
        int y;
        for (int i = 0; i < mapSections.length; i++) {
            y = i * 180;
            for (int j = 0; j < mapSections[i].length; j++) {
                x = 192 * j;
                mapSections[i][j] = new Section(x, y, nUnits*2);
            }
        }
    }

    private static void assignUnits() {
        int x, y;
        for (int i = 1; i < allyUnits.length; i++) {
            if (allyUnits[i] != null) {
                x = allyUnits[i].posX / 192;
                y = allyUnits[i].posY / 180;
                mapSections[y][x].units[mapSections[y][x].pos] = allyUnits[i];
                mapSections[y][x].pos++;
                allyUnits[i].section = mapSections[y][x];
            }
        }

        for (int i = 1; i < enemyUnits.length; i++) {
            if (enemyUnits[i] != null) {
                x = enemyUnits[i].posX / 192;
                y = enemyUnits[i].posY / 180;
                mapSections[y][x].units[mapSections[y][x].pos] = enemyUnits[i];
                mapSections[y][x].pos++;
                enemyUnits[i].section = mapSections[y][x];
            }
        }
    }

    private static void cleanMap() {
        for (int i = 0; i < mapSections.length; i++) {
            for (int j = 0; j < mapSections[i].length; j++) {
                for (int z = 0; z < mapSections[i][j].pos; z++)
                    mapSections[i][j].units[z] = null;
                mapSections[i][j].pos = 0;
            }
        }
    }

    private static Section[][] createFutureMap(int nUnits) {
        int x;
        int y;
        Section[][] futureMap = new Section[6][10];
        for (int i = 0; i < futureMap.length; i++) {
            y = i * 180;
            for (int j = 0; j < futureMap[i].length; j++) {
                x = 192 * j;
                futureMap[i][j] = new Section(x, y, nUnits*2);
            }
        }

        for (int i = 1; i < enemyUnits.length; i++) {
            if (enemyUnits[i] != null) {
                x = enemyUnits[i].posX / 192;
                y = enemyUnits[i].posY / 180;

                if (enemyUnits[i].moving == 1) {
                    if (enemyUnits[i].direction <= 2) {
                        y++;
                    } else if (enemyUnits[i].direction >= 4 && enemyUnits[i].direction <= 6) {
                        y--;
                    }

                    if (enemyUnits[i].direction >= 2 && enemyUnits[i].direction <= 4) {
                        x++;
                    } else if (enemyUnits[i].direction == 0 || enemyUnits[i].direction >= 6) {
                        x--;
                    }
                }

                if (y >= 6)
                    y = 5;
                if (x >= 10)
                    x = 9;

                if (y < 0)
                    y = 0;
                if (x < 0)
                    x = 0;
                futureMap[y][x].units[futureMap[y][x].pos] = enemyUnits[i];
                futureMap[y][x].pos++;
            }
        }

        return futureMap;
    }

    private static heuristicValue calculateFirstHeuristic(Unit ally, int nUnits) {
        heuristicValue[] heuristics = new heuristicValue[enemyUnits.length];
        float maxValue = 0;
        int maxPosition = 0;

        for (int i = 1; i < enemyUnits.length; i++) {
            if (enemyUnits[i] != null) {
                heuristicValue object = new heuristicValue(ally, enemyUnits[i], 0);

                float finalValue = 0;

                finalValue += typeHeuristicValue(ally, enemyUnits[i]);
                finalValue += lifeDifferenceHeuristicValue(ally, enemyUnits[i]);
                finalValue += areaEnemyArchersHeuristicValue(ally, enemyUnits[i]);
                finalValue += directionHeuristicValue(ally, enemyUnits[i], nUnits);
                finalValue += distanceHeuristic(ally, enemyUnits[i]);

                if (nUnits <= 9) {
                    object.value = finalValue / 5;
                } else {
                    if (enemyUnits[i].id == 1)
                        finalValue++;
                    object.value = finalValue / 6;
                }

                heuristics[i] = object;

                if (maxValue < object.value) {
                    maxValue = object.value;
                    maxPosition = i;
                }
            }
        }

        return heuristics[maxPosition];
    }

    private static float typeHeuristicValue(Unit ally, Unit enemy) {
        float value;

        if (ally.type == 0) {
            if (enemy.type == 3) {
                value = 1;
            } else if (enemy.type == 1) {
                value = 0.66f;
            } else if (enemy.type == 0) {
                value = 0.33f;
            } else {
                value = 0;
            }
        } else if (ally.type == 1) {
            if (enemy.type == 3) {
                value = 1;
            } else if (enemy.type == 1) {
                value = 0.33f;
            } else if (enemy.type == 0) {
                value = 0;
            } else {
                value = 0.66f;
            }
        } else if (ally.type == 3) {
            if (enemy.type == 3) {
                value = 0.33f;
            } else if (enemy.type == 1) {
                value = 0;
            } else if (enemy.type == 0) {
                value = 0;
            } else {
                value = 0;
            }
        } else {
            if (enemy.type == 3) {
                value = 1;
            } else if (enemy.type == 1) {
                value = 0;
            } else if (enemy.type == 0) {
                value = 0.66f;
            } else {
                value = 0.33f;
            }
        }

        return value;
    }

    private static float lifeDifferenceHeuristicValue(Unit ally, Unit enemy) {
        float value = 0;
        int originalAllyLife;
        int originalEnemyLife;

        if (ally.type <= 1)
            originalAllyLife = 250;
        else if (ally.type == 2)
            originalAllyLife = 200;
        else
            originalAllyLife = 100;

        if (enemy.type <= 1)
            originalEnemyLife = 250;
        else if (enemy.type == 2)
            originalEnemyLife = 200;
        else
            originalEnemyLife = 100;

        if (enemy.life < originalEnemyLife)
            value++;

        if (ally.life == originalAllyLife)
            value += 2;

        return value/3;
    }

    private static float directionHeuristicValue(Unit ally, Unit enemy, int nUnits) {
        float value = 0;

        for (int i = 1; i < allyUnits.length; i++) {
            if (allyUnits[i] != null && allyUnits[i] != ally) {
                if (isIntersect(enemy, allyUnits[i], nUnits)) {
                    value += 0.5f;
                    break;
                }
            }
        }

        if (checkFlanked(ally, enemy))
            value += 0.5f;

        return value;
    }

    private static boolean isIntersect(Unit unit1, Unit unit2, int nUnits) { //Comprueba si dos unidades estan colisionando
        int Ax = unit1.posX;
        int Ay = unit1.posY;
        int Aw, Ah, Bw, Bh;
        if (nUnits <= 9) {
            Aw = 150;
            Ah = 150;
            Bw = 150;
            Bh = 150;
        } else {
            Aw = 75;
            Ah = 75;
            Bw = 75;
            Bh = 75;
        }
        int Bx = unit2.posX;
        int By = unit2.posY;
        return Bx + Bw > Ax && By + Bh > Ay && Ax + Aw > Bx && Ay + Ah > By;
    }

    private static boolean checkFlanked(Unit ally, Unit enemy) {
        if (ally.direction == 0 && (enemy.direction <= 1 || enemy.direction == 7)) {
            return true;
        } else if (ally.direction == 1 && enemy.direction <= 2) {
            return true;
        } else if (ally.direction == 2 && (enemy.direction >= 1 && enemy.direction <= 3)) {
            return true;
        } else if (ally.direction == 3 && (enemy.direction >= 2 && enemy.direction <= 4)) {
            return true;
        } else if (ally.direction == 4 && (enemy.direction >= 3 && enemy.direction <= 5)) {
            return true;
        } else if (ally.direction == 5 && (enemy.direction >= 4 && enemy.direction <= 6)) {
            return true;
        } else if (ally.direction == 6 && (enemy.direction >= 5 && enemy.direction <= 7)) {
            return true;
        } else if (ally.direction == 7 && (enemy.direction >= 6 || enemy.direction == 0)) {
            return true;
        }

        return false;
    }

    private static float areaEnemyArchersHeuristicValue(Unit ally, Unit enemy) {
        int attackDistance = 450;

        for (int i = 1; i < enemyUnits.length; i++) {
            if (enemyUnits[i] != null && enemyUnits[i] != enemy) {
                if (calculateDistance(ally, enemyUnits[i]) < attackDistance && calculateDistance(ally, enemyUnits[i]) > attackDistance)
                    return 0;
            }
        }

        return 1;
    }

    private static double calculateDistance(Unit unit1, Unit unit2) { //Calcula la distancia entre dos unidades
        double x1 = unit1.posX;
        double y1 = unit1.posY;
        double x2 = unit2.posX;
        double y2 = unit2.posY;
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private static float distanceHeuristic(Unit ally, Unit enemy) {
        float value;
        float distance;

        distance = (float) calculateDistance(ally, enemy) / 50;
        if (distance == 0) {
            distance = 1;
        } else if (distance < 0) {
            distance *= -1;
        }

        value = 1/distance;

        if (value > 1)
            value = 1;

        if (ally.type == 3)
            value *= 100;

        return value*5;
    }

    private static position archerDistancePosition(Unit ally, Unit enemy) {
        position pos = new position(0, 0);
        if (calculateDistance(ally, enemy) > 450) {
            pos.x = ally.posX - enemy.posX;
            pos.y = enemy.posY - ally.posY;
        } else if (calculateDistance(ally,enemy) < 150) {
            pos.x = -(ally.posX - enemy.posX);
            pos.y = -(enemy.posY - ally.posY);
        }

        return pos;
    }

    private static float calculateHeuristic(Unit unit, Section futureSection, heuristicValue firstHeuristic, Section[][] futureMap) {
        float finalFloat = 0;
        Section section = futureMap[futureSection.y / 180][futureSection.x / 192];
        if (unit.type == 3) {
            for (int i = 0; i < section.units.length; i++) {
                if (section.units[i] == null) {
                    finalFloat += 0.33f;
                    break;
                } else if (section.units[i].team == "enemy") {
                    return 0;
                }
            }
            if (futureSection == unit.section)
                finalFloat += 0.66;

            Unit auxUnit = unit;
            auxUnit.posX = futureSection.x;
            auxUnit.posY = futureSection.y;

            if (areaEnemyArchersHeuristicValue(auxUnit, firstHeuristic.enemy) == 1)
                finalFloat += 0.33;
        } else {
            int total = 0;
            for (int i = 0; i < section.units.length; i++) {
                if (section.units[i] == null) {
                    break;
                } else if (section.units[i] == firstHeuristic.enemy) {
                    total++;
                    if (total == 1) {
                        finalFloat += 0.5;
                    } else {
                        finalFloat += 0.2;
                    }
                } else if (section.units[i].team == "enemy") {
                    total++;
                    if (total == 1) {
                        finalFloat += typeHeuristicValue(unit, section.units[i]);
                    } else {
                        total -= 0.4;
                    }
                }
            }
        }
        return finalFloat;
    }
}