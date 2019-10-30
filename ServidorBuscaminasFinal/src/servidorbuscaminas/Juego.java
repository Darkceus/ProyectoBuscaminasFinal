package servidorbuscaminas;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;

/**
 *
 * @author DARKCEUS
 */
public class Juego {

    private final int FILAS = 20;
    private final int COLUMNAS = 20;
    private final int TAM_ALTO = 20;
    private final int TAM_ANCHO = 20;
    private final int NUMERO_MINAS = 20;
    private int limpiarTablero;
    private final Campo[][] TABLERO;
    private final ArrayList<Campo> listaMinas;
    private int camposVisibles;
    private boolean inicio;
    private final int[] NUM_X = {-1, 0, 1, -1, 1, -1, 0, 1};
    private final int[] NUM_Y = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final int[] CRUZ_X = {0, -1, 1, 0};
    private final int[] CRUZ_Y = {-1, 0, 0, 1};
    private final Sala sala;
    private int minutos;
    private int segundos;
    private ArrayList<Campo> listaDisponibles;
    private Thread t;
    
    public Juego() {
        this.TABLERO = null;
        this.listaMinas = null;
        this.sala = null;
    }

    public Juego(Sala sala) {
        this.inicio = true;
        this.camposVisibles = 0;
        this.listaDisponibles = new ArrayList<>();
        this.segundos = 0;
        this.minutos = 0;
        this.sala = sala;
        this.TABLERO = new Campo[FILAS][COLUMNAS];
        this.listaMinas = new ArrayList<>();
        crearTablero();
        colocarMinas();
        checarPerimetro();
        imprimirInfo();
        iniciarTiempo();
    }
    
    public boolean validaciones(){
        if (FILAS != COLUMNAS) {
            System.out.println("El número de Filas y Columnas deben ser iguales.");
            JOptionPane.showMessageDialog(null, "El número de Filas y Columnas deben ser iguales.");
            return false;
        }
        if ((FILAS > 30 || FILAS < 10) || (COLUMNAS > 30 || COLUMNAS < 10)) {
            System.out.println("El número de Filas y Columnas deben ser menores o iguales a 30 y mayores a 10.");
            JOptionPane.showMessageDialog(null, "El número de Filas y Columnas deben ser menores o iguales a 30 y mayores a 10.");
            return false;
        }
        if (TAM_ALTO != TAM_ANCHO) {
            System.out.println("El tamaño de los campos deben ser iguales.");
            JOptionPane.showMessageDialog(null, "El tamaño de los campos deben ser iguales.");
            return false;
        }
        if ((TAM_ALTO > 20 || TAM_ALTO < 10) || (TAM_ANCHO > 20 || TAM_ANCHO < 10)) {
            System.out.println("El tamaño de los campos deben ser menores o iguales a 20 y mayores a 10.");
            JOptionPane.showMessageDialog(null, "El tamaño de los campos deben ser menores o iguales a 20 y mayores a 10.");
            return false;
        }
        /*if ((FILAS < TAM_ANCHO) || (COLUMNAS < TAM_ALTO)) {
            System.out.println("El tamaño de las filas o columnas debe ser mayor o igual al tamaño de los campos.");
            JOptionPane.showMessageDialog(null, "El tamaño de las filas o columnas debe ser mayor o igual al tamaño de los campos.");
            return false;
        }*/
        if (NUMERO_MINAS > (FILAS * COLUMNAS)) {
            System.out.println("El número de minas debe ser menor al tamaño total del tablero.");
            JOptionPane.showMessageDialog(null, "El número de minas debe ser menor al tamaño total del tablero.");
            return false;
        }
        return true;
    }

    public int getFILAS() {
        return FILAS;
    }

    public int getCOLUMNAS() {
        return COLUMNAS;
    }

    public int getTAM_ALTO() {
        return TAM_ALTO;
    }

    public int getTAM_ANCHO() {
        return TAM_ANCHO;
    }

    public int getNUMERO_MINAS() {
        return NUMERO_MINAS;
    }
    
