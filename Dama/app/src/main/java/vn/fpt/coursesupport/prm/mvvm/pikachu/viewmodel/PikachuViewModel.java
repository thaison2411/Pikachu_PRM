package vn.fpt.coursesupport.prm.mvvm.pikachu.viewmodel;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import vn.fpt.coursesupport.prm.mvvm.pikachu.R;
import vn.fpt.coursesupport.prm.mvvm.pikachu.logic.GamePikachu;
import vn.fpt.coursesupport.prm.mvvm.pikachu.logic.IGamePikachuObserver;
import vn.fpt.coursesupport.prm.mvvm.pikachu.logic.Tile;

// ĐÃ SỬA: Xóa từ khóa 'abstract'
public class PikachuViewModel extends ViewModel implements IGamePikachuObserver {
    private GamePikachu gamePikachu;
    private int rows, cols;
    private int[] pikachuImageResources; // Mảng chứa ID tài nguyên của 10+1 hình Pikachu
    public final ObservableField<Integer> level = new ObservableField<>(1);
    // Mảng 2 chiều các ObservableField để cập nhật giao diện
    public ObservableField<Integer>[][] cellImages;
    // Mảng 2 chiều để theo dõi trạng thái chọn của ô (dùng để đổi background)
    public ObservableField<Integer>[][] selectedCells;
    public final ObservableField<String> gameStateMessage = new ObservableField<>("Chọn Mức Độ!");

    private Handler handler = new Handler();
    private Tile selectedTile1 = null;
    private Tile selectedTile2 = null;

    // Khai báo tài nguyên background
    private final int TILE_NORMAL_BG = R.drawable.tile_background;
    private final int TILE_SELECTED_BG = R.drawable.tile_selected;

    // Khởi tạo
    public PikachuViewModel(int level, int[] imageResources) {
        this.pikachuImageResources = imageResources;
        this.level.set(level);

        // Bắt đầu với mức 1 (hoặc mức được chọn)
        initGame(level);
    }

    private static final int MAX_SIZE = 10;

    public void initGame(int level) {
        gamePikachu = new GamePikachu(level);
        gamePikachu.addObserver(this);
        rows = gamePikachu.getRows();
        cols = gamePikachu.getCols();

        // Luôn tạo mảng 10x10 để không crash
        if (cellImages == null) {
            cellImages = new ObservableField[MAX_SIZE][MAX_SIZE];
            selectedCells = new ObservableField[MAX_SIZE][MAX_SIZE];
            for (int r = 0; r < MAX_SIZE; r++) {
                for (int c = 0; c < MAX_SIZE; c++) {
                    cellImages[r][c] = new ObservableField<>(R.drawable.empty_tile);
                    selectedCells[r][c] = new ObservableField<>(TILE_NORMAL_BG);
                }
            }
        }

        // Chỉ cập nhật những ô hợp lệ trong màn hiện tại
        for (int r = 0; r < MAX_SIZE; r++) {
            for (int c = 0; c < MAX_SIZE; c++) {
                if (r < rows && c < cols) {
                    cellImages[r][c].set(getImageResource(r, c));
                } else {
                    cellImages[r][c].set(R.drawable.empty_tile); // ẩn ô ngoài
                }
                selectedCells[r][c].set(TILE_NORMAL_BG);
            }
        }

        gameStateMessage.set("Bắt đầu màn " + level);
    }





    // Phương thức lấy ID tài nguyên hình ảnh dựa trên Tile ID
    private int getImageResource(int row, int col) {
        Tile tile = gamePikachu.getTile(row, col);
        if (tile == null || tile.isMatched()) return pikachuImageResources[0];

        int id = tile.getImageId();
        if (id <= 0 || id >= pikachuImageResources.length) {
            id = 1; // fallback an toàn
        }
        return pikachuImageResources[id];
    }


    // Xử lý khi người dùng click vào ô
    public void onClickedOn(int row, int col) {
        // Bỏ qua click nếu đang chờ xử lý 2 ô
        if (selectedTile1 != null && selectedTile2 != null) return;

        gamePikachu.onClickTile(row, col);
    }

