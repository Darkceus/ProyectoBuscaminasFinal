package servidorbuscaminas;

import java.util.ArrayList;

/**
 *
 * @author DARKCEUS
 */
public class Sala {

    private int id;
    private Jugador Admin;
    private final ArrayList<Jugador> listaJugadores;
    private final ArrayList<Jugador> listaPerdedores;
    private boolean disponible;
    private boolean iniciado;
    private Juego juego = new Juego();

    public Sala(int id, Jugador jugador) {
        this.id = id;
        this.listaJugadores = new ArrayList<>();
        this.listaPerdedores = new ArrayList<>();
        this.Admin = null;
        this.disponible = true;
        this.iniciado = false;
        this.agregarAdmin(jugador);
    }

    public ArrayList<Jugador> getLista() {
        return this.listaJugadores;
    }
    
    public void agregarPerdedor(Jugador jugador) {
        this.listaPerdedores.add(jugador);
        if (this.listaPerdedores.size() == this.getTam()) {
            this.juego.mostrarPuntos();
        }
    }
    
    public void actualizarBanderas() {
        this.listaJugadores.forEach((jugador) -> {
            jugador.ponerBanderas();
        });
    }

    public Juego getJuego() {
        return this.juego;
    }

    public boolean checarJugador(Jugador jugador) {
        for (Jugador jugador2 : this.listaJugadores) {
            if (jugador2.getNombre().equals(jugador.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public void setAdmin(Jugador admin) {
        this.Admin = admin;
    }

    public Jugador getAdmin() {
        return Admin;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public boolean getIniciado() {
        return this.iniciado;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public Jugador getJugador(int indice) {
        return this.listaJugadores.get(indice);
    }
    
    public Jugador getPrimerJugador(){
        return this.listaJugadores.get(0);
    }

    public void enviarInfo(String mensaje) {
        listaJugadores.forEach((jugador) -> {
            jugador.getPW().println(mensaje);
        });
    }

    public void enviarDatosJuego() {
        listaJugadores.forEach((jugador) -> {
            jugador.getPW().println("DATOS " + juego.getFILAS() + "," + juego.getCOLUMNAS() + "," + juego.getTAM_ALTO() + "," + juego.getTAM_ANCHO()
                    + ","  + juego.getNUMERO_MINAS() + "," + jugador.getID() + "," + this.getAdmin().getID());
        });
    }
    
    public void actualizarDatos() {
        this.listaJugadores.forEach((jugador) -> {
            jugador.getPW().println("ACTUALIZAR " + jugador.getID() + "," + this.Admin.getID() + "," + this.listaPerdedores.contains(jugador));
            jugador.ponerBanderas();
        });
    }
    
    public void enviarDatos(){
        this.listaJugadores.forEach((jugador) -> {
            jugador.getPW().println("MESSAGE [Servidor] Ahora eres el jugador " + jugador.getID());
        });
    }

    public int getTam() {
        return this.listaJugadores.size();
    }

    public boolean verificarVacio() {
        return this.listaJugadores.isEmpty();
    }

    private void agregarAdmin(Jugador jugador) {
        this.Admin = jugador;
        this.agregarJugador(jugador);
    }

    public boolean agregarJugador(Jugador jugador) {
        jugador.setID(listaJugadores.size() + 1);
        jugador.setSala(this);
        return this.listaJugadores.add(jugador);
    }

    public boolean eliminarJugador(Jugador jugador) {
        if (this.Admin == jugador) {
            this.Admin = null;
        }
        boolean eliminado = this.listaJugadores.remove(jugador);
        corregirNumeros();
        return eliminado;
    }

    private void corregirNumeros() {
        int i = 0;
        for (Jugador jugador : this.listaJugadores) {
            i++;
            jugador.setID(i);
        }
    }
    
    private boolean checarTam(){
        return this.getTam() < 4;
    }

    public boolean checarDisponibilidad() {
        return (disponible = checarTam()) && !iniciado;
    }
    
    private void reiniciarJugadores() {
        this.listaJugadores.forEach((jugador) -> {
            jugador.reiniciarClic();
            jugador.reiniciarEstado();
            jugador.reiniciarPuntos();
        });
    }
    
    public void reiniciarDatos() {
        if (iniciado) {
            this.iniciado = false;
            if (checarTam()) {
                this.disponible = true;
            }
            reiniciarJugadores();
            this.listaPerdedores.clear();
        }
    }

    public void iniciarJuego(Jugador jugador) {
        if (getTam() > 1 && !iniciado && this.Admin.equals(jugador)) {
            juego = new Juego(this);
            this.iniciado = true;
            this.disponible = false;
            this.enviarInfo("MESSAGE [Servidor] El juego ha iniciado");
            this.enviarDatosJuego();
        }
        if (getTam() == 1) {
            jugador.getPW().println("INFOMESSAGE Debes esperar a que haya mínimo dos jugadores para jugar.");
        }
        if (!this.Admin.equals(jugador)) {
            jugador.getPW().println("INFOMESSAGE No eres el Admin, el Admin es: " + this.getAdmin().getNombre());
        }
    }
}