package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import client.Client;
import util.*;

import static util.Estados.*;

public class TrataConexao implements Runnable {

    private Server server;
    private Socket socket;
    private int id;
    private String user;
    User auth;

    Random random = new Random();

    ObjectOutputStream output;
    ObjectInputStream input;

    private Estados estado;

    public TrataConexao(Server server, Socket socket, int id) {
        this.server = server;
        this.socket = socket;
        this.id = id;
        estado = Estados.CONECTADO;
        

    }

    private void fechaSocket(Socket s) throws IOException {
        s.close();
    }

    private void enviaMsg(Object o, ObjectOutputStream out) throws IOException {
        out.writeObject(o);
        out.flush();
    }

    private void trataConexao() throws IOException, ClassNotFoundException {
        try {
            /*  Criar streams de entrada e saída; */

            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Tratando...");

            // receber
            // validar a msg
            // if if if
            estado = Estados.CONECTADO;
            Mensagem msgEmEspera = null;
            while (estado != SAIR) {

                Mensagem m = (Mensagem) input.readObject();
                System.out.println("Mensagem do cliente:\n" + m);

                String operacao = m.getOperacao();
                Mensagem reply = new Mensagem(operacao + "REPLY");

                // estados conectado autenticado
                switch (estado) {
                    case CONECTADO:
                        switch (operacao) {
                            case "LOGIN":
                                try {
                                    String user = (String) m.getParam("user");
                                    String pass = (String) m.getParam("pass");
                                    Integer id = (Integer) m.getParam(("clienteN"));
                                    ClienteNuvem clienteNuvem = new ClienteNuvem(id,"padrao") ;

                                    auth = server.getUser(user);

                                    if (auth.nome.equals(user) && auth.senha.equals(pass)) {
                                        reply.setStatus(Status.OK);
                                        this.user = user;
                                        estado = Estados.AUTENTICADO;
                                    } else {
                                        reply.setStatus(Status.ERROR);
                                    }

                                    server.clientes.add(clienteNuvem);

                                } catch (Exception e) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro nos parâmetros do protocolo.");
                                }
                                break;
                            case "SAIR":
                                reply.setStatus(Status.OK);
                                estado = SAIR;
                                break;
                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NÃO AUTORIZADA OU INVÁLIDA!");

                                break;
                        }
                        break;
                    case AUTENTICADO:
                        switch (operacao) {
                            case "ALOCAR":
                                try {

                                    Integer qtdRecurso = (Integer) m.getParam("qtdRecurso");
                                    Integer cliente = (Integer) m.getParam("cliente") ;

                                    if (cliente == null || qtdRecurso == null) {
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg", "Erro: parametros");
                                        break;
                                    }
                                    if (qtdRecurso > 100)
                                    {
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Qtd maior que o permitido");
                                        break;
                                    }

                                    Boolean alocou = server.alocaRecurso(qtdRecurso, random.nextInt(100)+ id , cliente);
                                    reply.setStatus(Status.OK);
                                    reply.setParam("msg","Alocado: "+ alocou);

                                } catch (Exception e) {
                                    m.setStatus(Status.PARAMERROR);
                                    m.setParam("msg","Erro trycatch");
                                }
                                break;
                            case "REMOVER":
                                try {
                                    Integer alocacao = (Integer) m.getParam("id") ;
                                    Integer cliente = (Integer) m.getParam("cliente") ;

                                    if (alocacao == null || cliente == null ) {
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg", "Erro: parametros");
                                        break;
                                    }


                                    Float valor = server.desalocaRecurso(alocacao,cliente);
                                    if (valor == null) {
                                        reply.setStatus(Status.ERROR);
                                        reply.setParam("msg","Id invalido");

                                    }
                                    //server.RealocaRecursos();
                                    reply.setStatus(Status.OK);
                                    reply.setParam("msg","Custo : "  + valor);

                                } catch (Exception e) {
                                    m.setStatus(Status.ERROR);
                                    m.setParam("msg","Erro trycatch");
                                }
                                break;
                            case "LISTARALOCACAO":
                                try {

                                    Integer clienteid = (Integer) m.getParam("cliente");

                                    if (clienteid == null) {
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg", "Erro: cliente");
                                        break;
                                    }

                                    String recursos = server.listaRecursoCliente(clienteid);

                                    reply.setStatus(Status.OK);
                                    reply.setParam("msg",recursos);

                                }catch (Exception e){
                                    m.setStatus(Status.ERROR);
                                }
                                break;
                            case "SAIR":
                                // DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = SAIR;
                                break;
                            case "LOGOUT":
                                reply.setStatus(Status.OK);
                                estado = CONECTADO;
                                break;
                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NÃO AUTORIZADA OU INVÁLIDA!");
                                break;
                        }
                            case ESPERA:

                                break;
                            case SAIR: // ESTADP
                                break;

                        }

                        output.writeObject(reply);
                        output.flush();// cambio do rádio amador
                }

            // 4.2 - Fechar streams de entrada e saída
            input.close();
            output.close();
        } catch (IOException e) {
            // tratamento de falhas
            System.out.println("Problema no tratamento da conexão com o cliente: " + socket.getInetAddress());
            System.out.println("Erro: " + e.getMessage());
            // throw e; //FOI REMOVIDO
        } finally {
            // final do tratamento do protocolo
            /* 4.1 - Fechar socket de comunicação entre servidor/cliente */
            fechaSocket(socket);
        }

    }

    @Override
    public void run() {
        try {
            trataConexao();
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Erro no tratamento de conexão" + e.getMessage());
        }
    }

}
