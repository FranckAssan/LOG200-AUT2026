import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Panneau extends JPanel {

    private final Board board;
    private final CPUPlayer cpuPlayer;
    private final JButton[][] jButtons;
    private final JLabel winnerLabel = new JLabel();
    private final JPanel gamePanel;

    private int humanRowPosition = 0;
    private int humanColPosition = 0;

    private final Mark humanMark;

    AtomicReference<AtomicReference<Move>> move = new AtomicReference<>();
    AtomicReference<AtomicReference<Move>> caseAdetruire = new AtomicReference<>();


    public Panneau(Board boards, CPUPlayer cpuPlayer) {
        this.cpuPlayer = cpuPlayer;
        this.humanMark = (cpuPlayer.getMark().equals(Mark.R)) ? Mark.N : Mark.R;
        this.board = boards;
        this.jButtons = new JButton[this.board.getBoard().length][this.board.getBoard()[0].length];
        this.gamePanel = new JPanel(
                new GridLayout(this.board.getBoard().length, this.board.getBoard()[0].length)
        );
        this.setLayout(new BorderLayout());
        this.initPanel(cpuPlayer);
        this.add(gamePanel, BorderLayout.CENTER);
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        winnerLabel.setText("Result : ");
        JPanel infosPanel = new JPanel();
        infosPanel.add(winnerLabel);
        this.add(infosPanel, BorderLayout.SOUTH);
    }

    private void checkForAWinner() {
        Mark gagnant = this.board.verifierGagnant(this.humanMark, this.humanRowPosition, this.humanColPosition);

        if (gagnant == null) return;           // partie pas finie

        if (gagnant == this.cpuPlayer.getMark()) {
            winnerLabel.setText("Defaite");
        } else {
            winnerLabel.setText(gagnant.name() + " Gagne!");
        }
    }


    private void initPanel(CPUPlayer cpuPlayer) {
        for (int i = 0; i < this.board.getBoard().length; i++) {
            for (int j = 0; j < this.board.getBoard()[i].length; j++) {
                jButtons[i][j] = new JButton();
                jButtons[i][j].setFont(new Font("Arial", Font.PLAIN, 30));
                jButtons[i][j].setFocusable(false);
                jButtons[i][j].setEnabled(false);
                this.gamePanel.add(jButtons[i][j]);
            }

        }

        Mark adversaire = (this.cpuPlayer.getMark() == Mark.R) ? Mark.N : Mark.R;

        this.jButtons[this.cpuPlayer.getCpuRowPos()][this.cpuPlayer.getCpuColPos()].setText(cpuPlayer.getMark().name());
        this.board.getBoard()[this.cpuPlayer.getCpuRowPos()][this.cpuPlayer.getCpuColPos()] = this.cpuPlayer.getMark();

        this.humanRowPosition = 11;
        this.humanColPosition = 5;

        this.jButtons[humanRowPosition][humanColPosition].setText(adversaire.name());
        this.jButtons[humanRowPosition][humanColPosition].setEnabled(true);
        this.board.getBoard()[humanRowPosition][humanColPosition] = adversaire;

        checkForAWinner();

        if (this.board.verifierGagnant(this.humanMark, this.humanRowPosition, this.humanColPosition) == null) {
            activerPionHumain(adversaire);
        }

        checkForAWinner();
    }

    private void activerPionHumain(Mark player) {
        this.jButtons[humanRowPosition][humanColPosition].setEnabled(true);
        this.jButtons[humanRowPosition][humanColPosition].addActionListener(e -> {
            List<Move> moveList = this.board.searchPossibleMove(humanRowPosition, humanColPosition, player);

            if (!moveList.isEmpty()) {
                this.drawPossibleMove(moveList, player);
                this.removeActionListerners(this.jButtons[humanRowPosition][humanColPosition]);
            } else {
                IO.println("No more avalaible move for Human player");
            }
        });
    }


    private void drawPossibleMove(List<Move> moveList, Mark humanMark) {
        for (Move m : moveList) {
            this.jButtons[m.getRow()][m.getCol()].setBackground(Color.CYAN);
            this.jButtons[m.getRow()][m.getCol()].setEnabled(true);

            this.jButtons[m.getRow()][m.getCol()].addActionListener(e -> {
                move.set(this.movePlayer(
                        this.humanRowPosition, this.humanColPosition, m.getRow(), m.getCol(), moveList, humanMark));

                caseAdetruire.set(this.getListCaseToDestroy(this.move.get(), humanMark));
            });
        }
    }

    private AtomicReference<Move> movePlayer(
            int previousRow, int previosCol, int newRow, int newCol, List<Move> list, Mark humanMark
    ) {
        this.jButtons[previousRow][previosCol].setText("");
        this.jButtons[previousRow][previosCol].setEnabled(false);
        this.board.getBoard()[previousRow][previosCol] = Mark.EMPTY;

        this.humanRowPosition = newRow;
        this.humanColPosition = newCol;
        this.jButtons[this.humanRowPosition][this.humanColPosition].setText(humanMark.name());
        this.jButtons[this.humanRowPosition][this.humanColPosition].setEnabled(false);

        this.board.getBoard()[humanRowPosition][humanColPosition] = humanMark;


        for (Move m : list) {
            this.jButtons[m.getRow()][m.getCol()].setBackground(null);
            this.jButtons[m.getRow()][m.getCol()].setEnabled(false);
            removeActionListerners(this.jButtons[m.getRow()][m.getCol()]);
        }

        return new AtomicReference<>(new Move(previousRow, previosCol));
    }

    private AtomicReference<Move> getListCaseToDestroy(AtomicReference<Move> caseDepart, Mark currentPlayer) {
        List<Move> caseADetruire = this.board.getNbOfDestroyedSquare(
                this.cpuPlayer.getCpuRowPos(), this.cpuPlayer.getCpuColPos(), currentPlayer
        );

        var copyCaseToDestroy = List.copyOf(caseADetruire);

        AtomicReference<Move> move = new AtomicReference<>();

        for (Move m : caseADetruire) {
            this.jButtons[m.getRow()][m.getCol()].setBackground(Color.GREEN);
            this.jButtons[m.getRow()][m.getCol()].setEnabled(true);
            this.jButtons[m.getRow()][m.getCol()].addActionListener(e -> {
                move.set(this.destroyCase(caseDepart, m, copyCaseToDestroy));
            });
        }

        return move;
    }


    private Move destroyCase(AtomicReference<Move> caseDepart, Move move, List<Move> copyCaseToDestroy) {
        this.jButtons[move.getRow()][move.getCol()].setEnabled(false);

        this.jButtons[move.getRow()][move.getCol()].setText(Mark.DESTROYED.name());
        this.jButtons[move.getRow()][move.getCol()].setBackground(Color.orange);
        this.board.getBoard()[move.getRow()][move.getCol()] = Mark.DESTROYED;

        for (Move m : copyCaseToDestroy) {
            if (m.getRow() == move.getRow() && m.getCol() == move.getCol()) {
                jButtons[m.getRow()][m.getCol()].setEnabled(false);
                continue;
            }
            jButtons[m.getRow()][m.getCol()].setEnabled(false);
            jButtons[m.getRow()][m.getCol()].setBackground(null);

            removeActionListerners(jButtons[m.getRow()][m.getCol()]);
        }

        var depart = this.board.getRowColFromPosMap(caseDepart.get().toString());
        var newPos = this.board.getRowColFromPosMap(new Move(this.humanRowPosition, this.humanColPosition).toString());
        var caseDetruite = this.board.getRowColFromPosMap(move.toString());

        String finalMove = depart + "-" + newPos + "-" + caseDetruite;

        var res = this.cpuPlayer.cpuPlayAlphaBeta(this.board, finalMove);
        IO.println(res);

        this.updatePanel();

        return move;
    }

    private void removeActionListerners(JButton button) {
        var listeners = button.getActionListeners();
        for (var l : listeners) {
            button.removeActionListener(l);
        }
    }

    private void updatePanel() {
        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard()[i].length; j++) {
                this.jButtons[i][j].setText(texte(this.board.getBoard()[i][j]));
                removeActionListerners(this.jButtons[i][j]);

                if (this.board.getBoard()[i][j] == Mark.DESTROYED) {
                    this.jButtons[i][j].setBackground(Color.orange);
                }
            }
        }
        Mark adversaire = (this.cpuPlayer.getMark() == Mark.R) ? Mark.N : Mark.R;

        checkForAWinner();
        if (this.board.verifierGagnant(this.humanMark, this.humanRowPosition, this.humanColPosition) == null) {
            activerPionHumain(adversaire);
        }
        checkForAWinner();

    }

    private String texte(Mark m) {
        return m == Mark.EMPTY ? "" : m.toString();
    }
}
