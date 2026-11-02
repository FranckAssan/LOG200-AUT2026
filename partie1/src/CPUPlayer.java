import java.util.ArrayList;
import java.util.List;

public class CPUPlayer {

    private int numExploredNodes;

    private final Mark cpu;

    private int cpuRowPos = 0;
    private int cpuColPos = 0;

    private final int PROFONDEUR_MAX = 3;

    public CPUPlayer(Mark cpu) {
        this.cpu = cpu;
    }

    public void setCpuRowPos(int cpuRowPos) {
        this.cpuRowPos = cpuRowPos;
    }

    public void setCpuColPos(int cpuColPos) {
        this.cpuColPos = cpuColPos;
    }

    public int getCpuRowPos() {
        return cpuRowPos;
    }

    public int getCpuColPos() {
        return cpuColPos;
    }

    // Ne pas changer cette méthode
    public int getNumOfExploredNodes() {
        return numExploredNodes;
    }

    public Mark getMark() {
        return this.cpu;
    }


    /// //////////////////////////// MinMax //////////////////////////////////

    public String cpuPlayMinMax(Board board, String coupRecu) {
        board.ajouterCoupsRecu(board, coupRecu, this.cpu);

        int oppRow = -1, oppCol = -1;
        Mark adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;
        for (int r = 0; r < board.getBoard().length; r++) {
            for (int c = 0; c < board.getBoard()[r].length; c++) {
                if (board.getBoard()[r][c] == this.cpu) {
                    this.cpuRowPos = r;
                    this.cpuColPos = c;
                } else if (board.getBoard()[r][c] == adversaire) {
                    oppRow = r;
                    oppCol = c;
                }
            }
        }

        int oldRow = this.cpuRowPos;
        int oldCol = this.cpuColPos;

        ArrayList<Move> coups = this.getNextMoveMinMax(board, oppRow, oppCol);

        if (coups != null && coups.size() >= 2) {
            Move deplace = coups.get(0);
            Move detruire = coups.get(1);

            board.getBoard()[oldRow][oldCol] = Mark.EMPTY;
            board.getBoard()[deplace.getRow()][deplace.getCol()] = this.cpu;
            board.destruireCase(detruire);
            this.cpuRowPos = deplace.getRow();
            this.cpuColPos = deplace.getCol();

            Move from = new Move(oldRow, oldCol);
            if (coupRecu != null && coupRecu.contains("row=")) {
                return from + "-" + deplace + "-" + detruire;
            } else {
                return board.toAlgebraic(from, board.getBoard().length) + "-" +
                        board.toAlgebraic(deplace, board.getBoard().length) + "-" +
                        board.toAlgebraic(detruire, board.getBoard().length);
            }
        }
        return "";
    }


    public ArrayList<Move> getNextMoveMinMax(Board board, int adverseRow, int adverseCol) {
        numExploredNodes = 0;
        int bestScore = Integer.MIN_VALUE;

        var adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;
        ArrayList<Move> nextsMinMaxMove = new ArrayList<>();

        for (int r = 0; r < board.getBoard().length; r++) {
            for (int c = 0; c < board.getBoard()[r].length; c++) {
                if (board.getBoard()[r][c] == this.cpu) {
                    this.cpuRowPos = r;
                    this.cpuColPos = c;
                } else if (board.getBoard()[r][c] == adversaire) {
                    adverseRow = r;
                    adverseCol = c;
                }
            }
        }

        if (adverseRow < 0 || adverseCol < 0) {
            adverseRow = (this.cpu == Mark.R) ? (board.getBoard().length - 1) : 0;
            adverseCol = 5;
        }

        List<Move> possibleMove = board.searchPossibleMove(this.cpuRowPos, this.cpuColPos, this.cpu);

        for (Move deplace : possibleMove) {
            board.getBoard()[this.cpuRowPos][this.cpuColPos] = Mark.EMPTY;
            board.getBoard()[deplace.getRow()][deplace.getCol()] = this.cpu;

            for (Move detruire : board.getNbOfDestroyedSquare(adverseRow, adverseCol, this.cpu)) {
                board.destruireCase(detruire);
                int score = minMax(
                        board,
                        adversaire,
                        deplace.getRow(),
                        deplace.getCol(),
                        adverseRow,
                        adverseCol,
                        1
                );
                board.getBoard()[detruire.getRow()][detruire.getCol()] = Mark.EMPTY;

                if (score > bestScore) {
                    bestScore = score;
                    nextsMinMaxMove.clear();
                    nextsMinMaxMove.add(deplace);
                    nextsMinMaxMove.add(detruire);
                } else if (score == bestScore) {
                    nextsMinMaxMove.add(deplace);
                    nextsMinMaxMove.add(detruire);
                }
            }

            board.getBoard()[deplace.getRow()][deplace.getCol()] = Mark.EMPTY;
            board.getBoard()[this.cpuRowPos][this.cpuColPos] = this.cpu;
        }

        return nextsMinMaxMove;
    }


