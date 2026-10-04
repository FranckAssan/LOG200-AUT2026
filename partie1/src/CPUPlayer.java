import java.util.ArrayList;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
class CPUPlayer
{

    // Contient le nombre de noeuds visités (le nombre
    // d'appel à la fonction MinMax ou Alpha Beta)
    // Normalement, la variable devrait être incrémentée
    // au début de votre MinMax ou Alpha Beta.
    private int numExploredNodes;
    private Mark cpu;
    private Mark opponent;

    // Le constructeur reçoit en paramètre le
    // joueur MAX (X ou O)
    public CPUPlayer(Mark cpu){
         this.cpu = cpu;

        if (cpu == Mark.X) {
            opponent = Mark.O;
        } else {
            opponent = Mark.X;
        }

    }

    private int minimax(Board board, boolean isMaxTurn) {

       numExploredNodes++;

       int score = board.evaluate(cpu);

       if (score == 100 || score == -100) {
           return score;
        }

       ArrayList<Move> moves = board.getPossibleMoves();

       if (moves.isEmpty()) {
           return 0;
        }

       if (isMaxTurn) {
          int bestScore = Integer.MIN_VALUE;

          for (Move move : moves) {

            Board newBoard = board.copy();

            newBoard.play(move, cpu);

            int currentScore = minimax(newBoard, false);

            bestScore = Math.max(bestScore, currentScore);
          }

          return bestScore;
        }
        else {
          int bestScore = Integer.MAX_VALUE;

          for (Move move : moves) {

            Board newBoard = board.copy();

            newBoard.play(move, opponent);

            int currentScore = minimax(newBoard, true);

            bestScore = Math.min(bestScore, currentScore);
          }

          return bestScore;
        }
    }

    // Ne pas changer cette méthode
    public int  getNumOfExploredNodes(){
        return numExploredNodes;
    }

    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveMinMax(Board board)
    {
        numExploredNodes = 0;
        ArrayList<Move> bestMoves = new ArrayList<>();
        ArrayList<Move> moves = board.getPossibleMoves();

        int bestScore = Integer.MIN_VALUE;

        for (Move move : moves) {

           Board newBoard = board.copy();

           newBoard.play(move, cpu);

           int currentScore = minimax(newBoard, false);
          

           if (currentScore > bestScore) {

               bestScore = currentScore;

               bestMoves.clear();

               bestMoves.add(move);

           } else if (currentScore == bestScore) {

               bestMoves.add(move);
           }
       }

       return bestMoves;

    }

    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveAB(Board board){
        numExploredNodes = 0;

    }

}
