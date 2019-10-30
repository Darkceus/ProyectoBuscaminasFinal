package servidorbuscaminas;

/**
 *
 * @author DARKCEUS
 */
public enum Constantes {
    //Campo
    CAMPO_VALOR_VACIO(0),
    CAMPO_VALOR_MINA(9),
    CAMPO_ESTADO_INICIAL(0),
    CAMPO_ESTADO_APLASTADO(1),
    CAMPO_ESTADO_BANDERA(2),
    CAMPO_ESTADO_BANDERA_NO_MINA(3),
    //Juego
    JUEGO_FILAS(20),
    JUEGO_COLUMNAS(20),
    JUEGO_TAM_ALTO(20),
    JUEGO_TAM_ANCHO(20),
    JUEGO_NUMERO_MINAS(20),
    //Jugador
    JUGADOR_ESTADO_JUGANDO(1),
    JUGADOR_ESTADO_ESPECTADOR(0),
    JUGADOR_J1(1),
    JUGADOR_J2(2),
    JUGADOR_J3(3),
    JUGADOR_J4(4);
    

    private final int valor;

    private Constantes(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return this.valor;
    }
}