    private void iniciarTiempo() {
        t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        segundos++;
                        if (segundos == 30){
                            aumentarMinas();
                        }
                        if (segundos == 60) {
                            aumentarMinas();
                            segundos = 0;
                            minutos++;
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        t.start();
    }

    public void setInicio(boolean inicio) {
        this.inicio = inicio;
    }

    public boolean getInicio() {
        return this.inicio;
    }
    
    private void crearTablero() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                TABLERO[x][y] = new Campo(x, y);
            }
        }
    }

    private void imprimirInfo() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                System.out.print(TABLERO[x][y].getValor());
            }
            System.out.println("");
        }
    }
    
    private int obtenerRandom(int num) {
        return (int) (Math.random() * num);
    }

    private boolean comprobarLimites(int x, int y) {
        return (x > NUM_X[0] && x < FILAS) && (y > NUM_Y[0] && y < COLUMNAS);
    }

    private void colocarMinas() {
        boolean minasC;
        int fila;
        int columna;
        Campo campo;
        int contarMinas = 0;
        do {
            minasC = false;
            for (int i = 0; i < NUMERO_MINAS; i++) {
                fila = obtenerRandom(FILAS);
                columna = obtenerRandom(COLUMNAS);
                campo = getCampo(fila, columna);
                if (campo.comprobarVacio()) {
                    campo.colocarMina();
                    listaMinas.add(campo);
                    contarMinas++;
                } else {
                    i--;
                }
            }
            if (contarMinas == NUMERO_MINAS) {
                minasC = true;
            }
        } while (minasC == false);
        System.out.println(contarMinas);
    }
    
    private int getX(int i, Campo campo){
        return NUM_X[i] + campo.getX();
    }
    
    private int getY(int i, Campo campo){
        return NUM_Y[i] + campo.getY();
    }
    
    private Campo getCampo(int x, int y){
        if (comprobarLimites(x, y)) {
            return TABLERO[x][y];
        }
        return null;
    }
    
    private void checarPerimetro() {
        int x2;
        int y2;
        Campo campo;
        for (Campo campo2 : this.listaMinas) {
            for (int i = 0; i < 8; i++) {
                x2 = getX(i, campo2);
                y2 = getY(i, campo2);
                if (comprobarLimites(x2, y2)) {
                    campo = getCampo(x2, y2);
                    if (!campo.comprobarMina()) {
                        campo.aumentarValor();
                    }
                }
            }
        }
    }
    
    private void checarPerimetro(Campo campo2) {
        int x2;
        int y2;
        Campo campo;
        System.out.println("Se agregó mina X:" + campo2.getX() + ", Y:" + campo2.getY());
        for (int i = 0; i < 8; i++) {
            x2 = getX(i, campo2);
            y2 = getY(i, campo2);
            if (comprobarLimites(x2, y2)) {
                campo = getCampo(x2, y2);
                if (!campo.comprobarMina()) {
                    campo.aumentarValor();
                    if (campo.comprobarVisible()) {
                        sala.enviarInfo("2ACTUALIZAR " + campo.getX() + "," + campo.getY() + "," + campo.getValor());
                    }
                }
            }
        }
        sala.enviarInfo("AUMENTARBANDERAS");
    }
    
    private void aumentarMinas() {
        this.listaDisponibles = new ArrayList<>();
        int num = checarEspacio();
        if (num >= 0) {
            checarPerimetro(listaDisponibles.get(num));
            imprimirInfo();
        }
    }
    
    private int checarEspacio() {
        agregarDisponibles();
        if (listaDisponibles.isEmpty()) {
            return -1;
        }
        return colocarMina();
    }
    
    private void agregarDisponibles() {
        Campo campo;
        for (int j = 0; j < COLUMNAS; j++) {
            for (int i = 0; i < FILAS; i++) {
                campo = getCampo(i, j);
                if (campo.comprobarOculto() && campo.comprobarValorValido()) {
                    listaDisponibles.add(campo);
                }
            }
        }
    }
    
    private int colocarMina() {
        int numeroRandom = obtenerRandom(listaDisponibles.size());
        listaDisponibles.get(numeroRandom).colocarMina();
        listaMinas.add(listaDisponibles.get(numeroRandom));
        return numeroRandom;
    }
    
    private boolean checarClic(Jugador jugador, Campo campo) {
        if (jugador.checarClic()) {
            return true;
        }
        boolean esquina = checarEsquina(jugador, campo);
        if (esquina) {
            jugador.darClic();
            return true;
        }
        if (jugador.checarJ1() && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Izquierda");
        } else if (jugador.checarJ2() && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Derecha");
        } else if (jugador.checarJ3() && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Arriba");
        } else if (jugador.checarJ4() && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Abajo");
        }
        return false;
    }
    
    private boolean checar(Jugador jugador) {
        int tam;
        switch (jugador.getID()) {
            case 1: {
                tam = COLUMNAS;
                for (int i = 0; i < tam; i++) {
                    if (getCampo(0, i).comprobarOculto()) {
                        return true;
                    }
                }
                break;
            }
            case 2: {
                tam = COLUMNAS;
                int info = (FILAS - 1);
                for (int i = 0; i < tam; i++) {
                    
                    if (getCampo(info, i).comprobarOculto()) {
                        return true;
                    }
                }
                break;
            }
            case 3: {
                tam = FILAS;
                for (int i = 0; i < tam; i++) {
                    if (getCampo(i, 0).comprobarOculto()) {
                        return true;
                    }
                }
                break;
            }
            case 4: {
                tam = FILAS;
                int info = (COLUMNAS - 1);
                for (int i = 0; i < tam; i++) {
                    if (getCampo(i, info).comprobarOculto()) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
    
    private boolean checarEsquina(Jugador jugador, Campo campo) {
        if (jugador.checarJ1() && (campo.getX() == 0 || !checar(jugador))) {
            return true;
        } else if (jugador.checarJ2() && (campo.getX() == (FILAS - 1) || !checar(jugador))) {
            return true;
        } else if (jugador.checarJ3() && (campo.getY() == 0 || !checar(jugador))) {
            return true;
        } else if (jugador.checarJ4() && (campo.getY() == (COLUMNAS - 1) || !checar(jugador))) {
            return true;
        }
        return false;
    }
    
    private void abrirCampo(Jugador jugador, Campo campo) {
        campo.hacerVisible();
        campo.setAdmin(jugador);
        if (!campo.comprobarMina()) {
            camposVisibles++;
        }
    }
    
    private void explotarMina(Jugador jugador, Campo campo, int x, int y) {
        jugador.perder();
        campo.hacerVisible();
        sala.agregarPerdedor(jugador);
        sala.enviarInfo("HAYMINA " + x + "," + y + "," + jugador.getID());
        sala.enviarInfo("MESSAGE [Servidor] " + jugador.getNombre() + " ha perdido");
    }
    
    public void descubrirCampo(Jugador jugador, int x, int y) {
        if (sala.getIniciado() && jugador.verificarSigueJugando()) {
            Campo campo = getCampo(x, y);
            if (checarClic(jugador, campo)) {
                if (campo.comprobarOculto()/* || (!campo.getAdmin().equals(jugador) && campo.getEstado() == Campo.ESTADO_BANDERA)*/) {
                    abrirCampo(jugador, campo);
                    sala.enviarInfo("DESCUBRIRCAMPO " + x + "," + y + "," + campo.getValor());
                    if (campo.comprobarMina()) {
                        explotarMina(jugador, campo, x, y);
                    } else if (campo.comprobarVacio()) {
                        revelarPerimetro(jugador, campo);
                    }
                }
                checarMinas();
            }
        } else {
            jugador.getPW().println("INFOMESSAGE Ya perdiste, no puedes jugar");
        }
    }
    
    private void revelarCampo(Jugador jugador, Campo campo2, int x2, int y2) {
        campo2.setAdmin(jugador);
        campo2.hacerVisible();
        camposVisibles++;
        sala.enviarInfo("DESCUBRIRCAMPO " + x2 + "," + y2 + "," + campo2.getValor());
        if (campo2.comprobarVacio()) {
            revelarPerimetro(jugador, campo2);
        }
    }

    private void revelarPerimetro(Jugador jugador, Campo campo) {
        int x2;
        int y2;
        Campo campo2;
        for (int i = 0; i < 8; i++) {
            x2 = NUM_X[i] + campo.getX();
            y2 = NUM_Y[i] + campo.getY();
            if (comprobarLimites(x2, y2)) {
                campo2 = TABLERO[x2][y2];
                if (campo2.comprobarOculto()) {
                    revelarCampo(jugador, campo2, x2, y2);
                }
            }
        }
    }
    
    private void ponerBandera(Jugador jugador, Campo campo, int x, int y) {
        if (checarClic(jugador, campo)) {
            campo.colocarBandera();
            campo.setAdmin(jugador);
            jugador.agregarBandera(campo);
            sala.enviarInfo("PONERBANDERA " + x + "," + y + "," + jugador.getID());
            checarMinas();
        }
    }
    
    private void quitarBandera(Jugador jugador, Campo campo, int x, int y) {
        campo.ocultar();
        campo.setAdmin(jugador);
        jugador.quitarBandera(campo);
        sala.enviarInfo("QUITARBANDERA " + x + "," + y + "," + jugador.getID());
    }
    
    public void gestionarBandera(Jugador jugador, int x, int y, int minasRes) {
        if (sala.getIniciado() && jugador.verificarSigueJugando()) {
            Campo campo = getCampo(x, y);
            if (campo.comprobarOculto() && minasRes > 0) {
                ponerBandera(jugador, campo, x, y);
            } else if (campo.comprobarBandera() && campo.comprobarAdmin(jugador)) {
                quitarBandera(jugador, campo, x, y);
            }
        } else {
            jugador.getPW().println("INFOMESSAGE Ya perdiste, no puedes jugar");
        }
    }
    
    private void actualizarDatos(){
        limpiarTablero = (FILAS * COLUMNAS) - listaMinas.size();
    }
    
    private boolean verificarCamposVisibles(){
        return camposVisibles == limpiarTablero;
    }
    
    public void checarMinas() {
        if (!inicio) {
            return;
        }
        boolean checarMinas = true;
        for (Campo minas : listaMinas) {
            if (!(minas.comprobarVisible() || minas.comprobarBandera())) {
                checarMinas = false;
                break;
            }
        }
        actualizarDatos();
        if (checarMinas && verificarCamposVisibles()) {
            sala.enviarInfo("INFOMESSAGE Han puesto banderas sobre todas las minas y descubierto todos los campos.");
            this.mostrarPuntos();
        }
    }
    
    private void sacarPuntos() {
        for (Jugador jugador : sala.getLista()) {
            for (Campo banderas : jugador.getBanderas()) {
                if (banderas.comprobarMina()) {
                    jugador.aumentarPuntos();
                } else {
                    jugador.quitarPuntos();
                }
            }
        }
    }
    
    private void ponerBanderaIncorrecta(Campo campo, int x, int y) {
        campo.ponerBanderaIncorrecta();
        sala.enviarInfo("NOMINA " + x + "," + y);
        campo.getAdmin().quitarPuntos();
    }
    
    private void sacarPuntaje(){
       Campo campo;
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                campo = getCampo(x, y);
                if (!campo.comprobarMina()) {
                    if (campo.comprobarBandera()) {
                        ponerBanderaIncorrecta(campo, x, y);
                    } else if (campo.comprobarOculto() || campo.comprobarVisible()) {
                        campo.hacerVisible();
                        sala.enviarInfo("2DESCUBRIRCAMPO " + x + "," + y);
                    }
                } else {
                    if (campo.comprobarBandera()) {
                        campo.getAdmin().aumentarPuntos();
                    } else if (campo.comprobarOculto()) {
                        campo.hacerVisible();
                        sala.enviarInfo("2HAYMINA " + x + "," + y);
                    }
                }
            }
        }
    }
    
    private void pararTiempo(){
        t.stop();
    }
    
    private void ordenarResultado(){
        Collections.sort(sala.getLista(), (Jugador j1, Jugador j2) -> new Integer(j2.getPuntos()).compareTo(j1.getPuntos()));
    }
    
    private String almacenarPuntajes(){
        String algo = "";
        for (Jugador jugador : sala.getLista()) {
            algo += "Jugador " + jugador.getID() + ", Nombre: " + jugador.getNombre() + ", Puntos: " + jugador.getPuntos() + ".";
        }
        return algo;
    }

    public void mostrarPuntos() {
        sacarPuntaje();
        inicio = false;
        pararTiempo();
        ordenarResultado();
        sala.enviarInfo("MESSAGE [Servidor] El juego ha terminado");
        String algo = almacenarPuntajes();
        sala.enviarInfo("PUNTOS " + algo);
        sala.reiniciarDatos();
    }
}