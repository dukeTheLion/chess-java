package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;

    private List<Piece> piecesOnTheBoars = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public boolean getCheck(){
        return check;
    }

    public int getTurn(){
        return turn;
    }

    public Color getCurrentPlayer(){
        return currentPlayer;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove (ChessPosition sourcePosition, ChessPosition targetPosition){
        Position sourse = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();

        validateSourcePosition(sourse);
        validateTargetPosition(sourse, target);

        Piece capturedPiece = makeMove(sourse, target);

        if (testCheck(currentPlayer)){
            undoMove(sourse, target, capturedPiece);
            throw new ChessExeception("Error: you can not put yourself in check");
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        nextTurn();

        return (ChessPiece)capturedPiece;
    }

    private void validateSourcePosition(Position position){
        if(!board.thereIsPiece(position)){
            throw new ChessExeception("Error: no piece on source position");
        }

        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessExeception("Error: this piece is not yours");
        }

        if (!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessExeception("Error: no moves to execute");
        }
    }

    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){
            throw new ChessExeception("Error: piece can't move");
        }
    }

    private void nextTurn(){
        turn++;

        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color){
        return (color == Color.BLACK) ? Color.WHITE : Color.BLACK;
    }

    private ChessPiece king(Color color){
        List<Piece> list = piecesOnTheBoars.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list){
            if(p instanceof King){
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("Error: no " + color + " king on the board");
    }

    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece>  opponentPieces = piecesOnTheBoars.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());

        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }

        return false;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePice(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoars.add(piece);
    }

    private Piece makeMove(Position source, Position target){
        Piece p = board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePice(p, target);

        if(capturedPiece != null) {
            piecesOnTheBoars.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        Piece p = board.removePiece(target);
        board.placePice(p, source);

        if(capturedPiece != null){
            board.placePice(capturedPiece, target);

            capturedPieces.remove(capturedPiece);
            piecesOnTheBoars.add(capturedPiece);
        }
    }

    private void initialSetup(){
        placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
    }
}
