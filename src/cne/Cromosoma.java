package cne;

public class Cromosoma {


    //VALORES QUE SE PUEDEN CAMBIAR
    /**
     * Cantidad de genes que tiene cada cromosoma
     */
    private int cantidadGenes = 4;

    //VARIABLES QUE NO SE PUEDEN MODIFICAR
    private int [] genes = new int[cantidadGenes];
    private int aptitud;


    /**
     * Constructor
     */
    public Cromosoma(){
        for (int i = 0; i < cantidadGenes ; i++){
            genes[i] = 0;
        }
        aptitud = 0;
    }

    /**
     * Constructor de copia
     * @param otroCromosoma
     */
    public Cromosoma(Cromosoma otroCromosoma){
        this.cantidadGenes = otroCromosoma.cantidadGenes;
        for (int i = 0; i <this.cantidadGenes; i++){
            this.genes[i] = otroCromosoma.getGenes()[i];
        }
        this.aptitud = otroCromosoma.aptitud;
    }

    /**
     * Getter de la aptitud del cromosoma
     * @return aptitud del cromosoma
     */
    public int getAptitud(){

        return this.aptitud;
    }

    /**
     * Setter de la aptitud del cromosoma
     * @param aptitud
     */
    public void setAptitud(int aptitud){
        this.aptitud = aptitud;
    }


    /**
     * Getter de los genes que tiene un cromosoma
     * @return
     */
    public int[] getGenes() {
        return this.genes;
    }

    /**
     * Getter de la cantidad de genes que tiene un cromosoma
     * @return cantidad de genes que tiene un cromosoma
     */
    public int getCantidadGenes() {
        return this.cantidadGenes;
    }


    /**
     * Método toString para devolver por pantalla los valores de los genes de cada cromosoma
     * @return Salida por pantalla de la representación de un cromosoma
     */
    @Override
    public String toString(){
        StringBuffer cadena = new StringBuffer();
        for (int i = 0; i < this.cantidadGenes; i++){
            cadena.append(this.getGenes()[i]+",");
        }
        return cadena.toString();
    }
}
