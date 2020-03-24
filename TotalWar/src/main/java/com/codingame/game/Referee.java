package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;

import java.awt.*;

public class Referee extends AbstractReferee {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    //Unidades de cada jugador
    Unit[] player0Units;
    Unit[] player1Units;

    //Matrices que guardaran las distintas animaciones de cada tipo de soldado
    String[][] Soldado_Left;
    String[][] Soldado_Right;
    String[][] Lancero_Left;
    String[][] Lancero_Right;
    String[][] Arquero_Left;
    String[][] Arquero_Right;
    String[][] Caballero_Left;
    String[][] Caballero_Right;
    String[] UpArrow; //Animación de indicador de influencia del general
    String[] LeftAttackArrows;//Animación de ataque recibido con flechas izquierda
    String[] RightAttackArrows; //Animación de ataque recibido con flechas derecha

    //Sprites del draft inicial
    Sprite sword, spear, bow, horse;

    private int round = 0; //Sirve para indicar cuando enviar el numero de undidades a los jugadores y cuando no
    private int n = 0; //Sirve para el draft
    private int numberUnits; //Numero de unidades que contendra cada ejercito al inicio
    private int size; //Tamaño de cada unidad
    private int generalInfluenceArea = 300;

    @Override
    public void init() {
        if (gameManager.getLeagueLevel() == 1) {
            size = 150;
            numberUnits = 4;
        } else if (gameManager.getLeagueLevel() == 2) {
            size = 150;
            numberUnits = 9;
        } else if (gameManager.getLeagueLevel() == 3) {
            size = 75;
            numberUnits = 30;
        }

        player0Units = new Unit[numberUnits];
        player1Units = new Unit[numberUnits];

        createUnits();

        drawBackground();
        drawUnits();
        drawHud();

        gameManager.setFrameDuration(600);
        gameManager.getPlayer(0).setScore(1);
        gameManager.getPlayer(1).setScore(1);
    }

    private void createUnits() { //Crea las unidades para las ligas sin draft
        if (gameManager.getLeagueLevel() == 1) {
            player0Units[0] = new Unit(1, Type.SWORD, null, null, null, null, null, null, null, Direction.SOUTH, null, null);
            player0Units[1] = new Unit(2, Type.SPEAR, null, null, null, null, null, null, null, Direction.SOUTH, null, null);
            player0Units[2] = new Unit(3, Type.HORSE, null, null, null, null, null, null, null, Direction.SOUTH, null, null);
            player0Units[3] = new Unit(4, Type.BOW, null, null, null, null, null, null, null, Direction.SOUTH, null, null);
            player1Units[0] = new Unit(1, Type.SWORD, null, null, null, null, null, null, null, Direction.NORTH, null, null);
            player1Units[1] = new Unit(2, Type.SPEAR, null, null, null, null, null, null, null, Direction.NORTH, null, null);
            player1Units[2] = new Unit(3, Type.HORSE, null, null, null, null, null, null, null, Direction.NORTH, null, null);
            player1Units[3] = new Unit(4, Type.BOW, null, null, null, null, null, null, null, Direction.NORTH, null, null);
        }
    }

    private void drawBackground() { //Dibuja los fondos y el logo del juego
        graphicEntityModule.createSprite()
                .setImage("Background.png")
                .setBaseHeight(1080)
                .setBaseWidth(1920)
                .setAnchor(0);

        graphicEntityModule.createSprite()
                .setImage("TotalBotWar.png")
                .setBaseWidth(280)
                .setBaseHeight(127)
                .setX(144)
                .setY(1080/2 - 128)
                .setRotation(1.58);

        graphicEntityModule.createSprite()
                .setImage("TotalBotWar.png")
                .setBaseWidth(280)
                .setBaseHeight(127)
                .setX(1920 - 144)
                .setY(1080/2 + 128)
                .setRotation(-1.58);

        if (gameManager.getLeagueLevel() >= 2) {
            sword = graphicEntityModule.createSprite()
                    .setImage("Soldado_Icon.png")
                    .setBaseWidth(200)
                    .setBaseHeight(200)
                    .setX(284)
                    .setY(1080 / 2 - 100)
                    .setAlpha(0.3)
                    .setZIndex(100);

            spear = graphicEntityModule.createSprite()
                    .setImage("Lanzero_Icon.png")
                    .setBaseWidth(200)
                    .setBaseHeight(200)
                    .setX(668)
                    .setY(1080 / 2 - 100)
                    .setAlpha(0.3)
                    .setZIndex(100);

            horse = graphicEntityModule.createSprite()
                    .setImage("Caballero_Icon.png")
                    .setBaseWidth(200)
                    .setBaseHeight(200)
                    .setX(1052)
                    .setY(1080 / 2 - 100)
                    .setAlpha(0.3)
                    .setZIndex(100);

            bow = graphicEntityModule.createSprite()
                    .setImage("Arquero_Icon.png")
                    .setBaseWidth(200)
                    .setBaseHeight(200)
                    .setX(1436)
                    .setY(1080 / 2 - 100)
                    .setAlpha(0.3)
                    .setZIndex(100);
        }
    }