    // Cập nhật hình ảnh của một ô (ẩn/hiện)
    private void updateCell(Tile tile) {
        if (tile != null) {
            cellImages[tile.getRow()][tile.getCol()].set(getImageResource(tile.getRow(), tile.getCol()));
        }
    }

    // Cập nhật background của một ô (hiệu ứng chọn)
    private void updateSelectedCell(Tile tile, boolean isSelected) {
        if (tile != null) {
            int row = tile.getRow();
            int col = tile.getCol();
            selectedCells[row][col].set(isSelected ? TILE_SELECTED_BG : TILE_NORMAL_BG);
        }
    }

    // --- IGamePikachuObserver Implementation ---

    @Override
    public void updateTileSelected(Tile tile) {
        if (selectedTile1 == null) {
            selectedTile1 = tile;
        } else {
            selectedTile2 = tile;
        }
        // Áp dụng hiệu ứng chọn
        updateSelectedCell(tile, true);
    }

    @Override
    public void updateTileUnselected(Tile tile) {
        // Áp dụng hiệu ứng hủy chọn
        updateSelectedCell(tile, false);

        //  Model đã reset selectedTile1. ViewModel cũng cần reset.
        selectedTile1 = null;
        selectedTile2 = null;
        gameStateMessage.set("Đã hủy chọn ô.");
    }

    @Override
    public void updateMatchFound(Tile tile1, Tile tile2) {
        //  Bỏ hiệu ứng chọn trước khi ẩn
        updateSelectedCell(tile1, false);
        updateSelectedCell(tile2, false);

        // Gọi resetSelections() trong Model để cho phép click tiếp
        gamePikachu.resetSelections();
        selectedTile1 = null;
        selectedTile2 = null;

        // Cập nhật giao diện để ẩn 2 ô (hiển thị hình nền trống)
        updateCell(tile1);
        updateCell(tile2);
        gameStateMessage.set("Tuyệt vời! Đã tìm thấy cặp.");
    }

    @Override
    public void updateNoMatch(Tile tile1, Tile tile2) {
        gameStateMessage.set("Sai rồi! Đợi một chút...");

        // Hủy chọn (Unselect) 2 ô trong View sau delay
        handler.postDelayed(() -> {
            // Hủy hiệu ứng chọn trên UI
            updateSelectedCell(tile1, false);
            updateSelectedCell(tile2, false);

            // Reset logic
            gamePikachu.resetSelections();
            selectedTile1 = null;
            selectedTile2 = null;
            gameStateMessage.set("Tiếp tục chơi!");
        }, 1000); // 1000ms delay
    }

    @Override
    public void updateGameWin(int level) {
        gameStateMessage.set("Chiến thắng! Màn " + level + " hoàn thành!");
        Log.d("PikachuVM", "updateNoMatch called");
        handler.postDelayed(() -> {
            int nextLevel = level + 1;
            Log.d("PikachuVM", "Handler called");
            if (nextLevel > 3) {
                gameStateMessage.set("Bạn đã hoàn thành tất cả các màn!");
                return;
            }

            // Reset trạng thái chọn
            selectedTile1 = null;
            selectedTile2 = null;

            // Cập nhật level và tạo game mới
            this.level.set(nextLevel);
            initGame(nextLevel);

            // Cập nhật thông báo sau khi grid đã khởi tạo xong
            gameStateMessage.set("Bắt đầu màn " + nextLevel + "!");

        }, 1500);


    }

    // =========================================================
    // FACTORY CLASS ĐỂ TRUYỀN THAM SỐ VÀO CONSTRUCTOR
    // =========================================================
    public static class Factory implements ViewModelProvider.Factory {
        private final int level;
        private final int[] imageResources;

        public Factory(int level, int[] imageResources) {
            this.level = level;
            this.imageResources = imageResources;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(PikachuViewModel.class)) {
                // Khởi tạo ViewModel với các tham số đã truyền vào Factory
                return (T) new PikachuViewModel(level, imageResources);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}