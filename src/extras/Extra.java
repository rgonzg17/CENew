package extras;

import java.util.concurrent.ThreadLocalRandom;
public class Extra {

    private Extra(){}

    public static int numeroAleatorio(int minimo, int maximo){
        return ThreadLocalRandom.current().nextInt(minimo, maximo+1);
    }

    public static float numeroAleatorioFlaot(){
        return ThreadLocalRandom.current().nextFloat();
    }

}
