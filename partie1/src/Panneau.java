import javax.swing.*;
import java.awt.*;

public class Panneau extends JPanel {

    private final Board board;
    private final CPUPlayer cpuPlayer;
    JButton[][] jButtons = new JButton[3][3];
    private final JLabel winnerLabel = new JLabel();
    private final JPanel gamePanel;

    public Panneau(Board boards, CPUPlayer cpuPlayer) {
        this.cpuPlayer = cpuPlayer;
        this.board = boards;
        this.gamePanel = new JPanel(new GridLayout(3, 3));
        this.setLayout(new BorderLayout());
        this.initPanel();
        this.add(gamePanel, BorderLayout.CENTER);
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        winnerLabel.setText("Result : ");
        JPanel infosPanel = new JPanel();
        infosPanel.add(winnerLabel);
        this.add(infosPanel, BorderLayout.SOUTH);
        this.updatePanel();
    }

    public void humanMove(Move move, Mark mark) {
        checkForAWinner();

        if (this.board.verifierGagnant() == null) {
            this.board.play(move, mark);
        }
        if (this.board.verifierGagnant() == null) {
//            cpuPlayer.cpuPlayMinMax(board);
            cpuPlayer.cpuPlayAlphaBeta(board);
        }
        checkForAWinner();
    }

    private void checkForAWinner() {
        if (!verifierEtatJeu()) {
            String winner = String.valueOf(this.board.verifierGagnant());
            if (winner.equals(Mark.EMPTY.toString())) {
                winnerLabel.setText("Egalité!");
            } else {
                winnerLabel.setText(winner + " Gagne!");
            }

        }
    }

    private boolean verifierEtatJeu() {
        return this.board.verifierGagnant() == null;
    }

    private void initPanel() {
        for (int i = 0; i < this.board.getBoard()[0].length; i++) {
            for (int j = 0; j < this.board.getBoard()[1].length; j++) {
                jButtons[i][j] = new JButton();
                jButtons[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                jButtons[i][j].setFocusable(false);
                final int ii = i;
                final  int jj = j;
                jButtons[i][j].addActionListener(e -> {
                    if (board.getBoard()[ii][jj].equals(Mark.EMPTY)) {
                        jButtons[ii][jj].setText(Mark.O.name());
                        if (verifierEtatJeu()) {
                            humanMove(new Move(ii, jj), Mark.O);
                        }

                        this.updatePanel();
                    }
                });
                this.gamePanel.add(jButtons[i][j]);
            }
        }
    }

    private void updatePanel() {
        for (int i = 0; i < board.getBoard()[0].length; i++) {
            for (int j = 0; j < board.getBoard()[1].length; j++) {
                this.jButtons[i][j].setText(this.board.getBoard()[i][j].toString());
            }
        }
    }
}
