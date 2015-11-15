package com.example.liusu.travelapp.functiontwo;

public class EditDistance {
    public static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }

    public static String getResult(CharSequence input, CharSequence[][] database){
        int currentEditDistance = input.toString().length();
        int currentRow = 0;
        for(int i = 0 ; i < database.length ; i++){
            for(int j = 0 ; j < database[i].length ; j++){
                if(computeLevenshteinDistance(input,database[i][j]) < currentEditDistance){
                    currentEditDistance = computeLevenshteinDistance(input,database[i][j]);
                    currentRow = i;
                }
            }
        }
        return database[currentRow][database[currentRow].length-1].toString();
    }
}
