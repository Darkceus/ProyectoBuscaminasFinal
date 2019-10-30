package clientebuscaminas;

/**
 *
 * @author DARKCEUS
 */
public enum Constantes {
    //Jugadores
    BANDERA_J1(11),
    BANDERA_J2 (13),
    BANDERA_J3(14),
    BANDERA_J4(15),
    //Valores de campo
    BASE_1(0),
    MINA(9),
    BASE_2(10),
    NO_MINA(12),
    //Imágenes
    NUM_IMAGENES(16);
    
    private final int valor;

    private Constantes (int valor) {
        this.valor = valor;
    }
    
    public int getValor(){
        return this.valor;
    }
}
