package servidorbuscaminas;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;

/**
 *
 * @author DARKCEUS
 */
public class ServidorBuscaminas {

    public static final Map<Integer, Sala> SALAS = new TreeMap<>();
    private int puerto;
    
    public static void main(String[] args) {
        ServidorBuscaminas sb = new ServidorBuscaminas();
        sb.iniciarServidor();
    }
    
    private void iniciarServidor() {
        if (!validar()) {
            System.exit(0);
        }
        if (!validarPuerto(getPuerto())) {
            JOptionPane.showMessageDialog(null, "El puerto no es válido");
            this.iniciarServidor();
        }
        System.out.println("El Servidor de Buscaminas está en línea...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(this.puerto)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        } catch (IOException e) {
            System.out.println("Error al Abrir el Servidor");
        }
    }
    
    private boolean validar() {
        Juego juego = new Juego();
        return juego.validaciones();
    }
    
    private String getPuerto() {
        return JOptionPane.showInputDialog(null, "Puerto", "Ingresa un puerto: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private boolean validarPuerto(String valor) {
        try {
            this.puerto = Integer.parseInt(valor);
            return (this.puerto > 1023 && this.puerto < 65536);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public class Handler implements Runnable {

        public Jugador jugador;
        public String nombre;
        public Sala sala;
        public Socket socket;
        public PrintWriter Escritor;
        public int numerosala;
        public Scanner Entrada;
        private boolean prueba = false;
        private final String SIN_REPETICIONES = "[a-zA-Z0-9]{1}";
        private final String ALFANUMERICO = "[a-zA-Z0-9]+";
        private final String PATRON_NOMBRE = ALFANUMERICO;

        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        private int convertirInt(String num) {
            int num2 = -1;
            try {
                num2 = Integer.parseInt(num);
            } catch (NumberFormatException e) {}
            return num2;
        }
        
        private boolean validarNombre() {
            return nombre == null || nombre.isEmpty() || nombre.equals("") || !nombre.matches(PATRON_NOMBRE) || nombre.indexOf(' ') >= 0 || nombre.startsWith("/") || nombre.length() > 15;
        }
        
        private boolean agregarJugadorASala() {
            this.jugador = new Jugador(nombre, Escritor);
            for (Sala sala2 : SALAS.values()) {
                if (sala2.checarDisponibilidad()) {
                    if (!sala2.checarJugador(this.jugador)) {
                        this.sala = sala2;
                        this.sala.agregarJugador(this.jugador);
                        this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre);
                        return true;
                    }
                }
            }
            return false;
        }
        
        private void crearSala() {
            int id = SALAS.size() + 1;
            this.sala = new Sala(id, this.jugador);
            SALAS.put(id, this.sala);
            this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre + ", eres el primero en entrar");
        }
        
        private void gestionarClicIzquierdo(boolean prueba2, String input, int espacio) {
            if (prueba2) {
                String[] coordenadas = input.substring(espacio + 1).split(",");
                if (coordenadas.length == 3) {
                    sala.getJuego().descubrirCampo(jugador, convertirInt(coordenadas[0]), convertirInt(coordenadas[1]));
                }
            }
        }
        
        private void gestionarClicDerecho(boolean prueba2, String input, int espacio) {
            if (prueba2) {
                String[] coordenadas = input.substring(espacio + 1).split(",");
                if (coordenadas.length == 3) {
                    sala.getJuego().gestionarBandera(jugador, convertirInt(coordenadas[0]), convertirInt(coordenadas[1]), convertirInt(coordenadas[2]));
                }
            }
        }
        
        private void gestionarEntrada() {
            String input;
            try {input = Entrada.nextLine();} catch (Exception e) {return;}
            if (input != null && !input.equals("") && !input.isEmpty()) {
                int espacio = input.indexOf(' ');
                boolean prueba2 = espacio >= 0 && espacio < input.length();
                if (input.toLowerCase().startsWith("/iniciarjuego")) {
                    sala.iniciarJuego(jugador);
                } else if (input.startsWith("CLICIZQUIERDO ")) {
                    gestionarClicIzquierdo(prueba2, input, espacio);
                } else if (input.startsWith("CLICDERECHO ")) {
                    gestionarClicDerecho(prueba2, input, espacio);
                } else {
                    sala.enviarInfo("MESSAGE " + jugador.getNombre() + ": " + input);
                }
            }
        }
        
        private void quitarJugador() {
            if (sala.getIniciado()) {
                jugador.quitarBanderas();
            }
            sala.eliminarJugador(jugador);
        }
        
        private void comprobarUltimoJugador() {
            if (sala.getTam() == 1 && sala.getIniciado()) {
                sala.enviarInfo("INFOMESSAGE Eres el único que queda, el juego va a terminar");
                sala.getJuego().mostrarPuntos();
            }
        }
        
        private void actualizarDatos() {
            sala.enviarInfo("MESSAGE [Servidor] " + jugador.getNombre() + " ha salido");
            if (sala.getIniciado()) {
                sala.enviarInfo("INFOMESSAGE " + jugador.getNombre() + " salió, se van a actualizar los Datos.");
            }
        }
        
        private void cambiarAdmin() {
            Jugador primerJugador = sala.getPrimerJugador();
            if (primerJugador != sala.getAdmin()) {
                sala.setAdmin(primerJugador);
                sala.enviarInfo("MESSAGE [Servidor] " + primerJugador.getNombre() + " es el nuevo Admin");
                primerJugador.getPW().println("INFOMESSAGE Eres el nuevo Admin");
                if (sala.getIniciado()) {
                    sala.actualizarDatos();
                } else {
                    sala.enviarDatos();
                }
            }
        }

        @Override
        public void run() {
            try {
                Entrada = new Scanner(socket.getInputStream());
                Escritor = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    Escritor.println("NOMBREDEENVIO");
                    nombre = Entrada.nextLine();
                    if (validarNombre()) {
                        continue;
                    }
                    if (nombre.equals("null")) {
                        prueba = true;
                        return;
                    }
                    synchronized (SALAS) {
                        boolean entro = agregarJugadorASala();
                        if (!entro) {
                            crearSala();
                        }
                        break;
                    }
                }
                Escritor.println("NAMEACCEPTED " + nombre + "," + sala.getID());
                sala.enviarInfo("MESSAGE [Servidor] " + jugador.getNombre() + " ha entrado");
                this.jugador.getPW().println("INFOMESSAGE Eres el jugador número " + jugador.getID());
                while (true) {
                    synchronized (this) {
                        gestionarEntrada();
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (Escritor != null || sala != null || jugador != null) {
                    if (!prueba) {
                        quitarJugador();
                        comprobarUltimoJugador();
                        if (!sala.verificarVacio()) {
                            actualizarDatos();
                            cambiarAdmin();
                        } else {
                            SALAS.remove(sala.getID());
                        }
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar el Socket, " + e);
                }
            }
        }
    }
}
