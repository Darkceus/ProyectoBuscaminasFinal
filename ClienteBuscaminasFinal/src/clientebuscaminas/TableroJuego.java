package clientebuscaminas;

/**
 *
 * @author DARKCEUS
 */
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import javax.swing.*;

public class TableroJuego extends JPanel {

    private ImageIcon[] IMAGENES;
    private final int FILAS;
    private final int COLUMNAS;
    private final int TAM_ALTO;
    private final int TAM_ANCHO;
    private final int NUMERO_MINAS;
    private int minasRes;
    private int minutos = 0;
    private int segundos = 0;
    private String minutos2 = "";
    private String segundos2 = "";
    private Thread t;
    private final Boton[][] BOTONES;
    private final JLabel texto;
    private final JLabel tiempo;
    private boolean inicio;
    private int NUM_JUGADOR;
    private final PrintWriter out;
    private int ADMIN;

    public TableroJuego() {
        this.texto = null;
        this.tiempo = null;
        this.out = null;
        this.FILAS = 0;
        this.COLUMNAS = 0;
        this.TAM_ALTO = 0;
        this.TAM_ANCHO = 0;
        this.NUMERO_MINAS = 0;
        this.NUM_JUGADOR = 0;
        this.ADMIN = 0;
        this.BOTONES = null;
        this.IMAGENES = null;
    }

    public TableroJuego(JLabel texto, JLabel tiempo, PrintWriter out, int filas, int columnas, int AltoCampo, int AnchoCampo, int NumMinas, int NumJugador, int NumAdmin) {
        this.FILAS = filas;
        this.COLUMNAS = columnas;
        this.TAM_ALTO = AltoCampo;
        this.TAM_ANCHO = AnchoCampo;
        this.NUMERO_MINAS = NumMinas;
        this.NUM_JUGADOR = NumJugador;
        this.ADMIN = NumAdmin;
        this.minasRes = NUMERO_MINAS;
        this.out = out;
        this.texto = texto;
        this.texto.setText("Banderas Restantes: " + minasRes);
        this.tiempo = tiempo;
        this.setLayout(new GridLayout(FILAS, COLUMNAS));
        crearImagenes(Constantes.NUM_IMAGENES);
        cargarImagenes();
        this.BOTONES = new Boton[FILAS][COLUMNAS];
        crearBotones();
        inicio = true;
        iniciarTiempo();
    }
    
    private void crearBotones() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                BOTONES[x][y] = new Boton(x, y);
                BOTONES[x][y].setIcon(cargarImagen(Constantes.BASE_2));
                BOTONES[x][y].setPreferredSize(new Dimension(TAM_ANCHO, TAM_ALTO));
                BOTONES[x][y].addMouseListener(new EventoClic());
                this.add(BOTONES[x][y]);
                BOTONES[x][y].setEnabled(true);
            }
        }
    }

    private void iniciarTiempo() {
        minutos = 0;
        segundos = 0;
        tiempo.setText("Tiempo: 00:00");
        minutos2 = "";
        segundos2 = "";
        t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        segundos++;
                        if (segundos == 60) {
                            segundos = 0;
                            minutos++;
                        }
                        minutos2 = (minutos < 10) ? ("0" + minutos) : ("" + minutos);
                        segundos2 = (segundos < 10) ? ("0" + segundos) : ("" + segundos);
                        tiempo.setText("Tiempo: " + minutos2 + ":" + segundos2);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        t.start();
    }
    
    private void crearImagenes(Constantes valor){
        this.IMAGENES = new ImageIcon[valor.getValor()];
    }

    private void cargarImagenes() {
        int numImagenes = Constantes.NUM_IMAGENES.getValor();
        for (int i = 0; i < numImagenes; i++) {
            String ruta = "/imagenes/" + i + ".png";
            IMAGENES[i] = new ImageIcon((new ImageIcon(this.getClass().getResource(ruta))).getImage().getScaledInstance(TAM_ANCHO, TAM_ALTO, java.awt.Image.SCALE_DEFAULT));
        }
    }
    
    public void parar(){
        this.t.stop();
    }

    public void ponerBandera(int x, int y, int numJugador) {
        BOTONES[x][y].setIcon(obtenerIcono(numJugador));
        if (numJugador == NUM_JUGADOR) {
            minasRes--;
            texto.setText("Banderas Restantes: " + minasRes);
        }
    }

    public void actualizarDatos(int numJugador, int numAdmin, boolean perdio) {
        if (this.NUM_JUGADOR != numJugador) {
            this.NUM_JUGADOR = numJugador;
        }
        if (perdio && inicio) {
            this.inicio = false;
            t.stop();
        } else if (!perdio && !inicio) {
            this.inicio = true;
            t.start();
        }
        if (this.ADMIN != numAdmin) {
            this.ADMIN = numAdmin;
        }
    }
    
    public void aumentarBanderas() {
        minasRes++;
        texto.setText("Banderas Restantes: " + minasRes);
    }
    
    private ImageIcon cargarImagen(Constantes imagen){
        return IMAGENES[imagen.getValor()];
    }
    
    private ImageIcon getImagen(int valor){
        if(valor >= Constantes.BASE_1.getValor() && valor < Constantes.MINA.getValor()){
            return IMAGENES[valor];
        }
        return IMAGENES[Constantes.BASE_2.getValor()];
    }

    public void quitarBandera(int x, int y, int numJugador) {
        BOTONES[x][y].setIcon(cargarImagen(Constantes.BASE_2));
        if (numJugador == NUM_JUGADOR) {
            minasRes++;
            texto.setText("Banderas Restantes: " + minasRes);
        }
    }

    public void colocarMina(int x, int y, int numJugador) {
        BOTONES[x][y].setIcon(cargarImagen(Constantes.MINA));
        if (numJugador == NUM_JUGADOR) {
            inicio = false;
            JOptionPane.showMessageDialog(this, "Perdiste");
        }
    }

    public void ponerNoMina(int x, int y) {
        BOTONES[x][y].setIcon(cargarImagen(Constantes.NO_MINA));
    }

    public void colocarMina2(int x, int y) {
        BOTONES[x][y].setIcon(cargarImagen(Constantes.MINA));
    }

    public void descubrirCampo(int x, int y, int valorCampo) {
            BOTONES[x][y].setIcon(getImagen(valorCampo));
    }

    public void descubrirCampo2(int x, int y) {
        BOTONES[x][y].setIcon(cargarImagen(Constantes.BASE_1));
    }

    private Icon obtenerIcono(int valor) {
        switch (valor) {
            case 1:
                return cargarImagen(Constantes.BANDERA_J1);
            case 2:
                return cargarImagen(Constantes.BANDERA_J2);
            case 3:
                return cargarImagen(Constantes.BANDERA_J3);
            case 4:
                return cargarImagen(Constantes.BANDERA_J4);
        }
        return null;
    }

    public void cerrarJuego() {
        JFrame frame = (JFrame) this.getTopLevelAncestor();
        frame.dispose();
    }

    private class EventoClic extends MouseAdapter {

        public EventoClic() {
            super();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (inicio) {
                Boton boton = (Boton) e.getSource();
                String clic = "CLIC";
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clic += "IZQUIERDO ";
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    clic += "DERECHO ";
                }
                clic += boton.getX2() + "," + boton.getY2() + "," + minasRes;
                out.println(clic);
            } else {
                JOptionPane.showMessageDialog(null, "Ya perdiste");
            }
        }
    }
}