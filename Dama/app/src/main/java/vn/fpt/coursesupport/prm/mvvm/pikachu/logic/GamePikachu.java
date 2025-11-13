package vn.fpt.coursesupport.prm.mvvm.pikachu.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GamePikachu {
	private int rows, cols;
	private int level; // 1: Dễ, 2: Trung bình, 3: Khó
	private List<Tile> allTiles;
	private Tile selectedTile1 = null;
	private Tile selectedTile2 = null;
	private List<IGamePikachuObserver> observerList;
	private int matchedPairs = 0;

	public GamePikachu(int level) {
		this.level = level;
		this.observerList = new ArrayList<>();
		initBoardDimensions(level);
		initGame();

	}

	private void initBoardDimensions(int level) {
		// Ví dụ kích thước bàn cờ
		switch (level) {
			case 1: // Dễ
				rows = 6;
				cols = 6;
				break;
			case 2: // Trung bình
				rows = 8;
				cols = 8;
				break;
			case 3: // Khó
			default:
				rows = 10;
				cols = 10;
				break;
		}
	}

	private void initGame() {
		allTiles = new ArrayList<>();
		int boardSize = rows * cols;
		int maxPikachuTypes = 10;
		int numPairsRequired = boardSize / 2; // Số cặp cần thiết để lấp đầy

		// Danh sách các ID hình ảnh (Pokemon ID: 1-10)
		List<Integer> imageIds = new ArrayList<>();

		// SỬA LỖI: Tạo danh sách các cặp Pokémon chẵn

		// B1: Xác định các loại Pokémon sẽ được sử dụng
		List<Integer> availableTypes = new ArrayList<>();
		for (int id = 1; id <= maxPikachuTypes; id++) {
			availableTypes.add(id);
		}
		Collections.shuffle(availableTypes); // Xáo trộn loại có sẵn

		// B2: Lặp lại các loại để tạo đủ số cặp
		int currentPairCount = 0;
		int typeIndex = 0;

		while (currentPairCount < numPairsRequired) {
			int pokemonId = availableTypes.get(typeIndex % availableTypes.size());

			// Thêm 1 cặp (2 Pokémon)
			imageIds.add(pokemonId);
			imageIds.add(pokemonId);
			currentPairCount++;

			typeIndex++;

			// Giới hạn số lượng loại Pokemon nếu numPairsRequired quá lớn
			if (typeIndex >= availableTypes.size()) {
				// Nếu đã sử dụng hết 10 loại, quay lại từ đầu để lặp lại
				typeIndex = 0;
			}
		}

		// Nếu sau vòng lặp mà kích thước vẫn lớn hơn boardSize (chỉ xảy ra khi boardSize lẻ)
		// Vì rows*cols luôn chẵn, nên imageIds.size() sẽ luôn bằng boardSize

		// B3: Xáo trộn tất cả các ID đã tạo
		Collections.shuffle(imageIds);

		// Tạo các ô Tile
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				int index = r * cols + c;
				int imageId = imageIds.get(index);
				Tile tile = new Tile(this, r, c, imageId);
				allTiles.add(tile);
			}
		}
		matchedPairs = 0;
	}

	// Lấy ô tại vị trí (r, c)
	public Tile getTile(int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= cols)
			return null;
		int index = row * cols + col;
		return allTiles.get(index);
	}

	public int getRows() { return rows; }
	public int getCols() { return cols; }

	// Logic xử lý khi click vào một ô
	public void onClickTile(int row, int col) {
		Tile clickedTile = getTile(row, col);
		if (clickedTile == null || clickedTile.isEmpty() || clickedTile.isMatched()) return;

		// 1. Nếu chưa chọn ô nào
		if (selectedTile1 == null) {
			selectedTile1 = clickedTile;
			notifyTileSelected(selectedTile1);
		}
		// 2. Chọn ô thứ hai (khác ô thứ nhất)
		else if (selectedTile2 == null && clickedTile != selectedTile1) {
			selectedTile2 = clickedTile;
			notifyTileSelected(selectedTile2);

			// 3. Kiểm tra cặp
			if (selectedTile1.getImageId() == selectedTile2.getImageId()) {
				// Giống nhau, kiểm tra đường nối
				if (PathChecker.canConnect(this, selectedTile1, selectedTile2)) {
					// Cặp hợp lệ: biến mất
					selectedTile1.setMatched(true);
					selectedTile2.setMatched(true);
					matchedPairs++;
					notifyMatchFound(selectedTile1, selectedTile2);

					// Kiểm tra kết thúc game
					if (matchedPairs * 2 == rows * cols) {
						notifyGameWin();
					}
				} else {
					// Không nối được: chờ 1 chút rồi lật lại
					notifyNoMatch(selectedTile1, selectedTile2);
				}
			} else {
				// Khác nhau: chờ 1 chút rồi lật lại
				notifyNoMatch(selectedTile1, selectedTile2);
			}
		}
		// 3. Nếu đã chọn ô 1 và click lại vào ô đó (hủy chọn)
		else if (clickedTile == selectedTile1) {
			// Bỏ chọn ô 1
			notifyTileUnselected(selectedTile1);
			selectedTile1 = null;
		}
	}

	// Phương thức để ViewModel gọi sau khi hiển thị NoMatch
	public void resetSelections() {
		selectedTile1 = null;
		selectedTile2 = null;
	}

	// --- Observer pattern ---
	public void addObserver(IGamePikachuObserver observer) {
		observerList.add(observer);
	}

	private void notifyTileSelected(Tile tile) {
		for (IGamePikachuObserver observer : observerList) {
			observer.updateTileSelected(tile);
		}
	}

	private void notifyTileUnselected(Tile tile) {
		for (IGamePikachuObserver observer : observerList) {
			observer.updateTileUnselected(tile);
		}
	}

	private void notifyMatchFound(Tile tile1, Tile tile2) {
		for (IGamePikachuObserver observer : observerList) {
			observer.updateMatchFound(tile1, tile2);
		}
	}

	private void notifyNoMatch(Tile tile1, Tile tile2) {
		for (IGamePikachuObserver observer : observerList) {
			observer.updateNoMatch(tile1, tile2);
		}
	}

	private void notifyGameWin() {
		for (IGamePikachuObserver observer : observerList) {
			observer.updateGameWin(level);
		}
	}

	// Các phương thức hỗ trợ cho PathChecker
	public boolean isTileEmpty(int row, int col) {
		Tile tile = getTile(row, col);
		// Ô trống nếu tile null, hoặc đã được ghép cặp
		return tile == null || tile.isMatched();
	}
}