import java.util.Scanner;

public class AgentGen {
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

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        numberUnits = in.nextInt();
        int turn = 0;

        while (true) {
            Unit[] allyUnits = new Unit[numberUnits + 1];

            for (int i = 1; i < numberUnits + 1; i++) {
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
            Unit[] futureEnemyUnits = new Unit[numberUnits + 1];

            for (int i = 1; i < numberUnits + 1; i++) {
                int enemyUnitId = in.nextInt();
                int enemyUnitPosX = in.nextInt();
                int enemyUnitPosY = in.nextInt();
                int enemyUnitDirection = in.nextInt();
                int enemyUnitLife = in.nextInt();
                int enemyUnitType = in.nextInt();
                int enemyUnitMoving = in.nextInt();

                if (enemyUnitId != -1) {
                    enemyUnits[i] = new Unit(enemyUnitId, enemyUnitPosX, enemyUnitPosY, enemyUnitDirection, enemyUnitLife, enemyUnitType, enemyUnitMoving);
                    futureEnemyUnits[i] = new Unit(enemyUnitId, enemyUnitPosX, enemyUnitPosY, enemyUnitDirection, enemyUnitLife, enemyUnitType, enemyUnitMoving);
                } else {
                    enemyUnits[i] = null;
                }
            }

            //System.out.println("0 0 0");

            if (numberUnits == 4)
                turn = numberUnits;

            if (turn >= numberUnits) {
                heuristicValue maxValue = new heuristicValue(null, null, 0);
                heuristicValue newValue = null;

                heuristicValue[] unitsValues = new heuristicValue[allyUnits.length];

                for (int i = 1; i < allyUnits.length; i++) {
                    if (allyUnits[i] != null) {
                        newValue = calculateHeuristic(allyUnits[i], allyUnits, enemyUnits);
                        if (newValue.value > maxValue.value)
                            maxValue = newValue;
                    }

                    if (maxValue.ally != null)
                        unitsValues[i] = maxValue;
                    else
                        unitsValues[i] = null;

                    maxValue = new heuristicValue(null, null, 0);
                }

                for (int i = 1; i < numberUnits + 1; i++) {
                    position pos;

                    if (futureEnemyUnits[i] != null) {
                        pos = nextEnemyUnitPosition(futureEnemyUnits[i]);
                        futureEnemyUnits[i].posX = pos.x;
                        futureEnemyUnits[i].posY = pos.y;
                    }
                }

                Unit[] finalUnits = new Unit[numberUnits + 1];

                Unit auxUnit;
                Unit maxUnit = null;
                float currentMaxValue = 0f;
                float newHeuristicValue;
                String finalString = "";
                String provisionalString = "";

                for (int i = 0; i < numberUnits + 1; i++) {
                    if (allyUnits[i] != null) {
                        for (int vertical = -1; vertical < 2; vertical++) {
                            for (int horizontal = -1; horizontal < 2; horizontal++) {
                                auxUnit = futureAllyUnitPos(allyUnits[i], horizontal, vertical);
                                if (horizontal == 0 && vertical == 0)
                                    auxUnit.moving = 0;
                                else
                                    auxUnit.moving = 1;
                                newHeuristicValue = calculateGeneticHeuristic(auxUnit, futureEnemyUnits, unitsValues);
                                if (newHeuristicValue > currentMaxValue) {
                                    currentMaxValue = newHeuristicValue;
                                    maxUnit = auxUnit;
                                    provisionalString = auxUnit.id + " " + horizontal * -1 * 30 + " " + vertical * 30;
                                    System.err.println(vertical);
                                }
                            }
                        }
                        finalUnits[i] = maxUnit;
                        if (maxUnit != null) {
                            if (finalString != "")
                                finalString += ";";

                            finalString += provisionalString;
                        }
                    }
                }

                if (finalString == "") {
                    System.out.println("0 0 0");
                } else {
                    System.out.println(finalString);
                }

                /*int x, y, id;
                String finalString = "";

                for (int i = 0; i < finalUnits.length; i++) {
                    if (finalUnits[i] != null) {
                        if (finalString != "")
                            finalString += "; ";


                        if (finalUnits[i].type == 3) {
                            position finalPos = archerDistancePosition(unitsValues[i].ally, unitsValues[i].enemy);
                            x = finalPos.x;
                            y = finalPos.y;
                        } else {
                            x = unitsValues[i].ally.posX - unitsValues[i].enemy.posX;
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
                }*/
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

        for (int i = 1; i < enemies.length; i++) {
            if (enemies[i] != null) {
                heuristicValue object = new heuristicValue(ally, enemies[i], 0);

                float finalValue = 0;

                finalValue += typeHeuristicValue(ally, enemies[i]);
                finalValue += lifeDifferenceHeuristicValue(ally, enemies[i]);
                finalValue += areaEnemyArchersHeuristicValue(ally, enemies[i], enemies);
                finalValue += directionHeuristicValue(ally, enemies[i], allies);
                finalValue += distanceHeuristic(ally, enemies[i]);

                if (numberUnits <= 9) {
                    object.value = finalValue / 5;
                } else {
                    if (enemies[i].id == 1)
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

    private static float directionHeuristicValue(Unit ally, Unit enemy, Unit[] allies) {
        float value = 0;

        for (int i = 1; i < allies.length; i++) {
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

    private static boolean isIntersectPositions(position unit1, position unit2) { //Comprueba si dos unidades estan colisionando
        int Ax = unit1.x;
        int Ay = unit1.y;
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
        int Bx = unit2.x;
        int By = unit2.y;
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
            pos.x = ally.posX - enemy.posX;
            pos.y = enemy.posY - ally.posY;
        } else if (calculateDistance(ally,enemy) < 150) {
            pos.x = -(ally.posX - enemy.posX);
            pos.y = -(enemy.posY - ally.posY);
        }

        return pos;
    }

    private static position nextEnemyUnitPosition(Unit enemy) {
        position newPosition = new position(enemy.posX, enemy.posY);

        int speed;

        if (enemy.type == 1) {
            speed = 10;
        } else if (enemy.type == 2) {
            speed = 40;
        } else {
            speed = 15;
        }

        if (enemy.moving == 1) {
            if (enemy.direction == 0) {
                newPosition.x += speed;
                newPosition.y += speed;
            } else if (enemy.direction == 1) {
                newPosition.y += speed;
            } else if (enemy.direction == 2) {
                newPosition.x -= speed;
                newPosition.y += speed;
            } else if (enemy.direction == 3) {
                newPosition.x -= speed;
            } else if (enemy.direction == 4) {
                newPosition.x -= speed;
                newPosition.y -= speed;
            } else if (enemy.direction == 5) {
                newPosition.y -= speed;
            } else if (enemy.direction == 6) {
                newPosition.x += speed;
                newPosition.y -= speed;
            } else if (enemy.direction == 7) {
                newPosition.x += speed;
            }
        }

        return newPosition;
    }

    private static float calculateGeneticHeuristic(Unit ally, Unit[] futureEnemies, heuristicValue[] values) {
        float total = 0;
        boolean follow = false;

        if (ally.type != 3) {
            //Parte de encontrarse con un enemigo por el medio
            for (int i = 1; i < futureEnemies.length; i++) {
                if (futureEnemies[i] != null) {
                    if (isIntersect(ally, futureEnemies[i])) {
                        if (futureEnemies[i].type == 3) {
                            total++;
                        } else if (ally.type == 2 && futureEnemies[i].type == 2) {
                            total += 0.25f;
                        } else if (ally.type == 2 && futureEnemies[i].type == 0) {
                            total++;
                        } else if (ally.type == 1 && futureEnemies[i].type == 1) {
                            total += 0.25f;
                        } else if (ally.type == 1 && futureEnemies[i].type == 2) {
                            total++;
                        } else if (ally.type == 0 && futureEnemies[i].type == 0) {
                            total += 0.25f;
                        } else if (ally.type == 0 && futureEnemies[i].type == 1) {
                            total++;
                        }

                        if (follow == false) {
                            follow = true;
                            if (numberUnits == 30 && i == 1)
                                total++;
                        } else {
                            total -= 2;
                        }
                    } else {
                        total += 0.5f;
                    }
                }
            }
        } else {
            for (int i = 0; i < futureEnemies.length; i++) {
                if (futureEnemies[i] != null) {
                    if (isIntersect(ally, futureEnemies[i])) {
                        total--;
                    }
                }
            }

            if (total >= 0) {
                for (int i = 0; i < futureEnemies.length; i++) {
                    if (futureEnemies[i] != null) {
                        if (distanceHeuristic(ally, futureEnemies[i]) < 450) {
                            total++;
                            break;
                        }
                    }
                }
            }
        }

        double dist = calculateDistance(ally, values[ally.id].enemy);

        dist = dist/1000;
        dist =  1 - dist;

        total += dist*2;

        return total;
    }

    private static Unit futureAllyUnitPos(Unit ally, int horizontal, int vertical) {
        int speed;

        if (ally.type == 1) {
            speed = 10;
        } else if (ally.type == 2) {
            speed = 40;
        } else {
            speed = 15;
        }

        Unit newUnit = ally;

        if (horizontal == -1) {
            newUnit.posX -= speed;
        } else if (horizontal == 1) {
            newUnit.posX += speed;
        }

        if (vertical == -1) {
            newUnit.posY -= speed;
        } else if (vertical == 1) {
            newUnit.posY += speed;
        }

        return  newUnit;
    }
}