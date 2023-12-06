/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.ClienteNuvem;
import util.Mensagem;
import util.Status;

public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insira um id para seu usuario:");
        Integer id = scanner.nextInt();

        try {

            System.out.println("Estabelecendo conexão...");
            Socket socket = new Socket("localhost", 5555);
            System.out.println("Conexão estabelecida.");

            // criação dos streams de entrada e saída
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Enviando mensagem...");
            Boolean execucao = true;

            while (execucao){
                Mensagem msgEnvio;
                Mensagem msgResposta;

                System.out.println("Menu:");
                System.out.println("1 - Sair");
                System.out.println("2 - Login");
                System.out.println("3 - Logout");
                System.out.println("4 - Nova Alocacao");
                System.out.println("5 - Remover Alocacao");
                System.out.println("8 - Listar Alocacoes");
                System.out.print("Escolha uma opção: ");

                int escolha = scanner.nextInt();
                scanner.nextLine(); // Limpar a nova linha após a leitura do número

                switch (escolha) {
                    case 1:
                        msgEnvio = new Mensagem("SAIR");
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta= (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        execucao=false;
                        break;
                    case 2:
                        msgEnvio = new Mensagem("LOGIN");

                        System.out.println("Insira o usuario:");
                        String user = scanner.next();
                        scanner.nextLine();
                        System.out.println("Insira a senha:");

                        String pass = scanner.next();
                        scanner.nextLine();

                        msgEnvio.setParam("user", user);
                        msgEnvio.setParam("pass", pass);
                        msgEnvio.setParam("clienteN", id);


                        output.writeObject(msgEnvio);
                        output.flush();

                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);

                        break;
                    case 3:
                        msgEnvio = new Mensagem("LOGOUT");

                        output.writeObject(msgEnvio);
                        output.flush();

                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 4:
                        msgEnvio = new Mensagem("ALOCAR");
                        System.out.println("Insira a quantidade recurso (max 100):");
                        int qtdRecurso = scanner.nextInt();
                        msgEnvio.setParam("qtdRecurso", qtdRecurso);
                        msgEnvio.setParam("cliente", id);
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta= (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 5:
                        System.out.println("Qual alocacao deseja REMOVER: ");
                        int IdRemover = scanner.nextInt();
                        scanner.nextLine();
                        msgEnvio = new Mensagem("REMOVER");
                        msgEnvio.setParam("id" , IdRemover);
                        msgEnvio.setParam("cliente", id);
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta= (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 8:
                        msgEnvio = new Mensagem("LISTARALOCACAO");
                        msgEnvio.setParam("cliente", id);
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            }


            input.close();
            output.close();
            socket.close();

        } catch (IOException ex) {
            System.out.println("Erro no cliente: " + ex);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro no cast: " + ex.getMessage());
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
