package util;

import java.io.Serializable;
import java.util.ArrayList;

public class ClienteNuvem implements Serializable {
    Integer id;
    String tipo;
    Float contabilidade;
    ArrayList<Alocacao> alocacoes;
    
    public ClienteNuvem(Integer id, String tipo) {
        this.id = id;
        this.tipo = tipo;
        this.contabilidade = 0.0f;
        this.alocacoes = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Float getContabilidade() {
        return contabilidade;
    }

    public void setContabilidade(Float contabilidade) {
        this.contabilidade = contabilidade;
    }

    public ArrayList<Alocacao> getAlocacoes() {
        return alocacoes;
    }

    public void setAlocacoes(ArrayList<Alocacao> alocacoes) {
        this.alocacoes = alocacoes;
    }

    public void addAlocacao(Alocacao alocacao){
        this.alocacoes.add(alocacao);
    }
    
}
