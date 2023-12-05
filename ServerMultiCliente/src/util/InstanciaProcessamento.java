package util;

public class InstanciaProcessamento {
    public Integer capacidadeTotal, capacidadeOcupada, id;

    public InstanciaProcessamento(Integer capacidadeTotal, Integer id){
        this.capacidadeTotal = capacidadeTotal;
        this.id = id;
        this.capacidadeOcupada = 0;
    }

    public Integer getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public void setCapacidadeTotal(Integer capacidadeTotal) {
        this.capacidadeTotal = capacidadeTotal;
    }

    public Integer getCapacidadeOcupada() {
        return capacidadeOcupada;
    }

    public void setCapacidadeOcupada(Integer capacidadeOcupada) {
        this.capacidadeOcupada = capacidadeOcupada;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
