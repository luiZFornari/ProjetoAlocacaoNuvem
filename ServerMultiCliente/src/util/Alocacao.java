package util;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Alocacao implements Serializable {
    Integer id;
    ClienteNuvem cliente;
    Unidade unidade;
    LocalDateTime  fim;
    LocalDateTime inicio;
    Integer migracoes, capacidadeComputacional;
    public Alocacao(Integer id,ClienteNuvem cliente, Unidade unidade, LocalDateTime inicio, Integer capacidadeComputacional) {
        this.id= id;
        this.cliente = cliente;
        this.unidade = unidade;
        this.inicio = inicio;
        this.capacidadeComputacional = capacidadeComputacional;
    }
    public ClienteNuvem getCliente() {
        return cliente;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setCliente(ClienteNuvem cliente) {
        this.cliente = cliente;
    }
    public Unidade getUnidade() {
        return unidade;
    }
    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }
    public LocalDateTime getInicio() {
        return inicio;
    }
    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }
    public LocalDateTime getFim() {
        return fim;
    }
    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }
    public Integer getMigracoes() {
        return migracoes;
    }
    public void setMigracoes(Integer migracoes) {
        this.migracoes = migracoes;
    }
    public Integer getCapacidadeComputacional() {
        return capacidadeComputacional;
    }
    public void setCapacidadeComputacional(Integer capacidadeComputacional) {
        this.capacidadeComputacional = capacidadeComputacional;
    }
    
}
