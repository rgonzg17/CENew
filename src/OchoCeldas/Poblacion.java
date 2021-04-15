package OchoCeldas;

import excepciones.AlgoritmoGeneticoExcepcion;
import extras.Extra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Poblacion {


    private static final Cromosoma solucion = new Cromosoma();
    //VALORES QUE SE PUEDEN CAMBIAR
    /**
     * Cantidad de cromosomas dentro de la población (Necesario que sea par para realizar bien el cruzamiento
     */
    private static final int cantidadCromosomas = 6;

    /**
     * Cantidad de generaciones que se van a repetir
     */
    private static final int maximoGeneraciones = 100;


    /**
     * Número de mejores cromosomas que se van a seleccionar, por defecto solo se escogerá al mejor
     */
    private static final int numeroElitismo = 3;

    /**
     * Variable que indica desde donde cortar en el cromosoma, por defecto se inicia en 2
     */
    private static final int puntoDeCorte = 4;

    /**
     * Probabilidad de cruzamiento
     */
    private static final float probabilidadCruzamiento= 0.9f;

    /**
     * Por defecto, la mutación será por cromosoma, a no ser que se ponga esta variable a true
     */
    private static final boolean mutacionPorGenes = true;

    /**
     * Probabilidad de que un gen de un cromosoma mute
     */

    private static final float probabilidadMutacionGen = 0.2f;
    /**
     * Probabilidad de que un cromosoma mute alguno de sus genes.
     */
    private static final float probabilidadMutacionCromosoma = 0.15f;

    /**
     * Si está false, se considerará el número de generaciones como condición de parada
     * Si está true, se considerará el método condición de parada junto a la variable generaciónMejora
     */
    private static final boolean condicionParada = true;

    /**
     * Cantidad de generaciones que tiene que ser mejor la última de ellas para continuar iterando.
     * Si la variable tiene el valor "2", el algoritmo deberá ser mejor que las dos últimas generaciones para continuar
     */
    private static final int generacionMejora = 5;

    /**
     * Si está en false, se mostrarán todas las generaciones en el gráfico.
     * Si está en true, se mostrarán las generaciones de 10 en 10 en el gráfico
     */
    private static final boolean intervaloGrafica = false;

    /**
     * Elegir cada cuantos números hacer un intervalo.
     * Solo estará habilitado cuando intervaloGrafica esté en true
     * Por defecto está en 10
     */
    private static final int numeroIntervalo = 10;

    /**
     * Mínimos que deben ser (según su par) menores que los máximos
     */
    private static int minimo = 0;

    /**
     * Máximos que deben ser (según su par) mayores que los mínimos
     */
    private static int maximo = 9;


    //VARIABLES QUE NO SE PUEDEN MODIFICAR
    private Cromosoma[] cromosomas = new Cromosoma[this.cantidadCromosomas];
    private static final ArrayList aptitudesGeneracion = new ArrayList();
    private static final ArrayList aptitudesMejorCromosomaGeneracion = new ArrayList();
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
     * Método para crear poblacion con números aleatorios entre 0 y 9 y diferentes
     *
     * @return Una población con todos los cromosomas creados de manera aleatoria
     */
    public Poblacion crearPoblacion() {
        int numero = 0;
        for (int i = 0; i < this.cantidadCromosomas; i++) {
            List<Integer> repetidos = new ArrayList<Integer>();
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                /*
                *Para que no se repitan números en cada cromosoma, añadimos los números que ya han salido a una lista
                * y obligamos a recalcular el número si ya había salido anteriormente (Si está en la lista de números que
                * ya han salido
                */
                while (repetidos.contains(numero))
                    numero = Extra.numeroAleatorio(minimo,maximo);
                this.cromosomas[i].getGenes()[j] = numero;
                repetidos.add(numero);
            }
        }
        return new Poblacion(this.cromosomas);
    }

    /**
     * Método para crear la solución final, de forma aleatoria
     *
     * @return
     */
    public void crearSolucion(){

        List<Integer> repetidos = new ArrayList<Integer>();
        int numero =0;
        for (int i=0; i<this.getCantidadGenesCromosoma(); i++){
            while (repetidos.contains(numero))
                numero = Extra.numeroAleatorio(minimo,maximo);
            solucion.getGenes()[i] = numero;
            repetidos.add(numero);
        }
    }



    /**
     * Método para evaluar los cromosomas. Se suma los valores de los genes para calcular su aptitud
     *
     * @return Una lista de aptitudes de los cromosomas
     */
    public void evaluar() {
        int aptitud=0;
        for (int i = 0; i < this.cantidadCromosomas; i++) {
            aptitud =evaluarCromosoma(cromosomas[i]);
            this.cromosomas[i].setAptitud(aptitud);
        }

    }

    public int evaluarCromosoma(Cromosoma cromosoma){

        int aptitud=0;
        boolean[] coincididoPosicion= new boolean[this.getCantidadGenesCromosoma()];
        for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
            if (coincididoPosicion[j]==false)
                if (solucion.getGenes()[j] == cromosoma.getGenes()[j]){
                    aptitud+=2;
                    coincididoPosicion[j] = true;
           }
        }
        return aptitud;
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

       Cromosoma[] nuevosCromosomas = new Cromosoma[this.cantidadCromosomas];
       int random=0;
       int cantidadLuchadores = 5;
       for (int i = 0; i<getCantidadCromosomas(); i++){
           List<Cromosoma> listaLuchadores = new ArrayList<Cromosoma>();
           List<Integer> listaPosicionesLuchadores = new ArrayList<Integer>();

           for (int j=0; j<cantidadLuchadores;j++){
               while (listaLuchadores.contains(cromosomas[random])) {
                   random = Extra.numeroAleatorio(0,getCantidadCromosomas()-1);
               }
               listaLuchadores.add(cromosomas[random]);
               listaPosicionesLuchadores.add(random);
           }
           while (nuevosCromosomas[random]!=null) {
               random = Extra.numeroAleatorio(0,getCantidadCromosomas()-1);
           }
           System.out.println("LUCHADORES QUE COMPITEN: ");
           for (Cromosoma cadaUno:listaLuchadores) {
               System.out.println(cadaUno + "  "+cadaUno.getAptitud());
           }
           System.out.println("EL MEJOR: " + listaLuchadores.stream().min(Cromosoma::compareTo)+" "+listaLuchadores.stream().min(Cromosoma::compareTo).get().getAptitud()+" En: "+
                   listaLuchadores.indexOf(listaLuchadores.stream().min(Cromosoma::compareTo).get()));

           nuevosCromosomas[i] = new Cromosoma(cromosomas[listaPosicionesLuchadores.get(listaLuchadores.indexOf(listaLuchadores.stream().min(Cromosoma::compareTo).get()))]);
           System.out.println("COINCIDE?: "+nuevosCromosomas[i]);
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
        Cromosoma[] nuevosCromosomas = new Cromosoma[this.cantidadCromosomas];
        int random;
        for (int i=0; i<getCantidadCromosomas()-1;i+=2){
            List<Integer> listaPadre1 = new ArrayList<Integer>();
            List<Integer> listaPadre2 = new ArrayList<Integer>();
            List<Integer> numerosOcupadosHijo1 = new ArrayList<Integer>();
            List<Integer> numerosOcupadosHijo2 = new ArrayList<Integer>();
            Cromosoma hijo1 = new Cromosoma();
            Cromosoma hijo2 = new Cromosoma();
            Cromosoma padre1 = new Cromosoma(cromosomas[i]);
            Cromosoma padre2 = new Cromosoma(cromosomas[i+1]);
            for (int j = puntoDeCorte; j<getCantidadGenesCromosoma(); j++){
                listaPadre1.add(padre1.getGenes()[j]);
                listaPadre2.add(padre2.getGenes()[j]);
                hijo1.getGenes()[j] = listaPadre2.get(j-puntoDeCorte);
                hijo2.getGenes()[j] = listaPadre1.get(j-puntoDeCorte);
                numerosOcupadosHijo1.add(hijo1.getGenes()[j]);
                numerosOcupadosHijo2.add(hijo2.getGenes()[j]);
            }
            for (int j = 0; j<puntoDeCorte;j++) {
                if (!numerosOcupadosHijo1.contains(padre1.getGenes()[j])) {
                    hijo1.getGenes()[j] = padre1.getGenes()[j];
                    numerosOcupadosHijo1.add(hijo1.getGenes()[j]);
                }
                if (!numerosOcupadosHijo2.contains(padre2.getGenes()[j])) {
                    hijo2.getGenes()[j] = padre2.getGenes()[j];
                    numerosOcupadosHijo2.add(hijo2.getGenes()[j]);
                }
            }

            for (int j = 0; j<getCantidadGenesCromosoma();j++) {
                rellenarHijos(numerosOcupadosHijo1, hijo1, j);
                rellenarHijos(numerosOcupadosHijo2, hijo2, j);
            }
            nuevosCromosomas[i] = hijo1;
            nuevosCromosomas[i+1]= hijo2;
        }
        this.cromosomas = nuevosCromosomas;

        return new Poblacion(this.cromosomas);
    }

    private void rellenarHijos(List<Integer> numerosOcupadosHijo, Cromosoma hijo, int j) {
        int random;
        if (hijo.getGenes()[j]==-1){
            random = Extra.numeroAleatorio(minimo,maximo);
            while (numerosOcupadosHijo.contains(random)){
                random = Extra.numeroAleatorio(minimo,maximo);
            }
            hijo.getGenes()[j] = random;
            numerosOcupadosHijo.add(hijo.getGenes()[j]);
        }
    }

    /**
     * Método para mutar determinados cromosomas de la población según la probabilidad de mutación
     *
     * @return Una nueva población, con cromosomas que han cambiado el valor de alguno de sus genes
     */
    public Poblacion mutar() {

        float randomMutacion = 0f;

        if (this.mutacionPorGenes == true) {
            for (int i = 0; i < this.cantidadCromosomas; i++) {
                for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                    cambioDeValores(i);
                }
            }
        } else {
            for (int i = 0; i < this.cantidadCromosomas; i++) {
                randomMutacion = Extra.numeroAleatorioFlaot();
                if (randomMutacion < this.probabilidadMutacionCromosoma) {
                     cambioDeValores( i);
                }
            }

        }
        return new Poblacion(this.cromosomas);
    }

    private void cambioDeValores(int i) {
        float randomMutacion;
        int randomGen1, randomGen2 = 0;
        int aux;
        randomMutacion = Extra.numeroAleatorioFlaot();
        randomGen1 = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
        while (randomGen1==randomGen2)
            randomGen2= Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
        if (randomMutacion < this.probabilidadMutacionGen) {
            aux = this.cromosomas[i].getGenes()[randomGen1];
            this.cromosomas[i].getGenes()[randomGen1] = this.cromosomas[i].getGenes()[randomGen2];
            this.cromosomas[i].getGenes()[randomGen2] = aux;
        }
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

        /*
        - Si la variable tipoParada tiene el valor 0, finalizará cuando el número de generaciones sea igual a la variable
        global maximoGeneraciones
        - Si la variable tipoParada tiene el valor 1, finalizará cuando se cumpla el método condicionParada()
         */
        int tipoParada = 0;
        if (this.condicionParada == true) tipoParada = 1;

        int generacion = 1;
        System.out.println("\n--------------------------------POBLACION INICIAL");
        this.crearPoblacion();
        System.out.println(this.toString());

        System.out.println("\n--------------------------------CREAMOS SOLUCION");
        this.crearSolucion();
        System.out.println(solucion.toString());


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


        } while (mejorCromosoma().esMismoCromosoma(solucion)==false);


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
     * Método auxiliar para determinar que tipo de parda se quiere para acabar con el agoritmo genético
     * Ya sea según el máximo de generaciones o la condición de parada en el método condicionParada
     *
     * @param tipo
     */
    public boolean parada(int tipo, int generacion, Cromosoma mejorCromosoma){
        if (tipo == 0) return maximoGeneraciones(generacion);
        else return condicionParada(mejorCromosoma);
    }

    /**
     * Método auxiliar para que la condición de parada del algoritmo genético sea la variable global, maximoGeneraciones
     */

    public boolean maximoGeneraciones(int generacion){
        if (generacion < this.maximoGeneraciones)  return false;
        else return true;
    }

    /**
     * Método auxiliar para que el algortimo finalice si no es mejor que el alguna de las "generacionMejora"
     * Si generacionMejora tiene el valor 1, la última aptitud del algoritmo deberá ser mejor que la anterior
     * Si generacionMejora tiene el valor 2, la última aptitud del algoritmo deberá ser mejor que al menos uno de los dos anteriores
     * @return
     */
    public boolean condicionParada(Cromosoma mejorCromosoma){
        return mejorCromosoma.compareTo(solucion) == 0;
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
