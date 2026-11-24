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
    ///  Alpha-beta avec approfondissement iteratif : on cherche a profondeur 1,
    ///  puis 2, 3, ... tant qu'il reste du temps. Si le temps manque pendant une
    ///  profondeur, on garde le meilleur coup de la derniere profondeur TERMINEE.

    private static final int VICTOIRE = 10000;

    // Temps maximal par coup (ms), recu du serveur dans le message 0.
    private long tempsMaxMs = 5000;
    private long echeanceNs;          // moment (System.nanoTime) ou il faut arreter
    private boolean tempsEcoule;      // vrai des que l'echeance est depassee
    private boolean limiteAtteinte;   // vrai si au moins une feuille a ete coupee par la profondeur
    private int profondeurMaxAB = 1;  // profondeur de l'iteration en cours
    private int derniereProfondeur;   // derniere profondeur terminee (pour l'affichage)

    public void setTempsMaxMs(long tempsMaxMs) {
        this.tempsMaxMs = tempsMaxMs;
    }

    public int getDerniereProfondeur() {
        return derniereProfondeur;
    }

    private boolean verifierTemps() {
        // On ne consulte l'horloge qu'une fois tous les 512 noeuds (c'est assez frequent).
        if (!tempsEcoule && (numExploredNodes & 511) == 0 && System.nanoTime() >= echeanceNs) {
            tempsEcoule = true;
        }
        return tempsEcoule;
    }

    public String cpuPlayAlphaBeta(Board board, String coupRecu) {
        long debut = System.nanoTime();
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

        // Marge de securite : 30 % du temps, au moins 1000 ms (reseau, JVM, ramasse-miettes),
        // mais jamais plus de la moitie du temps. Avec 5000 ms, la recherche s'arrete a 3500 ms.
        long marge = Math.min(tempsMaxMs / 2, Math.max(1000, tempsMaxMs * 3 / 10));
        long budget = Math.max(50, tempsMaxMs - marge);
        this.echeanceNs = debut + budget * 1_000_000L;

        ArrayList<Move> coups = this.getNextMoveAB(board, oppRow, oppCol);

        long dureeMs = (System.nanoTime() - debut) / 1_000_000L;
        System.out.println("CPU - profondeur " + derniereProfondeur + ", "
                + numExploredNodes + " noeuds, " + dureeMs + " ms");

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
        tempsEcoule = false;
        derniereProfondeur = 0;
        for (Killer[] pair : killers) java.util.Arrays.fill(pair, null);

        var adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;
        Mark[][] b = board.getBoard();

        for (int r = 0; r < b.length; r++) {
            for (int c = 0; c < b[r].length; c++) {
                if (b[r][c] == this.cpu) {
                    this.cpuRowPos = r;
                    this.cpuColPos = c;
                } else if (b[r][c] == adversaire) {
                    adverseRow = r;
                    adverseCol = c;
                }
            }
        }

        if (adverseRow < 0 || adverseCol < 0) {
            adverseRow = (this.cpu == Mark.R) ? (b.length - 1) : 0;
            adverseCol = 5;
        }

        // 1) Liste de tous les coups racine (deplacement + destruction), generee une seule fois.
        ArrayList<Move[]> candidats = new ArrayList<>();
        for (Move deplace : board.searchPossibleMove(this.cpuRowPos, this.cpuColPos, this.cpu)) {
            b[this.cpuRowPos][this.cpuColPos] = Mark.EMPTY;
            b[deplace.getRow()][deplace.getCol()] = this.cpu;
            for (Move detruire : board.getNbOfDestroyedSquare(adverseRow, adverseCol, this.cpu)) {
                candidats.add(new Move[]{deplace, detruire});
            }
            b[deplace.getRow()][deplace.getCol()] = Mark.EMPTY;
            b[this.cpuRowPos][this.cpuColPos] = this.cpu;
        }

        ArrayList<Move> meilleurCoup = new ArrayList<>();
        if (candidats.isEmpty()) {
            return meilleurCoup;   // aucun deplacement possible : partie perdue
        }
        // Coup de secours si meme la profondeur 1 n'a pas le temps de finir.
        meilleurCoup.add(candidats.getFirst()[0]);
        meilleurCoup.add(candidats.getFirst()[1]);

        int[] scores = new int[candidats.size()];

        // 2) Approfondissement iteratif.
        for (int profondeur = 1; profondeur <= 64; profondeur++) {
            this.profondeurMaxAB = profondeur;
            this.limiteAtteinte = false;

            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            int bestScore = Integer.MIN_VALUE;
            Move[] bestCeTour = null;

            for (int i = 0; i < candidats.size(); i++) {
                Move deplace = candidats.get(i)[0];
                Move detruire = candidats.get(i)[1];

                b[this.cpuRowPos][this.cpuColPos] = Mark.EMPTY;
                b[deplace.getRow()][deplace.getCol()] = this.cpu;
                board.destruireCase(detruire);

                int score = alphaBeta(board, adversaire, deplace.getRow(), deplace.getCol(),
                        adverseRow, adverseCol, 1, alpha, beta);

                b[detruire.getRow()][detruire.getCol()] = Mark.EMPTY;
                b[deplace.getRow()][deplace.getCol()] = Mark.EMPTY;
                b[this.cpuRowPos][this.cpuColPos] = this.cpu;

                if (tempsEcoule) break;   // resultat de cette profondeur incomplet : on l'ignore

                scores[i] = score;
                if (score > bestScore) {
                    bestScore = score;
                    bestCeTour = candidats.get(i);
                }
                alpha = Math.max(alpha, bestScore);
            }

            if (tempsEcoule) break;

            // Profondeur terminee : on retient son meilleur coup.
            derniereProfondeur = profondeur;
            if (bestCeTour == null) bestCeTour = candidats.get(0);
            meilleurCoup.clear();
            meilleurCoup.add(bestCeTour[0]);
            meilleurCoup.add(bestCeTour[1]);

            // Victoire ou defaite forcee trouvee, ou arbre explore jusqu'au bout : inutile d'aller plus loin.
            if (Math.abs(bestScore) > VICTOIRE - 100 || !limiteAtteinte) break;

            // Tri des coups racine : les meilleurs de cette profondeur seront essayes en premier
            // a la suivante, ce qui rend l'elagage alpha-beta beaucoup plus efficace.
            Integer[] ordre = new Integer[candidats.size()];
            for (int i = 0; i < ordre.length; i++) ordre[i] = i;
            final int[] s = scores;
            java.util.Arrays.sort(ordre, (x, y) -> Integer.compare(s[y], s[x]));
            ArrayList<Move[]> tries = new ArrayList<>();
            int[] nouveauxScores = new int[scores.length];
            for (int i = 0; i < ordre.length; i++) {
                tries.add(candidats.get(ordre[i]));
                nouveauxScores[i] = scores[ordre[i]];
            }
            candidats = tries;
            scores = nouveauxScores;
        }

        return meilleurCoup;
    }


    private int evaluer(Board board, int myRow, int myCol, int oppRow, int oppCol, Mark adversaire) {
        int myMobility = board.searchPossibleMove(myRow, myCol, this.cpu).size();
        int oppMobility = board.searchPossibleMove(oppRow, oppCol, adversaire).size();
        return (myMobility * 2) - (oppMobility * 3);
    }

    // Deux coups complets par profondeur (ply), conserves entre iterations.
    private final Killer[][] killers = new Killer[65][2];

    private record Killer(int row, int col, int toRow, int toCol, int destroyRow, int destroyCol) {
        boolean matches(int r, int c, Move m, Move d) {
            return row == r && col == c && toRow == m.getRow() &&
                    toCol == m.getCol() && destroyRow == d.getRow() && destroyCol == d.getCol();
        }
    }

    private void rememberKiller(int depth, int r, int c, Move m, Move d) {
        Killer first = killers[depth][0];
        if (first != null && first.matches(r, c, m, d)) return;
        killers[depth][1] = first;
        killers[depth][0] = new Killer(r, c, m.getRow(), m.getCol(),
                d.getRow(), d.getCol());
    }

    private int alphaBeta(
            Board board, Mark joueur, int myRow, int myCol, int oppRow, int oppCol, int depth, int alpha, int beta
    ) {
        numExploredNodes++;
        if (verifierTemps()) return 0;   // valeur ignoree : la profondeur en cours sera jetee

        var adversaire = (this.cpu == Mark.N) ? Mark.R : Mark.N;
        Mark[][] b = board.getBoard();

        boolean max = joueur == this.cpu;
        int r = max ? myRow : oppRow;
        int c = max ? myCol : oppCol;
        int targetR = max ? oppRow : myRow;
        int targetC = max ? oppCol : myCol;
        Mark next = max ? adversaire : this.cpu;
        List<Move> moves = board.searchPossibleMove(r, c, joueur);

        if (moves.isEmpty())
            return max ? -VICTOIRE + depth : VICTOIRE - depth;

        if (depth >= profondeurMaxAB) {
            limiteAtteinte = true;
            return evaluer(board, myRow, myCol, oppRow, oppCol, adversaire);
        }

        int meilleur = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        Killer tried0 = null, tried1 = null;

        // Tester les killers avant les autres coups, seulement s'ils sont legaux.
        for (int i = 0; i < 2; i++) {
            Killer k = killers[depth][i];
            if (k == null || k.row() != r || k.col() != c) continue;

            Move m = null;

            for (Move candidate : moves) {
                if (candidate.getRow() == k.toRow() && candidate.getCol() == k.toCol()) {
                    m = candidate;
                    break;
                }
            }
            if (m == null) continue;
            b[r][c] = Mark.EMPTY;
            b[m.getRow()][m.getCol()] = joueur;
            Move d = null;
            for (Move candidate : board.getNbOfDestroyedSquare(targetR, targetC, joueur)) {
                if (k.matches(r, c, m, candidate)) { d = candidate; break; }
            }
            if (d != null) {
                if (i == 0) tried0 = k; else tried1 = k;
                board.destruireCase(d);
                int score = alphaBeta(board, next,
                        max ? m.getRow() : myRow, max ? m.getCol() : myCol,
                        max ? oppRow : m.getRow(), max ? oppCol : m.getCol(),
                        depth + 1, alpha, beta);
                b[d.getRow()][d.getCol()] = Mark.EMPTY;
                meilleur = max ? Math.max(meilleur, score) : Math.min(meilleur, score);
                if (max) alpha = Math.max(alpha, meilleur); else beta = Math.min(beta, meilleur);
            }
            b[m.getRow()][m.getCol()] = Mark.EMPTY;
            b[r][c] = joueur;
            if (tempsEcoule) return meilleur;
            if (alpha >= beta) {
                rememberKiller(depth, r, c, m, d);
                return meilleur;
            }
        }
        // Recherche habituelle, sans rejouer les killers deja testes.
        for (Move m : moves) {
            b[r][c] = Mark.EMPTY;
            b[m.getRow()][m.getCol()] = joueur;
            for (Move d : board.getNbOfDestroyedSquare(targetR, targetC, joueur)) {
                if ((tried0 != null && tried0.matches(r, c, m, d))
                        || (tried1 != null && tried1.matches(r, c, m, d))) continue;
                board.destruireCase(d);
                int score = alphaBeta(board, next,
                        max ? m.getRow() : myRow, max ? m.getCol() : myCol,
                        max ? oppRow : m.getRow(), max ? oppCol : m.getCol(),
                        depth + 1, alpha, beta);
                b[d.getRow()][d.getCol()] = Mark.EMPTY;
                meilleur = max ? Math.max(meilleur, score) : Math.min(meilleur, score);
                if (max) alpha = Math.max(alpha, meilleur); else beta = Math.min(beta, meilleur);
                if (tempsEcoule) break;
                if (alpha >= beta) {
                    rememberKiller(depth, r, c, m, d);
                    break;
                }
            }
            b[m.getRow()][m.getCol()] = Mark.EMPTY;
            b[r][c] = joueur;
            if (alpha >= beta || tempsEcoule) break;
        }
        return meilleur;
    }
}
