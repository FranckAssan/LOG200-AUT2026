import java.util.ArrayList;
import java.util.List;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
class CPUPlayer {

    // Contient le nombre de noeuds visités (le nombre
    // d'appel à la fonction MinMax ou Alpha Beta)
    // Normalement, la variable devrait être incrémentée
    // au début de votre MinMax ou Alpha Beta.
    private int numExploredNodes;

    private final Mark cpu;

    // Le constructeur reçoit en paramètre le
    // joueur MAX (X ou O)
    public CPUPlayer(Mark cpu) {
        this.cpu = cpu;
    }


    // Ne pas changer cette méthode
    public int getNumOfExploredNodes() {
        return numExploredNodes;
    }


    /// //////////////////////////// MinMax //////////////////////////////////

    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveMinMax(Board board) {
        numExploredNodes = 0;

        int bestScore = Integer.MIN_VALUE;

        var adversaire = (this.cpu == Mark.O) ? Mark.X : Mark.O;
        ArrayList<Move> nextsMinMaxMove = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getBoard()[i][j] == Mark.EMPTY) {
                    board.play(new Move(i, j), this.cpu);
                    int score = miniMax(board, adversaire);
                    board.undoCoup(i, j);
                    if (score > bestScore) {
                        bestScore = score;
                        nextsMinMaxMove.clear();
                        nextsMinMaxMove.add(new Move(i, j));
                    } else if (score == bestScore) {
                        nextsMinMaxMove.add(new Move(i, j));
                    }
                }
            }
        }
        return nextsMinMaxMove;
    }

    private int miniMax(Board board, Mark joueur) {
        numExploredNodes++;

        var adversaire = (this.cpu == Mark.O) ? Mark.X : Mark.O;
        var checkEtatPlateau = board.verifierGagnant();

        if (checkEtatPlateau != null) {
            return board.evaluate(this.cpu);   // 100, -100 ou 0
        }

        List<Move> coups = board.genererCoups();

        int meilleur;

        // On maximize
        if (joueur.equals(this.cpu)) {
            meilleur = Integer.MIN_VALUE;
            for (Move m : coups) {
                board.play(m, this.cpu);
                int score = miniMax(board, adversaire);
                board.undoCoup(m.getRow(), m.getCol());
                meilleur = Math.max(meilleur, score);
            }
        } else { // Minimise
            meilleur = Integer.MAX_VALUE;
            for (Move m : coups) {
                board.play(m, joueur);
                int score = miniMax(board, this.cpu);
                board.undoCoup(m.getRow(), m.getCol());
                meilleur = Math.min(meilleur, score);
            }
        }
        return meilleur;
    }

    public void cpuPlayMinMax(Board board) {
        ArrayList<Move> coups = this.getNextMoveMinMax(board);
        if (!coups.isEmpty()) {
            board.play(coups.get(0), this.cpu);
        }
    }

    /// //////////////////////////// ALPHA BETA //////////////////////////////////


    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveAB(Board board) {
        numExploredNodes = 0;

        int bestScore = Integer.MIN_VALUE;

        var adversaire = (this.cpu == Mark.O) ? Mark.X : Mark.O;
        ArrayList<Move> nextsMinMaxMove = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getBoard()[i][j] == Mark.EMPTY) {
                    board.play(new Move(i, j), this.cpu);
                    int score = alphaBeta(board, adversaire, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board.undoCoup(i, j);
                    if (score > bestScore) {
                        bestScore = score;
                        nextsMinMaxMove.clear();
                        nextsMinMaxMove.add(new Move(i, j));
                    } else if (score == bestScore) {
                        nextsMinMaxMove.add(new Move(i, j));
                    }
                }
            }
        }

        return nextsMinMaxMove;
    }

    private int alphaBeta(Board board, Mark joueur, int alpha, int beta) {
        numExploredNodes++;

        var adversaire = (this.cpu == Mark.O) ? Mark.X : Mark.O;
        var checkEtatPlateau = board.verifierGagnant();

        if (checkEtatPlateau != null) {
            return board.evaluate(this.cpu);   // 100, -100 ou 0
        }

        List<Move> coups = board.genererCoups();

        int meilleur;

        // On maximize
        if (joueur.equals(this.cpu)) {
            meilleur = Integer.MIN_VALUE;
            for (Move m : coups) {
                board.play(m, this.cpu);
                int score = alphaBeta(board, adversaire, alpha, beta);
                board.undoCoup(m.getRow(), m.getCol());
                meilleur = Math.max(meilleur, score);
                alpha = Math.max(alpha, meilleur);

                if (alpha >= beta) {
                    break;
                }
            }
        } else { // Minimise
            meilleur = Integer.MAX_VALUE;
            for (Move m : coups) {
                board.play(m, joueur);
                int score = alphaBeta(board, this.cpu, alpha, beta);
                board.undoCoup(m.getRow(), m.getCol());
                meilleur = Math.min(meilleur, score);
                beta = Math.min(beta, meilleur);

                if (alpha >= beta) {
                    break;
                }
            }
        }
        return meilleur;
    }

    public void cpuPlayAlphaBeta(Board board) {
        ArrayList<Move> coups = this.getNextMoveAB(board);
        if (!coups.isEmpty()) {
            board.play(coups.get(0), this.cpu);
        }
    }

}
