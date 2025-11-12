package vn.fpt.coursesupport.prm.mvvm.pikachu.logic;

public class PathChecker {

    // Hàm kiểm tra đường nối (Tối đa 3 đoạn thẳng)
    public static boolean canConnect(GamePikachu game, Tile t1, Tile t2) {
        if (t1.equals(t2) || !t1.isSameImage(t2) || t1.isMatched() || t2.isMatched()) return false;

        // --- 1. Đường thẳng (0 góc) ---
        if (t1.getRow() == t2.getRow()) { // Cùng hàng
            if (isPathClearHorizontal(game, t1.getRow(), t1.getCol(), t2.getCol())) return true;
        }
        if (t1.getCol() == t2.getCol()) { // Cùng cột
            if (isPathClearVertical(game, t1.getCol(), t1.getRow(), t2.getRow())) return true;
        }

        // --- 2. Đường 2 đoạn (1 góc) ---
        // Góc tại (t1.row, t2.col)
        if (checkOneCorner(game, t1, t2, t1.getRow(), t2.getCol())) return true;
        // Góc tại (t2.row, t1.col)
        if (checkOneCorner(game, t1, t2, t2.getRow(), t1.getCol())) return true;

        // --- 3. Đường 3 đoạn (2 góc) ---

        // Mở rộng ra các hướng (ngang/dọc)
        for (int i = 0; i < game.getCols(); i++) { // Kiểm tra các điểm góc ngang bên ngoài
            if (i != t1.getCol() && i != t2.getCol()) {
                // Góc 1: (t1.row, i) -> Góc 2: (t2.row, i)
                if (checkTwoCorners(game, t1, t2, t1.getRow(), i, t2.getRow(), i)) return true;
            }
        }

        for (int i = 0; i < game.getRows(); i++) { // Kiểm tra các điểm góc dọc bên ngoài
            if (i != t1.getRow() && i != t2.getRow()) {
                // Góc 1: (i, t1.col) -> Góc 2: (i, t2.col)
                if (checkTwoCorners(game, t1, t2, i, t1.getCol(), i, t2.getCol())) return true;
            }
        }

        // Mở rộng ra các hướng ngoài lề bàn cờ
        // Kiểm tra đường nối ra ngoài biên ngang
        for (int c = -1; c <= game.getCols(); c++) {
            if (checkTwoCorners(game, t1, t2, t1.getRow(), c, t2.getRow(), c)) return true;
        }
        // Kiểm tra đường nối ra ngoài biên dọc
        for (int r = -1; r <= game.getRows(); r++) {
            if (checkTwoCorners(game, t1, t2, r, t1.getCol(), r, t2.getCol())) return true;
        }

        return false;
    }

    // Kiểm tra 1 góc (t1 -> p -> t2)
    private static boolean checkOneCorner(GamePikachu game, Tile t1, Tile t2, int r, int c) {
        if (!game.isTileEmpty(r, c)) return false; // Điểm góc phải trống

        // Đoạn 1: t1 -> p
        boolean path1 = (t1.getRow() == r)
                ? isPathClearHorizontal(game, r, t1.getCol(), c)
                : isPathClearVertical(game, c, t1.getRow(), r);

        // Đoạn 2: p -> t2
        boolean path2 = (t2.getRow() == r)
                ? isPathClearHorizontal(game, r, c, t2.getCol())
                : isPathClearVertical(game, c, r, t2.getRow());

        return path1 && path2;
    }

    // Kiểm tra 2 góc (t1 -> p1 -> p2 -> t2)
    private static boolean checkTwoCorners(GamePikachu game, Tile t1, Tile t2, int r1, int c1, int r2, int c2) {
        // p1: (r1, c1), p2: (r2, c2)
        // Đường giữa (p1 -> p2) phải là đường thẳng và trống
        boolean middlePath = (r1 == r2)
                ? isPathClearHorizontal(game, r1, c1, c2)
                : isPathClearVertical(game, c1, r1, r2);

        if (!middlePath) return false;

        // Đoạn 1: t1 -> p1 (1 đoạn thẳng)
        boolean path1 = (t1.getRow() == r1)
                ? isPathClearHorizontal(game, r1, t1.getCol(), c1)
                : isPathClearVertical(game, t1.getCol(), t1.getRow(), r1);

        // Đoạn 3: p2 -> t2 (1 đoạn thẳng)
        boolean path3 = (t2.getRow() == r2)
                ? isPathClearHorizontal(game, r2, c2, t2.getCol())
                : isPathClearVertical(game, t2.getCol(), r2, t2.getRow());

        return path1 && path3;
    }

    // Kiểm tra đường ngang giữa col1 và col2 (không bao gồm col1 và col2)
    private static boolean isPathClearHorizontal(GamePikachu game, int row, int col1, int col2) {
        int start = Math.min(col1, col2) + 1;
        int end = Math.max(col1, col2);
        for (int c = start; c < end; c++) {
            if (!game.isTileEmpty(row, c)) return false;
        }
        return true;
    }

    // Kiểm tra đường dọc giữa row1 và row2 (không bao gồm row1 và row2)
    private static boolean isPathClearVertical(GamePikachu game, int col, int row1, int row2) {
        int start = Math.min(row1, row2) + 1;
        int end = Math.max(row1, row2);
        for (int r = start; r < end; r++) {
            if (!game.isTileEmpty(r, col)) return false;
        }
        return true;
    }
}