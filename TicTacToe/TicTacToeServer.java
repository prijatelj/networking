/**
 * Network Assignment 2
 * Modified Knock Knock code from Oracle to play TicTacToe
 * @author Derek S. Prijatelj
 */

/*
 * Copyright (c) 1995, 2014, Oracle and/or its affiliates. All rights reserved.
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

public class TicTacToeServer {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java TicTacToeServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            Socket clientSocket2 = serverSocket.accept();
            
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter out2 =
                new PrintWriter(clientSocket2.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(
                new InputStreamReader(clientSocket2.getInputStream()));
        ) {
        
            String inputLine, outputLine;
            boolean initialPlay = true, p2InputError;
            int count = 0;
            
            // Initiate conversation with client
            TicTacToeProtocol kkp = new TicTacToeProtocol();
            outputLine = kkp.processInput(null, 0); // initial display to X
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null){ // Player 1
                outputLine = kkp.processInput(inputLine, 0); // process p1 input
                
                if (outputLine.length() > 6
                        && outputLine.startsWith("Error:")){
                    out.println(outputLine);
                    continue;
                }
                out2.println(outputLine); // display updated table to p2
                if (outputLine.startsWith("Game Over")){
                    out.println(outputLine);
                    //break;
                }
                if (outputLine.endsWith("Bye.")){
                    break;
                }

                // Player 2
                p2InputError = true; // 2nd players turn
                while (p2InputError && ((inputLine = in2.readLine()) != null)){
                    outputLine = kkp.processInput(inputLine, 1);
                    if (outputLine.length() > 6
                            && outputLine.startsWith("Error:")){
                        out2.println(outputLine);
                        continue;
                    }
                    p2InputError = false;
                    out.println(outputLine); // display updated table to p1

                    System.out.println("End of inner while loop for p2");
                }
                if (outputLine.startsWith("Game Over")){
                    out2.println(outputLine);
                    //break;
                }
                if (outputLine.endsWith("Bye.")){
                    break;
                }

                count++;
                System.out.println("Made it to the end! " + count);
            }
        } catch (IOException e) {
            System.out.println(
                "Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
