package excepciones;

public class AlgoritmoGeneticoExcepcion extends Exception{
    private int codigoError;

    public AlgoritmoGeneticoExcepcion(String mensaje){
        super(mensaje);
    }

}
