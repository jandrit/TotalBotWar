package com.codingame.game;

import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.SpriteAnimation;
import com.codingame.gameengine.module.entities.Text;

public class Unit { //Clase que crea las unidades y contiene sus estadisticas
    private int id;
    private Type type; //Tipo de unidad
    private Group group; //Grupo de sprites, animaciones y textos (que a su vez contiene la posición de la unidad)
    private Text textId;
    private Text textLife;
    private String[][] left; //Grupo de animaciones izquierdas de la unidad
    private String[][] right; //Grupo de animaciones derechas de la unidad
    private SpriteAnimation[] spriteLeftAnimation; //Animacies de la izquierda
    private SpriteAnimation[] spriteRightAnimation; //Animaciones de la derecha
    private SpriteAnimation buffSprite; //Sprite de la flecha que indica que una unidad esta siendo afectada por el general
    private SpriteAnimation arrows; //Sprite que indica que una unidad esta siendo atacada con flechas

    //Variables que indican las distintas estadisticas de las unidades
    private int defense;
    private int attack;
    private int chargeForce;
    private int chargeResistance;
    private int velocity;
    private int life;
    private int attackDistance;
    private int farAttack;
    private int farResistance;

    private boolean buffed; //Indica si la unidad esta siendo afectada por el general o no

    private int nextX = 0; //Hacia donde se dirige la unidad en el eje x
    private int nextY = 0; //Hacia donde se dirige la unidad en el eje y
    private boolean moveX = false; //Indica si la unidad se esta moviendo en el eje x
    private boolean moveY = false; // Indica si la unidad se esta moviendo en el eje y
    private Direction faceDirection; //Hacia donde esta mirando la unidad
    //private double nextRot = 0;
    //private boolean rot = false;
    private Unit objectiveUnit = null; //Indica a que unidad esta atacando (cuerpo a cuerpo) la unidad

    public Unit (int id, Type type, Group group, Text textId, Text textLife, String[][] left, String[][] right, SpriteAnimation[] spriteLeftAnimation, SpriteAnimation[] spriteRightAnimation, Direction faceDirection, SpriteAnimation buffSprite, SpriteAnimation arrows) {
        this.id = id;
        this.type = type;
        this.group = group;
        this.textId = textId;
        this.textLife = textLife;
        this.left = left;
        this.right = right;
        this.spriteLeftAnimation = spriteLeftAnimation;
        this.spriteRightAnimation = spriteRightAnimation;
        setStats();
        this.faceDirection = faceDirection;
        this.buffSprite = buffSprite;
        this.arrows = arrows;
        buffed = false;
    }

    private void setStats() { //Inicializa las stats para las unidades
        if (type == Type.HORSE) {
            defense = 12;
            attack = 12;
            chargeForce = 100;
            chargeResistance = 15;
            velocity = 40;
            life = 200;
            farResistance = 30;
        } else if (type == Type.SWORD) {
            defense = 10;
            attack = 20;
            chargeForce = 5;
            chargeResistance = 25;
            velocity = 15;
            life = 250;
            farResistance = 10;
        } else if (type == Type.SPEAR) {
            defense = 20;
            attack = 15;
            chargeForce = 10;
            chargeResistance = 125;
            velocity = 10;
            life = 250;
            farResistance = 30;
        } else if (type == Type.BOW) {
            defense = 5;
            attack = 10;
            chargeForce = 5;
            chargeResistance = 0;
            velocity = 15;
            life = 100;
            farResistance = 10;
        }

        if (type != Type.BOW) {
            attackDistance = 10;
            farAttack = 0;
        } else {
            attackDistance = 450;
            farAttack = 20;
        }
    }

    public void restoreStats() { //Restaura las estadisticas originales a excepción de la vida
        int lifeOriginal = life;
        setStats();
        life = lifeOriginal;
    }

    public void changeStats(float change) { //Cambia las estadisticas de la unidad debido a efectos como la muerte del general o su influencia
        defense *= change;
        attack *= change;
        chargeForce *= change;
        chargeResistance *= change;
        velocity *= change;
        attackDistance *= change;
        farAttack *= change;
        farResistance *= change;
    }

