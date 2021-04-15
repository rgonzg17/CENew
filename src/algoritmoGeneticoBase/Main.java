package algoritmoGeneticoBase;

import algoritmoGeneticoBase.Poblacion;
import excepciones.AlgoritmoGeneticoExcepcion;

public class Main {

    public static void main(String[] args) throws AlgoritmoGeneticoExcepcion {
       Poblacion poblacionInicial = new Poblacion();
       poblacionInicial.algoritmoGenetico();
    }

}

