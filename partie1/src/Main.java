import javax.swing.*;

void main() {
    Board board = new Board(12, 12);

    CPUPlayer cpu = new CPUPlayer(Mark.R);
    cpu.setCpuRowPos(0);
    cpu.setCpuColPos(5);

    // cpu.cpuPlayMinMax(board);
    // cpu.cpuPlayAlphaBeta(board);

    Panneau panel = new Panneau(board, cpu);

    Gui fenetre = new Gui(panel);
    SwingUtilities.invokeLater(fenetre);
}
