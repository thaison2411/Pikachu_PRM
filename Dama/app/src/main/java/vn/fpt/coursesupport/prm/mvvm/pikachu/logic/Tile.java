package vn.fpt.coursesupport.prm.mvvm.pikachu.logic;

public class Tile {
    private GamePikachu owner;
    private int row, col;
    private int imageId; // ID từ 1 đến 10 (Loại Pikachu)
    private boolean isMatched = false;
    private boolean isEmpty = false; // Có thể dùng để tạo ô trống giữa bàn cờ (nếu cần)

    public Tile(GamePikachu game, int row, int col, int imageId) {
        this.owner = game;
        this.row = row;
        this.col = col;
        this.imageId = imageId;
        this.isEmpty = (imageId == 0); // Giả sử imageId 0 là ô trống
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getImageId() { return imageId; }
    public boolean isMatched() { return isMatched; }
    public boolean isEmpty() { return isEmpty; }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    // Dùng để so sánh bằng tọa độ
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Tile)) return false;
        Tile otherTile = (Tile) other;
        return this.row == otherTile.row && this.col == otherTile.col;
    }

    // Dùng để so sánh bằng giá trị hình ảnh (Pikachu ID)
    public boolean isSameImage(Tile other) {
        return other != null && this.imageId == other.imageId;
    }
}