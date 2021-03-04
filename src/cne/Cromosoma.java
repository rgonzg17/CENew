package cne;

public class Cromosoma {

    private int cantidadGenes = 4;
    private int [] genes = new int[cantidadGenes];
    private int aptitud;

    public Cromosoma(){
        for (int i = 0; i < cantidadGenes ; i++){
            genes[i] = 0;
        }
        aptitud = 0;
    }

    public Cromosoma(int[]genes){
        this.genes = genes;
        getAptitud();
    }

    public int getAptitud(){

        this.aptitud = 0;
        for (int i = 0; i < this.cantidadGenes; i++){
            this.aptitud += this.getGenes()[i];
        }
        return this.aptitud;
    }

    public void setGenes(int[] genes) {
        this.genes = genes;
    }

    public int[] getGenes() {
        return this.genes;
    }

    public void setCantidadGenes(int cantidadGenes) {
        this.cantidadGenes = cantidadGenes;
    }

    public int getCantidadGenes() {
        return this.cantidadGenes;
    }
}
