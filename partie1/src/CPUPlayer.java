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

    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveMinMax(Board board) {
        numExploredNodes = 0;

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        var adversaire = (this.cpu == Mark.O) ? Mark.X : Mark.O;
        ArrayList<Move> nextsMinMaxMove = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getBoard()[i][j] == Mark.EMPTY) {
                    board.play(new Move(i, j), this.cpu);
                    int score = miniMax(board, adversaire, 1);
                    board.undoCoup(i, j);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new Move(i, j);
                    }
                }
            }
        }
        if (bestMove != null) {
            nextsMinMaxMove.add(bestMove);
        }
        return nextsMinMaxMove;
    }

    private int miniMax(Board board, Mark joueur, int niveau) {
        numExploredNodes++;

        var adversaire = (this.cpu == Mark.O) ? Mark.X : Mark.O;
        var checkEtatPlateau = board.verifierGagnant();

        if (checkEtatPlateau != null) {
            if (checkEtatPlateau.equals(this.cpu)) {
                return board.evaluate(this.cpu) - niveau;
            } else if (checkEtatPlateau.equals(Mark.EMPTY)) {
                return 0;
            }
            return niveau - board.evaluate(adversaire);
        }

        List<Move> coups = genererCoupsMinMax(board);

        int meilleur;

        // On maximize
        if (joueur.equals(this.cpu)) {
            meilleur = Integer.MIN_VALUE;
            for (Move m : coups) {
                board.play(m, this.cpu);
                int score = miniMax(board, adversaire, niveau + 1);
                board.undoCoup(m.getRow(), m.getCol());
                meilleur = Math.max(meilleur, score);
            }
        } else { // Minimise
            meilleur = Integer.MAX_VALUE;
            for (Move m : coups) {
                board.play(m, joueur);
                int score = miniMax(board, this.cpu, niveau + 1);
                board.undoCoup(m.getRow(), m.getCol());
                meilleur = Math.min(meilleur, score);
            }
        }
        return meilleur;
    }

    private List<Move> genererCoupsMinMax(Board board) {
        List<Move> listeDeCoups = new ArrayList<>();
        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard()[i].length; j++) {
                if (board.getBoard()[i][j] == Mark.EMPTY) {
                    listeDeCoups.add(new Move(i, j));
                }
            }
        }
        return listeDeCoups;
    }

    public void cpuPlayMinMax(Board board) {
        Move move = this.getNextMoveMinMax(board).getFirst();
        board.play(move, this.cpu);
    }


    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveAB(Board board) {
        numExploredNodes = 0;
        return null;
    }


}
