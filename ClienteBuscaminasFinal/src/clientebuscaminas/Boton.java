package clientebuscaminas;

import javax.swing.JButton;

/**
 *
 * @author DARKCEUS
 */
public class Boton extends JButton {

    private final int X;
    private final int Y;

    public Boton(int x, int y) {
        super();
        this.X = x;
        this.Y = y;
    }

    public int getX2() {
        return this.X;
    }

    public int getY2() {
        return this.Y;
    }
}