    private int minMax(Board board, Mark joueur, int row, int col, int oppRow, int oppCol, int niveau) {
        numExploredNodes++;
        var adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;

        List<Move> coupsPossibles = board.searchPossibleMove(row, col, this.cpu);

        if (joueur.equals(this.cpu)) {   // On maximize
            if (coupsPossibles.isEmpty()) {
                return -100 + niveau;
            }
            if (niveau >= this.PROFONDEUR_MAX) {
                int cpuNbCoupsPossible = coupsPossibles.size();
                int adversaireNbCoupsPossible = board.searchPossibleMove(oppRow, oppCol, adversaire).size();
                return (cpuNbCoupsPossible * 2) - (adversaireNbCoupsPossible * 3);
            }

            int meilleur = Integer.MIN_VALUE;

            for (Move m : coupsPossibles) {
                board.getBoard()[row][col] = Mark.EMPTY;
                board.getBoard()[m.getRow()][m.getCol()] = this.cpu;

                for (Move d : board.getNbOfDestroyedSquare(oppRow, oppCol, this.cpu)) {
                    board.destruireCase(d);
                    int score = minMax(board, adversaire, m.getRow(), m.getCol(), oppRow, oppCol, niveau + 1);
                    meilleur = Math.max(meilleur, score);
                    board.getBoard()[d.getRow()][d.getCol()] = Mark.EMPTY;
                }
                board.getBoard()[m.getRow()][m.getCol()] = Mark.EMPTY;
                board.getBoard()[row][col] = this.cpu;
            }
            return meilleur;

        } else {  // Minimise
            if (coupsPossibles.isEmpty()) {
                return 100 - niveau;
            }
            if (niveau >= this.PROFONDEUR_MAX) {
                int oppMobility = coupsPossibles.size();
                int myMobility = board.searchPossibleMove(row, col, this.cpu).size();
                return (myMobility * 2) - (oppMobility * 3);
            }

            int meilleur = Integer.MAX_VALUE;

            for (Move m : coupsPossibles) {
                board.getBoard()[oppRow][oppCol] = Mark.EMPTY;
                board.getBoard()[m.getRow()][m.getCol()] = adversaire;

                for (Move d : board.getNbOfDestroyedSquare(row, col, adversaire)) {
                    board.destruireCase(d);
                    int score = minMax(board, this.cpu, row, col, m.getRow(), m.getCol(), niveau + 1);
                    meilleur = Math.min(meilleur, score);
                    board.getBoard()[d.getRow()][d.getCol()] = Mark.EMPTY;
                }
                board.getBoard()[m.getRow()][m.getCol()] = Mark.EMPTY;
                board.getBoard()[oppRow][oppCol] = adversaire;
            }
            return meilleur;
        }
    }


    public void gererCoupsInvalideMinMax() {

    }

    /// //////////////////////////// ALPHA BETA //////////////////////////////////

    public String cpuPlayAlphaBeta(Board board, String coupRecu) {
        board.ajouterCoupsRecu(board, coupRecu, this.cpu);

        int oppRow = -1, oppCol = -1;
        Mark adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;
        for (int r = 0; r < board.getBoard().length; r++) {
            for (int c = 0; c < board.getBoard()[r].length; c++) {
                if (board.getBoard()[r][c] == this.cpu) {
                    this.cpuRowPos = r;
                    this.cpuColPos = c;
                } else if (board.getBoard()[r][c] == adversaire) {
                    oppRow = r;
                    oppCol = c;
                }
            }
        }

        int oldRow = this.cpuRowPos;
        int oldCol = this.cpuColPos;

        ArrayList<Move> coups = this.getNextMoveAB(board, oppRow, oppCol);

        if (coups != null && coups.size() >= 2) {
            Move deplace = coups.get(0);
            Move detruire = coups.get(1);

            board.getBoard()[oldRow][oldCol] = Mark.EMPTY;
            board.getBoard()[deplace.getRow()][deplace.getCol()] = this.cpu;
            board.destruireCase(detruire);
            this.cpuRowPos = deplace.getRow();
            this.cpuColPos = deplace.getCol();

            Move from = new Move(oldRow, oldCol);
            if (coupRecu != null && coupRecu.contains("row=")) {
                return from + "-" + deplace + "-" + detruire;
            } else {
                return board.toAlgebraic(from, board.getBoard().length) + "-" +
                        board.toAlgebraic(deplace, board.getBoard().length) + "-" +
                        board.toAlgebraic(detruire, board.getBoard().length);
            }
        }
        return "";
    }


