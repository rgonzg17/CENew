package nueveCeldas;

import excepciones.AlgoritmoGeneticoExcepcion;

public class Gen {

    private int valor;
    private int maximo = 0;
    private int minimo = 0;

    public Gen(){}

    public Gen(int valor, int minimo, int maximo){

        this.valor = valor;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public void setValor(int valor) throws AlgoritmoGeneticoExcepcion {
        if (valor > this.minimo && valor < this.maximo)
            this.valor = valor;
        else
            throw new AlgoritmoGeneticoExcepcion("El valor del gen debe estar entre su mínimo: "+this.minimo
        +" y su máximo: "+this.maximo);
    }

    public void setMaximo(int maximo) throws AlgoritmoGeneticoExcepcion {

        if (this.minimo == 0 || maximo > this.minimo)
            this.maximo = maximo;

        else
            throw new AlgoritmoGeneticoExcepcion("El máximo debe ser mayor que el mínimo que es: "+this.minimo);
    }

    public void setMinimo(int minimo) throws AlgoritmoGeneticoExcepcion {

        if (this.maximo == 0 || this.maximo > minimo)
            this.minimo = minimo;

        else
            throw new AlgoritmoGeneticoExcepcion("El máximo debe ser mayor que el mínimo que es: "+this.minimo);
    }

    public int getValor() {
        return this.valor;
    }

    public int getMaximo() { return this.maximo; }

    public int getMinimo() { return this.minimo; }
}
