import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        

      Board board = new Board();

      board.play(new Move(0, 0), Mark.O);
      board.play(new Move(0, 1), Mark.O);

      board.play(new Move(1, 0), Mark.X);
      board.play(new Move(2, 1), Mark.X);

      CPUPlayer cpu = new CPUPlayer(Mark.X);
        
        ArrayList<Move> bestMoves = cpu.getNextMoveMinMax(board);

        for (Move move : bestMoves) {
            System.out.println(
                "Meilleur coup : (" +
                move.getRow() + ", " +
                move.getCol() + ")"
            );
        }

        System.out.println(
            "Noeuds explores : " +
            cpu.getNumOfExploredNodes()
        );
    }
}