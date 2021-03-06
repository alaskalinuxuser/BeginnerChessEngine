package com.alaskalinuxuser.beginnerchess;

import android.util.Log;

import java.util.Arrays;

import static com.alaskalinuxuser.beginnerchess.MainActivity.chessBoard;
import static com.alaskalinuxuser.beginnerchess.MainActivity.globalDepth;
import static com.alaskalinuxuser.beginnerchess.MainActivity.kingPositionC;
import static com.alaskalinuxuser.beginnerchess.MainActivity.kingPositionL;

/*  Copyright 2017 by AlaskaLinuxUser (https://thealaskalinuxuser.wordpress.com)
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

public class ThinkTank {

    static boolean wKingMove=false, bKingMove=false, wQRookMove=false, wKRookMove=false,
            bQRookMove=false, bKRookMove=false, isWhite=true;

    public static String alphaBeta(int depth, int beta, int alpha, String move, int player) {
        //return in the form of 1234b##########
        String list=posibleMoves();
        if (depth==0 || list.length()==0) {return move+(Rating.rating(list.length(), depth)*(player*2-1));}
        list=sortMoves(list);
        player=1-player;//either 1 or 0
        for (int i=0;i<list.length();i+=5) {
            // Debugging only // Log.i("WJH", list);
            makeMove(list.substring(i,i+5));
            flipBoard();
            String returnString=alphaBeta(depth-1, beta, alpha, list.substring(i,i+5), player);
            int value=Integer.valueOf(returnString.substring(5));
            flipBoard();
            undoMove(list.substring(i,i+5));
            if (player==0) {
                if (value<=beta) {beta=value; if (depth==globalDepth) {move=returnString.substring(0,5);}}
            } else {
                if (value>alpha) {alpha=value; if (depth==globalDepth) {move=returnString.substring(0,5);}}
            }
            if (alpha>=beta) {
                if (player==0) {return move+beta;} else {return move+alpha;}
            }
        }
        if (player==0) {
            // Debugging only //Log.i("WJH", move+beta);
            return move+beta;
        } else {
            // Debugging only //Log.i("WJH", move+alpha);
            return move+alpha;
        }
    } // End alphabeta algorithm.

    public static void flipBoard() {
        String temp;
        for (int i=0;i<32;i++) {
            int r=i/8, c=i%8;
            if (Character.isUpperCase(chessBoard[r][c].charAt(0))) {
                temp=chessBoard[r][c].toLowerCase();
            } else {
                temp=chessBoard[r][c].toUpperCase();
            }
            if (Character.isUpperCase(chessBoard[7-r][7-c].charAt(0))) {
                chessBoard[r][c]=chessBoard[7-r][7-c].toLowerCase();
            } else {
                chessBoard[r][c]=chessBoard[7-r][7-c].toUpperCase();
            }
            chessBoard[7-r][7-c]=temp;
        }
        int kingTemp=kingPositionC;
        kingPositionC=63-kingPositionL;
        kingPositionL=63-kingTemp;

        //Set our boolean for white's turn.
        if (isWhite) {
            isWhite=false;
        } else {
            isWhite=true;
        }

    } // End flip board.

    public static void makeMove(String move) {
        /*
         * In theory, if there are no moves, then you are in checkmate or stalemate....
         */
        //Log.i("WJH", move);
        boolean checkStaleMate = false;
        if (move.length() < 4 || move.charAt(0)=='-') {
            Log.i("WJH", "Checkmate or stalemate.");
            checkStaleMate = true;
        }

        if (!checkStaleMate) {

            move=move.substring(0,5);
            if(!wKingMove && isWhite) {
                if (move.startsWith("74")) {
                     wKingMove = true;
                    }
                if (move.startsWith("70")) {
                        wQRookMove = true;
                    }
                if (move.startsWith("77")) {
                        wKRookMove = true;
                    }
            } else if(!bKingMove && !isWhite) {
                if (move.startsWith("74")) {
                        bKingMove = true;
                    }
                if (move.startsWith("70")) {
                        bQRookMove = true;
                    }
                if (move.startsWith("77")) {
                        bKRookMove = true;
                    }
            }

            if ("0-0kr".equals(move.substring(0,5))) {
                //Log.i("WJH", "make castle king side");
                if(isWhite) {
                    chessBoard[7][4] = " ";
                    chessBoard[7][7] = " ";
                    chessBoard[7][6] = "K";
                    chessBoard[7][5] = "R";
                    wKingMove=true;wKRookMove=true;wQRookMove=true;
                } else {
                    chessBoard[7][3] = " ";
                    chessBoard[7][0] = " ";
                    chessBoard[7][1] = "K";
                    chessBoard[7][2] = "R";
                    bKingMove=true;bKRookMove=true;bQRookMove=true;
                }
            } else if ("0-0-0".equals(move.substring(0,5))) {
                //Log.i("WJH", "make castle queen side");
                if(isWhite) {
                    chessBoard[7][4] = " ";
                    chessBoard[7][0] = " ";
                    chessBoard[7][1] = " ";
                    chessBoard[7][2] = "K";
                    chessBoard[7][3] = "R";
                    wKingMove=true;wQRookMove=true;wKRookMove=true;
                }
                else {
                    chessBoard[7][3] = " ";
                    chessBoard[7][7] = " ";
                    chessBoard[7][6] = " ";
                    chessBoard[7][5] = "K";
                    chessBoard[7][4] = "R";
                    bKingMove=true;bQRookMove=true;bKRookMove=true;
                }
            } else {
                // Debuging only //
                //for (int i=0;i<8;i++) {
                    //Log.i ("WJH", Arrays.toString(chessBoard[i]));
                //}
                //Log.i("WJH", move + " is move.");
                if (move.charAt(4) != 'P') {
                    chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))] = chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))];
                    chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))] = " ";
                    if ("K".equals(chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))])) {
                        kingPositionC = 8 * Character.getNumericValue(move.charAt(2)) + Character.getNumericValue(move.charAt(3));
                    }
                } else {
                    //if pawn promotion
                    chessBoard[1][Character.getNumericValue(move.charAt(0))] = " ";
                    chessBoard[0][Character.getNumericValue(move.charAt(1))] = String.valueOf(move.charAt(3));
                }
            }
        }
    } // End makeMove

    public static void undoMove(String move) {
        if ("0-0kr".equals(move)){
            //Log.i("WJH", "undo castle king side");
            if(isWhite) {
                chessBoard[7][4] = "K";
                chessBoard[7][7] = "R";
                chessBoard[7][6] = " ";
                chessBoard[7][5] = " ";
            } else {
                chessBoard[7][3] = "K";
                chessBoard[7][0] = "R";
                chessBoard[7][1] = " ";
                chessBoard[7][2] = " ";
            }
        } else if ("0-0-0".equals(move)){
            //Log.i("WJH", "undo castle queen side");
            if(isWhite) {
                chessBoard[7][4] = "K";
                chessBoard[7][0] = "R";
                chessBoard[7][1] = " ";
                chessBoard[7][2] = " ";
                chessBoard[7][3] = " ";
            } else {
                chessBoard[7][3] = "K";
                chessBoard[7][7] = "R";
                chessBoard[7][6] = " ";
                chessBoard[7][5] = " ";
                chessBoard[7][4] = " ";
            }
        } else if (move.charAt(4)!='P') {
            // Debuging only //
            //for (int i=0;i<8;i++) {
                //Log.i ("WJH", Arrays.toString(chessBoard[i]));
            //}
            chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))]=
                    chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))];
            chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))]=String.valueOf(move.charAt(4));
            if ("K".equals(chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))])) {
                kingPositionC=8*Character.getNumericValue(move.charAt(0))+Character.getNumericValue(move.charAt(1));
            }
        } else {
            //if pawn promotion
            chessBoard[1][Character.getNumericValue(move.charAt(0))]="P";
            chessBoard[0][Character.getNumericValue(move.charAt(1))]=String.valueOf(move.charAt(2));
        }
    } // End undo move.

    public static StringBuffer posibleMoves() {
        StringBuffer list=new StringBuffer();
        for (int i=0; i<64; i++) {
            switch (chessBoard[i/8][i%8]) {
                case "N": list.append(posibleN(i));
                    break;
                case "R": list.append(posibleR(i));
                    break;
                case "P": list.append(posibleP(i));
                    break;
                case "B": list.append(posibleB(i));
                    break;
                case "Q": list.append(posibleQ(i));
                    break;
                case "K": list.append(posibleK(i));
                    break;
            }
        }
        return list;//x1,y1,x2,y2,captured piece
    } // End possible moves.

    public static StringBuffer posibleP(int i) {
        StringBuffer list=new StringBuffer();
        String oldPiece;
        int r=i/8, c=i%8;
        for (int j=-1; j<=1; j+=2) {
            try {//capture
                if (Character.isLowerCase(chessBoard[r-1][c+j].charAt(0)) ) {//&& i>=16) {
                    oldPiece=chessBoard[r-1][c+j];
                    chessBoard[r][c]=" ";
                    chessBoard[r-1][c+j]="P";
                    if (kingSafe()) {
                    	list.append(r)
                            .append(c)
                            .append(r-1)
                            .append(c+j)
                            .append(oldPiece);
                    }
                    chessBoard[r][c]="P";
                    chessBoard[r-1][c+j]=oldPiece;
                }
            } catch (Exception e) {}
            /*try {//promotion && capture
                if (Character.isLowerCase(chessBoard[r-1][c+j].charAt(0)) && i<16) {
                    String[] temp={"Q","R","B","N"};
                    //for (int k=0; k<temp.length; k++) {
                        oldPiece=chessBoard[r-1][c+j];
                        chessBoard[r][c]=" ";
                        chessBoard[r-1][c+j]="Q";
                        if (kingSafe()) {
                            //column1,column2,captured-piece,new-piece,P
                            list=list+c+(c+j)+oldPiece+"Q"+"P";
                        }
                        chessBoard[r][c]="P";
                        chessBoard[r-1][c+j]=oldPiece;
                    }
                //}
            } catch (Exception e) {} */
        }
        try {//move one up
            if (" ".equals(chessBoard[r-1][c]) && i>=16) {
                oldPiece=chessBoard[r-1][c];
                chessBoard[r][c]=" ";
                chessBoard[r-1][c]="P";
                if (kingSafe()) {
                	list.append(r)
                         .append(c)
                         .append(r-1)
                         .append(c)
                         .append(oldPiece);
                }
                chessBoard[r][c]="P";
                chessBoard[r-1][c]=oldPiece;
            }
        } catch (Exception e) {}
        try {//promotion && no capture
            if (" ".equals(chessBoard[r-1][c]) && i<16) {
                String[] temp={"Q","R","B","N"};
                for (int k=0; k<4; k++) {
                    oldPiece=chessBoard[r-1][c];
                    chessBoard[r][c]=" ";
                    chessBoard[r-1][c]=temp[k];
                    if (kingSafe()) {
                        //column1,column2,captured-piece,new-piece,P
                        list.append(c)
                            .append(c)
                            .append(oldPiece)
                            .append(temp[k])
                            .append("P");
                    }
                    chessBoard[r][c]="P";
                    chessBoard[r-1][c]=oldPiece;
                }
            }
        } catch (Exception e) {}
        try {//move two up
            if (" ".equals(chessBoard[r-1][c]) && " ".equals(chessBoard[r-2][c]) && i>=48) {
                oldPiece=chessBoard[r-2][c];
                chessBoard[r][c]=" ";
                chessBoard[r-2][c]="P";
                if (kingSafe()) {
                	list.append(r)
                        .append(c)
                        .append(r-2)
                        .append(c)
                        .append(oldPiece);
                    list=list+r+c+(r-2)+c+oldPiece;
                }
                chessBoard[r][c]="P";
                chessBoard[r-2][c]=oldPiece;
            }
        } catch (Exception e) {}
        return list;
    } // End Possible pawn moves.

    public static StringBuffer posibleR(int i) {
        StringBuffer list=new StringBuffer();
        String oldPiece;
        int r=i/8, c=i%8;
        int temp=1;
        for (int j=-1; j<=1; j+=2) {
            try {
                while(" ".equals(chessBoard[r][c+temp*j]))
                {
                    oldPiece=chessBoard[r][c+temp*j];
                    chessBoard[r][c]=" ";
                    chessBoard[r][c+temp*j]="R";
                    if (kingSafe()) {
                    	list.append(r)
                            .append(c)
                            .append(r)
                            .append(c+temp*j)
                            .append(oldPiece);
                    }
                    chessBoard[r][c]="R";
                    chessBoard[r][c+temp*j]=oldPiece;
                    temp++;
                }
                if (Character.isLowerCase(chessBoard[r][c+temp*j].charAt(0))) {
                    oldPiece=chessBoard[r][c+temp*j];
                    chessBoard[r][c]=" ";
                    chessBoard[r][c+temp*j]="R";
                    if (kingSafe()) {
                    	list.append(r)
                            .append(c)
                            .append(r)
                            .append(c+temp*j)
                            .append(oldPiece);
                        list=list+r+c+r+(c+temp*j)+oldPiece;
                    }
                    chessBoard[r][c]="R";
                    chessBoard[r][c+temp*j]=oldPiece;
                }
            } catch (Exception e) {}
            temp=1;
            try {
                while(" ".equals(chessBoard[r+temp*j][c]))
                {
                    oldPiece=chessBoard[r+temp*j][c];
                    chessBoard[r][c]=" ";
                    chessBoard[r+temp*j][c]="R";
                    if (kingSafe()) {
                    	list.append(r)
                            .append(c)
                            .append(r+temp*j)
                            .append(c)
                            .append(oldPiece);
                        list=list+r+c+(r+temp*j)+c+oldPiece;
                    }
                    chessBoard[r][c]="R";
                    chessBoard[r+temp*j][c]=oldPiece;
                    temp++;
                }
                if (Character.isLowerCase(chessBoard[r+temp*j][c].charAt(0))) {
                    oldPiece=chessBoard[r+temp*j][c];
                    chessBoard[r][c]=" ";
                    chessBoard[r+temp*j][c]="R";
                    if (kingSafe()) {
                    	list.append(r)
                            .append(c)
                            .append(r+temp*j)
                            .append(c)
                            .append(oldPiece);
                    }
                    chessBoard[r][c]="R";
                    chessBoard[r+temp*j][c]=oldPiece;
                }
            } catch (Exception e) {}
            temp=1;
        }
        return list;
    } // End possible Rook moves.

    public static StringBuffer posibleN(int i) {
        StringBuffer list=new StringBuffer();
        String oldPiece;
        int r=i/8, c=i%8;
        for (int j=-1; j<=1; j+=2) {
            for (int k=-1; k<=1; k+=2) {
                try {
                    if (Character.isLowerCase(chessBoard[r+j][c+k*2].charAt(0)) || " ".equals(chessBoard[r+j][c+k*2])) {
                        oldPiece=chessBoard[r+j][c+k*2];
                        chessBoard[r][c]=" ";
                        chessBoard[r+j][c+k*2]="N";
                        if (kingSafe()) {
                            list.append(r)
                            	.append(c)
                            	.append(r+j)
                            	.append(c+k*2)
                            	.append(oldPiece);
                        }
                        chessBoard[r][c]="N";
                        chessBoard[r+j][c+k*2]=oldPiece;
                    }
                } catch (Exception e) {}
                try {
                    if (Character.isLowerCase(chessBoard[r+j*2][c+k].charAt(0)) || " ".equals(chessBoard[r+j*2][c+k])) {
                        oldPiece=chessBoard[r+j*2][c+k];
                        chessBoard[r][c]=" ";
                        chessBoard[r+j*2][c+k]="N";
                        if (kingSafe()) {
                        	list.append(r)
                            	.append(c)
                            	.append(r+j*2)
                            	.append(c+k)
                            	.append(oldPiece);
                        }
                        chessBoard[r][c]="N";
                        chessBoard[r+j*2][c+k]=oldPiece;
                    }
                } catch (Exception e) {}
            }
        }
        return list;
    } // End possible knight moves.

    public static StringBuffer posibleB(int i) {
        StringBuffer list=new StringBuffer();
        String oldPiece;
        int r=i/8, c=i%8;
        int temp=1;
        for (int j=-1; j<=1; j+=2) {
            for (int k=-1; k<=1; k+=2) {
                try {
                    while(" ".equals(chessBoard[r+temp*j][c+temp*k]))
                    {
                        oldPiece=chessBoard[r+temp*j][c+temp*k];
                        chessBoard[r][c]=" ";
                        chessBoard[r+temp*j][c+temp*k]="B";
                        if (kingSafe()) {
                        	list.append(r)
                            	.append(c)
                            	.append(r+temp*j)
                            	.append(c+temp*k)
                            	.append(oldPiece);
                        }
                        chessBoard[r][c]="B";
                        chessBoard[r+temp*j][c+temp*k]=oldPiece;
                        temp++;
                    }
                    if (Character.isLowerCase(chessBoard[r+temp*j][c+temp*k].charAt(0))) {
                        oldPiece=chessBoard[r+temp*j][c+temp*k];
                        chessBoard[r][c]=" ";
                        chessBoard[r+temp*j][c+temp*k]="B";
                        if (kingSafe()) {
                        	list.append(r)
                            	.append(c)
                            	.append(r+temp*j)
                            	.append(c+temp*k)
                            	.append(oldPiece);
                        }
                        chessBoard[r][c]="B";
                        chessBoard[r+temp*j][c+temp*k]=oldPiece;
                    }
                } catch (Exception e) {}
                temp=1;
            }
        }
        return list;
    } // End possible Bishop moves.

    public static StringBuffer posibleQ(int i) {
        StringBuffer list=new StringBuffer();
        String oldPiece;
        int r=i/8, c=i%8;
        int temp=1;
        for (int j=-1; j<=1; j++) {
            for (int k=-1; k<=1; k++) {
                if (j!=0 || k!=0) {
                    try {
                        while(" ".equals(chessBoard[r+temp*j][c+temp*k]))
                        {
                            oldPiece=chessBoard[r+temp*j][c+temp*k];
                            chessBoard[r][c]=" ";
                            chessBoard[r+temp*j][c+temp*k]="Q";
                            if (kingSafe()) {
                            	list.append(r)
                            		.append(c)
                            		.append(r+temp*j)
                            		.append(c+temp*k)
                            		.append(oldPiece);
                            }
                            chessBoard[r][c]="Q";
                            chessBoard[r+temp*j][c+temp*k]=oldPiece;
                            temp++;
                        }
                        if (Character.isLowerCase(chessBoard[r+temp*j][c+temp*k].charAt(0))) {
                            oldPiece=chessBoard[r+temp*j][c+temp*k];
                            chessBoard[r][c]=" ";
                            chessBoard[r+temp*j][c+temp*k]="Q";
                            if (kingSafe()) {
                            	list.append(r)
                            		.append(c)
                            		.append(r+temp*j)
                            		.append(c+temp*k)
                            		.append(oldPiece);
                            }
                            chessBoard[r][c]="Q";
                            chessBoard[r+temp*j][c+temp*k]=oldPiece;
                        }
                    } catch (Exception e) {}
                    temp=1;
                }
            }
        }
        return list;
    } // End possible Queen moves.

    public static StringBuffer posibleK(int i) {
        StringBuffer list=new StringBuffer();
        String oldPiece;
        int r=i/8, c=i%8;
        for (int j=0; j<9; j++) {
            if (j!=4) {
                try {
                    if (Character.isLowerCase(chessBoard[r-1+j/3][c-1+j%3].charAt(0)) || " ".equals(chessBoard[r-1+j/3][c-1+j%3])) {
                        oldPiece=chessBoard[r-1+j/3][c-1+j%3];
                        chessBoard[r][c]=" ";
                        chessBoard[r-1+j/3][c-1+j%3]="K";
                        int kingTemp=kingPositionC;
                        kingPositionC=i+(j/3)*8+j%3-9;
                        if (kingSafe()) {
                        	list.append(r)
                            	.append(c)
                            	.append(r-1+j/3)
                            	.append(c-1+j%3)
                            	.append(oldPiece);
                        }
                        chessBoard[r][c]="K";
                        chessBoard[r-1+j/3][c-1+j%3]=oldPiece;
                        kingPositionC=kingTemp;
                    }
                } catch (Exception e) {}
            }
        }
        /* Castle */
        try {
            if (isWhite && !wKingMove) {
                if (!wKRookMove) {
                    if ("K".equals(chessBoard[7][4]) && "R".equals(chessBoard[7][7]) && " ".equals(chessBoard[7][6]) &&
                            " ".equals(chessBoard[7][5])) {
                        if (kingSafe()) {list.append("0-0kr");}
                        // Debugging only //Log.i("WJH", "possible castle kings side");
                    }

                }
                if (!wQRookMove) {
                    if ("K".equals(chessBoard[7][4]) && " ".equals(chessBoard[7][1]) && " ".equals(chessBoard[7][2]) &&
                            " ".equals(chessBoard[7][3]) && "R".equals(chessBoard[7][0])) {
                        if (kingSafe()) {list.append("0-0-0");}
                        // Debugging only //Log.i("WJH", "possible castle queens side");
                    }
                }

            } else if (!isWhite && !bKingMove) {
                if (!bKRookMove) {
                    if ("K".equals(chessBoard[7][3]) && "R".equals(chessBoard[7][0]) && " ".equals(chessBoard[7][1]) &&
                            " ".equals(chessBoard[7][2])) {
                        if (kingSafe()) {list.append("0-0kr");}
                        // Debugging only //Log.i("WJH", "possible castle kings side");
                    }

                }
                if (!bQRookMove) {
                    if ("K".equals(chessBoard[7][3]) && " ".equals(chessBoard[7][4]) && " ".equals(chessBoard[7][5]) &&
                            " ".equals(chessBoard[7][6]) && "R".equals(chessBoard[7][7])) {
                        if (kingSafe()) {list.append("0-0-0");}
                        // Debugging only //Log.i("WJH", "possible castle queens side");
                    }
                }

            }
        } catch (Exception e) {}

        return list;
    } // End possible king moves.

    public static StringBuffer sortMoves(StringBuffer list) {
        int[] score=new int [list.length()/5];
        for (int i=0;i<list.length();i+=5) {
            makeMove(list.substring(i, i+5));
            score[i/5]=-Rating.rating(-1, 0);
            undoMove(list.substring(i, i+5));
        }
        StringBuffer newListA=new StringBuffer();
        StringBuffer newListB=list;
        for (int i=0;i<Math.min(6, list.length()/5);i++) {//first few moves only
            int max=-1000000, maxLocation=0;
            for (int j=0;j<list.length()/5;j++) {
                if (score[j]>max) {max=score[j]; maxLocation=j;}
            }
            score[maxLocation]=-1000000;
            newListA.append(list.substring(maxLocation*5,maxLocation*5+5));
            newListB=newListB.replace(maxLocation*5, maxLocation*5+5, "");
        }
        return newListA.append(newListB);
    } // End sort moves.

    public static boolean kingSafe() {
        //bishop/queen
        int temp=1;
        for (int i=-1; i<=1; i+=2) {
            for (int j=-1; j<=1; j+=2) {
                try {
                    while(" ".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8+temp*j])) {temp++;}
                    if ("b".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8+temp*j]) ||
                            "q".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8+temp*j])) {
                        return false;
                    }
                } catch (Exception e) {}
                temp=1;
            }
        }
        //rook/queen
        for (int i=-1; i<=1; i+=2) {
            try {
                while(" ".equals(chessBoard[kingPositionC/8][kingPositionC%8+temp*i])) {temp++;}
                if ("r".equals(chessBoard[kingPositionC/8][kingPositionC%8+temp*i]) ||
                        "q".equals(chessBoard[kingPositionC/8][kingPositionC%8+temp*i])) {
                    return false;
                }
            } catch (Exception e) {}
            temp=1;
            try {
                while(" ".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8])) {temp++;}
                if ("r".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8]) ||
                        "q".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8])) {
                    return false;
                }
            } catch (Exception e) {}
            temp=1;
        }
        //knight
        for (int i=-1; i<=1; i+=2) {
            for (int j=-1; j<=1; j+=2) {
                try {
                    if ("n".equals(chessBoard[kingPositionC/8+i][kingPositionC%8+j*2])) {
                        return false;
                    }
                } catch (Exception e) {}
                try {
                    if ("n".equals(chessBoard[kingPositionC/8+i*2][kingPositionC%8+j])) {
                        return false;
                    }
                } catch (Exception e) {}
            }
        }
        //pawn
                    try {
                        if ("p".equals(chessBoard[kingPositionC/8+(-1)][(kingPositionC%8)+1])) {
                            return false;
                        }
                        if ("p".equals(chessBoard[kingPositionC/8+(-1)][kingPositionC%8+(-1)])) {
                            return false;
                        }
                    } catch (Exception e) {}
        //king
        for (int i=-1; i<=1; i++) {
                for (int j=-1; j<=1; j++) {
                    if (i!=0 || j!=0) {
                        try {
                            if ("k".equals(chessBoard[kingPositionC/8+i][kingPositionC%8+j])) {
                                return false;
                            }
                        } catch (Exception e) {}
                    }
                }
        }
        return true;
    } // End king is safe check.




}