    private void drawUnits() { //Dibuja las unidades, se divide en dos partes

        //Iniciacion de las animaciones, estas se dividen en dos matrices por tipo de unidad que indican si son las animaciones izquierdas o derechas
        Lancero_Left = new String[4][];
        String[] Lancero_Idle_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Idle_Left.png")
                .setImageCount(4)
                .setWidth(383)
                .setHeight(293)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleLanceroL")
                .split();
        Lancero_Left[0] = Lancero_Idle_Left;
        String[] Lancero_Run_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Run_Left.png")
                .setImageCount(5)
                .setWidth(379)
                .setHeight(262)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("runLanceroL")
                .split();
        Lancero_Left[1] = Lancero_Run_Left;
        String[] Lancero_Attack_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Attack_Left.png")
                .setImageCount(5)
                .setWidth(406)
                .setHeight(263)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("attackLanceroL")
                .split();
        Lancero_Left[2] = Lancero_Attack_Left;
        String[] Lancero_Death_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Death_Left.png")
                .setImageCount(4)
                .setWidth(443)
                .setHeight(290)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathLanceroL")
                .split();
        Lancero_Left[3] = Lancero_Death_Left;

        Lancero_Right = new String[4][];
        String[] Lancero_Idle_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Idle_Right.png")
                .setImageCount(4)
                .setWidth(383)
                .setHeight(294)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleLanceroR")
                .split();
        Lancero_Right[0] = Lancero_Idle_Right;
        String[] Lancero_Run_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Run_Right.png")
                .setImageCount(5)
                .setWidth(383)
                .setHeight(262)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("runLanceroR")
                .split();
        Lancero_Right[1] = Lancero_Run_Right;
        String[] Lancero_Attack_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Attack_Right.png")
                .setImageCount(5)
                .setWidth(407)
                .setHeight(263)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("attackLanceroR")
                .split();
        Lancero_Right[2] = Lancero_Attack_Right;
        String[] Lancero_Death_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Lancero_Death_Right.png")
                .setImageCount(4)
                .setWidth(443)
                .setHeight(289)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathLanceroR")
                .split();
        Lancero_Right[3] = Lancero_Death_Right;

        Soldado_Left = new String[4][];
        String[] Soldado_Idle_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Idle_Left.png")
                .setImageCount(4)
                .setWidth(382)
                .setHeight(260)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleSoldadoL")
                .split();
        Soldado_Left[0] = Soldado_Idle_Left;
        String[] Soldado_Run_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Run_Left.png")
                .setImageCount(5)
                .setWidth(382)
                .setHeight(259)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("runSoldadoL")
                .split();
        Soldado_Left[1] = Soldado_Run_Left;
        String[] Soldado_Attack_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Attack_Left.png")
                .setImageCount(5)
                .setWidth(382)
                .setHeight(289)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("attackSoldadoL")
                .split();
        Soldado_Left[2] = Soldado_Attack_Left;
        String[] Soldado_Death_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Death_Left.png")
                .setImageCount(4)
                .setWidth(399)
                .setHeight(261)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathSoldadoL")
                .split();
        Soldado_Left[3] = Soldado_Death_Left;

        Soldado_Right = new String[4][];
        String[] Soldado_Idle_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Idle_Right.png")
                .setImageCount(4)
                .setWidth(364)
                .setHeight(253)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleSoldadoR")
                .split();
        Soldado_Right[0] = Soldado_Idle_Right;
        String[] Soldado_Run_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Run_Right.png")
                .setImageCount(5)
                .setWidth(383)
                .setHeight(259)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("runSoldadoR")
                .split();
        Soldado_Right[1] = Soldado_Run_Right;
        String[] Soldado_Attack_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Attack_Right.png")
                .setImageCount(5)
                .setWidth(382)
                .setHeight(289)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("attackSoldadoR")
                .split();
        Soldado_Right[2] = Soldado_Attack_Right;
        String[] Soldado_Death_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Soldado_Death_Right.png")
                .setImageCount(4)
                .setWidth(399)
                .setHeight(261)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathSoldadoR")
                .split();
        Soldado_Right[3] = Soldado_Death_Right;

        Arquero_Left = new String[4][];
        String[] Arquero_Idle_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Idle_Left.png")
                .setImageCount(4)
                .setWidth(381)
                .setHeight(254)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleArqueroL")
                .split();
        Arquero_Left[0] = Arquero_Idle_Left;
        String[] Arquero_Run_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Run_Left.png")
                .setImageCount(4)
                .setWidth(381)
                .setHeight(254)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("runArqueroL")
                .split();
        Arquero_Left[1] = Arquero_Run_Left;
        String[] Arquero_Attack_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Attack_Left.png")
                .setImageCount(6)
                .setWidth(381)
                .setHeight(257)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(6)
                .setName("attackArqueroL")
                .split();
        Arquero_Left[2] = Arquero_Attack_Left;
        String[] Arquero_Death_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Death_Left.png")
                .setImageCount(4)
                .setWidth(409)
                .setHeight(262)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathArqueroL")
                .split();
        Arquero_Left[3] = Arquero_Death_Left;

        Arquero_Right = new String[4][];
        String[] Arquero_Idle_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Idle_Right.png")
                .setImageCount(4)
                .setWidth(382)
                .setHeight(255)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleArqueroR")
                .split();
        Arquero_Right[0] = Arquero_Idle_Right;
        String[] Arquero_Run_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Run_Right.png")
                .setImageCount(4)
                .setWidth(382)
                .setHeight(254)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("runArqueroR")
                .split();
        Arquero_Right[1] = Arquero_Run_Right;
        String[] Arquero_Attack_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Attack_Right.png")
                .setImageCount(6)
                .setWidth(382)
                .setHeight(257)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(6)
                .setName("attackArqueroR")
                .split();
        Arquero_Right[2] = Arquero_Attack_Right;
        String[] Arquero_Death_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Arquero_Death_Right.png")
                .setImageCount(4)
                .setWidth(409)
                .setHeight(262)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathArqueroR")
                .split();
        Arquero_Right[3] = Arquero_Death_Right;

        Caballero_Left = new String[4][];
        String[] Caballero_Idle_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Idle_Left.png")
                .setImageCount(4)
                .setWidth(408)
                .setHeight(316)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleCaballeroL")
                .split();
        Caballero_Left[0] = Caballero_Idle_Left;
        String[] Caballero_Run_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Run_Left.png")
                .setImageCount(5)
                .setWidth(408)
                .setHeight(302)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("runCaballeroL")
                .split();
        Caballero_Left[1] = Caballero_Run_Left;
        String[] Caballero_Attack_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Attack_Left.png")
                .setImageCount(4)
                .setWidth(434)
                .setHeight(307)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("attackCaballeroL")
                .split();
        Caballero_Left[2] = Caballero_Attack_Left;
        String[] Caballero_Death_Left = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Death_Left.png")
                .setImageCount(4)
                .setWidth(408)
                .setHeight(317)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathCaballeroL")
                .split();
        Caballero_Left[3] = Caballero_Death_Left;

        Caballero_Right = new String[4][];
        String[] Caballero_Idle_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Idle_Right.png")
                .setImageCount(4)
                .setWidth(408)
                .setHeight(316)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("idleCaballeroR")
                .split();
        Caballero_Right[0] = Caballero_Idle_Right;
        String[] Caballero_Run_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Run_Right.png")
                .setImageCount(5)
                .setWidth(408)
                .setHeight(301)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(5)
                .setName("runCaballeroR")
                .split();
        Caballero_Right[1] = Caballero_Run_Right;
        String[] Caballero_Attack_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Attack_Right.png")
                .setImageCount(4)
                .setWidth(434)
                .setHeight(307)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("attackCaballeroR")
                .split();
        Caballero_Right[2] = Caballero_Attack_Right;
        String[] Caballero_Death_Right = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Caballero_Death_Right.png")
                .setImageCount(4)
                .setWidth(408)
                .setHeight(318)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("deathCaballeroR")
                .split();
        Caballero_Right[3] = Caballero_Death_Right;

        //Animacion para la influencia del general a una unidad
        UpArrow = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Flecha.png")
                .setImageCount(4)
                .setWidth(190)
                .setHeight(222)
                .setOrigRow(0)
                .setOrigCol(0)
                .setImagesPerRow(4)
                .setName("arrow")
                .split();

        //Animación para el ataque recibido de flechas, tanto izquierda como derecha
        LeftAttackArrows = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Flechas_Left.png")
                .setImageCount(5)
                .setWidth(382)
                .setHeight(264)
                .setOrigCol(0)
                .setOrigRow(0)
                .setImagesPerRow(5)
                .setName("leftArrows")
                .split();

        RightAttackArrows = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Flechas_Right.png")
                .setImageCount(5)
                .setWidth(382)
                .setHeight(264)
                .setOrigCol(0)
                .setOrigRow(0)
                .setImagesPerRow(5)
                .setName("rightArrows")
                .split();

        //Establece manualmente las unidades y su distribución en la liga donde no hay draft
        if (gameManager.getLeagueLevel() == 1) {
            //Establece las animaciones izquierdas y derechas
            player0Units[0].setLeft(Soldado_Left);
            player0Units[0].setRight(Soldado_Right);
            setupAnimations(player0Units[0], 0);

            //Inicializa los sprites, las animaciones y los textos
            Sprite star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            SpriteAnimation buffAnimation = arrowAnimation(UpArrow);
            Sprite idBase = graphicEntityModule.createSprite()
                    .setImage("idBase1.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            Text textId = graphicEntityModule.createText(String.valueOf(player0Units[0].getId()))
                    .setX(5)
                    .setY(-1);
            Sprite health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            Text textLife = graphicEntityModule.createText(String.valueOf(player0Units[0].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            Sprite banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Rojo.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            SpriteAnimation arrows = arrowsAttackAnimation(LeftAttackArrows, "Left");
            Sprite unit = graphicEntityModule.createSprite()
                    .setImage("unitBase1.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);

            //Crea el grupo con lo inicializado
            Group group = createGroup(unit, textId, idBase, textLife, health, banner, player0Units[0], star, buffAnimation, arrows);
            group.setX(1920 / 2 + 50);
            group.setY(275);

            //Le asigna lo inicializado al objeto unidad para poder modificar sus atributos más adelante
            player0Units[0].setTextId(textId);
            player0Units[0].setTextLife(textLife);
            player0Units[0].setBuffSprite(buffAnimation);
            player0Units[0].setArrows(arrows);
            player0Units[0].setGroup(group);

            player0Units[1].setLeft(Lancero_Left);
            player0Units[1].setRight(Lancero_Right);
            setupAnimations(player0Units[1], 0);
            star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            buffAnimation = arrowAnimation(UpArrow);
            idBase = graphicEntityModule.createSprite()
                    .setImage("idBase1.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            textId = graphicEntityModule.createText(String.valueOf(player0Units[1].getId()))
                    .setX(5)
                    .setY(-1);
            health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            textLife = graphicEntityModule.createText(String.valueOf(player0Units[1].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Rojo.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            arrows = arrowsAttackAnimation(LeftAttackArrows, "Left");
            unit = graphicEntityModule.createSprite()
                    .setImage("unitBase1.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);
            group = createGroup(unit, textId, idBase, textLife, health, banner, player0Units[1], star, buffAnimation, arrows);
            group.setX(1920 / 2 - 200);
            group.setY(275);
            player0Units[1].setTextId(textId);
            player0Units[1].setTextLife(textLife);
            player0Units[1].setBuffSprite(buffAnimation);
            player0Units[1].setArrows(arrows);
            player0Units[1].setGroup(group);

            player0Units[2].setLeft(Caballero_Left);
            player0Units[2].setRight(Caballero_Right);
            setupAnimations(player0Units[2], 0);
            star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            buffAnimation = arrowAnimation(UpArrow);
            idBase = graphicEntityModule.createSprite()
                    .setImage("idBase1.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            textId = graphicEntityModule.createText(String.valueOf(player0Units[2].getId()))
                    .setX(5)
                    .setY(-1);
            health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            textLife = graphicEntityModule.createText(String.valueOf(player0Units[2].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Rojo.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            arrows = arrowsAttackAnimation(LeftAttackArrows, "Left");
            unit = graphicEntityModule.createSprite()
                    .setImage("unitBase1.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);
            group = createGroup(unit, textId, idBase, textLife, health, banner, player0Units[2], star, buffAnimation, arrows);
            group.setX(1920 / 2 - 600);
            group.setY(175);
            player0Units[2].setTextId(textId);
            player0Units[2].setTextLife(textLife);
            player0Units[2].setBuffSprite(buffAnimation);
            player0Units[2].setArrows(arrows);
            player0Units[2].setGroup(group);

            player0Units[3].setLeft(Arquero_Left);
            player0Units[3].setRight(Arquero_Right);
            setupAnimations(player0Units[3], 0);
            star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            buffAnimation = arrowAnimation(UpArrow);
            idBase = graphicEntityModule.createSprite()
                    .setImage("idBase1.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            textId = graphicEntityModule.createText(String.valueOf(player0Units[3].getId()))
                    .setX(5)
                    .setY(-1);
            health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            textLife = graphicEntityModule.createText(String.valueOf(player0Units[3].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Rojo.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            arrows = arrowsAttackAnimation(LeftAttackArrows, "Left");
            unit = graphicEntityModule.createSprite()
                    .setImage("unitBase1.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);
            group = createGroup(unit, textId, idBase, textLife, health, banner, player0Units[3], star, buffAnimation, arrows);
            group.setX(1920 / 2 - size/2);
            group.setY(100);
            player0Units[3].setTextId(textId);
            player0Units[3].setTextLife(textLife);
            player0Units[3].setBuffSprite(buffAnimation);
            player0Units[3].setArrows(arrows);
            player0Units[3].setGroup(group);

            player1Units[0].setLeft(Soldado_Left);
            player1Units[0].setRight(Soldado_Right);
            setupAnimations(player1Units[0], 1);
            star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            buffAnimation = arrowAnimation(UpArrow);
            idBase = graphicEntityModule.createSprite()
                    .setImage("idBase.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            textId = graphicEntityModule.createText(String.valueOf(player1Units[0].getId()))
                    .setX(5)
                    .setY(-1);
            health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            textLife = graphicEntityModule.createText(String.valueOf(player1Units[0].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Azul.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            arrows = arrowsAttackAnimation(RightAttackArrows, "Right");
            unit = graphicEntityModule.createSprite()
                    .setImage("unitBase.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);
            group = createGroup(unit, textId, idBase, textLife, health, banner, player1Units[0], star, buffAnimation, arrows);
            group.setX(1920 / 2 + 50);
            group.setY(1080 - size - 275);
            player1Units[0].setTextId(textId);
            player1Units[0].setTextLife(textLife);
            player1Units[0].setBuffSprite(buffAnimation);
            player1Units[0].setArrows(arrows);
            player1Units[0].setGroup(group);

            player1Units[1].setLeft(Lancero_Left);
            player1Units[1].setRight(Lancero_Right);
            setupAnimations(player1Units[1], 1);
            star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            buffAnimation = arrowAnimation(UpArrow);
            idBase = graphicEntityModule.createSprite()
                    .setImage("idBase.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            textId = graphicEntityModule.createText(String.valueOf(player1Units[1].getId()))
                    .setX(5)
                    .setY(-1);
            health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            textLife = graphicEntityModule.createText(String.valueOf(player1Units[1].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Azul.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            arrows = arrowsAttackAnimation(RightAttackArrows, "Right");
            unit = graphicEntityModule.createSprite()
                    .setImage("unitBase.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);
            group = createGroup(unit, textId, idBase, textLife, health, banner, player1Units[1], star, buffAnimation, arrows);
            group.setX(1920 / 2  - 200);
            group.setY(1080 - size - 275);
            player1Units[1].setTextId(textId);
            player1Units[1].setTextLife(textLife);
            player1Units[1].setBuffSprite(buffAnimation);
            player1Units[1].setArrows(arrows);
            player1Units[1].setGroup(group);

            player1Units[2].setLeft(Caballero_Left);
            player1Units[2].setRight(Caballero_Right);
            setupAnimations(player1Units[2], 1);
            star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            buffAnimation = arrowAnimation(UpArrow);
            idBase = graphicEntityModule.createSprite()
                    .setImage("idBase.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            textId = graphicEntityModule.createText(String.valueOf(player1Units[2].getId()))
                    .setX(5)
                    .setY(-1);
            health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            textLife = graphicEntityModule.createText(String.valueOf(player1Units[2].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Azul.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            arrows = arrowsAttackAnimation(RightAttackArrows, "Right");
            unit = graphicEntityModule.createSprite()
                    .setImage("unitBase.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);
            group = createGroup(unit, textId, idBase, textLife, health, banner, player1Units[2], star, buffAnimation, arrows);
            group.setX(1920 / 2 + 450);
            group.setY(1080 - size - 175);
            player1Units[2].setTextId(textId);
            player1Units[2].setTextLife(textLife);
            player1Units[2].setBuffSprite(buffAnimation);
            player1Units[2].setArrows(arrows);
            player1Units[2].setGroup(group);

            player1Units[3].setLeft(Arquero_Left);
            player1Units[3].setRight(Arquero_Right);
            setupAnimations(player1Units[3], 1);
            star = graphicEntityModule.createSprite()
                    .setImage("Estrella.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setX(size - 30)
                    .setVisible(false);
            buffAnimation = arrowAnimation(UpArrow);
            idBase = graphicEntityModule.createSprite()
                    .setImage("idBase.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25);
            textId = graphicEntityModule.createText(String.valueOf(player1Units[3].getId()))
                    .setX(5)
                    .setY(-1);
            health = graphicEntityModule.createSprite()
                    .setImage("health.png")
                    .setBaseWidth(25)
                    .setBaseHeight(25)
                    .setY(0)
                    .setX(-30);
            textLife = graphicEntityModule.createText(String.valueOf(player1Units[3].getLife()))
                    .setX(-30)
                    .setY(30)
                    .setStrokeColor(Color.white.getRGB())
                    .setFillColor(Color.white.getRGB())
                    .setStrokeThickness(1);
            banner = graphicEntityModule.createSprite()
                    .setImage("Estandarte_Azul.png")
                    .setBaseHeight(60)
                    .setBaseWidth(30)
                    .setX(-30)
                    .setY(70);
            arrows = arrowsAttackAnimation(RightAttackArrows, "Right");
            unit = graphicEntityModule.createSprite()
                    .setImage("unitBase.png")
                    .setBaseWidth(size)
                    .setBaseHeight(size)
                    .setAlpha(0.3)
                    .setZIndex(-50);
            group = createGroup(unit, textId, idBase, textLife, health, banner, player1Units[3], star, buffAnimation, arrows);
            group.setX(1920 / 2 - size/2);
            group.setY(1080 - size - 100);
            player1Units[3].setTextId(textId);
            player1Units[3].setTextLife(textLife);
            player1Units[3].setBuffSprite(buffAnimation);
            player1Units[3].setArrows(arrows);
            player1Units[3].setGroup(group);
        }
    }

    //Una vez obtenidas todas las animaciones, las inicializa y las ajustas para la unidad en cuestión
    private void setupAnimations(Unit unit, int player) {
        SpriteAnimation[] leftAnimation = new SpriteAnimation[4];
        SpriteAnimation[] rightAnimation = new SpriteAnimation[4];

        leftAnimation[0] = idleAnimation(unit, "Left");
        leftAnimation[1] = runAnimation(unit, "Left");
        leftAnimation[2] = attackAnimation(unit, "Left");
        leftAnimation[3] = deathAnimation(unit, "Left");
        rightAnimation[0] = idleAnimation(unit, "Right");
        rightAnimation[1] = runAnimation(unit, "Right");
        rightAnimation[2] = attackAnimation(unit, "Right");
        rightAnimation[3] = deathAnimation(unit, "Right");

        unit.setSpriteLeftAnimation(leftAnimation);
        unit.setSpriteRightAnimation(rightAnimation);

        //Hace que la primera animación en ser activada sea la animación idle
        if (player == 0)
            unit.setSpriteAnimation(0, "Left");
        else
            unit.setSpriteAnimation(0, "Right");
    }

    //Crea el grupo para cada unidad
    private Group createGroup (Sprite unitBase, Text textId, Sprite idBase, Text textLife, Sprite health, Sprite banner, Unit unit, Sprite star, SpriteAnimation buffAnimation, SpriteAnimation arrows) {
        return graphicEntityModule.createGroup(textId, idBase, textLife, health, banner,
                unit.getSpriteLeftAnimation()[0], unit.getSpriteLeftAnimation()[1], unit.getSpriteLeftAnimation()[2], unit.getSpriteLeftAnimation()[3], unit.getSpriteRightAnimation()[0], unit.getSpriteRightAnimation()[1], unit.getSpriteRightAnimation()[2], unit.getSpriteRightAnimation()[3],
                unitBase, star, buffAnimation, arrows)
                .setX(0)
                .setY(0);
    }

    //Dibuja lo relacionado con los jugadores y sus nombres
    private void drawHud() {
        for (Player player : gameManager.getPlayers()) {
            int x = player.getIndex() == 0 ? 125 : 1920 - 125;
            int y = player.getIndex() == 0 ? 125 : 1080 - 150;

            graphicEntityModule
                    .createRectangle()
                    .setWidth(140)
                    .setHeight(140)
                    .setX(x - 70)
                    .setY(y - 70)
                    .setLineWidth(0)
                    .setFillColor(player.getColorToken());

            graphicEntityModule
                    .createRectangle()
                    .setWidth(120)
                    .setHeight(120)
                    .setX(x - 60)
                    .setY(y - 60)
                    .setLineWidth(0)
                    .setFillColor(0xffffff);

            Text text = graphicEntityModule.createText(player.getNicknameToken())
                    .setX(x)
                    .setY(y + 100)
                    .setZIndex(50)
                    .setFontSize(40)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5);

            Sprite avatar = graphicEntityModule.createSprite()
                    .setX(x)
                    .setY(y)
                    .setZIndex(50)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5)
                    .setBaseHeight(116)
                    .setBaseWidth(116);

            player.hud = graphicEntityModule.createGroup(text, avatar);
        }
    }

    //Función principal de la clase
    @Override
    public void gameTurn(int turn) {
        //Si la liga no tiene draft, se salta los turnos del draft
        if (gameManager.getLeagueLevel() == 1)
            n = numberUnits;

        //Controla el turndo de cada jugador
        Player player = gameManager.getPlayer(turn % gameManager.getPlayerCount());

        //A partir del segundo turno de juego se reduce la duración de cada turno
        if (n == numberUnits + 1) {
            gameManager.frameDuration = 200;
            n++;
        }

        if (n == numberUnits && player.getIndex() == 1) { //Cuando se termine el draft y sea el turno del jugador 1
            if (gameManager.getLeagueLevel() != 1) { //Esconder los sprites del draft
                sword.setAlpha(0);
                spear.setAlpha(0);
                horse.setAlpha(0);
                bow.setAlpha(0);
            }
            gameManager.frameDuration = 1000; //Establecer la duración del prierm turno
            if (gameManager.getLeagueLevel() != 1 && !player0Units[0].getBuffed() && !player1Units[0].getBuffed()) { //Aumentar los stats a los generales
                player0Units[0].changeStats(1.25f);
                player0Units[0].setBuffed(true);
                player1Units[0].changeStats(1.25f);
                player1Units[0].setBuffed(true);
            }
        } else if (n == numberUnits && player.getIndex() == 0)
            n++;

        sendInputs(player); //Envia los inputs al jugador
        player.execute(); //Ejecuta el código del jugador

        try {
            String output = player.getOutputs().get(0);

            //Consulta la posbile multiacción del jugador
            new Action(0, 0, 0);
            for (String str : output.split(";"))
            {
                str = str.trim();
                if (str.isEmpty())
                    continue; // empty action is a valid action
                Action.parse(str);

                //Incluye en el sumario del juego la acción dada
                gameManager.addToGameSummary(String.format("Player %s played (%d %d %d)", player.getNicknameToken(), Action.id, Action.x, Action.y));

                if (n >= numberUnits) { //Si no esta en los turnos del draft
                    if (Action.id != 0 && Action.id <= player0Units.length) { //Si la unidad puede existir
                        if (player.getIndex() == 0 && player0Units[Action.id - 1] != null && player0Units[Action.id - 1].getObjectiveUnit() == null) { //Comprobaciones para evitar errores
                            //Establece las animaciones de movimiento
                            if (Action.x < 0) {
                                //El problema es que se debe establecer las animaciones con el set
                                player0Units[Action.id - 1].setSpriteAnimation(1, "Left");
                            } else {
                                player0Units[Action.id - 1].setSpriteAnimation(1, "Right");
                            }

                            //Establece la posición a la que se va a dirigir
                            player0Units[Action.id - 1].setNextX(player0Units[Action.id - 1].getGroup().getX() + Action.x);
                            if (player0Units[Action.id - 1].getNextX() < 0)
                                player0Units[Action.id - 1].setNextX(0);
                            else if (player0Units[Action.id - 1].getNextX() > (1920 - size))
                                player0Units[Action.id - 1].setNextX(1920 - size);
                            player0Units[Action.id - 1].setNextY(player0Units[Action.id - 1].getGroup().getY() + Action.y);
                            if (player0Units[Action.id - 1].getNextY() < 0)
                                player0Units[Action.id - 1].setNextY(0);
                            else if (player0Units[Action.id - 1].getNextY() > (1080 - size))
                                player0Units[Action.id - 1].setNextY(1080 - size);

                            //Cambia los booleanos que indican que se esta moviendo
                            player0Units[Action.id - 1].setMoveX(true);
                            player0Units[Action.id - 1].setMoveY(true);

                            //Cambia la dirección
                            checkDirection(player0Units[Action.id - 1]);
                            /*player0Units[action.id - 1].setNextRot(Math.toRadians(-action.rot));
                            player0Units[action.id - 1].setRot(true);*/
                        } else if (player1Units[Action.id - 1] != null && player1Units[Action.id - 1].getObjectiveUnit() == null && player.getIndex() == 1) { //Lo mismo que el anterior pero haciendolo simetrico para el otro jugador
                            if (Action.x < 0) {
                                player1Units[Action.id - 1].setSpriteAnimation(1, "Right");
                            } else {
                                player1Units[Action.id - 1].setSpriteAnimation(1, "Left");
                            }
                            player1Units[Action.id - 1].setNextX(player1Units[Action.id - 1].getGroup().getX() - Action.x);
                            if (player1Units[Action.id - 1].getNextX() < 0)
                                player1Units[Action.id - 1].setNextX(0);
                            else if (player1Units[Action.id - 1].getNextX() > (1920 - size))
                                player1Units[Action.id - 1].setNextX(1920 - size);
                            player1Units[Action.id - 1].setNextY(player1Units[Action.id - 1].getGroup().getY() - Action.y);
                            if (player1Units[Action.id - 1].getNextY() < 0)
                                player1Units[Action.id - 1].setNextY(0);
                            else if (player1Units[Action.id - 1].getNextY() > (1080 - size))
                                player1Units[Action.id - 1].setNextY(1080 - size);
                            player1Units[Action.id - 1].setMoveX(true);
                            player1Units[Action.id - 1].setMoveY(true);
                            checkDirection(player1Units[Action.id - 1]);
                            /*player1Units[action.id - 1].setNextRot(Math.toRadians(-action.rot));
                            player1Units[action.id - 1].setRot(true);*/
                        }
                    } else if (Action.id > player0Units.length) { //Si decides mover a una unidad que no existe
                        throw new InvalidAction("Invalid action."); //Lanza una acción invalida
                    }
                }
            }

            moveUnits();
            //rotUnits();
            if (n >= numberUnits) {
                attackUnits();
                farAttack();
            }
            if (gameManager.getLeagueLevel() >= 3 && ((player.getIndex() == 0 && player0Units[0] != null) || (player.getIndex() == 1 && player1Units[0] != null))) //Si el general no esta muerto
                generalInfluence();
            destroyUnits();
            if (n >= numberUnits) //Si no esta en el draft
                finishGame(turn);

            if (n < numberUnits && gameManager.getLeagueLevel() != 1 && player.getIndex() == 1) { //Si se encuentra en el draft
                Group group;

                //Lee e inicia que tipo de unidad a elegido el jugador
                if (Action.id == 1 || Action.id == 0) {
                    player1Units[n] = new Unit(n + 1, Type.SWORD, null, null, null, Soldado_Left, Soldado_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player1Units[n], 1);
                    sword.setAlpha(1);
                    spear.setAlpha(0.3);
                    bow.setAlpha(0.3);
                    horse.setAlpha(0.3);
                } else if (Action.id == 2) {
                    player1Units[n] = new Unit(n + 1, Type.SPEAR, null, null, null, Lancero_Left, Lancero_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player1Units[n], 1);
                    sword.setAlpha(0.3);
                    spear.setAlpha(1);
                    bow.setAlpha(0.3);
                    horse.setAlpha(0.3);
                } else if (Action.id == 3) {
                    player1Units[n] = new Unit(n + 1, Type.HORSE, null, null, null, Caballero_Left, Caballero_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player1Units[n], 1);
                    sword.setAlpha(0.3);
                    spear.setAlpha(0.3);
                    bow.setAlpha(0.3);
                    horse.setAlpha(1);
                } else {
                    player1Units[n] = new Unit(n + 1, Type.BOW, null, null, null, Arquero_Left, Arquero_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player1Units[n], 1);
                    sword.setAlpha(0.3);
                    spear.setAlpha(0.3);
                    bow.setAlpha(1);
                    horse.setAlpha(0.3);
                }

                //Establece los sprites iniciales
                Sprite star = graphicEntityModule.createSprite()
                        .setImage("Estrella.png")
                        .setBaseWidth(25)
                        .setBaseHeight(25)
                        .setX(size - 30)
                        .setVisible(false);

                if (gameManager.getLeagueLevel() == 3) {
                    star.setX(size + 30);
                }



                SpriteAnimation buffAnimation = arrowAnimation(UpArrow);

                Sprite idBase = graphicEntityModule.createSprite()
                        .setImage("idBase.png")
                        .setBaseWidth(35)
                        .setBaseHeight(30);
                Text textId = graphicEntityModule.createText(String.valueOf(player1Units[n].getId()))
                        .setX(2)
                        .setY(0);
                Sprite health = graphicEntityModule.createSprite()
                        .setImage("health.png")
                        .setBaseWidth(25)
                        .setBaseHeight(25)
                        .setY(0)
                        .setX(-30);
                Text textLife = graphicEntityModule.createText(String.valueOf(player1Units[n].getLife()))
                        .setX(-30)
                        .setY(30)
                        .setStrokeColor(Color.white.getRGB())
                        .setFillColor(Color.white.getRGB())
                        .setStrokeThickness(1);
                Sprite banner = graphicEntityModule.createSprite()
                        .setImage("Estandarte_Azul.png")
                        .setBaseHeight(60)
                        .setBaseWidth(30)
                        .setX(-30)
                        .setY(70);
                SpriteAnimation arrows = arrowsAttackAnimation(RightAttackArrows, "Right");
                Sprite unit = graphicEntityModule.createSprite()
                        .setImage("unitBase.png")
                        .setBaseWidth(150)
                        .setBaseHeight(150)
                        .setAlpha(0.3)
                        .setZIndex(-50);

                if (gameManager.getLeagueLevel() >= 3 && n == 0) { //Le añade la estrella si es la unidad del general
                    star.setVisible(true);
                    unit.setAlpha(0.8);
                }

                //Crea el grupo
                group = createGroup(unit, textId, idBase, textLife, health, banner, player1Units[n], star, buffAnimation, arrows);
                int posX = Action.x;
                int posY = Action.y;

                boolean amount = true; //Mantiene ejecutando el bucle hasta encontrar un espacio libre para añadir la unidad
                boolean jump = false; //Conoce si debe, o no, cambiar de fila

                posY = 1080 - size - posY;

                if (posX < 0) {
                    posX = 0;
                } else if (posX > 1920 - size) {
                    posX = 1920 - size;
                }

                if (posY < 1080/2)
                    posY = 1080/2;
                else if (posY > 1080 - size)
                    posY = 1080 - size;

                while (amount) { //Bucle que busca un espacio libre donde añadir la undiad si esta aparece por primera vez en el sitio de otra
                    if (posX < 0) {
                        posX = 0;
                    } else if (posX > 1920 - size) {
                        posX = 1920 - size;
                    }

                    if (posY < 1080/2)
                        posY = 1080/2;
                    else if (posY > 1080 - size)
                        posY = 1080 - size;

                    for (int i = 0; i < n; i++) {

                        if (posX < 0 || posX > 1920 - size)
                            jump = true;

                        if (player1Units[i]!= null && player1Units[i].getGroup().getX() == posX && player1Units[i].getGroup().getY() == posY) {
                            if (!jump)
                                posX += size;
                            else {
                                posY -= size;

                                posX = Action.x;
                                if (posX < 0) {
                                    posX = 0;
                                } else if (posX > 1920 - size) {
                                    posX = 1920 - size;
                                }

                                jump = false;
                            }

                            continue;
                        }
                    }

                    amount = false;
                }

                group.setX(posX);
                group.setY(posY);
                //group.setScale(0.5);

                if (gameManager.getLeagueLevel() == 3) {
                    group.setScale(0.5);
                }

                //Incluye en el objeto de la unidad todos los datos que pueden querer modificarse más adelante
                player1Units[n].setGroup(group);
                player1Units[n].setTextLife(textLife);
                player1Units[n].setTextId(textId);
                player1Units[n].setBuffSprite(buffAnimation);
                player1Units[n].setArrows(arrows);
            } else if (n < numberUnits && gameManager.getLeagueLevel() != 1 && player.getIndex() == 0) { //Lo mismo que en para el otro jugador pero simetrico
                Group group;

                if (Action.id == 1 || Action.id == 0) {
                    player0Units[n] = new Unit(n + 1, Type.SWORD, null, null, null, Soldado_Left, Soldado_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player0Units[n], 0);
                    sword.setAlpha(1);
                    spear.setAlpha(0.3);
                    bow.setAlpha(0.3);
                    horse.setAlpha(0.3);
                } else if (Action.id == 2) {
                    player0Units[n] = new Unit(n + 1, Type.SPEAR, null, null, null, Lancero_Left, Lancero_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player0Units[n], 0);
                    sword.setAlpha(0.3);
                    spear.setAlpha(1);
                    bow.setAlpha(0.3);
                    horse.setAlpha(0.3);
                } else if (Action.id == 3) {
                    player0Units[n] = new Unit(n + 1, Type.HORSE, null, null, null, Caballero_Left, Caballero_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player0Units[n], 0);
                    sword.setAlpha(0.3);
                    spear.setAlpha(0.3);
                    bow.setAlpha(0.3);
                    horse.setAlpha(1);
                } else {
                    player0Units[n] = new Unit(n + 1, Type.BOW, null, null, null, Arquero_Left, Arquero_Right, null, null, Direction.NORTH, null, null);
                    setupAnimations(player0Units[n], 0);
                    sword.setAlpha(0.3);
                    spear.setAlpha(0.3);
                    bow.setAlpha(1);
                    horse.setAlpha(0.3);
                }

                Sprite star = graphicEntityModule.createSprite()
                        .setImage("Estrella.png")
                        .setBaseWidth(25)
                        .setBaseHeight(25)
                        .setX(size - 30)
                        .setVisible(false);

                if (gameManager.getLeagueLevel() == 3) {
                    star.setX(size + 30);
                }

                SpriteAnimation buffAnimation = arrowAnimation(UpArrow);

                Sprite idBase = graphicEntityModule.createSprite()
                        .setImage("idBase1.png")
                        .setBaseWidth(35)
                        .setBaseHeight(30);
                Text textId = graphicEntityModule.createText(String.valueOf(player0Units[n].getId()))
                        .setX(2)
                        .setY(0);
                Sprite health = graphicEntityModule.createSprite()
                        .setImage("health.png")
                        .setBaseWidth(25)
                        .setBaseHeight(25)
                        .setY(0)
                        .setX(-30);
                Text textLife = graphicEntityModule.createText(String.valueOf(player0Units[n].getLife()))
                        .setX(-30)
                        .setY(30)
                        .setStrokeColor(Color.white.getRGB())
                        .setFillColor(Color.white.getRGB())
                        .setStrokeThickness(1);
                Sprite banner = graphicEntityModule.createSprite()
                        .setImage("Estandarte_Rojo.png")
                        .setBaseHeight(60)
                        .setBaseWidth(30)
                        .setX(-30)
                        .setY(70);
                SpriteAnimation arrows = arrowsAttackAnimation(LeftAttackArrows, "Left");
                Sprite unit = graphicEntityModule.createSprite()
                        .setImage("unitBase1.png")
                        .setBaseWidth(150)
                        .setBaseHeight(150)
                        .setAlpha(0.3)
                        .setZIndex(-50);

                if (gameManager.getLeagueLevel() >= 3 && n == 0) { //Le añade la estrella si es la unidad del general
                    star.setVisible(true);
                    unit.setAlpha(0.8);
                }

                group = createGroup(unit, textId, idBase, textLife, health, banner, player0Units[n], star, buffAnimation, arrows);
                int posX = Action.x;
                int posY = Action.y;

                boolean amount = true;
                boolean jump = false;

                posX = 1920 - size - posX;

                if (posX < 0) {
                    posX = 0;
                }
                else if (posX > 1920 - size) {
                    posX = 1920 - size;
                }

                if (posY < 0)
                    posY = 0;
                else if (posY > 1080/2 - size)
                    posY = 1080/2 - size;

                while (amount) {
                    if (posX < 0) {
                        posX = 0;
                    }
                    else if (posX > 1920 - size) {
                        posX = 1920 - size;
                    }

                    if (posY < 0)
                        posY = 0;
                    else if (posY > 1080/2 - size)
                        posY = 1080/2 - size;

                    for (int i = 0; i < n; i++) {
                        if (posX < 0 || posX > 1920 - size)
                            jump = true;

                        if (player0Units[i] != null && player0Units[i].getGroup().getX() == posX && player0Units[i].getGroup().getY() == posY) {
                            if (!jump)
                                posX -= size;
                            else {
                                posY += size;

                                posX = 1920 - size - Action.x;
                                if (posX < 0) {
                                    posX = 0;
                                }
                                else if (posX > 1920 - size) {
                                    posX = 1920 - size;
                                }

                                jump = false;
                            }

                            continue;
                        }
                    }

                    amount = false;
                }

                group.setX(posX);
                group.setY(posY);
                //group.setScale(0.5);

                if (gameManager.getLeagueLevel() == 3) {
                    group.setScale(0.5);
                }

                player0Units[n].setGroup(group);
                player0Units[n].setTextLife(textLife);
                player0Units[n].setTextId(textId);
                player0Units[n].setBuffSprite(buffAnimation);
                player0Units[n].setArrows(arrows);
                n++;
            }

        } catch (NumberFormatException e) {
            player.deactivate("Wrong output!");
            player.setScore(-1);
            endGame();
        } catch (AbstractPlayer.TimeoutException e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
            player.deactivate(player.getNicknameToken() + " timeout!");
            player.setScore(-1);
            endGame();
        } catch (InvalidAction e) {
            player.deactivate(e.getMessage());
            player.setScore(-1);
            endGame();
        }
    }

    private void sendInputs(Player player) { //Manda los inputs a los jugadores
        if (round == 0 || round == 1) {
            round++;
            player.sendInputLine(String.valueOf(numberUnits)); //En el primer turno les paso la cantidad de unidades que contendrá cada ejército
        }

        int moving; //Para indicar si la unidad se esta moviendo o no

        if (player.getIndex() == 0) {
            for (Unit player0Unit : player0Units) { //Manda las unidades aliadas
                if (player0Unit != null) {
                    if (player0Unit.getMoveX() || player0Unit.getMoveY())
                        moving = 1;
                    else
                        moving = 0;

                    int posX = 1920 - size - player0Unit.getGroup().getX();
                    player.sendInputLine(player0Unit.getId() + " " + posX + " " + player0Unit.getGroup().getY() + " " + player0Unit.getFaceDirection().ordinal() + " " + player0Unit.getLife() + " " + player0Unit.getType().ordinal() + " " + moving + " " + (1920 - size - player0Unit.getNextX()) + " " + player0Unit.getNextY());
                } else { //Si la unidad esta muerta o aun no existe
                    player.sendInputLine(-1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1);
                }
            }

            for (int i = 0; i < player1Units.length; i++) { //Manda las unidades enemigas
                if (player1Units[i] != null && i != n) {
                    if (player1Units[i].getMoveX() || player1Units[i].getMoveY())
                        moving = 1;
                    else
                        moving = 0;
                    int posX = 1920 - size - player1Units[i].getGroup().getX();
                    player.sendInputLine(player1Units[i].getId() + " " + posX + " " + player1Units[i].getGroup().getY() + " " + player1Units[i].getFaceDirection().ordinal() + " " + player1Units[i].getLife() + " " + player1Units[i].getType().ordinal() + " " + moving);
                } else {
                    player.sendInputLine(-1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1);
                }
            }
        } else { //Igual que el otro jugador pero cambiando los arrays de unidades y haciendolo simétrico
            for (Unit player1Unit : player1Units) {
                if (player1Unit != null) {
                    if (player1Unit.getMoveX() || player1Unit.getMoveY())
                        moving = 1;
                    else
                        moving = 0;

                    int posY = 1080 - size - player1Unit.getGroup().getY();
                    player.sendInputLine(player1Unit.getId() + " " + player1Unit.getGroup().getX() + " " + posY + " " + player1Unit.getFaceDirection().ordinal() + " " + player1Unit.getLife() + " " + player1Unit.getType().ordinal() + " " + moving + " " + player1Unit.getNextX() + " " + (1080 - size - player1Unit.getNextY()));
                } else {
                    player.sendInputLine(-1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1);
                }
            }

            for (Unit player0Unit : player0Units) {
                if (player0Unit != null) {
                    if (player0Unit.getMoveX() || player0Unit.getMoveY())
                        moving = 1;
                    else
                        moving = 0;

                    int posY = 1080 - size - player0Unit.getGroup().getY();
                    player.sendInputLine(player0Unit.getId() + " " + player0Unit.getGroup().getX() + " " + posY + " " + player0Unit.getFaceDirection().ordinal() + " " + player0Unit.getLife() + " " + player0Unit.getType().ordinal() + " " + moving);
                } else {
                    player.sendInputLine(-1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1 + " " + -1);
                }
            }
        }
    }

    private void moveUnits() { //Se encarga de mover las unidades
        for (Unit player0Unit : player0Units) { //Para las unidades del jugador 0
            if (player0Unit != null) { //Si existe
                if (player0Unit.getMoveX() && player0Unit.getGroup().getX() < player0Unit.getNextX()) { //Si su objetivo esta en la derecha
                    player0Unit.getGroup().setX(player0Unit.getGroup().getX() + player0Unit.getVelocity());
                    if (player0Unit.getGroup().getX() > player0Unit.getNextX() - player0Unit.getVelocity()) {
                        player0Unit.setMoveX(false);
                        checkDirection(player0Unit);
                        if (!player0Unit.getMoveY() && !player0Unit.getMoveX())
                            player0Unit.setSpriteAnimation(0, "Right");
                    }
                } else if (player0Unit.getMoveX() && player0Unit.getGroup().getX() > player0Unit.getNextX()) { //Si su objetivo esta en la izquierda
                    player0Unit.getGroup().setX(player0Unit.getGroup().getX() - player0Unit.getVelocity());
                    if (player0Unit.getGroup().getX() < player0Unit.getNextX() + player0Unit.getVelocity()) {
                        player0Unit.setMoveX(false);
                        checkDirection(player0Unit);
                        if (!player0Unit.getMoveY() && !player0Unit.getMoveX())
                            player0Unit.setSpriteAnimation(0, "Left");
                    }
                } else { //Si ya esta en la parte del eje x que quiere
                    player0Unit.setMoveX(false);
                }

                if (player0Unit.getMoveY() && player0Unit.getGroup().getY() < player0Unit.getNextY()) { //Si su objetivo esta debajo
                    player0Unit.getGroup().setY(player0Unit.getGroup().getY() + player0Unit.getVelocity());
                    if (player0Unit.getGroup().getY() > player0Unit.getNextY() - player0Unit.getVelocity()) {
                        player0Unit.setMoveY(false);
                        checkDirection(player0Unit);
                        if (!player0Unit.getMoveX() && !player0Unit.getMoveY())
                            player0Unit.setSpriteAnimation(0, "Left");
                    }
                } else if (player0Unit.getMoveY() && player0Unit.getGroup().getY() > player0Unit.getNextY()) { //Si su objetivo esta encima
                    player0Unit.getGroup().setY(player0Unit.getGroup().getY() - player0Unit.getVelocity());
                    if (player0Unit.getGroup().getY() < player0Unit.getNextY() - player0Unit.getVelocity()) {
                        player0Unit.setMoveY(false);
                        checkDirection(player0Unit);
                        if (!player0Unit.getMoveX() && !player0Unit.getMoveY())
                            player0Unit.setSpriteAnimation(0, "Left");
                    }
                } else { //Si no se mueve en el eje Y
                    player0Unit.setMoveY(false);
                    if (player0Unit.getObjectiveUnit() != null) //Si encuentra enemigo comienza la accion de luchar
                        player0Unit.setSpriteAnimation(2, "Left");
                    else if (!player0Unit.getMoveX()) //Si no se esta moviendo se queda en la animacion de idle
                        player0Unit.setSpriteAnimation(0, "Left");
                }
            }
        }

        for (Unit player1Unit : player1Units) { //Lo mismo que lo anterior pero simetrico para el otro jugador
            if (player1Unit != null) {
                if (player1Unit.getMoveX() && player1Unit.getGroup().getX() < player1Unit.getNextX()) {
                    player1Unit.getGroup().setX(player1Unit.getGroup().getX() + player1Unit.getVelocity());
                    if (player1Unit.getGroup().getX() > player1Unit.getNextX() - player1Unit.getVelocity()) {
                        player1Unit.setMoveX(false);
                        checkDirection(player1Unit);
                        if (!player1Unit.getMoveX() && !player1Unit.getMoveY())
                            player1Unit.setSpriteAnimation(0, "Right");
                    }
                } else if (player1Unit.getMoveX() && player1Unit.getGroup().getX() > player1Unit.getNextX()) {
                    player1Unit.getGroup().setX(player1Unit.getGroup().getX() - player1Unit.getVelocity());
                    if (player1Unit.getGroup().getX() < player1Unit.getNextX() + player1Unit.getVelocity()) {
                        player1Unit.setMoveX(false);
                        checkDirection(player1Unit);
                        if (!player1Unit.getMoveX() && !player1Unit.getMoveY())
                            player1Unit.setSpriteAnimation(0, "Left");
                    }
                } else {
                    player1Unit.setMoveX(false);
                }

                if (player1Unit.getMoveY() && player1Unit.getGroup().getY() < player1Unit.getNextY()) {
                    player1Unit.getGroup().setY(player1Unit.getGroup().getY() + player1Unit.getVelocity());
                    if (player1Unit.getGroup().getY() > player1Unit.getNextY() - player1Unit.getVelocity()) {
                        player1Unit.setMoveY(false);
                        checkDirection(player1Unit);
                        if (!player1Unit.getMoveX() && !player1Unit.getMoveX())
                            player1Unit.setSpriteAnimation(0, "Right");
                    }
                } else if (player1Unit.getMoveY() && player1Unit.getGroup().getY() > player1Unit.getNextY()) {
                    player1Unit.getGroup().setY(player1Unit.getGroup().getY() - player1Unit.getVelocity());
                    if (player1Unit.getGroup().getY() < player1Unit.getNextY() + player1Unit.getVelocity()) {
                        player1Unit.setMoveY(false);
                        checkDirection(player1Unit);
                        if (!player1Unit.getMoveX() && !player1Unit.getMoveX())
                            player1Unit.setSpriteAnimation(0, "Right");
                    }
                } else {
                    player1Unit.setMoveY(false);
                    if (player1Unit.getObjectiveUnit() != null)
                        player1Unit.setSpriteAnimation(2, "Right");
                    else if (!player1Unit.getMoveX())
                        player1Unit.setSpriteAnimation(0, "Right");
                }
            }
        }
    }

    /*private void rotUnits() {
        for (int i = 0; i < player0Units.length; i++) {
            if (player0Units[i] != null && player0Units[i].getRot()) {
                if (player0Units[i].getNextRot() > 0) {
                    player0Units[i].getGroup().setRotation(player0Units[i].getGroup().getRotation() + Math.toRadians(player0Units[i].getVelocity()));
                    player0Units[i].setNextRot(player0Units[i].getNextRot() - Math.toRadians(player0Units[i].getVelocity()));
                    if (player0Units[i].getNextRot() < Math.toRadians(5) && player0Units[i].getNextRot() > Math.toRadians(-5))
                        player0Units[i].setRot(false);
                } else if (player0Units[i].getNextRot() < 0) {
                    player0Units[i].getGroup().setRotation(player0Units[i].getGroup().getRotation() - Math.toRadians(player0Units[i].getVelocity()));
                    player0Units[i].setNextRot(player0Units[i].getNextRot() + Math.toRadians(player0Units[i].getVelocity()));
                    if (player0Units[i].getNextRot() < Math.toRadians(5) && player0Units[i].getNextRot() > Math.toRadians(-5))
                        player0Units[i].setRot(false);
                }
            }
        }

        for (int i = 0; i < player1Units.length; i++) {
            if (player1Units[i] != null && player1Units[i].getRot()) {
                if (player1Units[i].getNextRot() > 0) {
                    player1Units[i].getGroup().setRotation(player1Units[i].getGroup().getRotation() + Math.toRadians(player1Units[i].getVelocity()));
                    player1Units[i].setNextRot(player1Units[i].getNextRot() - Math.toRadians(player1Units[i].getVelocity()));
                    if (player1Units[i].getNextRot() < Math.toRadians(5) && player1Units[i].getNextRot() > Math.toRadians(-5))
                        player1Units[i].setRot(false);
                } else if (player1Units[i].getNextRot() < 0) {
                    player1Units[i].getGroup().setRotation(player1Units[i].getGroup().getRotation() - Math.toRadians(player1Units[i].getVelocity()));
                    player1Units[i].setNextRot(player1Units[i].getNextRot() + Math.toRadians(player1Units[i].getVelocity()));
                    if (player1Units[i].getNextRot() < Math.toRadians(5) && player1Units[i].getNextRot() > Math.toRadians(-5))
                        player1Units[i].setRot(false);
                }
            }
        }
    }*/

    private void checkDirection(Unit unit) { //Comprueba la dirección en la que esta mirando cada unidad
        int vertical = 0; //1 Norte, 0 Centro y -1 Sur
        int horizontal = 0; //1 Este, 0 Centro y -1 Oeste

        if (unit.getMoveX() && unit.getNextX() > unit.getGroup().getX())
            horizontal = 1;
        else if (unit.getMoveX() && unit.getNextX() < unit.getGroup().getX())
            horizontal = -1;

        if (unit.getMoveY() && unit.getNextY() > unit.getGroup().getY())
            vertical = -1;
        else if (unit.getMoveY() && unit.getNextY() < unit.getGroup().getY())
            vertical = 1;

        if (horizontal == 1 && vertical == 1)
            unit.setFaceDirection(Direction.NORTHEAST);
        else if (horizontal == 1 && vertical == 0)
            unit.setFaceDirection(Direction.EAST);
        else if (horizontal == 1)
            unit.setFaceDirection(Direction.SOUTHEAST);
        else if (horizontal == 0 && vertical == -1)
            unit.setFaceDirection(Direction.SOUTH);
        else if (horizontal == -1 && vertical == -1)
            unit.setFaceDirection(Direction.SOUTHWEST);
        else if (horizontal == -1 && vertical == 0)
            unit.setFaceDirection(Direction.WEST);
        else if (horizontal == -1)
            unit.setFaceDirection(Direction.NORTHWEST);
        else if (vertical == 1)
            unit.setFaceDirection(Direction.NORTH);
    }

    private boolean isIntersect(Unit unit1, Unit unit2) { //Comprueba si dos unidades estan colisionando
        int Ax = unit1.getGroup().getX();
        int Ay = unit1.getGroup().getY();
        int Aw = size;
        int Ah = size;
        int Bx = unit2.getGroup().getX();
        int By = unit2.getGroup().getY();
        int Bw = size;
        int Bh = size;
        return Bx + Bw > Ax && By + Bh > Ay && Ax + Aw > Bx && Ay + Ah > By;
    }

    private void attackUnits() { //Mira que unidades estan luchando
        for (Unit player0Unit : player0Units) {
            if (player0Unit != null) {
                for (Unit player1Unit : player1Units) {
                    if (player1Unit != null && isIntersect(player0Unit, player1Unit)) {
                        fight(player0Unit, player1Unit);
                    }
                }
            }
        }
    }

    private void fight(Unit unit1, Unit unit2) { //Ejecuta las luchas y sus comprobaciones
        if (unit1.getObjectiveUnit() == null) { //Si la unidad 1 no tiene objetivo, su objetivo se convierte en la unidad con quien esta en contacto
            unit1.setObjectiveUnit(unit2);
            if (unit1.getMoveX() || unit1.getMoveY()) { //Si esta en movimiento
                int life = unit1.getChargeForce() - (unit2.getChargeResistance() / charge(unit1, unit2)); //Se ejecuta la carga
                if (life < 0) //Evita que una unidad con mucha resistencia de carga, en vez de restar, le sume vida
                    life = 0;
                unit2.setLife(unit2.getLife() - life); //Se cambia la vida de la unidad objetivo
            }
        }

        if (unit2.getObjectiveUnit() == null) { //Lo mismo pero para la segunda unidad
            unit2.setObjectiveUnit(unit1);
            if (unit2.getMoveX() || unit2.getMoveY()) {
                int life = unit2.getChargeForce() - (unit1.getChargeResistance() / charge(unit2, unit1));
                if (life < 0)
                    life = 0;
                unit1.setLife(unit1.getLife() - life);
            }
        }

        //Para las unidades
        unit1.setMoveX(false);
        unit1.setMoveY(false);
        unit2.setMoveX(false);
        unit2.setMoveY(false);

        if (unit1.getObjectiveUnit() == unit2) { //La unidad 1 le hace daño a la 2
            int life = unit1.getAttack() - unit2.getDefense() / 2; //Ajusta el daño final
            if (life < 0)
                life = 0;
            unit2.setLife(unit2.getLife() - life);
            unit2.getTextLife().setText(String.valueOf(unit2.getLife())); //Cambia el texto
        }

        if (unit2.getObjectiveUnit() == unit1) { //Lo mismo pero a la inversa
            int life = unit2.getAttack() - unit1.getDefense() / 2;
            if (life < 0)
                life = 0;
            unit1.setLife(unit1.getLife() - life);
            unit1.getTextLife().setText(String.valueOf(unit1.getLife()));
        }

        if (unit1.getLife() <= 0 && unit2.getObjectiveUnit() == unit1) { //Si la unidad objetivo muere
            unit2.setObjectiveUnit(null); //Deja de ser objetivo
        }

        if (unit2.getLife() <= 0 && unit1.getObjectiveUnit() == unit2) { //Lo mismo pero con las unidades cambiadas
            unit1.setObjectiveUnit(null);
        }
    }

    private int charge(Unit unit1, Unit unit2) { //Comrpueba si la carga es frontal o no
        if ((unit1.getFaceDirection() == Direction.NORTH && (unit2.getFaceDirection() == Direction.SOUTH || unit2.getFaceDirection() == Direction.SOUTHWEST || unit2.getFaceDirection() == Direction.SOUTHEAST)) || (unit2.getFaceDirection() == Direction.NORTH && (unit1.getFaceDirection() == Direction.SOUTH || unit1.getFaceDirection() == Direction.SOUTHWEST || unit1.getFaceDirection() == Direction.SOUTHEAST))
            || (unit1.getFaceDirection() == Direction.EAST && (unit2.getFaceDirection() == Direction.WEST || unit2.getFaceDirection() == Direction.SOUTHWEST || unit2.getFaceDirection() == Direction.NORTHWEST)) || (unit1.getFaceDirection() == Direction.WEST && (unit2.getFaceDirection() == Direction.EAST || unit1.getFaceDirection() == Direction.SOUTHEAST || unit1.getFaceDirection() == Direction.NORTHEAST))
            || (unit1.getFaceDirection() == Direction.NORTHEAST && (unit2.getFaceDirection() == Direction.SOUTHWEST || unit2.getFaceDirection() == Direction.SOUTH || unit2.getFaceDirection() == Direction.WEST)) || (unit2.getFaceDirection() == Direction.NORTHEAST && (unit1.getFaceDirection() == Direction.SOUTHWEST || unit1.getFaceDirection() == Direction.WEST || unit1.getFaceDirection() == Direction.SOUTH))
            || (unit1.getFaceDirection() == Direction.SOUTHEAST && (unit2.getFaceDirection() == Direction.NORTHWEST || unit2.getFaceDirection() == Direction.WEST || unit2.getFaceDirection() == Direction.NORTH)) || (unit2.getFaceDirection() == Direction.SOUTHEAST && (unit1.getFaceDirection() == Direction.NORTHWEST || unit1.getFaceDirection() == Direction.WEST || unit1.getFaceDirection() == Direction.NORTH)))
            return 1; //Carga de cara
        else
            return 3; //Carga trasera o lateral, ejerce mas daño
    }

    private void farAttack() { //Ataque a distancia de los arqueros
        for (Unit player1Unit : player1Units) { //Quita el efecto de ser atacado por arqueros a todas las unidades del jugador 1
            if (player1Unit != null) {
                player1Unit.getArrows().setVisible(false);
            }
        }

        for (Unit player0Unit : player0Units) {
            if (player0Unit != null) { //Quita el efecto de ser atacado por arqueros a todas las unidades del jugador 0
                player0Unit.getArrows().setVisible(false);
            }
            if (player0Unit != null && player0Unit.getType() == Type.BOW) { //Si la unidad es un arquero
                if (!player0Unit.getMoveY() && !player0Unit.getMoveX() && player0Unit.getObjectiveUnit() == null) { //Si no esta luchando cuerpo a cuerpo
                    for (Unit player1Unit : player1Units) { //Para cada unidad del otro jugador
                        if (player1Unit != null && calculateDistance(player0Unit, player1Unit) < player0Unit.getAttackDistance() && calculateDistance(player0Unit, player1Unit) > -player0Unit.getAttackDistance()) { //Calcula si esta a distancia de tiro
                            int life = player0Unit.getFarAttack() - (player1Unit.getFarResistance() / 2); //Daño final
                            if (life < 0)
                                life = 0;
                            player1Unit.setLife(player1Unit.getLife() - life);
                            player1Unit.getArrows().setVisible(true); //Pone el efecto de daño por arqueros en la unidad atacada
                            player1Unit.getTextLife().setText(String.valueOf(player1Unit.getLife()));
                            player0Unit.setSpriteAnimation(2, "Left"); //Establece animacion de ataque
                            if (player1Unit.getObjectiveUnit() != null && calculateDistance(player1Unit.getObjectiveUnit(), player0Unit) < calculateDistance(player0Unit, player1Unit)) { //Si la unidad objetivo esta luchando con una unidad aliada
                                life = player0Unit.getFarAttack() - (player1Unit.getObjectiveUnit().getFarResistance()); //La unidad aliada también recibe daño de los arqueros, aunque menos que la enemiga
                                if (life < 0)
                                    life = 0;
                                player1Unit.getObjectiveUnit().setLife(player1Unit.getObjectiveUnit().getLife() - life);
                                player1Unit.getObjectiveUnit().getTextLife().setText(String.valueOf(player1Unit.getObjectiveUnit().getLife()));
                            }
                            break;
                        }
                    }
                }
            }
        }

        for (Unit player1Unit : player1Units) { //Lo mismo pero con las tropas del otro jugador
            if (player1Unit != null && player1Unit.getType() == Type.BOW) {
                if (!player1Unit.getMoveY() && !player1Unit.getMoveX() && player1Unit.getObjectiveUnit() == null) {
                    for (Unit player0Unit : player0Units) {
                        if (player0Unit != null && calculateDistance(player1Unit, player0Unit) < player1Unit.getAttackDistance() && calculateDistance(player1Unit, player0Unit) > -player1Unit.getAttackDistance()) {
                            int life = player1Unit.getFarAttack() - (player0Unit.getFarResistance() / 2);
                            if (life < 0)
                                life = 0;
                            player0Unit.setLife(player0Unit.getLife() - life);
                            player0Unit.getArrows().setVisible(true);
                            player0Unit.getTextLife().setText(String.valueOf(player0Unit.getLife()));
                            player1Unit.setSpriteAnimation(2, "Right");
                            if (player0Unit.getObjectiveUnit() != null && calculateDistance(player0Unit.getObjectiveUnit(), player1Unit) < calculateDistance(player1Unit, player0Unit)) {
                                life = player1Unit.getFarAttack() - (player0Unit.getObjectiveUnit().getFarResistance());
                                if (life < 0)
                                    life = 0;
                                player0Unit.getObjectiveUnit().setLife(player0Unit.getObjectiveUnit().getLife() - life);
                                player0Unit.getObjectiveUnit().getTextLife().setText(String.valueOf(player0Unit.getObjectiveUnit().getLife()));
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void generalInfluence() { //Circulo de influencia que ejercen los generales
        for (int i = 1; i < player0Units.length; i++) { //Para cada unidad que no sea el general
            if (player0Units[0] != null && player0Units[i] != null) { //Si el general sigue vivo
                if (!player0Units[i].getBuffed() && calculateDistance(player0Units[0], player0Units[i]) < generalInfluenceArea && calculateDistance(player0Units[0], player0Units[i]) > -generalInfluenceArea) { //Si se encuentra en el area de influencia y no estaba en ella antes
                    player0Units[i].setBuffed(true);
                    player0Units[i].changeStats(1.25f); //Se aumentan sus stats
                    player0Units[i].getBuffSprite().setVisible(true);
                } else if (player0Units[i].getBuffed() && (calculateDistance(player0Units[0], player0Units[i]) >= generalInfluenceArea || calculateDistance(player0Units[0], player0Units[i]) <= -generalInfluenceArea)) { //Si se encuentra fuera del area de influencia
                    player0Units[i].setBuffed(false);
                    player0Units[i].restoreStats(); //Se le devuelven los stats normales
                    player0Units[i].getBuffSprite().setVisible(false);
                }
            }
        }

        for (int i = 1; i < player1Units.length; i++) { //Lo mismo para las tropas del otro jugador
            if (player1Units[0] != null && player1Units[i] != null) {
                if (!player1Units[i].getBuffed() && calculateDistance(player1Units[0], player1Units[i]) < generalInfluenceArea && calculateDistance(player1Units[0], player1Units[i]) > -generalInfluenceArea) {
                    player1Units[i].setBuffed(true);
                    player1Units[i].changeStats(1.25f);
                    player1Units[i].getBuffSprite().setVisible(true);
                } else if (player1Units[i].getBuffed() && (calculateDistance(player1Units[0], player1Units[i]) >= generalInfluenceArea || calculateDistance(player1Units[0], player1Units[i]) <= -generalInfluenceArea)) {
                    player1Units[i].setBuffed(false);
                    player1Units[i].restoreStats();
                    player1Units[i].getBuffSprite().setVisible(false);
                }
            }
        }
    }

    public double calculateDistance(Unit unit1, Unit unit2) { //Calcula la distancia entre dos unidades
        double x1 = unit1.getGroup().getX();
        double y1 = unit1.getGroup().getY();
        double x2 = unit2.getGroup().getX();
        double y2 = unit2.getGroup().getY();
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private void destroyUnits() { //Destruye las unidades muertas
        boolean deadGeneral = false; //Para comprobar si ha muerto el general en este turno

        for (int i = 0; i < player0Units.length; i++) {
            if (deadGeneral && player0Units[i] != null) { //Si el general ha muerto
                player0Units[i].restoreStats(); //Normaliza las stats
                player0Units[i].getBuffSprite().setVisible(false);
                player0Units[i].setBuffed(false);
                player0Units[i].changeStats(0.75f); //Reduce las stats de las tropas
            }

            if (player0Units[i] != null) { //Si la unidad aun existia
                if (player0Units[i].getLife() <= 0) { //Si ha muerto
                    for (Unit player1Unit : player1Units) { //Todas las unidades que le estaban haciendo objetivo le dejan de hacerlo
                        if (player1Unit != null && player1Unit.getObjectiveUnit() == player0Units[i]) {
                            player1Unit.setObjectiveUnit(null);
                        }
                    }
                    player0Units[i].setSpriteAnimation(3, "Left"); //Animacion de muerte
                    player0Units[i].getArrows().setLoop(false);
                    player0Units[i].getGroup().setAlpha(0.3); //La hace un poco transparente
                    player0Units[i].getTextLife().setText("0"); //Le deja la vida a 0 para que no aparezcan valores negativos
                    player0Units[i] = null; //La hace dejar de existir
                    if (i == 0 && gameManager.getLeagueLevel() >= 3) //Si es la unidad del general
                        deadGeneral = true; //Marcas que el general ha muerto este turno
                }
            }
        }

        deadGeneral = false;
        for (int i = 0; i < player1Units.length; i++) { //Lo mismo para las otras tropas
            if (deadGeneral && player1Units[i] != null) {
                player1Units[i].restoreStats();
                player1Units[i].getBuffSprite().setVisible(false);
                player1Units[i].setBuffed(false);
                player1Units[i].changeStats(0.75f);
            }
            if (player1Units[i] != null) {
                if (player1Units[i].getLife() <= 0) {
                    for (Unit player0Unit : player0Units) {
                        if (player0Unit != null && player0Unit.getObjectiveUnit() == player1Units[i]) {
                            player0Unit.setObjectiveUnit(null);
                        }
                    }
                    player1Units[i].setSpriteAnimation(3, "Right");
                    player1Units[i].getArrows().setLoop(false);
                    player1Units[i].getGroup().setAlpha(0.3);
                    player1Units[i].getTextLife().setText("0");
                    player1Units[i] = null;
                    if (i == 0 && gameManager.getLeagueLevel() >= 3)
                        deadGeneral = true;
                }
            }
        }
    }

    private void finishGame(int turn) { //Comrpueba si ha terminado la partida
        boolean dead = true;
        for (Unit player0Unit : player0Units) {
            if (player0Unit != null) {
                dead = false;
                break;
            }
        }

        if (dead)
            gameManager.getPlayer(0).setScore(0); //El jugador 0 ha muerto

        dead = true;
        for (Unit player1Unit : player1Units) {
            if (player1Unit != null) {
                dead = false;
                break;
            }
        }

        if (dead)
            gameManager.getPlayer(1).setScore(0); //El jugador 1 ha muerto

        if (gameManager.getPlayer(0).getScore() == 0 || gameManager.getPlayer(1).getScore() == 0 || turn == 400) { //Si alguno de los dos jugadores ha muerto o se ha superado el límite máximo de turnos
            endGame();
        }
    }

    private void endGame() { //Función del final del jueog
        gameManager.endGame();

        Player p0 = gameManager.getPlayers().get(0);
        Player p1 = gameManager.getPlayers().get(1);

        if (p0.getScore() == -1) { //Si la partida ha terminado por acción invalida a favor del jugador 0
            p1.setScore(1);
            p0.hud.setAlpha(0.3);
            return;
        } else if (p1.getScore() == -1) { //Si la partida ha terminado por acción invalida a favor del jugador 1
            p0.setScore(1);
            p1.hud.setAlpha(0.3);
            return;
        }

        if (p0.getScore() != 0 && p1.getScore() != 0) { //Si la partida ha terminado por turnos
            int n1 = 0, n2 = 0;
            for (int i = 0; i < player0Units.length; i++) { //Comprueba que unidad tiene más tropas vivas
                if (player0Units[i] != null)
                    n1++;

                if (player1Units[i] != null) {
                    n2++;
                }
            }

            if (n1 > n2) { //Gana el jugador 0
                p0.setScore(1);
                p1.setScore(0);
            } else if (n2 > n1) { //Gana el jugador 1
                p0.setScore(0);
                p1.setScore(1);
            } else { //Empatan
                p0.setScore(0);
                p1.setScore(0);
                System.out.println("It is a draw!");
            }
        }

        if (p0.getScore() == 0) { //Cuando pierde el jugador 0
            p0.hud.setAlpha(0.3);
            if (p1.getScore() != 0) {
                System.out.println(p1.getNicknameToken() + " has won the match!");
            }
        }

        if (p1.getScore() == 0) { //Cuando pierde el jugador 1
            p1.hud.setAlpha(0.3);
            if (p0.getScore() != 0) {
                System.out.println(p0.getNicknameToken() + " has won the match!");
            }
        }

    }

    public SpriteAnimation idleAnimation(Unit unit, String direct) { //Inicializa las animaciones de idle
        SpriteAnimation spriteAnimation;
        if (direct.equals("Left")) {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getLeft()[0])
                    .setX(0)
                    .setY(25)
                    .setDuration(800)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(true)
                    .setPlaying(true);

        } else {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getRight()[0])
                    .setX(0)
                    .setY(25)
                    .setDuration(800)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(true)
                    .setPlaying(true);
        }
        if (unit.getType() == Type.HORSE)
            spriteAnimation.setY(spriteAnimation.getY() - 25);
        return spriteAnimation;
    }

    public SpriteAnimation runAnimation(Unit unit, String direct) { //Inicializa las animaciones de correr
        SpriteAnimation spriteAnimation;
        if (direct.equals("Left")) {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getLeft()[1])
                    .setX(0)
                    .setY(25)
                    .setDuration(1000)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(true)
                    .setPlaying(true);
        } else {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getRight()[1])
                    .setX(0)
                    .setY(25)
                    .setDuration(1000)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(true)
                    .setPlaying(true);
        }
        if (unit.getType() == Type.HORSE)
            spriteAnimation.setY(spriteAnimation.getY() - 25);
        return spriteAnimation;
    }

    public SpriteAnimation attackAnimation(Unit unit, String direct) { //Inicializa las animaciones de atacar
        SpriteAnimation spriteAnimation;
        if (direct.equals("Left")) {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getLeft()[2])
                    .setX(0)
                    .setY(25)
                    .setDuration(900)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(true)
                    .setPlaying(true);
        } else {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getRight()[2])
                    .setX(0)
                    .setY(25)
                    .setDuration(900)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(true)
                    .setPlaying(true);
        }
        if (unit.getType() == Type.HORSE)
            spriteAnimation.setY(spriteAnimation.getY() - 25);
        return spriteAnimation;
    }

    public SpriteAnimation deathAnimation(Unit unit, String direct) { //Inicializa las animaciones de morir
        SpriteAnimation spriteAnimation;
        if (direct.equals("Left")) {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getLeft()[3])
                    .setX(0)
                    .setY(25)
                    .setDuration(1200)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(false)
                    .setPlaying(false);
        } else {
            spriteAnimation = graphicEntityModule.createSpriteAnimation()
                    .setImages(unit.getRight()[3])
                    .setX(0)
                    .setY(25)
                    .setDuration(1200)
                    .setScale(0.4)
                    .setVisible(false)
                    .setLoop(false)
                    .setPlaying(false);
        }
        return spriteAnimation;
    }

    public SpriteAnimation arrowAnimation(String[] upArrow) { //Inicializa las animaciones de los bufos por la influencia del general
        SpriteAnimation spriteAnimation = graphicEntityModule.createSpriteAnimation()
                .setImages(upArrow)
                .setX(size + 15)
                .setY(-10)
                .setDuration(900)
                .setScale(0.2)
                .setVisible(false)
                .setLoop(true)
                .setPlaying(true);

        if (gameManager.getLeagueLevel() != 3)
            spriteAnimation.setX(size - 65);

        return spriteAnimation;
    }

    public SpriteAnimation arrowsAttackAnimation(String[] arrowsAttack, String direction) { //Inicializa las animaciones del ataque recibido por los arqueros
        SpriteAnimation spriteAnimation = graphicEntityModule.createSpriteAnimation()
                .setImages(arrowsAttack)
                .setX(-10)
                .setY(20)
                .setDuration(800)
                .setVisible(false)
                .setLoop(true)
                .setPlaying(true)
                .setScale(0.5);

        if (direction.equals("Left")) {
            spriteAnimation.setX(-20);
            spriteAnimation.setY(18);
        }

        return spriteAnimation;
    }
}