package applications;

import chess.ChessExeception;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Program {
    public static void main (String[] args){

        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();

        double turn;

        String ANSI_RESET = "\u001B[0m";
        String ANSI_BLACK_BACKGROUND = "\u001B[40m";
        String ANSI_WHITE_BACKGROUND = "\u001B[47m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_BLUE = "\u001B[34m";

        while (!chessMatch.getCheckMate()) {
            try {
                turn = chessMatch.getTurn();

                UI.clearScreen();
                UI.printMatch(chessMatch, captured);
                System.out.print(" | ");
                if (turn % 2.0 != 0){
                    System.out.print(ANSI_BLACK_BACKGROUND + ANSI_BLUE + " Source: " + ANSI_RESET + " ");
                } else {
                    System.out.print(ANSI_WHITE_BACKGROUND + ANSI_YELLOW + " Source: " + ANSI_RESET + " ");
                }

                ChessPosition source = UI.readChessPosition(sc);

                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();

                UI.printMatch(chessMatch, possibleMoves, captured);
                System.out.print(" | ");
                if (turn % 2.0 != 0){
                    System.out.print(ANSI_BLACK_BACKGROUND + ANSI_BLUE + " Target: " + ANSI_RESET + " ");
                } else {
                    System.out.print(ANSI_WHITE_BACKGROUND + ANSI_YELLOW + " Target: " + ANSI_RESET + " ");
                }
                ChessPosition target = UI.readChessPosition(sc);

                ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

                if (capturedPiece != null){
                    captured.add(capturedPiece);
                }

            } catch (ChessExeception | InputMismatchException e) {
                System.out.println (e.getMessage());
                sc.nextLine();
            }
        }
        UI.clearScreen();
        UI.printMatch(chessMatch, captured);
    }
}
