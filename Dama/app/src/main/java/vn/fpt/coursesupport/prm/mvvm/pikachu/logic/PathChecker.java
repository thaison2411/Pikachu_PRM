package vn.fpt.coursesupport.prm.mvvm.pikachu.logic;

public class PathChecker {

    // Kiểm tra 2 ô có thể nối được không
    public static boolean canConnect(GamePikachu game, Tile t1, Tile t2) {
        if (t1.equals(t2) || !t1.isSameImage(t2) || t1.isMatched() || t2.isMatched()) return false;

        // --- 0 góc ---
        if (t1.getRow() == t2.getRow()) {
            if (isPathClearHorizontal(game, t1.getRow(), t1.getCol(), t2.getCol())) return true;
        }
        if (t1.getCol() == t2.getCol()) {
            if (isPathClearVertical(game, t1.getCol(), t1.getRow(), t2.getRow())) return true;
        }

        // --- 1 góc ---
        if (checkOneCorner(game, t1, t2, t1.getRow(), t2.getCol())) return true;
        if (checkOneCorner(game, t1, t2, t2.getRow(), t1.getCol())) return true;

        // --- 2 góc (có thể qua ngoài biên) ---
        // Kiểm tra tất cả hàng
        for (int r = -1; r <= game.getRows(); r++) { // -1 và rows cho phép ra ngoài biên
            if (checkTwoCorners(game, t1, t2, r, t1.getCol(), r, t2.getCol())) return true;
        }
        // Kiểm tra tất cả cột
        for (int c = -1; c <= game.getCols(); c++) {
            if (checkTwoCorners(game, t1, t2, t1.getRow(), c, t2.getRow(), c)) return true;
        }

        return false;
    }

    // 1 góc
    private static boolean checkOneCorner(GamePikachu game, Tile t1, Tile t2, int r, int c) {
        if (isInsideBoard(game, r, c) && !game.isTileEmpty(r, c)) return false;

        boolean path1 = (t1.getRow() == r)
                ? isPathClearHorizontal(game, r, t1.getCol(), c)
                : isPathClearVertical(game, c, t1.getRow(), r);

        boolean path2 = (t2.getRow() == r)
                ? isPathClearHorizontal(game, r, c, t2.getCol())
                : isPathClearVertical(game, c, r, t2.getRow());

        return path1 && path2;
    }

    // 2 góc
    private static boolean checkTwoCorners(GamePikachu game, Tile t1, Tile t2, int r1, int c1, int r2, int c2) {
        // Điểm góc trong board: nếu trong board phải trống
        if (isInsideBoard(game, r1, c1) && !game.isTileEmpty(r1, c1)) return false;
        if (isInsideBoard(game, r2, c2) && !game.isTileEmpty(r2, c2)) return false;

        boolean middlePath = (r1 == r2)
                ? isPathClearHorizontal(game, r1, c1, c2)
                : isPathClearVertical(game, c1, r1, r2);
        if (!middlePath) return false;

        boolean path1 = (t1.getRow() == r1)
                ? isPathClearHorizontal(game, r1, t1.getCol(), c1)
                : isPathClearVertical(game, t1.getCol(), t1.getRow(), r1);

        boolean path3 = (t2.getRow() == r2)
                ? isPathClearHorizontal(game, r2, c2, t2.getCol())
                : isPathClearVertical(game, t2.getCol(), r2, t2.getRow());

        return path1 && path3;
    }

    private static boolean isPathClearHorizontal(GamePikachu game, int row, int col1, int col2) {
        int start = Math.min(col1, col2) + 1;
        int end = Math.max(col1, col2);
        for (int c = start; c < end; c++) {
            if (isInsideBoard(game, row, c) && !game.isTileEmpty(row, c)) return false;
        }
        return true;
    }

    private static boolean isPathClearVertical(GamePikachu game, int col, int row1, int row2) {
        int start = Math.min(row1, row2) + 1;
        int end = Math.max(row1, row2);
        for (int r = start; r < end; r++) {
            if (isInsideBoard(game, r, col) && !game.isTileEmpty(r, col)) return false;
        }
        return true;
    }

    private static boolean isInsideBoard(GamePikachu game, int r, int c) {
        return r >= 0 && r < game.getRows() && c >= 0 && c < game.getCols();
    }
}
