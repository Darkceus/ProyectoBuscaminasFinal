package servidorbuscaminas;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author DARKCEUS
 */
public class Jugador {

    private final ArrayList<Campo> listaBanderas;
    private int id;
    private int estado;
    private boolean clic;
    private Sala sala;
    private String nombre;
    private PrintWriter pw;
    private int puntos;
    

    public Jugador(String nombre, PrintWriter pw) {
        this.nombre = nombre;
        this.pw = pw;
        this.estado = Constantes.JUGADOR_ESTADO_JUGANDO.getValor();
        this.clic = false;
        this.puntos = 0;
        this.listaBanderas = new ArrayList<>();
    }
    
    public ArrayList<Campo> getBanderas(){
        return this.listaBanderas;
    } 
    
    public boolean sinBanderas(){
        return this.listaBanderas.isEmpty();
    }
    
    public boolean agregarBandera(Campo campo){
        return this.listaBanderas.add(campo);
    }
    
    public boolean quitarBandera(Campo campo) {
        return this.listaBanderas.remove(campo);
    }
    
    public void quitarBanderas() {
        this.listaBanderas.forEach((campo) -> {
            campo.ocultar();
            campo.quitarAdmin();
            sala.enviarInfo("QUITARBANDERA " + campo.getX() + "," + campo.getY() + "," + this.getID());
        });
        this.listaBanderas.clear();
    }

    public void ponerBanderas() {
        this.listaBanderas.forEach((campo) -> {
            sala.enviarInfo("PONERBANDERA " + campo.getX() + "," + campo.getY() + "," + this.getID());
        });
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Sala getSala() {
        return this.sala;
    }
    
    public void reiniciarPuntos(){
        this.puntos = 0;
    }
    
    public void aumentarPuntos(){
        this.puntos++;
    }
    
    public void quitarPuntos(){
        this.puntos--;
    }
    
    public int getPuntos(){
        return this.puntos;
    }
    
    public void reiniciarClic(){
        this.clic = false;
    }
    
    public boolean checarClic(){
        return this.clic;
    }
    
    public void darClic(){
        this.clic = true;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String id) {
        this.nombre = id;
    }

    public PrintWriter getPW() {
        return pw;
    }

    public void setPW(PrintWriter pw) {
        this.pw = pw;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
    public void perder(){
        this.estado = Constantes.JUGADOR_ESTADO_ESPECTADOR.getValor();
    }
    
    public void reiniciarEstado(){
        this.estado = Constantes.JUGADOR_ESTADO_JUGANDO.getValor();
    }
    
    public boolean verificarSigueJugando(){
        return this.estado == Constantes.JUGADOR_ESTADO_JUGANDO.getValor();
    }
    
    public boolean checarJ1(){
        return this.id == Constantes.JUGADOR_J1.getValor();
    }
    
    public boolean checarJ2(){
        return this.id == Constantes.JUGADOR_J2.getValor();
    }
    
    public boolean checarJ3(){
        return this.id == Constantes.JUGADOR_J3.getValor();
    }
    
    public boolean checarJ4(){
        return this.id == Constantes.JUGADOR_J4.getValor();
    }
}
