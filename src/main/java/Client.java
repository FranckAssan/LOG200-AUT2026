import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Client {

    public static void main(String[] args) {
        String host = args.length >= 1 ? args[0] : "localhost";
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : 8888;

        CPUPlayer cpuPlayer = null;
        Board gameBoard = null;

        try (GameConnection connection = new GameConnection(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))
        ) {

            GameConnection.GameInfo game = null;

            System.out.println("Connecté à " + host + ":" + port);

            while (true) {
                GameConnection.Message message = connection.readMessage();

                if (message == null) {
                    System.out.println("Connexion fermée.");
                    break;
                }

                switch (message.getCommand()) {

                    case GameConnection.GAME_INFO:
                        game = connection.parseGameInfo(message);
                        System.out.println("Nouvelle partie :"
                                + "\n  Plateau : " + game.getRows() + " x " + game.getCols()
                                + "\n  Temps maximal par coup : "
                                + game.getMaxMoveTime() + " ms"
                        );

                        gameBoard = new Board(game.getRows(), game.getCols());

                        if (game.getSide() == 1) {
                            cpuPlayer = new CPUPlayer(Mark.N);
                        } else {
                            cpuPlayer = new CPUPlayer(Mark.R);
                        }
                        // Temps alloue par coup, utilise par l'approfondissement iteratif.
                        cpuPlayer.setTempsMaxMs(game.getMaxMoveTime());

                        break;

                    case GameConnection.PLAY_RED:
                        assert game != null;

                        parseGameBoard(message.getData(), gameBoard, game.getRows(), game.getCols());


                        System.out.println("Nouvelle partie! Vous jouez rouge.");
                        System.out.println("Entrez votre premier coup : ");

                        String coupsToSend = cpuPlayer.cpuPlayAlphaBeta(gameBoard, message.getData());
                        System.out.println("CPU - CPU move: " + coupsToSend);

                        connection.sendMove(coupsToSend);
                        break;

                    case GameConnection.PLAY_BLACK:
                        assert game != null;

                        System.out.println("Nouvelle partie! Vous jouez noir.");
                        System.out.println("Attendez le coup des rouges.");

                        parseGameBoard(message.getData(), gameBoard, game.getRows(), game.getCols());

                        break;

                    case GameConnection.PLAY:
                        System.out.println("Dernier coup : " + message.getData());
                        System.out.println("Entrez votre coup : ");

                        assert cpuPlayer != null;
                        String coupsToSendRouge = cpuPlayer.cpuPlayAlphaBeta(gameBoard, message.getData());
                        System.out.println("CPU - CPU move: " + coupsToSendRouge);

                        connection.sendMove(coupsToSendRouge);
                        break;

                    case GameConnection.INVALID_MOVE:
                        System.out.println("Coup invalide, entrez un nouveau coup : ");
                        connection.sendMove(console.readLine());
                        break;

                    case GameConnection.GAME_OVER:
                        if (message.getData().isEmpty()) {
                            System.out.println("Partie terminée.");
                        } else {
                            System.out.println("Partie terminée. Dernier coup : " + message.getData());
                        }
                        // On libere la memoire maintenant, entre deux parties, pour eviter
                        // une pause de Java pendant le premier coup (chronometre) de la partie suivante.
                        System.gc();
                        break;

                    default:
                        System.out.println("Commande inconnue : " + message.getCommand());
                        break;
                }
            }

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erreur réseau : " + e.getMessage());
        }
    }

    private static void readBoard(String data, int[][] board, int rows, int cols) {
        String[] values = data.trim().split("\\s+");

        if (values.length != rows * cols)
            throw new IllegalArgumentException("Invalid board received.");

        int index = 0;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                board[x][y] = Integer.parseInt(values[index++]);
            }
        }
    }


    private static void parseGameBoard(String data, Board board, int rows, int cols) {
        String[] values = data.trim().split("\\s+");
        int index = 0;
        Mark[][] b = board.getBoard();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                int value = Integer.parseInt(values[index++]);

                switch (value) {
                    case 0 -> b[row][col] = Mark.EMPTY;
                    case 1 -> b[row][col] = Mark.DESTROYED;
                    case 3 -> b[row][col] = Mark.N;
                    case 5 -> b[row][col] = Mark.R;
                    default -> throw new IllegalArgumentException(
                            "Unknown value: " + value
                    );
                }
            }
        }

        System.out.println(board);
    }
}
