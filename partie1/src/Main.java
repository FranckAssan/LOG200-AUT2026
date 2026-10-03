import javax.swing.*;

void main() {
    Board board = new Board();
    CPUPlayer cpu = new CPUPlayer(Mark.X);

//   cpu.cpuPlayMinMax(board);
   // cpu.cpuPlayAlphaBeta(board);


    Panneau panel = new Panneau(board, cpu);
    Gui fenetre = new Gui(panel);
    SwingUtilities.invokeLater(fenetre);


}
