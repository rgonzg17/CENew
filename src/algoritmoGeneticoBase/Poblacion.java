package algoritmoGeneticoBase;

import excepciones.AlgoritmoGeneticoExcepcion;
import extras.Extra;
import extras.Ventana;

import java.util.ArrayList;
import java.util.Arrays;

public class Poblacion {

    //VARIABLES QUE SE PUEDEN MODIFICAR

    /**
     * Cantidad de cromosomas dentro de la población
     */
    private static final int CANTIDAD_CROMOSOMAS = 20;

    /**
     * Cantidad de generaciones que se van a repetir
     */
    private static final int MAXIMO_GENERACIONES = 100;

    /**
     * Número de mejores cromosomas que se van a seleccionar, por defecto solo se escogerá al mejor
     */
    private static final int NUMERO_ELITISMO = 3;

    /**
     * Variable que indica desde donde cortar en el cromosoma (no en cuantas partes), por defecto se inicia en 2
     */
    private static final int PUNTO_DE_CORTE = 2;

    /**
     * Probabilidad de cruzamiento
     */
    private static final float PROBABILIDAD_CRUZAMIENTO = 0.9f;

    /**
     * Por defecto, la mutación será por cromosoma, a no ser que se ponga esta variable a true
     */
    private static final boolean MUTACION_POR_GENES = true;

    /**
     * Probabilidad de que un gen de un cromosoma mute
     */

    private static final float PROBABILIDAD_MUTACION_GEN = 0.015f;
    /**
     * Probabilidad de que un cromosoma mute alguno de sus genes.
     */
    private static final float PROBABILIDAD_MUTACION_CROMOSOMA = 0.015f;

    /**
     * Si está false, se considerará el número máximo de generaciones como condición de parada
     * Si está true, se considerará el método condición de parada junto a la variable generaciónMejora (parará si no mejora en x generaciones)
     */
    private static final boolean CONDICION_PARADA = false;

    /**
     * Cantidad de generaciones que tiene que ser mejor la última de ellas para continuar iterando.
     * Si la variable tiene el valor "2", el algoritmo deberá ser mejor que las dos últimas generaciones para continuar
     */
    private static final int GENERACION_MEJORA = 5;

    /**
     * Si está en false, se mostrarán todas las generaciones en el gráfico.
     * Si está en true, se mostrarán las generaciones de 10 en 10 en el gráfico
     */
    private static final boolean INTERVALO_GRAFICA = false;

    /**
     * Elegir cada cuantos números hacer un intervalo.
     * Solo estará habilitado cuando intervaloGrafica esté en true
     * Por defecto está en 10
     */
    private static final int NUMERO_INTERVALO = 10;

    /**
     * Mínimos que deben ser (según su par) menores que los máximos
     */
    private static final int[] MINIMOS = {
            1,
            5,
            2,
            25,
            15,
            60,
            8,
            9
    };

    /**
     * Máximos que deben ser (según su par) mayores que los mínimos
     */
    private static final int[] MAXIMOS = {
            30,
            10,
            35,
            100,
            55,
            200,
            40,
            50
    };

    /**
     * SALIDAS POR PANTALLA
     */

    /**
     * Mostrar por pantalla la creacion de la población
     */
    private static final boolean DEBUG_CREAR = false;

    /**
     * Mostrar por pantalla la evaluación de los cromosomas de la población
     */
    private static final boolean DEBUG_EVALUAR = false;

    /**
     * Mostrar por pantalla la selección de los cromosomas de la población
     */
    private static final boolean DEBUG_SELECCIONAR = false;

    /**
     * Mostrar por pantalla el cruce de los cromosomas de la población
     */
    private static final boolean DEBUG_CRUZAR = false;

    /**
     * Mostrar por pantalla la mutación de la población
     */
    private static final boolean DEBUG_MUTAR = false;

    /**
     * Mostrar por pantalla el tiempo que tarda en ejecutar el programa
     */
    private static final boolean DEBUG_TIEMPO = true;

    //VARIABLES QUE NO SE PUEDEN MODIFICAR
    private Cromosoma[] cromosomas = new Cromosoma[this.CANTIDAD_CROMOSOMAS];
    private static final ArrayList aptitudesGeneracion = new ArrayList();
    private static final ArrayList aptitudesMejorCromosomaGeneracion = new ArrayList();
    private int totalAptitudes;

