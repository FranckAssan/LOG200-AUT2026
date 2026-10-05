import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Board board = new Board();

        board.play(new Move(0, 0), Mark.O);
        board.play(new Move(0, 1), Mark.O);

        board.play(new Move(1, 0), Mark.X);
        board.play(new Move(2, 1), Mark.X);

        CPUPlayer cpu = new CPUPlayer(Mark.X);

        ArrayList<Move> minmaxMoves =
            cpu.getNextMoveMinMax(board);

        System.out.println("=== MINIMAX ===");

        for (Move move : minmaxMoves) {
            System.out.println(
                "Meilleur coup : (" +
                move.getRow() + ", " +
                move.getCol() + ")"
            );
        }

        System.out.println(
            "Noeuds explores Minimax : " +
            cpu.getNumOfExploredNodes()
        );

        ArrayList<Move> abMoves =
            cpu.getNextMoveAB(board);

        System.out.println("\n=== ALPHA-BETA ===");

        for (Move move : abMoves) {
            System.out.println(
                "Meilleur coup : (" +
                move.getRow() + ", " +
                move.getCol() + ")"
            );
        }

        System.out.println(
            "Noeuds explores Alpha-Beta : " +
            cpu.getNumOfExploredNodes()
        );
    }
}