package nueveCeldas;

import excepciones.AlgoritmoGeneticoExcepcion;
import extras.Extra;
import extras.Ventana;

import java.util.ArrayList;
import java.util.List;

public class Poblacion {

    //VARIABLES QUE SE PUEDEN MODIFICAR

    /**
     * Cantidad de cromosomas dentro de la población (Necesario que sea par para realizar bien el cruzamiento
     * <p>
     * NECESARIO QUE SEA 6 Ó MÁS CROMOSOMAS PARA UN CORRECTO FUNCIONAMIENTO DE LA SELECCIÓN POR TORNEO
     */
    private static final int CANTIDAD_CROMOSOMAS = 6;

    /**
     * Variable que indica desde donde cortar en el cromosoma, por defecto se inicia en 2
     */
    private static final int PUNTO_DE_CORTE = 4;

    /**
     * Por defecto, la mutación será por cromosoma, a no ser que se ponga esta variable a true
     */
    private static final boolean MUTACION_POR_GENES = true;

    /**
     * Probabilidad de que un gen de un cromosoma mute
     */

    private static final float PROBABILIDAD_MUTACION_GEN = 0.2f;
    /**
     * Probabilidad de que un cromosoma mute alguno de sus genes.
     */
    private static final float PROBABILIDAD_MUTACION_CROMOSOMA = 0.15f;

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
    private static final int MINIMO = 0;

    /**
     * Máximos que deben ser (según su par) mayores que los mínimos
     */
    private static final int MAXIMO = 9;

    /**
     * SALIDAS POR PANTALLA
     */

    /**
     * Mostrar por pantalla la solucion del problema (recomendado)
     */
    private static final boolean DEBUG_SOLUCION = true;

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
    private static final Cromosoma solucion = new Cromosoma();
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

        int generacion = 1;
        this.crearPoblacion();
        debugCreacion(this.toString() + "\n");
        debugCreacion("Las aptitudes todavía son -1 puesto que no han sido calculadas en el método evaluar\n");

