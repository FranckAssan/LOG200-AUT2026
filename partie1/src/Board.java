import java.util.ArrayList;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
class Board
{
    private Mark[][] board;

    // Ne pas changer la signature de cette méthode
    public Board() {
        board = new Mark[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Mark.EMPTY;
            }
        }

    }

    // Place la pièce 'mark' sur le plateau, à la
    // position spécifiée dans Move
    //
    // Ne pas changer la signature de cette méthode
    public void play(Move m, Mark mark){
        board[m.getRow()][m.getCol()] = mark;


    }
     public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[i][j] == Mark.EMPTY) {
                    moves.add(new Move(i, j));
                }
            }
        }

        return moves;
    }


    // retourne  100 pour une victoire
    //          -100 pour une défaite
    //           0   pour un match nul
    // Ne pas changer la signature de cette méthode
    public int evaluate(Mark mark){
          Mark opponent;

    if (mark == Mark.X) {
        opponent = Mark.O;
    } else {
        opponent = Mark.X;
    }

    // Vérifier les lignes
    for (int i = 0; i < 3; i++) {
        if (board[i][0] == mark &&
            board[i][1] == mark &&
            board[i][2] == mark) {
            return 100;
        }

        if (board[i][0] == opponent &&
            board[i][1] == opponent &&
            board[i][2] == opponent) {
            return -100;
        }
    }

    // Vérifier les colonnes
    for (int j = 0; j < 3; j++) {
        if (board[0][j] == mark &&
            board[1][j] == mark &&
            board[2][j] == mark) {
            return 100;
        }

        if (board[0][j] == opponent &&
            board[1][j] == opponent &&
            board[2][j] == opponent) {
            return -100;
        }
    }

    // Diagonale principale
    if (board[0][0] == mark &&
        board[1][1] == mark &&
        board[2][2] == mark) {
        return 100;
    }

    if (board[0][0] == opponent &&
        board[1][1] == opponent &&
        board[2][2] == opponent) {
        return -100;
    }

    // Deuxième diagonale
    if (board[0][2] == mark &&
        board[1][1] == mark &&
        board[2][0] == mark) {
        return 100;
    }

    if (board[0][2] == opponent &&
        board[1][1] == opponent &&
        board[2][0] == opponent) {
        return -100;
    }

    return 0;

    }

    public Board copy() {
    Board newBoard = new Board();

    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            newBoard.board[i][j] = this.board[i][j];
        }
    }

    return newBoard;
}
}
