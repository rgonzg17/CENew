package cne;

import excepciones.AlgoritmoGeneticoExcepcion;
import extras.Extra;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Poblacion {


    //VALORES QUE SE PUEDEN CAMBIAR
    /**
     * Cantidad de cromosomas dentro de la población
     */
    private int cantidadCromosomas = 6;

    /**
     * Cantidad de generaciones que se van a repetir
     */
    private int maximoGeneraciones = 10;


    /**
     * Número de mejores cromosomas que se van a seleccionar, por defecto solo se escogerá al mejor
     */
    private int numeroElitismo = 3;

    /**
     * Variable que indica desde donde cortar en el cromosoma, por defecto se inicia en 2
     */
    private int puntoDeCorte = 2;

    /**
     * Por defecto, la mutación será por cromosoma, a no ser que se ponga esta variable a true
     */
    private boolean mutacionPorGenes = true;

    /**
     * Probabilidad de que un gen de un cromosoma mute
     */

    private float probabilidadMutacionGen = 0.015f;
    /**
     * Probabilidad de que un cromosoma mute alguno de sus genes.
     */
    private float probabilidadMutacionCromosoma = 0.015f;

    /**
     * Si está en false, se mostrarán todas las generaciones en el gráfico.
     * Si está en true, se mostrarán las generaciones de 10 en 10 en el gráfico
     */
    private boolean intervaloGrafica = false;

    /**
     * Elegir cada cuantos números hacer un intervalo.
     * Solo estará habilitado cuando intervaloGrafica esté en true
     * Por defecto está en 10
     */
    private int numeroIntervalo = 10;

    /**
     * Mínimos que deben ser (según su par) menores que los máximos
     */
    private int[] minimos = {1, 5, 2, 25, 15, 60, 8, 9};

    /**
     * Máximos que deben ser (según su par) mayores que los mínimos
     */
    private int[] maximos = {30, 10, 35, 100, 55, 200, 40, 50};


    //VARIABLES QUE NO SE PUEDEN MODIFICAR
    private Cromosoma[] cromosomas = new Cromosoma[this.cantidadCromosomas];
    private ArrayList aptitudesGeneracion = new ArrayList();
    private ArrayList aptitudesMejorCromosomaGeneracion = new ArrayList();
    private int totalAptitudes;

    /**
     * Constructor
     */
    public Poblacion() {
        for (int i = 0; i < cantidadCromosomas; i++) {
            cromosomas[i] = new Cromosoma();
        }
    }

    /**
     * Constructor con cromosomas creados
     *
     * @param cromosomas
     */
    public Poblacion(Cromosoma cromosomas[]) {
        this.cromosomas = cromosomas;
    }


    /**
     * Método para crear poblacion con números aleatorios
     *
     * @return Una población con todos los cromosomas creados de manera aleatoria
     */
    public Poblacion crearPoblacion() {

        for (int i = 0; i < this.cantidadCromosomas; i++) {
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                this.cromosomas[i].getGenes()[j] = Extra.numeroAleatorio(minimos[j], maximos[j]);
            }
        }
        return new Poblacion(this.cromosomas);
    }


    /**
     * Método para evaluar los cromosomas. Se suma los valores de los genes para calcular su aptitud
     *
     * @return Una lista de aptitudes de los cromosomas
     */
    public void evaluar() {

        int aptitud = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++) {
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                aptitud += this.cromosomas[i].getGenes()[j];
            }
            this.cromosomas[i].setAptitud(aptitud);
            aptitud = 0;
        }

    }

    /**
     * Método para calcular el total de la aptitud de toda la población
     *
     * @return La aptitud total de toda la población
     */
    public int calcularTotal() {
        this.totalAptitudes = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++) {
            this.totalAptitudes += this.cromosomas[i].getAptitud();
        }
        return this.totalAptitudes;
    }

    /**
     * Método para crear una nueva población, siguiendo la selección por ruleta de probabilidades
     *
     * @return Una nueva población con cromosomas seleccionados según la ruleta de probabilidades
     */
    public Poblacion seleccionar() {

        int[] rango = new int[this.cantidadCromosomas];
        int random = 0, randomElitismo = 0, elegido = 0;
        Cromosoma[] nuevosCromosomas = new Cromosoma[this.cantidadCromosomas];

        /*
        Calculamos el rango de aptitudes (la rueda de selección). Aquellos que tengan más aptitud, tendrán más espectro
        Y por tanto una posibilidad relativa a su aptitud
        */
        rango[0] = this.cromosomas[0].getAptitud();
        for (int i = 1; i < this.cantidadCromosomas; i++) {
            rango[i] = rango[i - 1] + this.cromosomas[i].getAptitud();
        }
        rango[this.cantidadCromosomas - 1] = rango[this.cantidadCromosomas - 2] + this.cromosomas[this.cantidadCromosomas - 1].getAptitud();

        /*
        Generamos un número aleatorio, y según donde caiga dentro del array de rangos, se añadirá el cromosoma relativo
        a su posición a la lista final de la población
         */
        System.out.println("RANGO");
        for (int k = 0; k < this.cantidadCromosomas; k++) {
            System.out.print(rango[k] + ",");
        }
        System.out.println();

        this.totalAptitudes = this.calcularTotal();

        /*
        Elitismo
        Se añade el numero de mejores cromosomassen una posición aleatoria dentro de la población
         */

        /*
        Creamos una lista auxiliar para ordenar los cromosomas por aptitud, y añadirlos a la nueva lista
         */
        Cromosoma [] cromosomasOrdenados = this.copiarCromosomas(this.cromosomas);
        this.ordenarCromosomas(cromosomasOrdenados);
        int x = 0;
        while (x < this.numeroElitismo){

            randomElitismo = Extra.numeroAleatorio(0,this.cantidadCromosomas-1);
            if(nuevosCromosomas[randomElitismo] == null){
                nuevosCromosomas[randomElitismo] = new Cromosoma(cromosomasOrdenados[x]);
                x++;
            }

        }

        int i = 0;
        while(i < this.cantidadCromosomas){

            random = Extra.numeroAleatorio(0, this.totalAptitudes);
            elegido = 0;

            while (random > rango[elegido]) {
                if (elegido < this.cantidadCromosomas)
                    elegido++;
            }

            System.out.println("RANDOM");
            System.out.println(random + " y el cromosoma escogido es : " + elegido);
            System.out.println(this.cromosomas[elegido]);

            /*
            Si ya existe un cromosoma en esa posición, buscamos avanzamos la posición de la población sin añadir el cromsoma
            que hemos encontrado en la ruleta con pesos
             */
            if (nuevosCromosomas[i] == null){
                nuevosCromosomas[i] = new Cromosoma(this.cromosomas[elegido]);
                i++;
            }else {
                i++;
            }
        }

        this.cromosomas = nuevosCromosomas;

        return new Poblacion(this.cromosomas);
    }

    /**
     * Método para cruzar valores de los genes entre los cromosomas de la población de 2 en 2
     *
     * @return Una nueva población en la que los cromosomas tienen valores intercambiados-
     * @throws AlgoritmoGeneticoExcepcion
     */
    public Poblacion cruzar() throws AlgoritmoGeneticoExcepcion {

        int mezclaAux = 0;
        if (this.puntoDeCorte >= this.getCantidadGenesCromosoma()) {
            throw new AlgoritmoGeneticoExcepcion("El punto de corte no puede ser mayor que el número de genes" +
                    "del cromosoma");
        }


        for (int i = 0; i < this.cantidadCromosomas; i += 2) {
            for (int j = this.puntoDeCorte; j < this.getCantidadGenesCromosoma(); j++) {
                mezclaAux = this.cromosomas[i].getGenes()[j];
                this.cromosomas[i].getGenes()[j] = this.cromosomas[i + 1].getGenes()[j];
                this.cromosomas[i + 1].getGenes()[j] = mezclaAux;
            }
        }
        return new Poblacion(this.cromosomas);
    }

    /**
     * Método para mutar determinados cromosomas de la población según la probabilidad de mutación
     *
     * @return Una nueva población, con cromosomas que han cambiado el valor de alguno de sus genes
     */
    public Poblacion mutar() {

        float randomMutacion = 0f;
        int randomGen = 0;

        if (this.mutacionPorGenes == true) {
            for (int i = 0; i < this.cantidadCromosomas; i++) {
                for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                    randomMutacion = Extra.numeroAleatorioFlaot();
                    randomGen = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
                    if (randomMutacion < this.probabilidadMutacionGen) {
                        this.cromosomas[i].getGenes()[randomGen] = Extra.numeroAleatorio(minimos[randomGen], maximos[randomGen]);
                    }

                }
            }
        } else {
            for (int i = 0; i < this.cantidadCromosomas; i++) {
                randomMutacion = Extra.numeroAleatorioFlaot();
                randomGen = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
                if (randomMutacion < this.probabilidadMutacionCromosoma) {
                    this.cromosomas[i].getGenes()[randomGen] = Extra.numeroAleatorio(minimos[randomGen], maximos[randomGen]);
                }
            }

        }
        return new Poblacion(this.cromosomas);
    }

    /**
     * Método que calcula toda la secuencia del algoritmo genético, pero sin salidas por pantalla
     *
     * @return Mejor cromosoma al final de la ejecución
     * @throws AlgoritmoGeneticoExcepcion
     */
    public Cromosoma algoritmoGeneticoBase() throws AlgoritmoGeneticoExcepcion {

        int generacion = 1;
        this.crearPoblacion();
        do {
            this.evaluar();
            this.seleccionar();
            this.cruzar();
            this.mutar();
            this.evaluar();
            this.calcularTotal();
            this.aptitudesGeneracion.add(totalAptitudes);
            generacion++;
        } while (generacion < this.maximoGeneraciones);
        return mejorCromosoma();
    }

    /**
     * Método que calcula toda la secuencia del algoritmo genético, con salidas por pantalla
     *
     * @return Mejor cromosoma al final de la ejecución
     * @throws AlgoritmoGeneticoExcepcion
     */
    public Cromosoma algoritmoGeneticoBaseString() throws AlgoritmoGeneticoExcepcion {

        int generacion = 1;

        System.out.println("\n--------------------------------POBLACION INICIAL");
        this.crearPoblacion();
        System.out.println(this.toString());

        do {
            this.evaluar();

            System.out.println("\n--------------------------------TOTAL TRAS EVALUAR: " + generacion);
            System.out.println(this.calcularTotal());
            System.out.println("------------------------MEJOR CROMOSOMA:");
            for (int i = 0; i < this.getCantidadGenesCromosoma(); i++) {
                System.out.print(this.mejorCromosoma().getGenes()[i] + ",");
            }
            System.out.println("Aptitud: " + this.mejorCromosoma().getAptitud());


            System.out.println("\n--------------------------------SELECCIONAR Generacion: " + generacion);
            this.seleccionar();
            System.out.println(this.toString());

            System.out.println("\n--------------------------------TOTAL TRAS SELECCIONAR");
            System.out.println(this.calcularTotal());

            System.out.println("\n--------------------------------CRUZAR Generacion: " + generacion);
            this.cruzar();
            System.out.println(this.toString());

            System.out.println("\n--------------------------------MUTAR Generacion: " + generacion);
            this.mutar();


            this.evaluar();
            System.out.println(this.toString());


            System.out.println("\n--------------------------------TOTAL FINAL Generacion: " + generacion);
            System.out.println(this.calcularTotal());

            if (this.intervaloGrafica == true) {
                if (generacion % this.numeroIntervalo == 0){
                    this.aptitudesGeneracion.add(totalAptitudes);
                    this.aptitudesMejorCromosomaGeneracion.add(mejorCromosoma().getAptitud());
                }
            } else{
                this.aptitudesGeneracion.add(totalAptitudes);
                this.aptitudesMejorCromosomaGeneracion.add(mejorCromosoma().getAptitud());
            }
            generacion++;


        } while (generacion < this.maximoGeneraciones);


        System.out.println("------------------------MEJOR CROMOSOMA:");
        for (int i = 0; i < this.getCantidadGenesCromosoma(); i++) {
            System.out.print(this.mejorCromosoma().getGenes()[i] + ",");
        }
        System.out.println("Aptitud: " + this.mejorCromosoma().getAptitud());

        new Ventana(this.aptitudesGeneracion, 0);
        new Ventana(this.aptitudesMejorCromosomaGeneracion, 1);
        return mejorCromosoma();
    }


    /**
     * Método auxiliar para copiar la lista de cromosomas
     *
     * @param cromosomas
     * @return
     */
    public Cromosoma[] copiarCromosomas(Cromosoma [] cromosomas){
        Cromosoma [] listaCopia = new Cromosoma[this.cantidadCromosomas];
        for (int i = 0; i < cantidadCromosomas; i++){
            listaCopia[i] = new Cromosoma(this.cromosomas[i]);
        }

        return listaCopia;
    }


    /**
     * Método que calcula el mejor cromosoma de la población
     *
     * @return El mejor cromosoma de la población
     */
    public Cromosoma mejorCromosoma() {

        int maximo = Integer.MIN_VALUE, indice = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++) {
            if (this.cromosomas[i].getAptitud() > maximo) {
                maximo = this.cromosomas[i].getAptitud();
                indice = i;
            }
        }
        return cromosomas[indice];
    }

    /**
     * Método auxiliar para ordenar la lista de cromosomas según su aptitud, y poder escoger los mejores para el elitismo
     *
     * @param listaCromosoma
     * @return lista ordenada según la aptitud de sus cromosomas
     */
    public Cromosoma[] ordenarCromosomas(Cromosoma[] listaCromosoma){
        Arrays.sort(listaCromosoma);
        return listaCromosoma;
    }

    /**
     * Getter de la cantidad de cromosomas que tiene la población
     *
     * @return Cantidad de cromosomas que tiene la aplicación
     */
    public int getCantidadCromosomas() {
        return this.cantidadCromosomas;
    }

    /**
     * Getter de la cantidad de genes que tienen los cromosomas de la población
     *
     * @return Cantidad de genes que tiene cada cromosoma de la población
     */
    public int getCantidadGenesCromosoma() {
        return new Cromosoma().getCantidadGenes();
    }


    public ArrayList getAptitudesGeneracion() {
        return aptitudesGeneracion;
    }


    public String aptitudesToString() {

        StringBuffer cadena = new StringBuffer();
        cadena.append("\n");
        for (int i = 0; i < this.getCantidadCromosomas(); i++) {
            cadena.append(this.cromosomas[i].getAptitud() + "\n");
        }
        return cadena.toString();
    }

    public String toString() {

        StringBuffer cadena = new StringBuffer();
        for (int i = 0; i < this.cantidadCromosomas; i++) {
            cadena.append("\n");
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                cadena.append(cromosomas[i].getGenes()[j] + ",");
            }
            cadena.append("\n Aptitud: " + cromosomas[i].getAptitud());
        }
        return cadena.toString();
    }
}