        this.crearSolucion();

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

        } while (mejorCromosoma().esMismoCromosoma(solucion) == false);

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
     * Método para crear poblacion con números aleatorios entre 0 y 9 y diferentes
     *
     * @return Una población con todos los cromosomas creados de manera aleatoria
     */
    public Poblacion crearPoblacion() {

        debugCreacion("------CREAMOS LA POBLACIÓN INICIAL DE NUESTRO ALGORITMO GENÉTICO: ------");

        int numero = 0;
        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
            List < Integer > repetidos = new ArrayList < Integer > ();
            for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                /*
                 *Para que no se repitan números en cada cromosoma, añadimos los números que ya han salido a una lista
                 * y obligamos a recalcular el número si ya había salido anteriormente (Si está en la lista de números que
                 * ya han salido
                 */
                while (repetidos.contains(numero))
                    numero = Extra.numeroAleatorio(MINIMO, MAXIMO);
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
    public void crearSolucion() {

        debugSolucion("------CREAMOS LA SOLUCIÓN: ------\n");

        List < Integer > repetidos = new ArrayList < Integer > ();
        int numero = 0;
        for (int i = 0; i < this.getCantidadGenesCromosoma(); i++) {
            while (repetidos.contains(numero))
                numero = Extra.numeroAleatorio(MINIMO, MAXIMO);
            solucion.getGenes()[i] = numero;
            repetidos.add(numero);
        }

        debugSolucion(solucion.toString());
    }

    /**
     * Método para evaluar los cromosomas. Si coinciden los números de nuestro cromosoma con la solución
     * se suman 2 puntos
     */
    public void evaluar() {

        debugEvaluacion("------EVALUAMOS CADA CROMOSOMA: ------\n");

        int aptitud = 0;
        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
            aptitud = evaluarCromosoma(cromosomas[i]);
            this.cromosomas[i].setAptitud(aptitud);
            debugEvaluacion("El cromosoma: " + cromosomas[i] + " tiene como aptitud: " + aptitud);
        }

        debugEvaluacion("\n");
    }

    /**
     * Método auxiliar para calcular la aptitud de cada cromosoma individualemnte
     *
     * @param cromosoma
     * @return aptitud de un cromosoma
     */
    public int evaluarCromosoma(Cromosoma cromosoma) {

        int aptitud = 0;
        boolean[] coincididoPosicion = new boolean[this.getCantidadGenesCromosoma()];
        for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
            if (coincididoPosicion[j] == false)
                if (solucion.getGenes()[j] == cromosoma.getGenes()[j]) {
                    aptitud += 2;
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
        for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
            this.totalAptitudes += this.cromosomas[i].getAptitud();
        }
        return this.totalAptitudes;
    }

    /**
     * Método para crear una nueva población, siguiendo la selección por torneo
     *
     * @return Una nueva población con cromosomas seleccionados tras competir entre ellos
     */
    public Poblacion seleccionar() {

        debugSeleccion("------SELECCIONAMOS DE LOS MEJORES CROMOSOMAS: ------\n");

        Cromosoma[] nuevosCromosomas = new Cromosoma[this.CANTIDAD_CROMOSOMAS];
        int random = 0;

        /*
         * Cantidad de luchadores que competirán en un torneo, solo habrá un ganador que pase a la poblacion final
         */
        int cantidadLuchadores = 5;

        debugSeleccion("Hay: " + cantidadLuchadores + " luchadores que competirán en un torneo para determinar quien pasa a la poblacion final");

        /*
         * Este algoritmo consiste en poner a competir a 5 cromosomas entre sí (que sean diferentes cromosomas dentro de la población)
         * Ganará el que tenga más aptitud de los 5 y pasará a la población final
         * El algoritmo se repite hasta que la nueva población tiene el mismo tamaño que la población anterior
         */
        for (int i = 0; i < getCantidadCromosomas(); i++) {
            List < Cromosoma > listaLuchadores = new ArrayList < Cromosoma > ();
            List < Integer > listaPosicionesLuchadores = new ArrayList < Integer > ();

            for (int j = 0; j < cantidadLuchadores; j++) {
                while (listaLuchadores.contains(cromosomas[random])) {
                    random = Extra.numeroAleatorio(0, getCantidadCromosomas() - 1);
                }
                listaLuchadores.add(cromosomas[random]);
                listaPosicionesLuchadores.add(random);
            }
            while (nuevosCromosomas[random] != null) {
                random = Extra.numeroAleatorio(0, getCantidadCromosomas() - 1);
            }

            debugSeleccion("\nLuchadores que compiten entre sí: ");

            for (Cromosoma cadaUno: listaLuchadores) {
                debugSeleccion(cadaUno + "  " + cadaUno.getAptitud());
            }

            debugSeleccion("El mejor ha sido: " + listaLuchadores.stream().min(Cromosoma::compareTo) + " " + listaLuchadores.stream().min(Cromosoma::compareTo).get().getAptitud() + " En: " +
                    listaLuchadores.indexOf(listaLuchadores.stream().min(Cromosoma::compareTo).get()));

            nuevosCromosomas[i] = new Cromosoma(cromosomas[listaPosicionesLuchadores.get(listaLuchadores.indexOf(listaLuchadores.stream().min(Cromosoma::compareTo).get()))]);
        }

        this.cromosomas = nuevosCromosomas;

        debugSeleccion("\nLa poblacion final tras la selección es: " + this.toString() + "\n");

        return new Poblacion(this.cromosomas);
    }

    /**
     * Método para cruzar valores de los genes entre los cromosomas de la población de 2 en 2
     * Para cruzar en este caso, se tienen que cambiar los valores pero mantener todos los números diferentes del 0 al 9
     *
     * @return Una nueva población en la que los cromosomas tienen valores intercambiados-
     * @throws AlgoritmoGeneticoExcepcion
     */
    public Poblacion cruzar() throws AlgoritmoGeneticoExcepcion {

        debugCruzamiento("------CRUZAMOS LOS CROMOSOMAS DE 2 EN 2: ------\n");

        Cromosoma[] nuevosCromosomas = new Cromosoma[this.CANTIDAD_CROMOSOMAS];

        /*
         * En este algoritmo, lo primero que hacemos es crear 2 hijos totalmente nuevos, a los que asociamos
         * los X genes del padre siguiente (o anterior) cruzándolos
         */
        debugSeleccion("Vamos a cruzar los siguientes 2 cromosomas: ");

        for (int i = 0; i < CANTIDAD_CROMOSOMAS - 1; i += 2) {

            List < Integer > listaPadre1 = new ArrayList < Integer > ();
            List < Integer > listaPadre2 = new ArrayList < Integer > ();
            List < Integer > numerosOcupadosHijo1 = new ArrayList < Integer > ();
            List < Integer > numerosOcupadosHijo2 = new ArrayList < Integer > ();
            Cromosoma hijo1 = new Cromosoma();
            Cromosoma hijo2 = new Cromosoma();
            Cromosoma padre1 = new Cromosoma(cromosomas[i]);
            Cromosoma padre2 = new Cromosoma(cromosomas[i + 1]);
            StringBuffer debugAux1 = new StringBuffer();
            StringBuffer debugAux2 = new StringBuffer();

            debugCruzamiento("Mezclamos el cromosoma: " + cromosomas[i] + " con el cromosoma: " + cromosomas[i + 1]);
            debugCruzamiento("Cogemos los genes desde " + PUNTO_DE_CORTE + " a " + getCantidadGenesCromosoma() + " de cada uno de los cromosomas");

            debugAux1.append("[");
            debugAux2.append("[");
            for (int j = 0; j < PUNTO_DE_CORTE; j++) {
                debugAux1.append(padre1.getGenes()[j] + ",");
                debugAux2.append(padre2.getGenes()[j] + ",");
            }
            debugAux1.append("|");
            debugAux2.append("|");

            /*
             * Cogemos desde el punto de corte hasta el final los genes del padre siguiente (o anterior) y lo añadimos
             * al hijo
             */
            for (int j = PUNTO_DE_CORTE; j < getCantidadGenesCromosoma(); j++) {
                listaPadre1.add(padre1.getGenes()[j]);
                listaPadre2.add(padre2.getGenes()[j]);
                hijo1.getGenes()[j] = listaPadre2.get(j - PUNTO_DE_CORTE);
                hijo2.getGenes()[j] = listaPadre1.get(j - PUNTO_DE_CORTE);
                numerosOcupadosHijo1.add(hijo1.getGenes()[j]);
                numerosOcupadosHijo2.add(hijo2.getGenes()[j]);

                debugAux1.append(padre1.getGenes()[j] + ",");
                debugAux2.append(padre2.getGenes()[j] + ",");
            }
            debugAux1.append("]");
            debugAux2.append("]");

            debugSeleccion(debugAux1.toString());
            debugSeleccion(debugAux2.toString());

            /*
             * Tras esto, tratamos de conservar los números originales de los padres en ambos hijos, que no hayan salido
             * o han sido ya colocados en el cruce previo
             */
            for (int j = 0; j < PUNTO_DE_CORTE; j++) {
                if (!numerosOcupadosHijo1.contains(padre1.getGenes()[j])) {
                    hijo1.getGenes()[j] = padre1.getGenes()[j];
                    numerosOcupadosHijo1.add(hijo1.getGenes()[j]);
                }
                if (!numerosOcupadosHijo2.contains(padre2.getGenes()[j])) {
                    hijo2.getGenes()[j] = padre2.getGenes()[j];
                    numerosOcupadosHijo2.add(hijo2.getGenes()[j]);
                }
            }

            /*
             * Rellenamos de forma aleatoria los números restantes que nos queden
             */
            for (int j = 0; j < getCantidadGenesCromosoma(); j++) {
                rellenarHijos(numerosOcupadosHijo1, hijo1, j);
                rellenarHijos(numerosOcupadosHijo2, hijo2, j);
            }
            nuevosCromosomas[i] = hijo1;
            nuevosCromosomas[i + 1] = hijo2;

            debugCruzamiento("Y el resultado es: ");
            debugCruzamiento("[" + cromosomas[i].toString() + "]");
            debugCruzamiento("[" + cromosomas[i + 1].toString() + "]");

        }
        this.cromosomas = nuevosCromosomas;

        return new Poblacion(this.cromosomas);
    }

    /**
     * Método auxiliar para rellenar los genes de los cromosomas hijos de los padres que se han cruzado
     *
     * @param numerosOcupadosHijo
     * @param hijo
     * @param j
     */
    private void rellenarHijos(List < Integer > numerosOcupadosHijo, Cromosoma hijo, int j) {
        int random;
        if (hijo.getGenes()[j] == -1) {
            random = Extra.numeroAleatorio(MINIMO, MAXIMO);
            while (numerosOcupadosHijo.contains(random)) {
                random = Extra.numeroAleatorio(MINIMO, MAXIMO);
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

        debugMutacion("\n------MUTAMOS ALGUN CROMOSOMA: ------\n");

        float randomMutacion = 0f;

        if (this.MUTACION_POR_GENES == true) {

            debugMutacion("Esta activada la mutación por genes: ");

            for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
                for (int j = 0; j < this.getCantidadGenesCromosoma(); j++) {
                    cambioDeValores(i);
                }
            }
        } else {

            debugMutacion("Esta activada la mutación por cromosoma: ");

            for (int i = 0; i < this.CANTIDAD_CROMOSOMAS; i++) {
                randomMutacion = Extra.numeroAleatorioFlaot();
                if (randomMutacion < this.PROBABILIDAD_MUTACION_CROMOSOMA) {
                    cambioDeValores(i);
                }
            }

        }

        debugMutacion("\nSi no aparece nada, es que la suerte (probabilidad) no ha querido que se mute ningún gen de ningún cromosoma\n");
        debugMutacion("La poblacion final tras la mutación es: " + this.toString() + "\n");
        return new Poblacion(this.cromosomas);
    }

    /**
     * Método auxiliar para cambiar los valores de los cromosomas de una manera de intercambio entre genes
     * Ya que no se pueden repetir valores entre los genes de los cromosomas
     *
     * @param i
     */
    private void cambioDeValores(int i) {
        float randomMutacion;
        int randomGen1, randomGen2 = 0;
        int aux;
        randomMutacion = Extra.numeroAleatorioFlaot();
        randomGen1 = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
        while (randomGen1 == randomGen2)
            randomGen2 = Extra.numeroAleatorio(0, this.getCantidadGenesCromosoma() - 1);
        if (randomMutacion < this.PROBABILIDAD_MUTACION_GEN) {
            aux = this.cromosomas[i].getGenes()[randomGen1];
            debugMutacion("Ha mutado el gen: " + aux + " del cromosoma :" + this.cromosomas[i].toString());
            this.cromosomas[i].getGenes()[randomGen1] = this.cromosomas[i].getGenes()[randomGen2];
            this.cromosomas[i].getGenes()[randomGen2] = aux;
            debugMutacion("Ahora ese gen es: " + this.cromosomas[i].getGenes()[randomGen1] + " y el cromosoma queda: " + this.cromosomas[i].toString());

        }
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
    private static void debugSolucion(String accion) {
        if (DEBUG_SOLUCION) System.out.println(accion);
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