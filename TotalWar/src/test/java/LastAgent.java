import java.util.Random;
import java.util.Scanner;

public class LastAgent {
    static int numberUnits;

    static class Unit {
        public int id;
        public int posX;
        public int posY;
        public int direction;
        public int life;
        public int type;
        public int moving;
        public int nextX;
        public int nextY;

        public Unit (int id, int posX, int posY, int direction, int life, int type, int moving) {
            this.id = id;
            this.posX = posX;
            this.posY = posY;
            this.direction = direction;
            this.life = life;
            this.type = type;
            this.moving = moving;
        }

        private void setNextX(int nextX) {
            this.nextX = nextX;
        }

        private void setNextY (int nextY) {
            this.nextY = nextY;
        }
    }

    static class heuristicValue {
        public Unit ally;
        public Unit enemy;
        public float value;

        public heuristicValue(Unit ally, Unit enemy, float value) {
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

    static heuristicValue[] unitsValues;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        numberUnits = in.nextInt();
        int turn = 0;
        int draftHorses = 0;

        unitsValues = new heuristicValue[numberUnits + 1];

        while (true) {
            Unit[] allyUnits = new Unit[numberUnits + 1];

            int iteraciones = 0;

            for (int i = + 1; i < numberUnits + 1; i++) {
                int allyUnitId = in.nextInt();
                int allyUnitPosX = in.nextInt();
                int allyUnitPosY = in.nextInt();
                int allyUnitDirection = in.nextInt();
                int allyUnitLife = in.nextInt();
                int allyUnitType = in.nextInt();
                int allyUnitMoving = in.nextInt();
                int allyUnitNextX = in.nextInt();
                int allyUnitNextY = in.nextInt();

                if (allyUnitId != -1) {
                    allyUnits[i] = new Unit(allyUnitId, allyUnitPosX, allyUnitPosY, allyUnitDirection, allyUnitLife, allyUnitType, allyUnitMoving);
                    allyUnits[i].setNextX(allyUnitNextX);
                    allyUnits[i].setNextY(allyUnitNextY);
                } else {
                    allyUnits[i] = null;
                }
            }

            Unit[] enemyUnits = new Unit[numberUnits + 1];

            for (int i = 1; i < numberUnits + 1; i++) {
                int enemyUnitId = in.nextInt();
                int enemyUnitPosX = in.nextInt();
                int enemyUnitPosY = in.nextInt();
                int enemyUnitDirection = in.nextInt();
                int enemyUnitLife = in.nextInt();
                int enemyUnitType = in.nextInt();
                int enemyUnitMoving = in.nextInt();

                if (enemyUnitId != -1)
                    enemyUnits[i] = new Unit(enemyUnitId, enemyUnitPosX, enemyUnitPosY, enemyUnitDirection, enemyUnitLife, enemyUnitType, enemyUnitMoving);
                else {
                    enemyUnits[i] = null;
                }
            }

            //System.out.println("0 0 0");

            if (numberUnits == 4)
                turn = numberUnits;

            if (turn >= numberUnits) {

                float maxHeuristic, auxHeuristic = 0, actualHeuristic = 0;
                long TInicio, TFin, tiempo;

                TInicio = System.currentTimeMillis();

                heuristicValue maxValue = new heuristicValue(null, null, 0);
                heuristicValue newValue = null;

                for (int i = 0; i < allyUnits.length; i++) {
                    if (allyUnits[i] != null) {
                        newValue = calculateHeuristic(allyUnits[i], allyUnits, enemyUnits);
                        if (newValue.value > maxValue.value)
                            maxValue = newValue;
                    }

                    if (maxValue.ally != null) {
                        unitsValues[i] = maxValue;
                        auxHeuristic += maxValue.value;
                    } else
                        unitsValues[i] = null;

                    maxValue = new heuristicValue(null, null, 0);
                }

                maxHeuristic = auxHeuristic;
                TFin = System.currentTimeMillis();

                /*System.err.print("Iteracion: " + iteraciones + " Target: [");
                for (int i = 1; i < unitsValues.length; i++) {
                    if (unitsValues[i] != null)
                        System.err.print(unitsValues[i].enemy.id + " ");
                }
                System.err.println("] Value: " + maxHeuristic);*/

                while (TFin - TInicio < 2) {
                    Random r = new Random();
                    int randomUnit = r.nextInt(allyUnits.length - 1) + 1; //Ya que el 0 no tiene nada
                    int randomEnemyUnit = r.nextInt(allyUnits.length - 1) + 1; //Ya que el 0 no tiene nada
                    if (allyUnits[randomUnit] != null) {
                        actualHeuristic = maxHeuristic - unitsValues[randomUnit].value;

                        /*System.err.print("Iteracion: " + iteraciones + " Target: [");
                        for (int i = 1; i < unitsValues.length; i++) {
                            if (unitsValues[i] != null)
                                System.err.print(unitsValues[i].enemy.id + " ");
                        }
                        System.err.print("] Value: " + maxHeuristic);*/

                        heuristicValue heu = new heuristicValue(allyUnits[randomUnit], enemyUnits[randomEnemyUnit], 0);
                        heu.value = calculateUnicHeuristic(allyUnits[randomUnit], enemyUnits[randomEnemyUnit], allyUnits, enemyUnits);
                        actualHeuristic += heu.value;

                        /*System.err.print(" Target: [");
                        for (int i = 1; i < unitsValues.length; i++) {
                            if (unitsValues[i] != null) {
                                if (i != randomUnit) {
                                    System.err.print(unitsValues[i].enemy.id + " ");
                                } else if (heu.enemy != null) {
                                    System.err.print(heu.enemy.id + " ");
                                }
                            }
                        }
                        System.err.println("] Value: " + actualHeuristic);*/

                        if (actualHeuristic >= maxHeuristic) {
                            maxHeuristic = actualHeuristic;
                            unitsValues[randomUnit] = heu;
                        }
                        iteraciones++;
                    }
                    TFin = System.currentTimeMillis();
                }

                /*for (int i = 1; i < unitsValues.length; i++) {
                    if (unitsValues[i] != null)
                        System.err.println("ID: " + unitsValues[i].ally.id + " Enemy: " + unitsValues[i].enemy.id + " Value: " + unitsValues[i].value);
                }*/

                System.err.println(iteraciones);

                int x, y, id;
                String finalString = "";

                for (int i = 0; i < unitsValues.length; i++) {
                    if (unitsValues[i] != null) {
                        if (finalString != "")
                            finalString += "; ";


                        if (unitsValues[i].ally.type == 3) {
                            position finalPos = archerDistancePosition(unitsValues[i].ally, unitsValues[i].enemy);
                            x = finalPos.x;
                            y = finalPos.y;
                        } else {
                            x = unitsValues[i].enemy.posX - unitsValues[i].ally.posX ;
                            y = unitsValues[i].enemy.posY - unitsValues[i].ally.posY;
                        }

                        id = unitsValues[i].ally.id;

                        finalString += id + " " + x + " " + y;
                    }
                }

                if (finalString == "") {
                    System.out.println("0 0 0");
                } else {
                    System.out.println(finalString);
                }
            } else { //Draft
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
                    type = selectUnitDraft(allyUnits, enemyUnits, turn);
                    y = 250;
                    if (numberUnits == 9) {
                        if (type == 2) {
                            if (draftHorses < 2) {
                                x = 285;
                                draftHorses++;
                            } else {
                                x = 1335;
                            }
                        } else
                            x = 585;
                    } else {
                        if (type == 2) {
                            x = 173;
                            y = 150;
                        } else {
                            x = 173;
                        }
                    }
                }
                turn++;
                System.out.println(type + " " + x + " " + y);
            }
        }
    }

