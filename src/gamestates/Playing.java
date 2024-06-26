package gamestates;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.GameOverOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static utilz.Constants.Environment.*;

public class Playing extends State implements  Statemethods{
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private PauseOverlay pauseOverlay ;
    private GameOverOverlay gameOverOverlay;
    private boolean paused = false;
    private int xLvlOffset;
    private int yLvlOffset;
    private int leftBorder = (int)(0.5 * Game.GAME_WIDTH);
    private int rightBorder = (int)(0.5 * Game.GAME_WIDTH);
    private int lvlTilesWide = LoadSave.GetLevelData()[0].length;
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;
    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private int[] smallCloudsPos;
    private Random rnd = new Random();
    private boolean gameOver;



    public Playing(Game game) {
        super(game);
        innitClasses();

        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud= LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos= new int[150];// render lenght for the clouds texture sheet/ aka it repeats 100 times
        for (int i = 0; i <smallCloudsPos.length; i++){
            smallCloudsPos[i] = (int)(95*Game.SCALE) + rnd.nextInt((int)(195* Game.SCALE));// something between 90 and 150
        }
    }

    private void innitClasses() {
        levelManager= new LevelManager(game);
        enemyManager = new EnemyManager(this);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE),this);//spawn point(on screen)
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        pauseOverlay =new PauseOverlay(this);
        gameOverOverlay=new GameOverOverlay(this);

    }

    public void unpauseGame(){
        paused = false;
    }

    public void windowFocusLost(){
        player.resetDirBooleans();
    }




    public Player getPlayer(){
        return player;
    }

    @Override
    public void update() {
        if(!paused && !gameOver){
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            checkCloseToBorder();
        }else{
            pauseOverlay.update();
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int)(player.getHitbox().x);
        int diff = playerX - xLvlOffset;

        if(diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if(diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if(xLvlOffset > maxLvlOffsetX)
            xLvlOffset=maxLvlOffsetX;
        else if (xLvlOffset <0)
            xLvlOffset = 0;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0,0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);
        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);

        if(paused) {
            g.setColor(new Color(0,0,0,100));//darken the background when paused
            g.fillRect(0,0, Game.GAME_WIDTH, Game.GAME_HEIGHT);// fills the darkening to the screen size
            pauseOverlay.draw(g);
        }else if(gameOver)
            gameOverOverlay.draw(g);

    }

    private void drawClouds(Graphics g) {
        for (int i = 0 ; i <100; i++)// clouds render lenght
            g.drawImage(bigCloud, 0 + i * BIG_CLOUD_WIDTH -(int)(xLvlOffset*0.3),(int)(204*Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT,null);
        for(int i = 0; i < smallCloudsPos.length; i ++) {
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH*1*i   -(int)(xLvlOffset*0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
        }
        // -(int)(xLvlOffset*0.3) in our code above, gives illusion of depth in out background sky!


    }
    public void resetAll(){
        /* TODO: reset playing and stuff */
        gameOver = false;
        paused=false;
        player.resetAll();
        enemyManager.resetAllEnemies();

    }
    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }
    public void checkEnemyHit(Rectangle2D.Float attackBox){
        enemyManager.checkEnemyHit(attackBox);



    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver)
            if(e.getButton()==MouseEvent.BUTTON1)
            player.setAttacking(true);

    }

    public void mouseDragged(MouseEvent e){
        if(!gameOver)
            if (paused)
            pauseOverlay.mouseDragged(e);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        if(!gameOver)
            if(paused )
            pauseOverlay.mousePressed(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!gameOver)
            if(paused )
            pauseOverlay.mouseReleased(e);

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!gameOver)
            if(paused )
            pauseOverlay.mouseMoved(e);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameOver)
            gameOverOverlay.keyPressed(e);
        else
            switch (e.getKeyCode()){

            case KeyEvent.VK_W:
                player.setUp(true);
                break;
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_S:
                player.setDown(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_BACK_SPACE:
                Gamestate.state = Gamestate.MENU;
                    break;
            case KeyEvent.VK_ESCAPE:
                paused= !paused;
                break;


        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!gameOver)
            switch (e.getKeyCode()){

            case KeyEvent.VK_W:
                player.setUp(false);
                break;
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_S:
                player.setDown(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);
                break;

        }

    }
}
