package cne;

import extras.Extra;

import java.util.ArrayList;

public class Poblacion {

    private int cantidadCromosomas = 20;
    private int maximoGeneraciones = 10;
    private Cromosoma[] cromosomas = new Cromosoma [this.cantidadCromosomas];
    private int aptitudes [] = new int[cantidadCromosomas];
    private ArrayList aptitudesGeneracion = new ArrayList();
    private int totalAptitudes;

    /*
    TODO
    Colocarlos como atributos de gen
     */
    private int[] minimos = {1, 5, 2, 25};
    private int[] maximos = {30, 10, 35, 100};

    public Poblacion (){
        for (int i = 0; i< cantidadCromosomas; i++){
            cromosomas[i] = new Cromosoma();
        }
    }

    public Poblacion (Cromosoma cromosomas []){
        this.cromosomas = cromosomas;
    }

    public Poblacion crearPoblacion() {

        for (int i = 0; i < this.cantidadCromosomas; i++) {
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                this.cromosomas[i].getGenes()[j] =  Extra.numeroAleatorio(minimos[j], maximos[j]);
            }
        }
        return new Poblacion(this.cromosomas);
    }


    public int [] evaluar (){


        for (int i = 0; i < this.cantidadCromosomas; i++){
            /*
            TODO
            chapuza
             */
            this.aptitudes[i] = 0;
            this.aptitudes[i] += this.cromosomas[i].getAptitud();
        }
        return this.aptitudes;
    }

    public int calcularTotal(){
        this.totalAptitudes = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++){
            this.totalAptitudes += aptitudes[i];
        }
        return this.totalAptitudes;
    }

    public Poblacion seleccionar(){

        int[] rango = new int[this.cantidadCromosomas];
        int random = 0, j = 0, contador = 0;

        /*
        Calculamos el rango de aptitudes (la rueda de selección). Aquellos que tengan más aptitud, tendrán más espectro
        Y por tanto una posibilidad relativa a su aptitud
        */

        rango[0] = this.aptitudes[0];
        for (int i = 1; i < this.cantidadCromosomas; i++){
            rango[i] = rango [i-1] + aptitudes[i];
        }
        rango[this.cantidadCromosomas-1] = rango[this.cantidadCromosomas-2] + aptitudes[this.cantidadCromosomas -1];

        /*
        Generamos un número aleatorio, y según donde caiga dentro del array de rangos, se añadirá el cromosoma relativo
        a su posición a la lista final de la población
         */

        this.totalAptitudes = this.calcularTotal();
        for (int i = 0; i < this.cantidadCromosomas; i++){

            random =  Extra.numeroAleatorio(1, this.totalAptitudes);
            while (random > rango[j]){
                if (j < this.cantidadCromosomas)j++;
            }

            /*
            TODO
            Arreglar chapuza esta
            */
            int[] nuevosGenes = new int[this.getCantidadGenesCromosoma()];
            for (int k = 0; k < this.getCantidadGenesCromosoma(); k++){
                nuevosGenes[k] = cromosomas[j].getGenes()[k];
            }
            Cromosoma nuevoCromosoma = new Cromosoma(nuevosGenes);
           this.cromosomas[i] = nuevoCromosoma;
            j = 0;
        }
        return new Poblacion(this.cromosomas);
    }

    public Poblacion cruzar(){

        int mezclaAux = 0;

        for (int i = 0; i < this.cantidadCromosomas ; i+=2){
            for (int j = 2; j < this.getCantidadGenesCromosoma(); j++){
                mezclaAux = this.cromosomas[i].getGenes()[j];
                this.cromosomas[i].getGenes()[j] = this.cromosomas[i+1].getGenes()[j];
                this.cromosomas[i+1].getGenes()[j] = mezclaAux;
            }
        }
        return new Poblacion(this.cromosomas);
    }

    public Poblacion mutar (){

        float random = 0f, probabilidadMutacion = 0.15f;

        for (int i = 0; i< this.cantidadCromosomas; i++){
            /*
            TODO
            Hacer esto en la clase Extra
             */
            random = (float)(Math.random() * (1));
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++){
                if (random < probabilidadMutacion){
                    this.cromosomas[i].getGenes()[j] = (int) (Math.random() * (maximos[j] + 1 - minimos[j])) + minimos[j];
                }
            }
        }
        return new Poblacion(this.cromosomas);
    }

    public Cromosoma algoritmoGeneticoBase(){

        int generacion = 1;
        this.crearPoblacion();
        this.evaluar();
        do{
            this.seleccionar();
            this.cruzar();
            this.mutar();
            this.evaluar();
            this.calcularTotal();
            this.aptitudesGeneracion.add(totalAptitudes);
            generacion++;
        }while (generacion < this.maximoGeneraciones);
        return mejorCromosoma();
    }

    public Cromosoma algoritmoGeneticoBaseString(){

        int generacion = 1;

        System.out.println("\n--------------------------------POBLACION INICIAL");
        this.crearPoblacion();
        System.out.println(this.toString());

        System.out.println("\n--------------------------------APTITUDES");
        this.evaluar();
        System.out.println(this.aptitudesToString());

        System.out.println("\n--------------------------------TOTAL");
        System.out.println(this.calcularTotal());



        do{

            System.out.println("\n--------------------------------SELECCIONAR Generacion: "+generacion);
            this.seleccionar();
            System.out.println(this.toString());

            System.out.println("\n--------------------------------CRUZAR Generacion: "+generacion);
            this.cruzar();
            System.out.println(this.toString());

            System.out.println("\n--------------------------------MUTAR Generacion: "+generacion);
            this.mutar();
            System.out.println(this.toString());

            System.out.println("\n--------------------------------APTITUDES Generacion: "+generacion);
            this.evaluar();
            System.out.println(aptitudesToString());

            System.out.println("\n--------------------------------TOTAL Generacion: "+generacion);
            System.out.println(this.calcularTotal());

            this.aptitudesGeneracion.add(totalAptitudes);
            generacion++;
        }while (generacion < this.maximoGeneraciones);

        System.out.println("------------------------MEJOR CROMOSOMA:");
        for (int i = 0; i < this.getCantidadGenesCromosoma(); i++){
            System.out.print(this.mejorCromosoma().getGenes()[i]+",");
        }

        new Ventana(this);
        return mejorCromosoma();
    }

    public Cromosoma mejorCromosoma (){

        int maximo = Integer.MIN_VALUE, indice = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++){
            if(aptitudes[i] > maximo){
                maximo = aptitudes[i];
                indice = i;
            }
        }
        return cromosomas[indice];
    }

    public Cromosoma[] getCromosomas() {
        return cromosomas;
    }

    public void setCantidadCromosomas(int cantidadCromosomas) {
        this.cantidadCromosomas = cantidadCromosomas;
    }

    public int getCantidadCromosomas() {
        return this.cantidadCromosomas;
    }

    public int getCantidadGenesCromosoma(){
        return new Cromosoma().getCantidadGenes();
    }

    public void setMaximos(int[] maximos) {
        this.maximos = maximos;
    }

    public void setMinimos(int[] minimos) {
        this.minimos = minimos;
    }

    public int getTotalAptitudes() {
        return totalAptitudes;
    }

    public int getMaximoGeneraciones() {
        return maximoGeneraciones;
    }

    public ArrayList getAptitudesGeneracion() {
        return aptitudesGeneracion;
    }

    public String aptitudesToString(){

        StringBuffer cadena = new StringBuffer();
        cadena.append("\n");
        for (int i = 0; i < this.getCantidadCromosomas(); i++) {
            cadena.append(aptitudes[i]+"\n");
        }
        return cadena.toString();
    }

    public String toString(){

    StringBuffer cadena = new StringBuffer();
        for (int i = 0; i < this.cantidadCromosomas; i++) {
            cadena.append("\n");
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                cadena.append(cromosomas[i].getGenes()[j] + ",");
            }
        }
        return cadena.toString();
    }
}
