package com.example.liusu.travelapp.functionone;

import java.util.ArrayList;
import java.util.Arrays;

public class Permutation {
    public static ArrayList<ArrayList<Integer>> getPermutations(ArrayList<Integer> arraylist, int start_index, int end_index) {
        ArrayList<ArrayList<Integer>> permutations = new ArrayList<>();
        Permutation.permutate(arraylist, end_index, start_index, end_index, permutations);
        return permutations;
    }

    public static ArrayList<ArrayList<Integer>> getPermutations(ArrayList<Integer> arraylist) {
        ArrayList<ArrayList<Integer>> permutations = new ArrayList<>();
        Permutation.permutate(arraylist, arraylist.size(), permutations);
        return permutations;
    }

    public static void permutate(ArrayList<Integer> array, int index, ArrayList<ArrayList<Integer>> permutations) {
        if (index <= 0)
            permutations.add(new ArrayList<Integer>(array));
        else {
            permutate(array, index - 1, permutations);
            
            int current_pointer = array.size() - index;
            for (int i = current_pointer + 1; i != array.size(); ++i) {
                Permutation.swap(array, current_pointer, i);
                permutate(array, index - 1, permutations);
                Permutation.swap(array, current_pointer, i);
            }
        }
    }

    public static void permutate(ArrayList<Integer> array, int index, int start, int end, ArrayList<ArrayList<Integer>> permutations) {
        if (index <= start)
            permutations.add(new ArrayList<Integer>(array));
        else {
            permutate(array, index - 1, start, end, permutations);
            
            int current_pointer = end - index + start;
            for (int i = current_pointer + 1; i <= end; ++i) {
                Permutation.swap(array, current_pointer, i);
                permutate(array, index - 1, start, end, permutations);
                Permutation.swap(array, current_pointer, i);
            }
        }
    }

    public static void swap(ArrayList<Integer> array, int pos1, int pos2) {
        Integer temp = array.get(pos1);
        array.set(pos1, array.get(pos2));
        array.set(pos2, temp);
    }
}