    /**
     * Constructor
     */
    public Poblacion() {
        for (int i = 0; i < CANTIDAD_CROMOSOMAS; i++) {
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
     * Método que calcula toda la secuencia del algoritmo genético
     *
     * @return Mejor cromosoma al final de la ejecución
     * @throws AlgoritmoGeneticoExcepcion
     */
    public Cromosoma algoritmoGenetico() throws AlgoritmoGeneticoExcepcion {

        long startTime = System.currentTimeMillis();
        debugTiempo("Comienza el programa: ");
        /*
         *  - Si la variable tipoParada tiene el valor 0, finalizará cuando el número de generaciones sea igual a la variable
         * global maximoGeneraciones
         * - Si la variable tipoParada tiene el valor 1, finalizará cuando se cumpla el método condicionParada()
         */
        int tipoParada = 0;
        if (this.CONDICION_PARADA == true) tipoParada = 1;

        int generacion = 1;
        this.crearPoblacion();
        debugCreacion(this.toString() + "\n");
        debugCreacion("Las aptitudes todavía son 0 puesto que no han sido calculadas en el método evaluar\n");

        do {
            System.out.println("------GENERACIÓN: " + generacion + "------\n");

            this.evaluar();
            this.seleccionar();
            this.cruzar();
            this.mutar();
            this.evaluar();

            System.out.println("\n------TOTAL FINAL Generacion: " + generacion + "------");
            System.out.println(this.calcularTotal() + "\n");

            if (this.INTERVALO_GRAFICA == true) {
                if (generacion % this.NUMERO_INTERVALO == 0) {
                    this.aptitudesGeneracion.add(totalAptitudes);
                    this.aptitudesMejorCromosomaGeneracion.add(mejorCromosoma().getAptitud());
                }
            } else {
                this.aptitudesGeneracion.add(totalAptitudes);
                this.aptitudesMejorCromosomaGeneracion.add(mejorCromosoma().getAptitud());
            }
            generacion++;

        } while (parada(tipoParada, generacion) == false);

        System.out.println("------MEJOR CROMOSOMA: ------");
        System.out.print(this.mejorCromosoma().toString());
        System.out.println("Aptitud: " + this.mejorCromosoma().getAptitud());

        new Ventana(this.aptitudesGeneracion, 0);
        new Ventana(this.aptitudesMejorCromosomaGeneracion, 1);

        long endTime = System.currentTimeMillis() - startTime;
        debugTiempo("El programa ha tardado: "+endTime+"ms");

        return mejorCromosoma();
    }

    /**
     * Método para crear poblacion con números aleatorios
     *
     * @return Una población con todos los cromosomas creados de manera aleatoria
     */
    public Poblacion crearPoblacion() {

        debugCreacion("------CREAMOS LA POBLACIÓN INICIAL DE NUESTRO ALGORITMO GENÉTICO: ------");

        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                this.cromosomas[i].getGenes()[j] = Extra.numeroAleatorio(MINIMOS[j], MAXIMOS[j]);
            }
        }
        return new Poblacion(this.cromosomas);
    }

