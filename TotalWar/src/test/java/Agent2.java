import java.util.Random;
import java.util.Scanner;

public class Agent2 {
    static class Unit {
        int id;
        int posX;
        int posY;
        int direction;
        int life;
        int type;
        int moving;

        public Unit (int id, int posX, int posY, int direction, int life, int type, int moving) {
            this.id = id;
            this.posX = posX;
            this.posY = posY;
            this.direction = direction;
            this.life = life;
            this.type = type;
            this.moving = moving;
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = 0;
        boolean draft = false;

        int numberUnits = in.nextInt();

        while (true) {
            Unit[] allyUnits = new Unit[numberUnits + 1];

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

                if (allyUnitId != -1)
                    allyUnits[i] = new Unit(allyUnitId, allyUnitPosX, allyUnitPosY, allyUnitDirection, allyUnitLife, allyUnitType, allyUnitMoving);
                else {
                    allyUnits[i] = null;
                    if (n == 0)
                        draft = true;
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
                else
                    enemyUnits[i] = null;
            }

            //System.out.println("0 0 0");

            Random rand = new Random(); //instance of random class
            //generate random values from 0-24
            int int_random_unit = rand.nextInt(numberUnits) + 1;
            int int_random_x = rand.nextInt(3840) - 1920;
            int int_random_y = rand.nextInt(2160) - 1080;

            if (numberUnits == 4) {
                //System.out.println(int_random_unit + " " + int_random_x + " " + int_random_y);
                /*if (n == 0) {
                    System.out.println("1 0 400 ; 2 0 400 ; 3 0 700; 4 0 200");
                    n++;
                } else if (n == 1) {
                    System.out.println("2 0 400");
                    n++;
                } else if (n == 2) {
                    System.out.println("3 500 700");
                    n++;
                } else if (n == 3) {
                    System.out.println("4 0 200");
                    n++;
                } else {
                    /*if (n == 4 && allyUnits[3] != null && allyUnits[2] == null) {
                        System.out.println("3 5000 0");
                        n++;
                    } else {*/
                    System.out.println("0 0 0");
                    //}
                //}
            } else if (draft && numberUnits == 9) {
               /*if (n < 9) {
                    n++;
                    int int_random_draft = rand.nextInt(4) + 1;
                    int_random_x = rand.nextInt(1920);
                    int_random_y = rand.nextInt(540);
                    System.out.println(int_random_draft + " " + int_random_x + " " + int_random_y);
                } else {
                    System.out.println(int_random_unit + " " + int_random_x + " " + int_random_y);
                }

                /*if (n == 0) {
                    System.out.println("1 805 225");
                    n++;
                } else if (n == 1) {
                    System.out.println("1 965 225");
                    n++;
                } else if (n == 2) {
                    System.out.println("2 650 225");
                    n++;
                } else if (n == 3) {
                    System.out.println("2 1120 225");
                    n++;
                } else if (n == 4) {
                    System.out.println("3 470 125");
                    n++;
                } else if (n == 5) {
                    System.out.println("3 1300 125");
                    n++;
                } else if (n == 6) {
                    System.out.println("4 685 50");
                    n++;
                } else if (n == 7) {
                    System.out.println("4 885 50");
                    n++;
                } else if (n == 8) {
                    System.out.println("4 1085 50");
                    n++;
                } else if (n == 9) {
                    System.out.println("1 0 250; 2 0 250; 3 0 250; 4 0 250; 5 0 400; 6 0 400; 7 0 250; 8 0 250; 9 0 250");
                    n++;
                } /*else if (n == 10) {
                    System.out.println("2 0 250");
                    n++;
                } else if (n == 11) {
                    System.out.println("3 0 250");
                    n++;
                } else if (n == 12) {
                    System.out.println("4 0 250");
                    n++;
                } else if (n == 13) {
                    System.out.println("5 0 400");
                    n++;
                } else if (n == 14) {
                    System.out.println("6 0 400");
                    n++;
                } else if (n == 15) {
                    System.out.println("7 0 250");
                    n++;
                } else if (n == 16) {
                    System.out.println("8 0 250");
                    n++;
                } else if (n == 17) {
                    System.out.println("9 0 250");
                    n++;
                }else {*/
                    System.out.println("0 0 0");
                //}
            } else if (numberUnits == 30) {
                /*if (n < 30) {
                    n++;
                    int int_random_draft = rand.nextInt(4) + 1;
                    int_random_x = rand.nextInt(1920);
                    int_random_y = rand.nextInt(540);
                    System.out.println(int_random_draft + " " + int_random_x + " " + int_random_y);
                } else {
                    System.out.println(int_random_unit + " " + int_random_x + " " + int_random_y);
                }
                /*if (n == 0) {
                    System.out.println("1 565 225");
                    n++;
                } else if (n == 1) {
                    System.out.println("1 645 225");
                    n++;
                } else if (n == 2) {
                    System.out.println("1 725 225");
                    n++;
                } else if (n == 3) {
                    System.out.println("1 805 225");
                    n++;
                } else if (n == 4) {
                    System.out.println("1 885 225");
                    n++;
                } else if (n == 5) {
                    System.out.println("1 965 225");
                    n++;
                } else if (n == 6) {
                    System.out.println("1 1045 225");
                    n++;
                } else if (n == 7) {
                    System.out.println("1 1125 225");
                    n++;
                } else if (n == 8) {
                    System.out.println("1 1205 225");
                    n++;
                } else if (n == 9) {
                    System.out.println("1 1285 225");
                    n++;
                } else if (n == 10) {
                    System.out.println("2 325 190");
                    n++;
                } else if (n == 11) {
                    System.out.println("2 405 190");
                    n++;
                } else if (n == 12) {
                    System.out.println("2 485 190");
                    n++;
                } else if (n == 13) {
                    System.out.println("2 1365 190");
                    n++;
                } else if (n == 14) {
                    System.out.println("2 1445 190");
                    n++;
                } else if (n == 15) {
                    System.out.println("2 1525 190");
                    n++;
                } else if (n == 16) {
                    System.out.println("3 85 125");
                    n++;
                } else if (n == 17) {
                    System.out.println("3 165 125");
                    n++;
                } else if (n == 18) {
                    System.out.println("3 245 125");
                    n++;
                } else if (n == 19) {
                    System.out.println("3 1605 125");
                    n++;
                } else if (n == 20) {
                    System.out.println("3 1685 125");
                    n++;
                } else if (n == 21) {
                    System.out.println("3 1765 125");
                    n++;
                } else if (n == 22) {
                    System.out.println("4 645 125");
                    n++;
                } else if (n == 23) {
                    System.out.println("4 725 125");
                    n++;
                } else if (n == 24) {
                    System.out.println("4 805 125");
                    n++;
                } else if (n == 25) {
                    System.out.println("4 885 125");
                    n++;
                } else if (n == 26) {
                    System.out.println("4 965 125");
                    n++;
                } else if (n == 27) {
                    System.out.println("4 1045 125");
                    n++;
                } else if (n == 28) {
                    System.out.println("4 1125 125");
                    n++;
                } else if (n == 29) {
                    System.out.println("4 1205 125");
                    n++;
                } else if (n == 30) {
                    System.out.println("1 0 400; 2 0 400; 3 0 400; 4 0 400; 5 0 400; 6 0 400; 7 0 400; 8 0 400; 9 0 400; 10 0 400; " +
                            "11 0 400; 12 0 400; 13 0 400; 14 0 400; 15 0 400; 16 0 400; 17 0 400; 18 0 400; 19 0 400; 20 0 400; " +
                            "21 0 400; 22 0 400; 23 0 300; 24 0 300; 25 0 300; 26 0 300; 27 0 300; 28 0 300; 29 0 300; 30 0 300");
                    n++;
                } /*else if (n == 31) {
                    System.out.println("2 0 400");
                    n++;
                } else if (n == 32) {
                    System.out.println("3 0 400");
                    n++;
                } else if (n == 33) {
                    System.out.println("4 0 400");
                    n++;
                } else if (n == 34) {
                    System.out.println("5 0 400");
                    n++;
                } else if (n == 35) {
                    System.out.println("6 0 400");
                    n++;
                } else if (n == 36) {
                    System.out.println("7 0 400");
                    n++;
                } else if (n == 37) {
                    System.out.println("8 0 400");
                    n++;
                } else if (n == 38) {
                    System.out.println("9 0 400");
                    n++;
                } else if (n == 39) {
                    System.out.println("10 0 400");
                    n++;
                } else if (n == 40) {
                    System.out.println("11 0 600");
                    n++;
                } else if (n == 41) {
                    System.out.println("12 0 600");
                    n++;
                } else if (n == 42) {
                    System.out.println("13 0 600");
                    n++;
                } else if (n == 43) {
                    System.out.println("14 0 600");
                    n++;
                } else if (n == 44) {
                    System.out.println("15 0 600");
                    n++;
                } else if (n == 45) {
                    System.out.println("16 0 600");
                    n++;
                } else if (n == 46) {
                    System.out.println("17 0 600");
                    n++;
                } else if (n == 47) {
                    System.out.println("18 0 600");
                    n++;
                } else if (n == 48) {
                    System.out.println("19 0 600");
                    n++;
                } else if (n == 49) {
                    System.out.println("20 0 600");
                    n++;
                } else if (n == 50) {
                    System.out.println("21 0 600");
                    n++;
                } else if (n == 51) {
                    System.out.println("22 0 600");
                    n++;
                } else if (n == 52) {
                    System.out.println("23 0 300");
                    n++;
                } else if (n == 53) {
                    System.out.println("24 0 300");
                    n++;
                } else if (n == 54) {
                    System.out.println("25 0 300");
                    n++;
                } else if (n == 55) {
                    System.out.println("26 0 300");
                    n++;
                } else if (n == 56) {
                    System.out.println("27 0 300");
                    n++;
                } else if (n == 57) {
                    System.out.println("28 0 300");
                    n++;
                } else if (n == 58) {
                    System.out.println("29 0 300");
                    n++;
                } else if (n == 59) {
                    System.out.println("30 0 300");
                    n++;
                } else {*/
                    System.out.println("0 0 0");
                //}
            }
        }
    }
}