    public ArrayList<Move> getNextMoveAB(Board board, int adverseRow, int adverseCol) {
        numExploredNodes = 0;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        var adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;
        ArrayList<Move> nextsABMove = new ArrayList<>();

        for (int r = 0; r < board.getBoard().length; r++) {
            for (int c = 0; c < board.getBoard()[r].length; c++) {
                if (board.getBoard()[r][c] == this.cpu) {
                    this.cpuRowPos = r;
                    this.cpuColPos = c;
                } else if (board.getBoard()[r][c] == adversaire) {
                    adverseRow = r;
                    adverseCol = c;
                }
            }
        }

        if (adverseRow < 0 || adverseCol < 0) {
            adverseRow = (this.cpu == Mark.R) ? (board.getBoard().length - 1) : 0;
            adverseCol = 5;
        }

        List<Move> possibleMove = board.searchPossibleMove(this.cpuRowPos, this.cpuColPos, this.cpu);

        for (Move deplace : possibleMove) {
            board.getBoard()[this.cpuRowPos][this.cpuColPos] = Mark.EMPTY;
            board.getBoard()[deplace.getRow()][deplace.getCol()] = this.cpu;

            for (Move detruire : board.getNbOfDestroyedSquare(adverseRow, adverseCol, this.cpu)) {
                board.destruireCase(detruire);
                int score = alphaBeta(
                        board,
                        adversaire,
                        deplace.getRow(),
                        deplace.getCol(),
                        adverseRow,
                        adverseCol,
                        1,
                        alpha,
                        beta
                );
                board.getBoard()[detruire.getRow()][detruire.getCol()] = Mark.EMPTY;

                if (score > bestScore) {
                    bestScore = score;
                    nextsABMove.clear();
                    nextsABMove.add(deplace);
                    nextsABMove.add(detruire);
                } else if (score == bestScore) {
                    nextsABMove.add(deplace);
                    nextsABMove.add(detruire);
                }
                alpha = bestScore;
            }

            board.getBoard()[deplace.getRow()][deplace.getCol()] = Mark.EMPTY;
            board.getBoard()[this.cpuRowPos][this.cpuColPos] = this.cpu;
        }

        return nextsABMove;
    }


    private int alphaBeta(
            Board board, Mark joueur, int myRow, int myCol, int oppRow, int oppCol, int depth, int alpha, int beta
    ) {
        numExploredNodes++;
        var adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;

        if (joueur.equals(this.cpu)) {
            List<Move> possibleMove = board.searchPossibleMove(myRow, myCol, this.cpu);
            if (possibleMove.isEmpty()) {
                return -100 + depth;
            }
            if (depth >= this.PROFONDEUR_MAX) {
                int myMobility = possibleMove.size();
                int oppMobility = board.searchPossibleMove(oppRow, oppCol, adversaire).size();
                return (myMobility * 2) - (oppMobility * 3);
            }

            int meilleur = Integer.MIN_VALUE;
            for (Move m : possibleMove) {
                board.getBoard()[myRow][myCol] = Mark.EMPTY;
                board.getBoard()[m.getRow()][m.getCol()] = this.cpu;

                for (Move d : board.getNbOfDestroyedSquare(oppRow, oppCol, this.cpu)) {
                    board.destruireCase(d);
                    int score = alphaBeta(
                            board, adversaire, m.getRow(), m.getCol(), oppRow, oppCol, depth + 1, alpha, beta
                    );
                    board.getBoard()[d.getRow()][d.getCol()] = Mark.EMPTY;

                    meilleur = Math.max(meilleur, score);
                    alpha = Math.max(alpha, meilleur);
                    if (alpha >= beta) {
                        break;
                    }
                }

                board.getBoard()[m.getRow()][m.getCol()] = Mark.EMPTY;
                board.getBoard()[myRow][myCol] = this.cpu;
                if (alpha >= beta) {
                    break;
                }
            }
            return meilleur;
        } else {
            List<Move> possibleMove = board.searchPossibleMove(oppRow, oppCol, adversaire);
            if (possibleMove.isEmpty()) {
                return 100 - depth;
            }
            if (depth >= this.PROFONDEUR_MAX) {
                int oppMobility = possibleMove.size();
                int myMobility = board.searchPossibleMove(myRow, myCol, this.cpu).size();
                return (myMobility * 2) - (oppMobility * 3);
            }

            int meilleur = Integer.MAX_VALUE;
            for (Move m : possibleMove) {
                board.getBoard()[oppRow][oppCol] = Mark.EMPTY;
                board.getBoard()[m.getRow()][m.getCol()] = adversaire;

                for (Move d : board.getNbOfDestroyedSquare(myRow, myCol, adversaire)) {
                    board.destruireCase(d);
                    int score = alphaBeta(
                            board, this.cpu, myRow, myCol, m.getRow(), m.getCol(), depth + 1, alpha, beta
                    );
                    board.getBoard()[d.getRow()][d.getCol()] = Mark.EMPTY;

                    meilleur = Math.min(meilleur, score);
                    beta = Math.min(beta, meilleur);
                    if (alpha >= beta) {
                        break;
                    }
                }

                board.getBoard()[m.getRow()][m.getCol()] = Mark.EMPTY;
                board.getBoard()[oppRow][oppCol] = adversaire;
                if (alpha >= beta) {
                    break;
                }
            }
            return meilleur;
        }
    }


    public void gererCoupsInvalideAlphaBeta() {

    }
}
