package inputs;

import gamestates.Gamestate;
import main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static utilz.Constants.Directions.*;

public class KeyboardInputs  implements KeyListener {
    private GamePanel gamePanel;
    public KeyboardInputs(GamePanel gamePanel){
        this.gamePanel = gamePanel;

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    public void keyReleased(KeyEvent e) {
        switch (Gamestate.state){
            case PLAYING :
                gamePanel.getGame().getPlaying().keyReleased(e);
                break;
            case MENU :
                gamePanel.getGame().getMenu().keyReleased(e);
                break;
        }

    }



    public void keyPressed(KeyEvent e) {
        switch (Gamestate.state){
            case PLAYING :
                gamePanel.getGame().getPlaying().keyPressed(e);
                break;
            case MENU :
                gamePanel.getGame().getMenu().keyPressed(e);
                break;
        }
    }

}
