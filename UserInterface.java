
/*
 * Benjamin Yan
 * 6/10/2020
 * The purpose of this program is to create the GUI with the components that the user will interact with.
 */

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class UserInterface extends JPanel implements MouseListener, MouseMotionListener{
    static int mouseX, mouseY, newMouseX, newMouseY;
    static int squareSize = 32;
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.yellow);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        for (int i=0;i<64;i+=2) {
            g.setColor(new Color(255,200,100));
            g.fillRect((i%8+(i/8)%2)*squareSize, (i/8)*squareSize, squareSize, squareSize);
            g.setColor(new Color(150,50,30));
            g.fillRect(((i+1)%8-((i+1)/8)%2)*squareSize, ((i+1)/8)*squareSize, squareSize, squareSize);
        }
        Image chessPiecesImage;
        chessPiecesImage=new ImageIcon("ChessPieces.png").getImage();
        for (int i=0;i<64;i++) {
            int j=-1,k=-1;
            switch (AlphaBetaChess.chessBoard[i/8][i%8]) {
                case "P": j=5; k=0;
                    break;
                case "p": j=5; k=1;
                    break;
                case "R": j=2; k=0;
                    break;
                case "r": j=2; k=1;
                    break;
                case "K": j=4; k=0;
                    break;
                case "k": j=4; k=1;
                    break;
                case "B": j=3; k=0;
                    break;
                case "b": j=3; k=1;
                    break;
                case "Q": j=1; k=0;
                    break;
                case "q": j=1; k=1;
                    break;
                case "A": j=0; k=0;
                    break;
                case "a": j=0; k=1;
                    break;
            }
            if (j!=-1 && k!=-1) {
                g.drawImage(chessPiecesImage, (i%8)*squareSize, (i/8)*squareSize, (i%8+1)*squareSize, (i/8+1)*squareSize, j*64, k*64, (j+1)*64, (k+1)*64, this);
            }
        }
        /*g.setColor(Color.BLUE);
        g.fillRect(x - 20, y - 20, 40, 40);
        g.setColor(new Color(190, 81, 215));
        g.fillRect(40, 20, 80, 50);
        g.drawString("Ben", x, y);
        Image chessPieceImage = new ImageIcon("ChessPieces.png").getImage();
        g.drawImage(chessPieceImage, x, 0, x + 100, 100, x, 0, x+100, 100, this);*/
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        if (e.getX() < 8 * squareSize && e.getY() < 8 * squareSize) {
            mouseX = e.getX();
            mouseY = e.getY();
            repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getX() < 8 * squareSize && e.getY() < 8 * squareSize) {
            newMouseX = e.getX();
            newMouseY = e.getY();
            String dragMove;
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (newMouseY/squareSize == 0 && mouseY/squareSize == 1 &&
                        "P".equals(AlphaBetaChess.chessBoard[mouseY/squareSize][mouseX/squareSize])) {
                    // pawn promotion
                    dragMove = "" + mouseX/squareSize + newMouseX/squareSize +
                            AlphaBetaChess.chessBoard[newMouseY/squareSize][newMouseX/squareSize] + "QP";
                } else {
                    // non-pawn promotion
                    dragMove = "" + mouseY/squareSize + mouseX/squareSize +
                            newMouseY/squareSize + newMouseX/squareSize +
                            AlphaBetaChess.chessBoard[newMouseY/squareSize][newMouseX/squareSize];
                }
                String userPossibilities = AlphaBetaChess.possibleMoves();
                if (userPossibilities.replaceAll(dragMove, "").length() < userPossibilities.length()) {
                    // if valid move
                    AlphaBetaChess.makeMove(dragMove);
                    AlphaBetaChess.flipBoard();
                    AlphaBetaChess.makeMove(AlphaBetaChess.alphaBeta(AlphaBetaChess.globalDepth,
                            1000000, -1000000, "", 0));
                    AlphaBetaChess.flipBoard();
                    repaint();
                }
            }
            repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
}