    public boolean getBuffed() {
        return buffed;
    }

    public void setBuffed (boolean buffed) {
        this.buffed = buffed;
    }

    public SpriteAnimation getBuffSprite() {
        return buffSprite;
    }

    public void setBuffSprite(SpriteAnimation buffSprite) {
        this.buffSprite = buffSprite;
    }

    public SpriteAnimation getArrows() {
        return arrows;
    }

    public void setArrows(SpriteAnimation arrows) {
        this.arrows = arrows;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Text getTextId() {
        return textId;
    }

    public void setTextId(Text textId) {
        this.textId = textId;
    }

    public Text getTextLife() {
        return textLife;
    }

    public void setTextLife(Text textLife) {
        this.textLife = textLife;
    }

    public String[][] getLeft() {
        return left;
    }

    public void setLeft(String[][] left) {
        this.left = left;
    }

    public String[][] getRight() {
        return right;
    }

    public void setRight(String[][] right) {
        this.right = right;
    }

    public SpriteAnimation[] getSpriteLeftAnimation() {
        return spriteLeftAnimation;
    }

    public void setSpriteLeftAnimation(SpriteAnimation[] spriteLeftAnimation) {
        this.spriteLeftAnimation = spriteLeftAnimation;
    }

    public SpriteAnimation[] getSpriteRightAnimation() {
        return spriteRightAnimation;
    }

    public void setSpriteRightAnimation(SpriteAnimation[] spriteRightAnimation) {
        this.spriteRightAnimation = spriteRightAnimation;
    }

    public void setSpriteAnimation(int animation, String direct) { //Establece la animacion a ejecutar para la unidad en cuestión, se le debe indicar que animación se quiere y su orientación (derecha o izquierda)
        if (direct.equals("Left")) {
            for (int i = 0; i < spriteLeftAnimation.length; i++) {
                if (i != animation) {
                    spriteLeftAnimation[i].setVisible(false);
                } else {
                    spriteLeftAnimation[animation].setVisible(true);
                    if (animation == 3) {
                        spriteLeftAnimation[animation].setPlaying(true);
                    }
                }
                spriteRightAnimation[i].setVisible(false);
            }
        } else {
            for (int i = 0; i < spriteRightAnimation.length; i++) {
                if (i != animation) {
                    spriteRightAnimation[i].setVisible(false);
                } else {
                    spriteRightAnimation[animation].setVisible(true);
                    if (animation == 3) {
                        spriteRightAnimation[animation].setPlaying(true);
                    }
                }
                spriteLeftAnimation[i].setVisible(false);
            }
        }
    }

    public int getDefense() {
        return defense;
    }

    public int getAttack() {
        return attack;
    }

    public int getChargeForce() {
        return chargeForce;
    }

    public int getChargeResistance() {
        return chargeResistance;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getAttackDistance() {
        return attackDistance;
    }

    public int getFarAttack() {
        return farAttack;
    }

    public int getFarResistance() {
        return farResistance;
    }

    public int getNextX() {
        return nextX;
    }

    public void setNextX(int x) {
        nextX = x;
    }

    public int getNextY() {
        return nextY;
    }

    public void setNextY(int y) {
        nextY = y;
    }

    public boolean getMoveX() {
        return moveX;
    }

    public void setMoveX(boolean moveX) {
        this.moveX = moveX;
    }

    public boolean getMoveY() {
        return moveY;
    }

    public void setMoveY(boolean moveY) {
        this.moveY = moveY;
    }

    /*public double getNextRot() {
        return nextRot;
    }

    public void setNextRot(double nextRot) {
        this.nextRot = nextRot;
    }

    public boolean getRot() {
        return rot;
    }

    public void setRot(boolean rot) {
        this.rot = rot;
    }*/

    public Unit getObjectiveUnit() {
        return objectiveUnit;
    }

    public void setObjectiveUnit(Unit objectiveUnit) {
        this.objectiveUnit = objectiveUnit;
    }

    public Direction getFaceDirection() {
        return faceDirection;
    }

    public void setFaceDirection(Direction faceDirection) {
        this.faceDirection = faceDirection;
    }
}