    /**
     * Método para evaluar los cromosomas. Se suma los valores de los genes para calcular su aptitud
     *
     */
    public void evaluar() {

        debugEvaluacion("------EVALUAMOS CADA CROMOSOMA: ------\n");

        int aptitud = 0;
        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                aptitud += this.cromosomas[i].getGenes()[j];
                debugEvaluacion("El cromosoma: " + cromosomas[i] + " tiene como aptitud: " + aptitud);
            }
            this.cromosomas[i].setAptitud(aptitud);
            aptitud = 0;
        }
        debugEvaluacion("\n");

    }

    /**
     * Método para calcular el total de la aptitud de toda la población
     *
     * @return La aptitud total de toda la población
     */
    public int calcularTotal() {
        this.totalAptitudes = 0;
        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
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

        debugSeleccion("------SELECCIONAMOS DE LOS MEJORES CROMOSOMAS: ------\n");

        int[] rango = new int[this.CANTIDAD_CROMOSOMAS];
        int random = 0, randomElitismo = 0, elegido = 0;
        Cromosoma[] nuevosCromosomas = new Cromosoma[this.CANTIDAD_CROMOSOMAS];

        debugSeleccion("Creamos el rango de aptitudes para la ruleta con pesos: ");

        /*
         * Calculamos el rango de aptitudes (la ruleta con pesos). Aquellos que tengan más aptitud, tendrán más espectro
         * Y por tanto una posibilidad relativa a su aptitud
         */
        rango[0] = cromosomas[0].getAptitud();
        for (int i = 1; i < CANTIDAD_CROMOSOMAS; i++) {
            rango[i] = rango[i - 1] + this.cromosomas[i].getAptitud();
        }
        rango[CANTIDAD_CROMOSOMAS - 1] = rango[CANTIDAD_CROMOSOMAS - 2] + cromosomas[CANTIDAD_CROMOSOMAS - 1].getAptitud();

        debugSeleccion(Arrays.toString(rango) + "\n");

        /*
         * Recalculamos el total de aptitudes
         */
        this.totalAptitudes = this.calcularTotal();

        /*
         * Elitismo
         * Se añade el numero de mejores cromosomassen una posición aleatoria dentro de la población
         * Creamos una lista auxiliar para ordenar los cromosomas por aptitud, y añadirlos a la nueva lista
         */
        debugSeleccion("El numero de elitismo seleccionado ha sido: " + NUMERO_ELITISMO);
        Cromosoma[] cromosomasOrdenados = this.copiarCromosomas(this.cromosomas);

        debugSeleccion("Se ordenan los cromosomas de menor a mayor aptitud para el elitismo: ");

        this.ordenarCromosomas(cromosomasOrdenados);
        for (int i = 0; i < getCantidadCromosomas(); i++)
            debugSeleccion(cromosomasOrdenados[i].toString() + " Aptitud: " + cromosomasOrdenados[i].getAptitud());
        int x = 0;

        debugSeleccion("\nSe introducen los " + NUMERO_ELITISMO + " mejores cromosomas en posiciones aleatorias dentro de la nueva poblacion");

        while (x < this.NUMERO_ELITISMO) {
            randomElitismo = Extra.numeroAleatorio(0, this.CANTIDAD_CROMOSOMAS - 1);
            if (nuevosCromosomas[randomElitismo] == null) {
                nuevosCromosomas[randomElitismo] = new Cromosoma(cromosomasOrdenados[x]);
                debugSeleccion("Se introduce el cromosoma: " + cromosomasOrdenados[x] + " Aptitud: " + cromosomasOrdenados[x].getAptitud());
                x++;
            }

        }

        /*
         *Generamos un número aleatorio, y según donde caiga dentro del array de rangos, se añadirá el cromosoma relativo
         * a su posición a la lista final de la población
         */

        debugSeleccion("\nGeneramos un numero aleatorio para ver en que posicion cae dentro de la ruleta con pesos: ");

        int i = 0;
        while (i < this.CANTIDAD_CROMOSOMAS) {

            random = Extra.numeroAleatorio(0, this.totalAptitudes);
            elegido = 0;
            debugSeleccion("El numero aleatorio ha sido el: " + random);
            while (random > rango[elegido]) {
                if (elegido < this.CANTIDAD_CROMOSOMAS)
                    elegido++;
            }

            debugSeleccion("El cromomosoma escogido ha sido el: " + cromosomas[elegido] + " en la posicion: " + elegido);

      /*
      Si ya existe un cromosoma en esa posición, buscamos avanzamos la posición de la población sin añadir el cromsoma
      que hemos encontrado en la ruleta con pesos
       */
            if (nuevosCromosomas[i] == null) {
                nuevosCromosomas[i] = new Cromosoma(this.cromosomas[elegido]);
                i++;
            } else {
                i++;
            }
        }
        this.cromosomas = nuevosCromosomas;

        debugSeleccion("\nLa poblacion final tras la selección es: " + this.toString() + "\n");

        return new Poblacion(this.cromosomas);
    }

    /**
     * Método para cruzar valores de los genes entre los cromosomas de la población de 2 en 2
     *
     * @return Una nueva población en la que los cromosomas tienen valores intercambiados-
     * @throws AlgoritmoGeneticoExcepcion
     */
    public Poblacion cruzar() throws AlgoritmoGeneticoExcepcion {

        debugCruzamiento("------CRUZAMOS LOS CROMOSOMAS DE 2 EN 2: ------\n");

        int mezclaAux = 0;
        float random = 0;

        /*
         * Se genera excepcion si se escoge un punto de corte mayor que la cantidad de genes que tenemos
         */
        if (this.PUNTO_DE_CORTE >= this.getCantidadGenesCromosoma()) {
            throw new AlgoritmoGeneticoExcepcion("El punto de corte no puede ser mayor que el número de genes" +
                    "del cromosoma");
        }

        debugCruzamiento("El punto de corte escogido ha sido: " + PUNTO_DE_CORTE);

        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i += 2) {
            StringBuffer debugAux1 = new StringBuffer();
            StringBuffer debugAux2 = new StringBuffer();
            random = Extra.numeroAleatorioFlaot();
            if (random < this.PROBABILIDAD_CRUZAMIENTO) {
                debugCruzamiento("Mezclamos el cromosoma: " + cromosomas[i] + " con el cromosoma: " + cromosomas[i + 1]);
                debugCruzamiento("Cogemos los genes desde " + PUNTO_DE_CORTE + " a " + getCantidadGenesCromosoma() + " de cada uno de los cromosomas");
                debugAux1.append("[");
                debugAux2.append("[");

                for (int k = 0; k < PUNTO_DE_CORTE; k++) {
                    debugAux1.append(cromosomas[i].getGenes()[k] + ",");
                    debugAux2.append(cromosomas[i + 1].getGenes()[k] + ",");
                }
                debugAux1.append("|");
                debugAux2.append("|");

                /*
                 * Vamos guardando los valores de la parte que queremos cruzar del primer cromosoma en la variable 'mezclaAux'
                 * Después realizamos la sustitución entre los dos cromosomas
                 */
                for (int j = this.PUNTO_DE_CORTE; j < this.getCantidadGenesCromosoma(); j++) {
                    mezclaAux = this.cromosomas[i].getGenes()[j];
                    this.cromosomas[i].getGenes()[j] = this.cromosomas[i + 1].getGenes()[j];
                    this.cromosomas[i + 1].getGenes()[j] = mezclaAux;
                    debugAux1.append(mezclaAux + ",");
                    debugAux2.append(this.cromosomas[i].getGenes()[j] + ",");
                }
                debugAux1.append("]");
                debugAux2.append("]");

                debugCruzamiento(debugAux1.toString());
                debugCruzamiento(debugAux2.toString());

                debugCruzamiento("Y el resultado es: ");
                debugCruzamiento("[" + cromosomas[i].toString() + "]");
                debugCruzamiento("[" + cromosomas[i + 1].toString() + "]");
            }
        }

        debugCruzamiento("\nSi algunos cromosomas no han sido cruzados es porque no han la suerte (probabilidad) ha querido que no se cruzasen\n");

        debugCruzamiento("\nLa poblacion final tras el cruzamiento es: " + this.toString() + "\n");
        return new Poblacion(this.cromosomas);
    }

    /**
     * Método para mutar determinados cromosomas de la población según la probabilidad de mutación
     *
     * @return Una nueva población, con cromosomas que han cambiado el valor de alguno de sus genes
     */
    public Poblacion mutar() {

        debugMutacion("------MUTAMOS ALGUN CROMOSOMA: ------\n");

        float randomMutacion = 0f;
        int randomGen = 0;

        /*
         * La mutación consiste en sustituir un gen aleatoriamente por un valor que esté comprendido entre los mínimos y máximos establecidos al principio de la clase
         */
        if (this.MUTACION_POR_GENES == true) {

            debugMutacion("Esta activada la mutación por genes: ");

            for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
                for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                    randomMutacion = Extra.numeroAleatorioFlaot();
                    randomGen = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
                    if (randomMutacion < this.PROBABILIDAD_MUTACION_GEN) {
                        debugMutacion("Ha mutado el gen: " + this.cromosomas[i].getGenes()[randomGen] + " del cromosoma :" + this.cromosomas[i].toString());
                        this.cromosomas[i].getGenes()[randomGen] = Extra.numeroAleatorio(MINIMOS[randomGen], MAXIMOS[randomGen]);
                        debugMutacion("Ahora ese gen es: " + this.cromosomas[i].getGenes()[randomGen] + " y el cromosoma queda: " + this.cromosomas[i].toString());
                    }

                }
            }
        } else {

            debugMutacion("Esta activada la mutación por cromosoma: ");

            for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
                randomMutacion = Extra.numeroAleatorioFlaot();
                randomGen = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
                if (randomMutacion < this.PROBABILIDAD_MUTACION_CROMOSOMA) {
                    debugMutacion("Ha mutado el gen: " + this.cromosomas[i].getGenes()[randomGen] + " del cromosoma :" + this.cromosomas[i].toString());
                    this.cromosomas[i].getGenes()[randomGen] = Extra.numeroAleatorio(MINIMOS[randomGen], MAXIMOS[randomGen]);
                    debugMutacion("Ahora ese gen es: " + this.cromosomas[i].getGenes()[randomGen] + " y el cromosoma queda: " + this.cromosomas[i].toString());
                }
            }

        }
        debugMutacion("\nSi no aparece nada, es que la suerte (probabilidad) no ha querido que se mute ningún gen de ningún cromosoma\n");
        debugMutacion("La poblacion final tras la mutación es: " + this.toString() + "\n");
        return new Poblacion(this.cromosomas);
    }


    /**
     * Método auxiliar para copiar la lista de cromosomas
     *
     * @param cromosomas
     * @return
     */
    public Cromosoma[] copiarCromosomas(Cromosoma[] cromosomas) {
        Cromosoma[] listaCopia = new Cromosoma[this.CANTIDAD_CROMOSOMAS];
        for (int i = 0; i < CANTIDAD_CROMOSOMAS; i++) {
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
        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
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
    public Cromosoma[] ordenarCromosomas(Cromosoma[] listaCromosoma) {
        Arrays.sort(listaCromosoma);
        return listaCromosoma;
    }

    /**
     * Método auxiliar para determinar que tipo de parda se quiere para acabar con el agoritmo genético
     * Ya sea según el máximo de generaciones o la condición de parada en el método condicionParada
     *
     * @param tipo
     */
    public boolean parada(int tipo, int generacion) {
        if (tipo == 0) return maximoGeneraciones(generacion);
        else return condicionParada();
    }

    /**
     * Método auxiliar para que la condición de parada del algoritmo genético sea la variable global, maximoGeneraciones
     */

    public boolean maximoGeneraciones(int generacion) {
        if (generacion < this.MAXIMO_GENERACIONES) return false;
        else return true;
    }

    /**
     * Método auxiliar para que el algoritmo finalice si no es mejor que el alguna de las "generacionMejora"
     * Si generacionMejora tiene el valor 1, la última aptitud del algoritmo deberá ser mejor que la anterior
     * Si generacionMejora tiene el valor 2, la última aptitud del algoritmo deberá ser mejor que al menos uno de los dos anteriores
     *
     * @return
     */
    public boolean condicionParada() {

        if (this.aptitudesGeneracion.size() <= this.GENERACION_MEJORA) return false;

        int i = this.aptitudesGeneracion.size() - this.GENERACION_MEJORA;

        while (i < this.aptitudesGeneracion.size()) {
            if ((int) this.aptitudesGeneracion.get(this.aptitudesGeneracion.size() - 1) < (int) this.aptitudesGeneracion.get(i - 1)) {
                i++;
            } else return false;
        }
        return true;
    }

    /**
     * Getter de la cantidad de cromosomas que tiene la población
     *
     * @return Cantidad de cromosomas que tiene la aplicación
     */
    public int getCantidadCromosomas() {
        return this.CANTIDAD_CROMOSOMAS;
    }

    /**
     * Getter de la cantidad de genes que tienen los cromosomas de la población
     *
     * @return Cantidad de genes que tiene cada cromosoma de la población
     */
    public int getCantidadGenesCromosoma() {
        return new Cromosoma().getCantidadGenes();
    }

    /**
     * Método para sacar por pantalla las acciones de la creación de la población
     *
     * @param accion
     */
    private static void debugCreacion(String accion) {
        if (DEBUG_CREAR) System.out.println(accion);
    }

    /**
     * Método para sacar por pantalla las acciones de la evaluación de la población
     *
     * @param accion
     */
    private static void debugEvaluacion(String accion) {
        if (DEBUG_EVALUAR) System.out.println(accion);
    }

    /**
     * Método para sacar por pantalla las acciones de la selección de la población
     *
     * @param accion
     */
    private static void debugSeleccion(String accion) {
        if (DEBUG_SELECCIONAR) System.out.println(accion);
    }

    /**
     * Método para sacar por pantalla las acciones del cruzamiento de la población
     *
     * @param accion
     */
    private static void debugCruzamiento(String accion) {
        if (DEBUG_CRUZAR) System.out.println(accion);
    }

    /**
     * Método para sacar por pantalla las acciones de la mutacion de la población
     *
     * @param accion
     */
    private static void debugMutacion(String accion) {
        if (DEBUG_MUTAR) System.out.println(accion);
    }

    /**
     * Método para sacar por pantalla las acciones de la mutacion de la población
     *
     * @param accion
     */
    private static void debugTiempo(String accion) {
        if (DEBUG_TIEMPO) System.out.println(accion);
    }


    /**
     * Método para mostrar por pantalla los genes de cada cromosoma de una forma clara
     *
     * @return
     */
    public String toString() {

        StringBuffer cadena = new StringBuffer();
        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
            cadena.append("\n");
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                cadena.append(cromosomas[i].getGenes()[j] + ",");
            }
            cadena.append("\n Aptitud: " + cromosomas[i].getAptitud());
        }
        return cadena.toString();
    }
}