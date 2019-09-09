package com.asha.tictactoe;

import java.util.Arrays;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.asha.tictactoe.Constants.BOARD_SIZE;

@Controller
public class GameController {
		
	private static final Logger log = LoggerFactory.getLogger(GameController.class);

	/**
	 * Starts new Tic Tac Toe game.
	 * 
	 * @param session 
	 * @param model Spring framework Model
	 * @return Spring framework View name
	 */
	@RequestMapping(value = "/tictactoe", method = RequestMethod.GET)
	public String game(
			HttpSession session,
			Model model) {
		
		GameState gameState = getStateFromSession(session);
		gameState.startNewGame();
		model.addAttribute(Constants.GAME_STATE, gameState);

		return Constants.VIEW_GAME;
	}
	
	/**
	 * Places a marker for the current player in the requested position.
	 * 
	 * @param session 
	 * @param row Number of row to place marker
	 * @param col Number of column to place marker
	 * @param model Spring framework Model
	 * @return Spring framework View name
	 */
	@RequestMapping(value = "/tictactoe/move", method = RequestMethod.GET)
	public String playerMove(
			HttpSession session,
			@RequestParam(value = "row", required = true) Integer row, 
			@RequestParam(value = "col", required = true) Integer col, 
			Model model) {
		
		GameState gameState = getStateFromSession(session);
		model.addAttribute(Constants.GAME_STATE, gameState);
		log.info("move=(" + row + ", " + col + ")");

		// If not in the midst of a game, don't allow move.
		if(!gameState.getGameStage().equals(GameState.GameStage.IN_GAME)) {
			log.info("Game not in progress); ignoring move request.");
			return Constants.VIEW_GAME;
		}
		
		Board board = gameState.getBoard();

		try {
			board.move(row, col, gameState.getTurn());
			evaluateBoard(gameState);
			
			// If game has not ended one way or another, determine where it will move.
			if(gameState.getGameStage().equals(GameState.GameStage.IN_GAME)) {
				determineBestMove(gameState);
				evaluateBoard(gameState);
			}
		}
		catch( Exception e )
		{
			log.error("Cannot complete move", e);
		}
		
		return Constants.VIEW_GAME;
	}
	
	/**
	 * Evaluate the game board to see if a winner can be declared, or if there is a draw.
	 * If neither of these conditions is detected, switch active player.
	 * 
	 * @param gameState
	 */
	public void evaluateBoard(GameState gameState) {
		Board board = gameState.getBoard();

		//First check if there is a winner
		if(board.isWinner(gameState.getTurn())) {
			if(gameState.getTurn().equals(Board.Marker.O)) {
				gameState.setGameMessage("O wins!");
			}
			else {
				gameState.setGameMessage("X wins!");
			}
			gameState.setGameStage(GameState.GameStage.POST_GAME);
		}
		//Check if it is a draw
		else if(board.isDraw()) {
			gameState.setGameMessage("It's a draw!");
			gameState.setGameStage(GameState.GameStage.POST_GAME);
		}
		else
		{
			if(gameState.getTurn() == Board.Marker.X) {
				gameState.setTurn(Board.Marker.O);
				gameState.setTurnMessage("Turn: O");
			}
			else {
				gameState.setTurn(Board.Marker.X);
				gameState.setTurnMessage("Turn: X");
			}
		}
	}

	/**
	 * This method is called during play against the computer, and 
	 * attempts to find the best possible move.
	 * It tries to block the winning of the opponent
	 * @param gameState
	 */
	public void determineBestMove(GameState gameState)
	{
		Board board = gameState.getBoard();
		Board.Marker boardArray[][] = gameState.getBoard().board;
		Board.Marker playerMarker = gameState.getTurn();
		Board.Marker opponentMarker = playerMarker.equals(Board.Marker.X) ? Board.Marker.O : Board.Marker.X ;

		// First, determine if there is a block that needs to be made.
		// Check the center first, if empty, blocker-wise
		if( boardArray[1][1].equals(Board.Marker.BLANK)) {
			try {
					gameState.getBoard().move(1, 1, playerMarker );
					return;
				}
				catch(Exception e) {
					// Already checked
				}
		}
		// Check if there is a block move in the rows.
		else if (board.canBlockWinHorizontally(gameState)) {
         	return;
		}
		// Check if there is a block move in the columns.
        else if (board.canBlockWinVertically(gameState)) {
			return;
		}
        // Check if there is a block move in the diagonals.
        else if (board.canBlockWinDiagonally(gameState)) {
			return;
		}
        // Else keep generating random positions until a blank spot is found
        else {
			 boolean found = false;
			 Random random = new Random();
			 while(!found) {
				 int row = random.nextInt(BOARD_SIZE);
				 int col = random.nextInt(BOARD_SIZE);
				 if(boardArray[row][col].equals(Board.Marker.BLANK)) {
					 try {
						 gameState.getBoard().move(row, col, playerMarker );
						 found = true;
					 }
					 catch(Exception e) {
						 log.error("Problem making random move!", e);
					 }
				 }
			 }
		}
	}
	
	/**
	 * Convenience method to retrieve game state from session.
	 * 
	 * @param session
	 * @return Current game state.
	 */
	private GameState getStateFromSession(HttpSession session)
	{
		GameState gameState = (GameState)session.getAttribute(Constants.GAME_STATE);

		if(gameState == null) {
			log.info("New GameState created and put in session");
			gameState = new GameState();
			putStateInSession(session, gameState);
		}

		return gameState;
	}
	
	/**
	 * Convenience method to save game state in session.
	 * 
	 * @param session
	 */
	private void putStateInSession(HttpSession session, GameState gameState) {
		session.setAttribute(Constants.GAME_STATE, gameState);
	}
}
