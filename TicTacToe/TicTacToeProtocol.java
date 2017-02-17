/**
 * Network Assignment 2
 * @author Derek S. Prijatelj
 * Modified Knock Knock code by Oracle for TicTacToe
 */

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.net.*;
import java.io.*;

public class TicTacToeProtocol {
    private static final int WAITING = 0;
    private static final int TURN = 1;
    private static final int GAMEOVER = 2;
    private static final int ANOTHER = 3;
    private int state = WAITING;
    
    private static TicTacToeGame game = new TicTacToeGame();

    public String processInput(String theInput, int pID) {
        String theOutput = null;

        if (state == WAITING) {
            System.out.println("WAITING");
            theOutput = game.print();
            state = TURN;
        }
        else if (state == TURN){
            System.out.println("TURN");

            if (theInput.length() == 1 && '1' <= theInput.charAt(0)
                &&  theInput.charAt(0) <= '9'){
                try{
                    if (game.turnHandler(pID, theInput.charAt(0))){
                        //theOutput = game.print();
                        theOutput = "Game Over\n" + game.print();
                        if (game.result == '0') {
                            theOutput += "Tie Game.\n";
                        } else if (game.result == '#'){
                           System.out.println("big ol' ERROR. . . !\n"); 
                        } else{
                            theOutput += "Player " + game.result + " wins!\n";
                        }
                        //theOutput += "Want to play again? (y/n)\n";
                        //state = ANOTHER;
                        theOutput += "Bye.";
                        state = GAMEOVER;
                    } else {
                        theOutput = game.print();
                        //state = WAITING;
                    }
                
                } catch (CoordinatesDNE e){
                    theOutput = "Error: CoordinatesDNE: Enter the digits 1-9 to"
                        + " place your token in an unoccupied space. Try"
                        + " again.\n";
                } catch  (LocationTaken e) {
                    theOutput = "Error: Location Taken: try another spot marked"
                        + " with a number 1-9.\n";
                } catch (PlayerDNE e){
                    e.printStackTrace();
                }
            } else {
                theOutput = "Error: CoordinatesDNE: Enter the digits 1-9 to"
                    + " place your token in an unoccupied space. Try again.\n";
            }
        } else if (state == GAMEOVER){
            theOutput = "Bye.";
            state = GAMEOVER;
        }
        else if (state == ANOTHER) {
            System.out.println("ANOTHER");
            
            if (theInput.equalsIgnoreCase("y")) {
                game.init();
                theOutput = game.print();
                state = TURN;
            } else {
                theOutput = "Bye.";
                state = WAITING;
            }
        }
        return theOutput;
    }
}
