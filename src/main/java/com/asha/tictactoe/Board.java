package com.asha.tictactoe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.asha.tictactoe.Constants.BOARD_SIZE;

public class Board {
	enum Marker { BLANK, X, O };
	
	public Marker[][] board = new Marker[BOARD_SIZE][BOARD_SIZE];
	
	private static final Logger log = LoggerFactory.getLogger(Board.class);

	/**
	 * Clears the TicTacTow board of all of the markers.
	 */
	public void clear() {
		for(int row = 0;  row < BOARD_SIZE;  ++row ) {
			for(int col = 0;  col < BOARD_SIZE;  ++col) {
				board[row][col] = Marker.BLANK;
			}
		}
	}
	
	public String markAt(int row, int col)
	{
		Marker marker = board[row][col];
		if(marker.equals(Marker.X)) {
			return "X";
		}
		else if(marker.equals(Marker.O)) {
			return "O";
		}
		else if(marker.equals(Marker.BLANK)) {
			return " ";
		}
		return "#";
	}
	
	/**
	 * Place specified marker on the board at the specified row and column.
	 * @param row Row index to place marker
	 * @param col Column index to place marker
	 * @param marker Marker type
	 * @throws Exception
	 */
	public void move(int row, int col, Marker marker) throws Exception {
		if( board[row][col] != Marker.BLANK) {
			throw new Exception( "Square @ (" + row + ", " + col + ") is not empty");
		}
		if(marker == Marker.BLANK) {
			throw new IllegalArgumentException("Playing a BLANK marker is not valid");
		}

		board[row][col] = marker;
	}
	
