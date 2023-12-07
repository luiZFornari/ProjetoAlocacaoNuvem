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
import java.util.*;
import java.util.concurrent.BlockingQueue;

import javax.lang.model.util.ElementScanner6;

import util.Alocacao;
import util.ClienteNuvem;
import util.Estados;
import util.Status;
import util.Unidade;


public class Server {


    Random random = new Random();
    private ServerSocket serverSocket;

    private ArrayList<User> usuarios;
    
    ArrayList<Unidade> poolDeRecursos;
    ArrayList<ClienteNuvem> clientes;
    ArrayList<Alocacao> alocacoes;

    Float valorLucro = 0f;
    Float valorGasto = 0f;
  
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

        for (int i = 0; i < 5; i++) {
            criarRecursos();
        }

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


    protected Boolean alocaRecurso( Integer qtdRecurso, Integer id, Integer cliente ){

        //Algoritmo para decidir onde alocar
        ClienteNuvem clienteN = null;
        Alocacao novaAloc = null;
        LocalDateTime currentTime = LocalDateTime.now();


        for (int i=0; i<=clientes.size() ;i++){
            if (clientes.get(i).getId().equals(cliente)){
                clienteN = clientes.get(i) ;
                novaAloc = new Alocacao(id,clienteN, null, currentTime, qtdRecurso);

                //algoritmo pra alocar
                boolean alocou = firstfit(novaAloc);
                if (alocou) {
                    clientes.get(i).addAlocacao(novaAloc);
                    listarAlocacoes();
                    return true;
                }else {
                    return false;
                }
            }
        }

        return  null;
    }

    private void listarAlocacoes(){
        for (Alocacao u : alocacoes) {
            System.out.println("\n|ID:" + u.getId() + " , Unidade:" + u.getUnidade().getId() + ", Poder:" + u.getCapacidadeComputacional()+ "|");
        }
    }
    protected Float desalocaRecurso(Integer id, Integer cliente){
        Float valorLucroUn = null;
        Float valorGastoUn = null;

        LocalDateTime currentTime = LocalDateTime.now();

        for (Alocacao u : alocacoes) {
            if (id.equals(u.getId())) {
                Unidade unidadealoc = u.getUnidade();

                Duration duration = Duration.between(u.getInicio(), currentTime);

                valorLucroUn = (float) (duration.getSeconds() * (unidadealoc.getCustoPorTempo())) * 2;
                valorGastoUn = (float) (duration.getSeconds() * unidadealoc.getCustoPorTempo());

                valorLucro += valorLucroUn;
                valorGasto += valorGastoUn;
                unidadealoc.removeItem(u.getCapacidadeComputacional());
                if (valorLucroUn != null && valorGastoUn != null) {
                    System.out.println("Valor lucro unidade:" + (valorLucroUn - valorGastoUn));
                    System.out.println("Valor gasto unidade:" + ( valorGastoUn));
                    System.out.println("Valor Lucro Global: " + (valorLucro - valorGasto));
                    System.out.println("Valor Gasto Global: " + (valorGasto));
                }
                if (unidadealoc.getCapacidadeAtual().equals( unidadealoc.getCapacidadeTotal()))
                {
                    unidadealoc.setLigado(false);
                }

                for (ClienteNuvem clienteObj : clientes) {
                    if (cliente.equals(clienteObj.getId())) {
                       clienteObj.removeAlocacao(id);
                    }
                }

                return valorLucroUn;
            }
        }

    return  valorLucroUn;
    }


    protected String listaRecursoCliente(Integer cliente){
        String listaRecurso = "Alocacoes:\n";
        ClienteNuvem clienteN = null;

        for (int i=0; i<=clientes.size() ;i++){
            if (clientes.get(i).getId().equals(cliente)){
                clienteN = clientes.get(i) ;
                break;
            }
        }

        for (Alocacao u : clienteN != null ? clienteN.getAlocacoes() : null) {
            listaRecurso +="\n|ID:" + u.getId() + " , Unidade:" + u.getUnidade().getId() + ", Poder:" + u.getCapacidadeComputacional()+ "|";

       }

        return  listaRecurso;
    };

    private Boolean firstfit(Alocacao alocacao){
        boolean alocado = false;
        boolean execucao = true;
        Integer qtdRecurso = alocacao.getCapacidadeComputacional();

        while (execucao) {

            for (Unidade unidade : poolDeRecursos) {
                if (unidade.getLigado() && unidade.canFit(qtdRecurso)) {
                    unidade.addItem(qtdRecurso);
                    alocacao.setUnidade(unidade);
                    alocacoes.add(alocacao);
                    System.out.println("Recurso alocado na unidade: " + unidade.getId());
                    execucao = false;
                    alocado = true;
                }
            }

            if (!alocado) {
                int cont=0;
                for (Unidade unidade : poolDeRecursos) {
                    cont ++;
                    if (!unidade.getLigado()) {
                        unidade.setLigado(true);
                        break;
                    }
                    if (cont == 5){
                        execucao = false;
                    }

                }
            }


        }
        return alocado;
    }

    private void criarRecursos(){
        Unidade unidade1 = new Unidade(random.nextInt(100),false, 10.0f, 100, 100, LocalDateTime.now());
        poolDeRecursos.add(unidade1);
    }

    public  void RealocaRecursos(){

        for (Alocacao aloc : alocacoes) {
            aloc.setUnidade(null);
        }

        for (Alocacao aloc : alocacoes) {
            firstfit(aloc);
        }


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
