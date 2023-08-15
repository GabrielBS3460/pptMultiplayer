import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    public static void main(String[] args) throws IOException {
        // Cria o socket do servidor
        ServerSocket serverSocket = new ServerSocket(12345);

        // Aguarda conexões de dois clientes
        System.out.println("Aguardando conexoes...");
        Socket player1Socket = serverSocket.accept();
        System.out.println("Jogador 1 conectado!");
        Socket player2Socket = serverSocket.accept();
        System.out.println("Jogador 2 conectado!");

        // Cria os streams de entrada e saída para os jogadores
        DataInputStream player1Input = new DataInputStream(player1Socket.getInputStream());
        DataOutputStream player1Output = new DataOutputStream(player1Socket.getOutputStream());
        DataInputStream player2Input = new DataInputStream(player2Socket.getInputStream());
        DataOutputStream player2Output = new DataOutputStream(player2Socket.getOutputStream());

        // Inicia o jogo
        boolean playing = true;
        List<String> rounds = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        while (playing) {
            // Recebe as jogadas dos jogadores
            String player1Move = player1Input.readUTF();
            String player2Move = player2Input.readUTF();

            // Verifica quem foi o vencedor
            String result;
            if (player1Move.equals(player2Move)) {
                result = "Empate!";
            } else if ((player1Move.equals("Pedra") && player2Move.equals("Tesoura")) ||
                    (player1Move.equals("Papel") && player2Move.equals("Pedra")) ||
                    (player1Move.equals("Tesoura") && player2Move.equals("Papel"))) {
                result = "Jogador 1 venceu!";
            } else {
                result = "Jogador 2 venceu!";
            }

            // Armazena as informações sobre a rodada
            rounds.add("Jogador 1: " + player1Move + ", Jogador 2: " + player2Move + ", Resultado: " + result);

            // Envia o resultado aos jogadores
            player1Output.writeUTF(result);
            player2Output.writeUTF(result);

            // Verifica se os jogadores querem continuar
            boolean player1Continue = player1Input.readBoolean();
            boolean player2Continue = player2Input.readBoolean();
            if (!player1Continue || !player2Continue) {
                playing = false;
                // Calcula a duração da partida
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                // Gera o relatório de todas as jogadas
                StringBuilder report = new StringBuilder();
                report.append("Relatorio de todas as jogadas:\n");
                report.append("Duracao da partida: " + duration/1000 + "s\n");
                for (String round : rounds) {
                    report.append(round).append("\n");
                }
                // Envia o relatório aos jogadores
                player1Output.writeUTF(report.toString());
                player2Output.writeUTF(report.toString());
                // Notifica os jogadores que a partida terminou
                if (!player1Continue) {
                    player2Output.writeUTF("O outro jogador saiu. Fim da partida.");
                } else {
                    player1Output.writeUTF("O outro jogador saiu. Fim da partida.");
                }
                // Encerra a conexão com os jogadores
                player1Input.close();
                player1Output.close();
                player1Socket.close();
                player2Input.close();
                player2Output.close();
                player2Socket.close();
            }
        }
    }
}
