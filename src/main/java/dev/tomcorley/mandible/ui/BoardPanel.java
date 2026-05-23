package dev.tomcorley.mandible.ui;

import dev.tomcorley.mandible.game_logic.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    private static final int HEX_SIZE = 40;
    private static final int HAND_HEX_SIZE = 20;
    private static final int HAND_SPACING = 44;
    private static final int DIVIDER_Y_RATIO_NUMERATOR = 2;
    private static final int DIVIDER_Y_RATIO_DENOMINATOR = 3;

    private final HiveGame game;

    public BoardPanel(HiveGame game) {
        this.game = game;
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(1000, 700));
    }

    public void update() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int dividerY = getHeight() * DIVIDER_Y_RATIO_NUMERATOR / DIVIDER_Y_RATIO_DENOMINATOR;

        drawBoard(g2d, dividerY);

        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawLine(0, dividerY, getWidth(), dividerY);

        drawHands(g2d, dividerY);
    }

    private void drawBoard(Graphics2D g, int sectionHeight) {
        int cx = getWidth() / 2;
        int cy = sectionHeight / 2;

        for (Map.Entry<HexCoordinate, Deque<HivePiece>> entry : game.getBoard().getGrid().getGrid().entrySet()) {
            HexCoordinate hex = entry.getKey();
            HivePiece piece = entry.getValue().peek();
            if (piece == null) continue;

            int px = cx + (int) (HEX_SIZE * 1.5 * hex.getQ());
            int py = cy + (int) (HEX_SIZE * (Math.sqrt(3) / 2.0 * hex.getQ() + Math.sqrt(3) * hex.getR()));

            drawHex(g, px, py, piece, HEX_SIZE);
        }
    }

    private void drawHands(Graphics2D g, int yOffset) {
        Set<HivePiece> placed = game.getBoard().getPieceLocations().keySet();
        int handCenterY = yOffset + (getHeight() - yOffset) / 2;

        drawPlayerHand(g, game.getWhitePlayer(), placed, 0, getWidth() / 2, handCenterY);
        drawPlayerHand(g, game.getBlackPlayer(), placed, getWidth() / 2, getWidth(), handCenterY);
    }

    private void drawPlayerHand(Graphics2D g, Player player, Set<HivePiece> placed, int xStart, int xEnd, int cy) {
        List<HivePiece> unplaced = player.getHand().stream()
            .filter(p -> !placed.contains(p))
            .toList();

        if (unplaced.isEmpty()) return;

        int totalWidth = unplaced.size() * HAND_SPACING;
        int x = xStart + (xEnd - xStart - totalWidth) / 2 + HAND_HEX_SIZE;

        for (HivePiece piece : unplaced) {
            drawHex(g, x, cy, piece, HAND_HEX_SIZE);
            x += HAND_SPACING;
        }
    }

    private void drawHex(Graphics2D g, int cx, int cy, HivePiece piece, int size) {
        Polygon hex = flatTopHexagon(cx, cy, size);

        g.setColor(piece.getColour() == PlayerColour.WHITE ? Color.WHITE : Color.BLACK);
        g.fillPolygon(hex);

        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(hex);

        String label = initial(piece.getType());
        g.setFont(g.getFont().deriveFont(Font.BOLD, size * 0.45f));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(piece.getColour() == PlayerColour.WHITE ? Color.BLACK : Color.WHITE);
        g.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 1);
    }

    private Polygon flatTopHexagon(int cx, int cy, int size) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            hex.addPoint(
                cx + (int) (size * Math.cos(angle)),
                cy + (int) (size * Math.sin(angle))
            );
        }
        return hex;
    }

    private String initial(HivePieceType type) {
        return switch (type) {
            case QUEEN_BEE   -> "Q";
            case LADYBUG     -> "L";
            case GRASSHOPPER -> "G";
            case SPIDER      -> "S";
            case ANT         -> "A";
            case BEETLE      -> "B";
            case PILLBUG     -> "P";
            case MOSQUITO    -> "M";
        };
    }
}
