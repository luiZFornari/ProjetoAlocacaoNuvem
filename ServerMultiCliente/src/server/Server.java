/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.Client;
import util.Mensagem;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.lang.model.util.ElementScanner6;

import util.Alocacao;
import util.ClienteNuvem;
import util.Estados;
import util.Status;
import util.Unidade;

/**
 *
 * @author elder
 */
public class Server {
    /* 1 - Criar o servidor de conexões
     2 -Esperar o um pedido de conexão;
     Outro processo
     2.1 e criar uma nova conexão;
     3 - Criar streams sobre o  socket de comunicação entre servidor/cliente
     4.2 - Fechar streams de entrada e saída
     trada e saída;
     4 - Tratar a conversação entre cliente e 
     servidor (tratar protocolo);
     4.1 - Fechar socket de comunicação entre servidor/cliente
     4.2 - Fechar streams de entrada e saída

     */

    private ServerSocket serverSocket;


    private ArrayList<User> usuarios;
    
    ArrayList<Unidade> poolDeRecursos;
    ArrayList<ClienteNuvem> clientes;
    ArrayList<Alocacao> alocacoes;
  
    public Server(){
        usuarios = new ArrayList<>();

        poolDeRecursos = new ArrayList<>();
        clientes = new ArrayList<>();
        alocacoes = new ArrayList<>();
        ClienteNuvem cn = new ClienteNuvem(10,"teste");
        clientes.add(cn);
        usuarios.add(new User("Bianca", "159645"));
        usuarios.add(new User("Lucas", "12345"));
        usuarios.add(new User("luiz", "1"));

    }

    /*- Criar o servidor de conexões*/

    private void criarServerSocket(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);
    }

    protected User getUser(String nome){
        for (User u : usuarios) {
            if(u.nome.equals(nome))
             return u;
            
        }
        return null;
    }

    /*2 -Esperar o um pedido de conexão;
     Outro processo*/
    private Socket esperaConexao() throws IOException {
        Socket socket = serverSocket.accept();
        return socket;
    }

   
    public void connectionLoop() throws IOException{
        int id = 0;
        while (true) {
            System.out.println("Aguardando conexão...");
            Socket socket = esperaConexao();//protocolo
            System.out.println("Cliente conectado.");
            //Outro processo
            TrataConexao tc = new TrataConexao(this, socket, id++);
            Thread th = new Thread(tc);
            th.start();
            System.out.println("Cliente finalizado.");
        }

    }


    protected Alocacao alocaRecurso( Integer qtdRecurso, Integer id, Integer cliente ){

        //Algoritmo para decidir onde alocar
        ClienteNuvem clienteN = null;
        Alocacao novaAloc = null;
        LocalDateTime currentTime = LocalDateTime.now();

        for (int i=0; i<=clientes.size() ;i++){
            if (clientes.get(i).getId().equals(cliente)){
                clienteN = clientes.get(i) ;
                novaAloc = new Alocacao(id,clienteN, null, currentTime, qtdRecurso);
                clientes.get(i).addAlocacao(novaAloc);
                return novaAloc;
            }
        }

        return  novaAloc;
    }
    protected Float[] desalocaRecurso(Integer id){
        Float valorLucro = null;
        Float valorGasto = null;

        LocalDateTime currentTime = LocalDateTime.now();

        for (int i = 0; i < alocacoes.size(); i++) {
            if(alocacoes.get(i).getId() ==  id){
                Unidade unidadealoc = alocacoes.get(i).getUnidade();

                Duration duration = Duration.between(currentTime, alocacoes.get(i).getInicio());

                valorLucro = (float) (duration.getSeconds() * (unidadealoc.getCustoPorTempo()+ unidadealoc.getCustoPorTempo() * 0.2 ) * (alocacoes.get(i).getCapacidadeComputacional()/2));
                valorGasto = (duration.getSeconds() * unidadealoc.getCustoPorTempo());
                unidadealoc.removeItem(alocacoes.get(i).getCapacidadeComputacional());
                alocacoes.remove(i);
            };
        }
        Float[] valores = {valorGasto,valorLucro};

        return valores ;
    }

    protected String listaRecursoCliente(Integer cliente){
        String listaRecurso = "Alocacoes:\n";
        ClienteNuvem clienteN = null;

        System.out.println(clientes.size());
        for (int i=0; i<=clientes.size() ;i++){
            if (clientes.get(i).getId().equals(cliente)){
                System.out.println("entrou if");
                clienteN = clientes.get(i) ;
                System.out.println(clienteN.getId());
            }
            System.out.println("teste for");
        }
        System.out.println("saiu for");
        System.out.println(clienteN.getId());
//        for (Alocacao u : clienteN != null ? clienteN.getAlocacoes() : null) {
//            listaRecurso +="\n|ID" + u.getId() + " , Unidade" + u.getUnidade() + ", Poder" + u.getCapacidadeComputacional()+ "|";
//
//        }

        return  listaRecurso;
    };

    private void firstfit(Alocacao alocacao){
        boolean execucao = true;
        Integer qtdRecurso = alocacao.getCapacidadeComputacional();

        if (poolDeRecursos.isEmpty()){
            criarRecursos();
        }

        while (execucao) {
            for (Unidade unidade : poolDeRecursos) {
                if(unidade.getLigado() == false){
                    unidade.setLigado(true);
                }

                if (unidade.getLigado() && unidade.canFit(qtdRecurso)) {
                    unidade.addItem(qtdRecurso);
                    alocacao.setUnidade(unidade);
                    alocacoes.add(alocacao);
                    System.out.println("Recurso alocado na unidade: " + unidade);
                    execucao = false;
                }
            }
            criarRecursos();
        }

    }

    private void criarRecursos(){
        Unidade unidade1 = new Unidade(true, 10.0f, 100, 100, LocalDateTime.now());
    
        poolDeRecursos.add(unidade1);
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException {
        
        try {
            //1
            Server server = new Server();
            server.criarServerSocket(5555);

            server.connectionLoop();
        } catch (IOException e) {
            //trata exceção
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }



}
