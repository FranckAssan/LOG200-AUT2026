import java.util.*;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
public class Board {
    private final Mark[][] board;

    private final HashMap<String, Move> positionsMap = new HashMap<>();

    // Ne pas changer la signature de cette méthode
    public Board() {
        this.board = new Mark[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Mark.EMPTY;
                Move m = new Move(i, j);
                this.positionsMap.put(m.toString(), m);
            }
        }
    }

    public Board(int rows, int cols) {
        this.board = new Mark[rows][cols];
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                Move m = new Move(i, j);
                this.positionsMap.put(m.toString(), m);
                String alg = "" + (char) ('A' + j) + (rows - i);
                this.positionsMap.put(alg, m);
                this.positionsMap.put(alg.toLowerCase(), m);
                board[i][j] = Mark.EMPTY;
            }
        }
    }


    public Move getRowColFromPosMap(String key) {
        if (key == null) return null;
        key = key.trim();
        if (this.positionsMap.containsKey(key)) {
            return this.positionsMap.get(key);
        }
        if (this.positionsMap.containsKey(key.toUpperCase())) {
            return this.positionsMap.get(key.toUpperCase());
        }
        if (key.startsWith("[") && key.contains("row=")) {
            try {
                int r = Integer.parseInt(key.substring(key.indexOf("row=") + 4, key.indexOf(",")).trim());
                int c = Integer.parseInt(key.substring(key.indexOf("col=") + 4, key.indexOf("]")).trim());
                return new Move(r, c);
            } catch (Exception ignored) {
            }
        }
        if (key.length() >= 2) {
            char colChar = Character.toUpperCase(key.charAt(0));
            int c = colChar - 'A';
            try {
                int rowNum = Integer.parseInt(key.substring(1));
                int r = board.length - rowNum;
                if (r >= 0 && r < board.length && c >= 0 && c < board[0].length) {
                    return new Move(r, c);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public List<Move> searchPossibleMove(int row, int col) {
        List<Move> move = new ArrayList<>();
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
            return move;
        }

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : directions) {
            for (int step = 1; step <= 3; step++) {
                int r = row + dir[0] * step;
                int c = col + dir[1] * step;
                if (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] == Mark.EMPTY) {
                    move.add(new Move(r, c));
                } else {
                    break;
                }
            }
        }

        return move;
    }

    public List<Move> getNbOfDestroyedSquare(int adversaireRow, int adversaireCol, Mark currentPlayer) {
        int n = 0;
        for (var row : this.board) {
            for (var col : row) {
                if (col == Mark.DESTROYED) {
                    n++;
                }
            }
        }

        List<Move> caseADetruire = new ArrayList<>();

        if (n < 10) {
            caseADetruire = this.searchCaseToDestroy(adversaireRow, adversaireCol, 3, currentPlayer);
            if (caseADetruire.isEmpty()) {
                caseADetruire = this.searchCaseToDestroy(adversaireRow, adversaireCol, 2, currentPlayer);
            }
            if (caseADetruire.isEmpty()) {
                caseADetruire = this.searchCaseToDestroy(adversaireRow, adversaireCol, 1, currentPlayer);
            }
        } else if (n <= 19) {
            caseADetruire = this.searchCaseToDestroy(adversaireRow, adversaireCol, 2, currentPlayer);
            if (caseADetruire.isEmpty()) {
                caseADetruire = this.searchCaseToDestroy(adversaireRow, adversaireCol, 1, currentPlayer);
            }
        } else {
            caseADetruire = this.searchCaseToDestroy(adversaireRow, adversaireCol, 1, currentPlayer);
            if (caseADetruire.isEmpty()) {
                caseADetruire = this.searchCaseToDestroy(adversaireRow, adversaireCol, 2, currentPlayer);
            }
        }

        if (caseADetruire.isEmpty()) {
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[r].length; c++) {
                    if (board[r][c] == Mark.EMPTY) {
                        caseADetruire.add(new Move(r, c));
                        break;
                    }
                }
                if (!caseADetruire.isEmpty()) break;
            }
        }

        return caseADetruire;
    }

    public List<Move> searchCaseToDestroy(int row, int col, int distanceSelonPhase, Mark player) {
        List<Move> move = new ArrayList<>();
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
            return move;
        }

        int[][] offsets = {
                {-distanceSelonPhase, 0}, {distanceSelonPhase, 0},
                {0, -distanceSelonPhase}, {0, distanceSelonPhase},
                {-distanceSelonPhase, -distanceSelonPhase}, {-distanceSelonPhase, distanceSelonPhase},
                {distanceSelonPhase, -distanceSelonPhase}, {distanceSelonPhase, distanceSelonPhase}
        };

        for (int[] offset : offsets) {
            int r = row + offset[0];
            int c = col + offset[1];
            if (r >= 0 && r < board.length && c >= 0 && c < board[0].length) {
                if (board[r][c] == Mark.EMPTY) {
                    move.add(new Move(r, c));
                }
            }
        }

        return move;
    }


    // Place la pièce 'mark' sur le plateau, à la
    // position spécifiée dans Move
    //
    // Ne pas changer la signature de cette méthode
    public void play(Move deplacement, Mark mark) {
        if (this.board[deplacement.getRow()][deplacement.getCol()] == Mark.EMPTY) {
            this.board[deplacement.getRow()][deplacement.getCol()] = mark;
        }
    }

    public void destruireCase(Move caseAsupprimer) {
        if (this.board[caseAsupprimer.getRow()][caseAsupprimer.getCol()] == Mark.EMPTY) {
            this.board[caseAsupprimer.getRow()][caseAsupprimer.getCol()] = Mark.DESTROYED;
        }
    }


    public int evaluate(Mark currentPlayer, int row, int col) {
        Mark gagnant = verifierGagnant(currentPlayer, row, col);
        if (gagnant == null || gagnant == Mark.EMPTY) {
            return 0; // pas fini ou nul
        }
        return gagnant == currentPlayer ? 100 : -100;
    }

    public Mark verifierGagnant(Mark player, int row, int col) {
        var adversaire = (player == Mark.N) ? Mark.R : Mark.N;
        List<Move> moveDisponiblesForPlayer = this.searchPossibleMove(row, col);
        if (moveDisponiblesForPlayer.isEmpty()) {
            return adversaire;
        }
        return null;
    }


    public Mark[][] getBoard() {
        return board;
    }


    public Mark verifierGagnant(Mark player, int row, int col, int adversaireRow, int adversaireCol) {
        Mark adversaire = (player == Mark.N) ? Mark.R : Mark.N;
        if (searchPossibleMove(row, col).isEmpty()) {
            return adversaire;
        }
        if (searchPossibleMove(adversaireRow, adversaireCol).isEmpty()) {
            return player;
        }
        return null;
    }

    public void undoCoup(int row, int col) {
        if (row >= 0 && row < board.length && col >= 0 && col < board[0].length) {
            this.board[row][col] = Mark.EMPTY;
        }
    }

    public void undoCoup(Move deplacement, Move caseDetruite) {
        if (deplacement != null && deplacement.getRow() >= 0 && deplacement.getRow() < board.length
                && deplacement.getCol() >= 0 && deplacement.getCol() < board[0].length) {
            this.board[deplacement.getRow()][deplacement.getCol()] = Mark.EMPTY;
        }
        if (caseDetruite != null && caseDetruite.getRow() >= 0 && caseDetruite.getRow() < board.length
                && caseDetruite.getCol() >= 0 && caseDetruite.getCol() < board[0].length) {
            this.board[caseDetruite.getRow()][caseDetruite.getCol()] = Mark.EMPTY;
        }
    }


    public void ajouterCoupsRecu(Board board, String coupRecu, Mark cpu) {
        if (coupRecu == null || coupRecu.trim().isEmpty()) {
            return;
        }
        String[] parts = coupRecu.trim().split("-");
        Mark adversaire = (cpu == Mark.N) ? Mark.R : Mark.N;
        if (parts.length >= 3) {
            Move from = parseMoveString(board, parts[0]);
            Move to = parseMoveString(board, parts[1]);
            Move destr = parseMoveString(board, parts[2]);

            if (from != null && board.getBoard()[from.getRow()][from.getCol()] == adversaire) {
                board.getBoard()[from.getRow()][from.getCol()] = Mark.EMPTY;
            }
            if (to != null) {
                board.getBoard()[to.getRow()][to.getCol()] = adversaire;
            }
            if (destr != null) {
                board.getBoard()[destr.getRow()][destr.getCol()] = Mark.DESTROYED;
            }
        } else if (parts.length == 2) {
            Move to = parseMoveString(board, parts[0]);
            Move destr = parseMoveString(board, parts[1]);
            if (to != null) {
                board.getBoard()[to.getRow()][to.getCol()] = adversaire;
            }
            if (destr != null) {
                board.getBoard()[destr.getRow()][destr.getCol()] = Mark.DESTROYED;
            }
        }
    }

    public static Move parseMoveString(Board board, String str) {
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return null;
        Move m = board.getRowColFromPosMap(str);
        if (m != null) return m;

        if (str.startsWith("[") && str.contains("row=")) {
            try {
                int r = Integer.parseInt(str.substring(str.indexOf("row=") + 4, str.indexOf(",")).trim());
                int c = Integer.parseInt(str.substring(str.indexOf("col=") + 4, str.indexOf("]")).trim());
                return new Move(r, c);
            } catch (Exception ignored) {
            }
        }
        if (str.length() >= 2) {
            char colChar = Character.toUpperCase(str.charAt(0));
            int c = colChar - 'A';
            try {
                int rowNum = Integer.parseInt(str.substring(1));
                int r = board.getBoard().length - rowNum;
                if (r >= 0 && r < board.getBoard().length && c >= 0 && c < board.getBoard()[0].length) {
                    return new Move(r, c);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public String toAlgebraic(Move m, int rows) {
        if (m == null) return "";
        char col = (char) ('A' + m.getCol());
        int row = rows - m.getRow();
        return "" + col + row;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Mark[] marks : this.board) {
            for (Mark mark : marks) {

                String text = mark.name();

                if (text.isEmpty()) {
                    text = ".";
                }
                if (text.equals(Mark.EMPTY.name())) {
                    text = ".";
                }
                if (text.equals(Mark.DESTROYED.name())) {
                    text = "@";
                }

                sb.append(String.format("%-4s", text)).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }


    public int evaluer(int playerRow, int plaerCol, int oppRow, int oppCol) {
//        int myMobility = searchPossibleMove(myRow, myCol).size();
//        int oppMobility = searchPossibleMove(oppRow, oppCol).size();
//        return (myMobility * 2) - (oppMobility * 3);
        return voronoiTerritory(playerRow, plaerCol, oppRow, oppCol);
    }

    public int voronoiTerritory(int playerRow, int playerCol, int oppRow, int oppCol) {
        int[][] playerVoisins = caseSVoisines(playerRow, playerCol);
        int[][] opponentVoisins = caseSVoisines(oppRow, oppCol);
        int playerScore = 0;
        int opponentScore = 0;

//        String[][] testBoad = new String[this.board.length][this.board[0].length];

        for (int i = 0; i < this.board.length; i++) {

            for (int j = 0; j < this.board[i].length; j++) {
                if (this.board[i][j] == Mark.DESTROYED || this.board[i][j] != Mark.EMPTY) {
                    continue;
                }

                if (playerVoisins[i][j] == Integer.MAX_VALUE && opponentVoisins[i][j] == Integer.MAX_VALUE) {
                    continue;
                }

                if (playerVoisins[i][j] < opponentVoisins[i][j]) {
                    playerScore++;
//                    testBoad[i][j] = "+";

                } else if (opponentVoisins[i][j] < playerVoisins[i][j]) {
                    opponentScore++;
//                    testBoad[i][j] = "-";
                }
//                else {
//                    testBoad[i][j] = "=";
//
//                }
            }
        }

//        System.out.println(toStringVeronoi(testBoad, playerRow, playerCol, oppRow, oppCol));

        int scoreTerritoire = playerScore - opponentScore;
        int mobility = searchPossibleMove(playerRow, playerCol).size() - searchPossibleMove(oppRow, oppCol).size();

        return 3 * scoreTerritoire + 2 * mobility;
    }


    private int[][] caseSVoisines(int row, int col) {
        int rows = board.length;
        int cols = board[0].length;

        int[][] dist = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }

        Queue<Move> q = new ArrayDeque<>();

        dist[row][col] = 0;
        q.add(new Move(row, col));

        while (!q.isEmpty()) {
            Move current = q.poll();

            for (Move next : searchPossibleMove(current.getRow(), current.getCol())) {
                if (dist[next.getRow()][next.getCol()] != Integer.MAX_VALUE) {
                    continue;
                }

                dist[next.getRow()][next.getCol()] = dist[current.getRow()][current.getCol()] + 1;
                q.add(next);
            }
        }

        return dist;
    }

    public String toStringVeronoi(String[][] board, int playerRow, int playerCol, int oppRow, int oppCol) {
        board[playerRow][playerCol] = "P";
        board[oppRow][oppCol] = "O";

        StringBuilder sb = new StringBuilder();

        for (String[] marks : board) {
            for (String mark : marks) {
                sb.append(String.format("%-4s", mark)).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