	/**
	 * Determine if the requested marker type has won the game.
	 * 
	 * @param marker Marker type to check
	 * @return true if the indicated marker has won the game.
	 */
	public boolean isWinner(Marker marker) {
		// Check for three in a row across
		for(int row = 0; row < BOARD_SIZE;  ++row) {
			boolean isWinner = true;
			for(int col = 0; isWinner && (col < BOARD_SIZE); ++col) {
				if(board[row][col] != marker) {
					isWinner = false;
				}
			}
			if(isWinner) {
				return true;
			}
		}
		
		// Check for three in a column
		for(int col = 0; col < BOARD_SIZE;  ++col) {
			boolean isWinner = true;
			for(int row = 0; isWinner && (row < BOARD_SIZE); ++row) {
				if(board[row][col] != marker) {
					isWinner = false;
				}
			}
			if(isWinner) {
				return true;
			}
		}
		
		// Check the forward diagonals
		boolean isWinner = true;
		for(int i = 0; (i < BOARD_SIZE) && isWinner;  ++i) {
			if ((board[i][i] != marker)) {
				isWinner = false;
			}
		}
		if(isWinner) {
			return true;
		}

		//Check the reverse diagonals
		isWinner = true;
		for(int i = 0; (i < BOARD_SIZE) && isWinner ;  ++i) {
			if (board[BOARD_SIZE - i - 1][i] != marker) {
				isWinner = false;
			}
		}
		if(isWinner) {
			return true;
		}

		return false;
	}
	/**
	 * Determine if the game is a draw
	 * @return true if the game cannot be won.
	 */
	public boolean isDraw() {
		// If all squares are filled, and a winner not declared, it's a draw
		for(int row = 0 ;  row < BOARD_SIZE;  ++row) {
			for(int col = 0 ;  col < BOARD_SIZE;  ++col) {
				if(board[row][col].equals(Marker.BLANK)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean canBlockWinHorizontally(GameState gameState) {

		Board.Marker boardArray[][] = gameState.getBoard().board;
		Board.Marker playerMarker = gameState.getTurn();
		Board.Marker opponentMarker = playerMarker.equals(Board.Marker.X) ? Board.Marker.O : Board.Marker.X ;

		// Check if there is a block move in the rows.
		for(int row = 0; row < BOARD_SIZE; ++row) {
			int blankCount = 0;
			int opponentCount = 0;
			for(int col = 0; col < BOARD_SIZE; ++col) {
				if(boardArray[row][col].equals(opponentMarker)) {
					++opponentCount;
				}
				if(boardArray[row][col].equals(Board.Marker.BLANK)) {
					++blankCount;
				}
			}

			// If there were two opponent markers and a blank,
			// move to the blank spot.
			if((opponentCount == 2) && (blankCount == 1)) {
				for(int col = 0; col < BOARD_SIZE; ++col) {
					if(boardArray[row][col].equals(Board.Marker.BLANK)) {
						try {
							gameState.getBoard().move(row, col, playerMarker);
							return true;
						}
						catch(Exception e) {
							// Already checked
						}
					}
				}
			}
		}
		return false;
	}

	public boolean canBlockWinVertically(GameState gameState) {

		Board.Marker boardArray[][] = gameState.getBoard().board;
		Board.Marker playerMarker = gameState.getTurn();
		Board.Marker opponentMarker = playerMarker.equals(Board.Marker.X) ? Board.Marker.O : Board.Marker.X ;

		// Check columns for blockers.
		for(int col = 0; col < BOARD_SIZE; ++col) {
			int blankCount = 0;
			int opponentCount = 0;
			for(int row = 0; row < BOARD_SIZE; ++row) {
				if(boardArray[row][col].equals(opponentMarker)) {
					++opponentCount;
				}
				if(boardArray[row][col].equals(Board.Marker.BLANK)) {
					++blankCount;
				}
			}

			// If there were two opponent markers and a blank,
			// move to the blank spot.
			if((opponentCount == 2) && (blankCount == 1)) {
				for(int row = 0; row < BOARD_SIZE; ++row) {
					if(boardArray[row][col].equals(Board.Marker.BLANK)) {
						try {
							gameState.getBoard().move(row, col, playerMarker);
							return true;
						}
						catch(Exception e) {
							// Already checked
						}
					}
				}
			}
		}
		return false;
	}

	public boolean canBlockWinDiagonally(GameState gameState) {

		Board.Marker boardArray[][] = gameState.getBoard().board;
		Board.Marker playerMarker = gameState.getTurn();
		Board.Marker opponentMarker = playerMarker.equals(Board.Marker.X) ? Board.Marker.O : Board.Marker.X ;

		// And lastly for blockers, check for diagonals
		//Checking forward diagonals
		int blankCount = 0;
		int opponentCount = 0;
		for(int i = 0; i < BOARD_SIZE; ++i) {
			if(boardArray[i][i].equals(opponentMarker)) {
				++opponentCount;
			}
			if(boardArray[i][i].equals(Board.Marker.BLANK)) {
				++blankCount;
			}
		}
		if((opponentCount == 2) && (blankCount == 1)) {
			for(int i = 0; i < BOARD_SIZE; ++i) {
				if(boardArray[i][i].equals(Board.Marker.BLANK)) {
					try {
						gameState.getBoard().move(i, i, playerMarker);
						return true;
					}
					catch(Exception e) {
						// Already checked
					}
				}
			}
		}

		//checking for reverse diagonals
		blankCount = 0;
		opponentCount = 0;
		for(int i = 0; i < BOARD_SIZE; ++i) {
			if(boardArray[BOARD_SIZE - i - 1][i].equals(opponentMarker)) {
				++opponentCount;
			}
			if(boardArray[BOARD_SIZE - i - 1][i].equals(Board.Marker.BLANK)) {
				++blankCount;
			}
		}
		if((opponentCount == 2) && (blankCount == 1)) {
			for(int i = 0; i < BOARD_SIZE; ++i) {
				if(boardArray[BOARD_SIZE - i - 1][i].equals(Board.Marker.BLANK)) {
					try {
						gameState.getBoard().move((BOARD_SIZE - i - 1), i, playerMarker);
						return true;
					}
					catch(Exception e) {
						// Already checked
					}
				}
			}
		}
		return false;
	}
}