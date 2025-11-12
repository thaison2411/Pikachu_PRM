package vn.fpt.coursesupport.prm.mvvm.pikachu.logic;

public interface IGamePikachuObserver {
	void updateTileSelected(Tile tile);
	void updateTileUnselected(Tile tile);
	void updateMatchFound(Tile tile1, Tile tile2);
	void updateNoMatch(Tile tile1, Tile tile2);
	void updateGameWin(int level);
	// Có thể thêm updateGameOver nếu hết thời gian/hết lượt.
}