    private static int selectUnitDraft(Unit[] allies, Unit[] enemies, int n) {
        int archersAndSoldiers = 0, cavalry = 0, spears = 0;
        for (int i = 1; i < n + 1; i++) {
            if (enemies[i].type == 0 || enemies[i].type == 3) {
                archersAndSoldiers++;
            } else if (enemies[i].type == 1) {
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

    private static heuristicValue calculateHeuristic(Unit ally, Unit[] allies, Unit[] enemies) {
        heuristicValue[] heuristics = new heuristicValue[enemies.length];
        float maxValue = 0;
        int maxPosition = 0;

        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i] != null) {
                heuristicValue object = new heuristicValue(ally, enemies[i], 0);

                float finalValue = 0;

                finalValue += typeHeuristicValue(ally, enemies[i]);
                finalValue += lifeDifferenceHeuristicValue(ally, enemies[i]);
                finalValue += areaEnemyArchersHeuristicValue(ally, enemies[i], enemies);
                finalValue += directionHeuristicValue(ally, enemies[i], allies);
                finalValue += distanceHeuristic(ally, enemies[i]);
                finalValue += sameObjective(ally, enemies[i]);

                if (numberUnits <= 9) {
                    object.value = finalValue / 6;
                } else {
                    if (enemies[i].id == 1)
                        finalValue++;
                    object.value = finalValue / 7;
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

    private static float calculateUnicHeuristic (Unit ally, Unit enemy, Unit[] allies, Unit[] enemies) {
        heuristicValue object = new heuristicValue(ally, enemy, 0);

        float finalValue = 0;

        if (enemy != null) {
            finalValue += typeHeuristicValue(ally, enemy);
            finalValue += lifeDifferenceHeuristicValue(ally, enemy);
            finalValue += areaEnemyArchersHeuristicValue(ally, enemy, enemies);
            finalValue += directionHeuristicValue(ally, enemy, allies);
            finalValue += distanceHeuristic(ally, enemy);
            finalValue += sameObjective(ally, enemy);

            return finalValue;
        } else
            return 0;
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

    private static float directionHeuristicValue(Unit ally, Unit enemy, Unit[] allies) {
        float value = 0;

        for (int i = 0; i < allies.length; i++) {
            if (allies[i] != null && allies[i] != ally) {
                if (isIntersect(enemy, allies[i])) {
                    value += 0.5f;
                    break;
                }
            }
        }

        if (checkFlanked(ally, enemy))
            value += 0.5f;

        return value;
    }

    private static boolean isIntersect(Unit unit1, Unit unit2) { //Comprueba si dos unidades estan colisionando
        int Ax = unit1.posX;
        int Ay = unit1.posY;
        int Aw, Ah, Bw, Bh;
        if (numberUnits <= 9) {
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

    private static float areaEnemyArchersHeuristicValue(Unit ally, Unit enemy, Unit[] enemies) {
        int attackDistance = 450;

        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i] != null && enemies[i] != enemy) {
                if (calculateDistance(ally, enemies[i]) < attackDistance && calculateDistance(ally, enemies[i]) > attackDistance)
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
            pos.x = enemy.posX - ally.posX;
            pos.y = enemy.posY - ally.posY;
        } else if (calculateDistance(ally,enemy) < 150) {
            pos.x = ally.posX - enemy.posX;
            pos.y = -(enemy.posY - ally.posY);
        }

        return pos;
    }

    private static float sameObjective(Unit ally, Unit enemy) {
        for (int i = 0; i < unitsValues.length; i++) {
            if (unitsValues[i] != null && ally != unitsValues[i].ally) {
                if (unitsValues[i].enemy == enemy) {
                    return 1f;
                }
            }
        }
        return 0;
    }
}