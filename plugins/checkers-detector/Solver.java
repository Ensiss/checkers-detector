public class Solver {
    public static Image findCheckers(Image img) {
        Board board = new Board(img);
        Image out = new Image(img.getWidth(), img.getHeight());
        return (img.drawBoard(board));
    }
}
