package cne;

import extras.Extra;

import java.util.ArrayList;

public class Poblacion {

    private int cantidadCromosomas = 20;
    private int maximoGeneraciones = 100;
    private Cromosoma[] cromosomas = new Cromosoma [this.cantidadCromosomas];
    private ArrayList aptitudesGeneracion = new ArrayList();
    private int totalAptitudes;
    //private int intervaloGrafica;

    /*
    TODO
    Colocarlos como atributos de gen
     */
    private int[] minimos = {1, 5, 2, 25, 15, 60, 8, 9};
    private int[] maximos = {30, 10, 35, 100, 55, 200, 40, 50};

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

        int [] aptitudes = new int[this.cantidadCromosomas];
        for (int i = 0; i < this.cantidadCromosomas; i++){
            /*
            TODO
            chapuza
             */
            aptitudes[i] = 0;
            aptitudes[i] += this.cromosomas[i].getAptitud();
        }
        return aptitudes;
    }

    public int calcularTotal(){
        this.totalAptitudes = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++){
            this.totalAptitudes += this.cromosomas[i].getAptitud();
        }
        return this.totalAptitudes;
    }

    public Poblacion seleccionar(){

        int[] rango = new int[this.cantidadCromosomas];
        int random = 0, elegido = 0;
        Cromosoma[] nuevosCromosomas = new Cromosoma[this.cantidadCromosomas];

        /*
        Calculamos el rango de aptitudes (la rueda de selección). Aquellos que tengan más aptitud, tendrán más espectro
        Y por tanto una posibilidad relativa a su aptitud
        */

        rango[0] = this.cromosomas[0].getAptitud();
        for (int i = 1; i < this.cantidadCromosomas; i++){
            rango[i] = rango [i-1] + this.cromosomas[i].getAptitud();
        }
        rango[this.cantidadCromosomas-1] = rango[this.cantidadCromosomas-2] + this.cromosomas[this.cantidadCromosomas -1].getAptitud();

        /*
        Generamos un número aleatorio, y según donde caiga dentro del array de rangos, se añadirá el cromosoma relativo
        a su posición a la lista final de la población
         */
        System.out.println("RANGO");
        for (int k = 0; k <this.cantidadCromosomas; k++){
            System.out.print(rango[k] + ",");
        }
        System.out.println();

        this.totalAptitudes = this.calcularTotal();

        for (int i = 0; i < this.cantidadCromosomas; i++){

            random =  Extra.numeroAleatorio(1, this.totalAptitudes);
            elegido = 0;

            while (random > rango[elegido]){
                if (elegido < this.cantidadCromosomas)
                    elegido++;
            }

            System.out.println("RANDOM");
            System.out.println(random+" y el cromosoma escogido es : "+elegido);
            System.out.println(this.cromosomas[elegido]);
            nuevosCromosomas[i] = new Cromosoma(this.cromosomas[elegido]);

        }

        this.cromosomas = nuevosCromosomas;

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

        float randomMutacion = 0f, probabilidadMutacion = 0.01f;
        int randomGen = 0;

        for (int i = 0; i< this.cantidadCromosomas; i++){
            /*
            TODO
            Hacer esto en la clase Extra
             */
            randomMutacion = Extra.numeroAleatorioFlaot();
            randomGen = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma()-1);
            if (randomMutacion < probabilidadMutacion){
                this.cromosomas[i].getGenes()[randomGen] = Extra.numeroAleatorio(minimos[randomGen], maximos[randomGen]);
            }
        }
        return new Poblacion(this.cromosomas);
    }

    public Cromosoma algoritmoGeneticoBase(){

        int generacion = 1;
        this.crearPoblacion();
        do{
            this.evaluar();
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

    do{
            //System.out.println("\n--------------------------------APTITUDES");
            this.evaluar();
            //System.out.println(this.aptitudesToString());


            System.out.println("\n--------------------------------TOTAL TRAS EVALUAR");
            System.out.println(this.calcularTotal());

            System.out.println("------------------------MEJOR CROMOSOMA:");
            for (int i = 0; i < this.getCantidadGenesCromosoma(); i++){
                System.out.print(this.mejorCromosoma().getGenes()[i]+",");
            }
            System.out.println("Aptitud: "+this.mejorCromosoma().getAptitud());


            System.out.println("\n--------------------------------SELECCIONAR Generacion: "+generacion);
            this.seleccionar();
            System.out.println(this.toString());

        System.out.println("\n--------------------------------TOTAL TRAS SELECCIONAR");
        System.out.println(this.calcularTotal());

            System.out.println("\n--------------------------------CRUZAR Generacion: "+generacion);
            this.cruzar();
            System.out.println(this.toString());

            System.out.println("\n--------------------------------MUTAR Generacion: "+generacion);
            this.mutar();
            System.out.println(this.toString());


            //System.out.println("\n--------------------------------APTITUDES Generacion: "+generacion);
            this.evaluar();
            //System.out.println(aptitudesToString());


            System.out.println("\n--------------------------------TOTAL FINAL Generacion: "+generacion);
            System.out.println(this.calcularTotal());

            //if (generacion%10 == 0)
            this.aptitudesGeneracion.add(totalAptitudes);
            generacion++;
        }while (generacion < this.maximoGeneraciones);


        System.out.println("------------------------MEJOR CROMOSOMA:");
        for (int i = 0; i < this.getCantidadGenesCromosoma(); i++){
            System.out.print(this.mejorCromosoma().getGenes()[i]+",");
        }
        System.out.println("Aptitud: "+this.mejorCromosoma().getAptitud());

        //intervaloGrafica = aptitudesGeneracion.size();
        new Ventana(this);
        return mejorCromosoma();
    }

    public Cromosoma mejorCromosoma (){

        int maximo = Integer.MIN_VALUE, indice = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++){
            if(this.cromosomas[i].getAptitud() > maximo){
                maximo = this.cromosomas[i].getAptitud();
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
   /* public int getIntervaloGrafica() {
        return intervaloGrafica;
    }
    */

    public String aptitudesToString(){

        StringBuffer cadena = new StringBuffer();
        cadena.append("\n");
        for (int i = 0; i < this.getCantidadCromosomas(); i++) {
            cadena.append(this.cromosomas[i].getAptitud()+"\n");
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
            cadena.append("\n Aptitud: "+cromosomas[i].getAptitud());
        }
        return cadena.toString();
    }
}
