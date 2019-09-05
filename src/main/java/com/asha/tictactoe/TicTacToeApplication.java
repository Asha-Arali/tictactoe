package com.asha.tictactoe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TicTacToeApplication {
	private static final Logger log = LoggerFactory.getLogger(TicTacToeApplication.class);
    	
 	public static void main(String[] args) {
    	SpringApplication.run(TicTacToeApplication.class);
    }    
}
