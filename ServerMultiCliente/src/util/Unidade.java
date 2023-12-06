package util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Unidade  implements Serializable {
    Integer id;
    Boolean ligado;
    Float custoPorTempo;
    Integer capacidadeTotal, capacidadeAtual;

    LocalDateTime tempoInicio;
    LocalDateTime tempoFim;
   
    public Unidade(int id,Boolean ligado, Float custo, Integer capacidadeTotal, Integer capacidadeAtual, LocalDateTime tempoInicio) {
        this.id = id;
        this.ligado = ligado;
        this.custoPorTempo = custo;
        this.capacidadeTotal = capacidadeTotal;
        this.capacidadeAtual = capacidadeAtual;
        this.tempoInicio = tempoInicio;
        tempoFim = null;
    }

    public boolean canFit(Integer size) {
        boolean temCapacidade = false;
        if ( this.capacidadeAtual - size >= 0) {
            temCapacidade = true;
        }

        return temCapacidade;
    }

    public void removeItem(Integer size) {
        this.capacidadeAtual += size;
    }

    public void addItem(Integer size) {
        this.capacidadeAtual -= size;
    }

    public int getRemainingSpace() {
        return this.capacidadeTotal - this.capacidadeAtual;
    }
    
    public Boolean getLigado() {
        return ligado;
    }
    public void setLigado(Boolean ligado) {
        this.ligado = ligado;
        if(ligado){
            //pegar tempo de referÊncia de quando foi ligada
            this.tempoInicio = LocalDateTime.now();
        }else{
            //pegar tempo de referencia do fim
            this.tempoFim = LocalDateTime.now();
        }
    }

    public Integer getId() {
        return id;
    }



    public LocalDateTime getTempoInicio() {
        return tempoInicio;
    }

    public void setTempoInicio(LocalDateTime tempoInicio) {
        this.tempoInicio = tempoInicio;
    }

    public LocalDateTime getTempoFim() {
        return tempoFim;
    }

    public void setTempoFim(LocalDateTime tempoFim) {
        this.tempoFim = tempoFim;
    }

    public Float getCustoPorTempo() {
        return custoPorTempo;
    }
    public void setCustoPorTempo(Float custo) {
        this.custoPorTempo = custo;
    }
    public Integer getCapacidadeTotal() {
        return capacidadeTotal;
    }
    public void setCapacidadeTotal(Integer capacidadeTotal) {
        this.capacidadeTotal = capacidadeTotal;
    }
    public Integer getCapacidadeAtual() {
        return capacidadeAtual;
    }
    public void setCapacidadeAtual(Integer capacidadeAtual) {
        this.capacidadeAtual = capacidadeAtual;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ligado == null) ? 0 : ligado.hashCode());
        result = prime * result + ((custoPorTempo == null) ? 0 : custoPorTempo.hashCode());
        result = prime * result + ((capacidadeTotal == null) ? 0 : capacidadeTotal.hashCode());
        result = prime * result + ((capacidadeAtual == null) ? 0 : capacidadeAtual.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Unidade other = (Unidade) obj;
        if (ligado == null) {
            if (other.ligado != null)
                return false;
        } else if (!ligado.equals(other.ligado))
            return false;
        if (custoPorTempo == null) {
            if (other.custoPorTempo != null)
                return false;
        } else if (!custoPorTempo.equals(other.custoPorTempo))
            return false;
        if (capacidadeTotal == null) {
            if (other.capacidadeTotal != null)
                return false;
        } else if (!capacidadeTotal.equals(other.capacidadeTotal))
            return false;
        if (capacidadeAtual == null) {
            if (other.capacidadeAtual != null)
                return false;
        } else if (!capacidadeAtual.equals(other.capacidadeAtual))
            return false;
        return true;
    }
}
