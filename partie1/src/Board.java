import java.util.ArrayList;
import java.util.Arrays;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
class Board {
    private Mark[][] board;

    // Ne pas changer la signature de cette méthode
    public Board() {
        this.board = new Mark[3][3];
        Arrays.fill(board[0], Mark.EMPTY);
        Arrays.fill(board[1], Mark.EMPTY);
        Arrays.fill(board[2], Mark.EMPTY);
    }

    // Place la pièce 'mark' sur le plateau, à la
    // position spécifiée dans Move
    //
    // Ne pas changer la signature de cette méthode
    public void play(Move m, Mark mark) {
        if (this.board[m.getRow()][m.getCol()] == Mark.EMPTY) {
            this.board[m.getRow()][m.getCol()] = mark;
        }
    }


    // retourne  100 pour une victoire
    //          -100 pour une défaite
    //           0   pour un match nul
    // Ne pas changer la signature de cette méthode
    public int evaluate(Mark mark) {
        var gagant = this.verifierGagnant();
        if (!gagant.equals(mark) && !mark.equals(Mark.EMPTY)) return -100;
        if (mark == gagant) return 100;
        return 0;
    }

    public Mark[][] getBoard() {
        return board;
    }


    /**
     * Retourne X ou O si on on un gagnant (Horizontalement, verticalement ou en digonale)
     * Empty si toutes les cases ne pas sont rempli et
     *
     * @return Mark
     */
    public Mark verifierGagnant() {

        // Verifie les colonnes
        for (int i = 0; i < 3; i++) {
            // Verifie les diagonales
            if (getBoard()[0][0] != Mark.EMPTY && getBoard()[0][0] == getBoard()[1][1] &&
                    getBoard()[1][1] == getBoard()[2][2]) {
                return getBoard()[0][0];
            }

            if (getBoard()[0][2] != Mark.EMPTY && getBoard()[0][2] == getBoard()[1][1] &&
                    getBoard()[1][1] == getBoard()[2][0]) {
                return getBoard()[0][2];
            }

            // lignes
            if (getBoard()[i][0] != Mark.EMPTY && getBoard()[i][0] == getBoard()[i][1] &&
                    getBoard()[i][1] == getBoard()[i][2]) {
                return getBoard()[i][0];
            }

            // Colonnes
            if (getBoard()[0][i] != Mark.EMPTY && getBoard()[0][i] == getBoard()[1][i] &&
                    getBoard()[1][i] == getBoard()[2][i]) {
                return getBoard()[0][i];
            }
        }

        // Si aucun cas s'applique, null ou pas fini
        boolean empty = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                if (getBoard()[i][j] == Mark.EMPTY) {
                    empty = true;
                    break;
                }
        }

        return empty ? null : Mark.EMPTY;
    }

    public void undoCoup(int row, int col) {
        this.board[row][col] = Mark.EMPTY;
    }
}
