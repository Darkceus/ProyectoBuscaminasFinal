package servidorbuscaminas;

/**
 *
 * @author DARKCEUS
 */
public class Campo {
    
    private Jugador Admin;
    private int X = 0;
    private int Y = 0;
    private int Valor = 0;
    private int Estado = 0;

    public Campo(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public int getX() {
        return this.X;
    }

    public int getY() {
        return this.Y;
    }

    public int getValor() {
        return Valor;
    }

    public Jugador getAdmin() {
        return Admin;
    }

    public void setAdmin(Jugador Admin) {
        this.Admin = Admin;
    }
    
    public void quitarAdmin(){
        this.Admin = null;
    }
    
    public boolean comprobarAdmin(Jugador jugador){
        return this.Admin.equals(jugador);
    }
    
    public boolean comprobarVacio(){
        return getValor() == Constantes.CAMPO_VALOR_VACIO.getValor();
    }
    
    public void colocarMina(){
        this.Valor = Constantes.CAMPO_VALOR_MINA.getValor();
    }
    
    public void colocarBandera(){
        this.Estado = Constantes.CAMPO_ESTADO_BANDERA.getValor();
    }
    
    public boolean comprobarMina(){
        return Valor == Constantes.CAMPO_VALOR_MINA.getValor();
    }
    
    public boolean comprobarBandera(){
        return Estado == Constantes.CAMPO_ESTADO_BANDERA.getValor();
    }
    
    public void aumentarValor(){
        this.Valor++;
    }
    
    public boolean comprobarVisible(){
        return Estado == Constantes.CAMPO_ESTADO_APLASTADO.getValor();
    }
    
    public boolean comprobarOculto(){
        return Estado == Constantes.CAMPO_ESTADO_INICIAL.getValor();
    }
    
    public boolean comprobarValorValido(){
        return (Valor > Constantes.CAMPO_VALOR_VACIO.getValor() && Valor < Constantes.CAMPO_VALOR_MINA.getValor());
    }
    
    public void ponerBanderaIncorrecta(){
        this.Estado = Constantes.CAMPO_ESTADO_BANDERA_NO_MINA.getValor();
    }
    
    public void hacerVisible(){
        this.Estado = Constantes.CAMPO_ESTADO_APLASTADO.getValor();
    }
    
    public void ocultar(){
        this.Estado = Constantes.CAMPO_ESTADO_INICIAL.getValor();
    }
}
