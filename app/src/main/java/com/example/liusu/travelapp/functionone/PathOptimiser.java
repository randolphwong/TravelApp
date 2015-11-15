package com.example.liusu.travelapp.functionone;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PathOptimiser {
    
    private PathOptimiser() {}

    public static IncidentArray[] getBestPath(String hotel, double budget, AttractionDatabase attraction_database) {
        int home = attraction_database.indexOf(hotel);
        // get greedy path
        LinkedList<Integer> greedy_path = PathOptimiser.getGreedyPath(home, home, attraction_database, new ArrayList<Integer>(), attraction_database.size() + 1);

        // swap till optimised
        LinkedList<Integer> optimised_path = PathOptimiser.optimisePathBySwap(greedy_path, attraction_database);

        // setup for branch and bound
        IncidentArray[] expected_best_path = PathOptimiser.createIncidentArrayFromNodeList(optimised_path, TransportMode.TAXI);
        IncidentArray[] best_path = PathOptimiser.createIncidentArrayFromNodeList(optimised_path, TransportMode.FOOT);

        // branch and bound search for optimal transport mode for given path
        IncidentArray[] new_best_path = PathOptimiser.insertEdgeToPath(optimised_path, 0, 0, budget, attraction_database, expected_best_path, best_path);

        //IncidentArray[] new_best_path = PathOptimiser.exhaustiveSearch(home, budget, attraction_database);
        return new_best_path;
    }

    public static IncidentArray[] createIncidentArrayFromNodeList(ArrayList<Integer> path, TransportMode transport_mode) {
        IncidentArray[] incident_path = new IncidentArray[path.size() - 1];
        for (int i = 0; i != incident_path.length; ++i) {
            incident_path[path.get(i)] = new IncidentArray(path.get(i + 1), transport_mode);
        }
        return incident_path;
    }

    public static IncidentArray[] createIncidentArrayFromNodeList(LinkedList<Integer> path, TransportMode transport_mode) {
        IncidentArray[] incident_path = new IncidentArray[path.size() - 1];
        for (int i = 0; i != incident_path.length; ++i) {
            incident_path[path.get(i)] = new IncidentArray(path.get(i + 1), transport_mode);
        }
        return incident_path;
    }

    public static void reverseInternalOfList(List<Integer> list, int start, int end) {
        Integer temp;
        while (end > start) {
            temp = list.get(start);
            list.set(start++, list.get(end));
            list.set(end--, temp);
        }
    }

    public static IncidentArray[] deepCopyIncidentArray(IncidentArray[] path) {
        IncidentArray[] new_path = new IncidentArray[path.length];
        for (int i = 0; i != path.length; ++i) {
            new_path[i] = new IncidentArray(path[i].getDestinationNode(), path[i].getTransportMode());
        }
        return new_path;
    }

    public static LinkedList<Integer> getGreedyPath(int source, int home, AttractionDatabase attraction_database, ArrayList<Integer> visited, int destination_count) {
        if (destination_count == 1) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            l.add(home);
            return l;
        }

        visited.add(source);
        
        int minimum_travel_time = 0;
        int closest_destination = 0;
        
        for (int destination : attraction_database.allDestinationOf(source)) {
            if (visited.contains(destination))
                continue;

            int time_to_destination = attraction_database.timeBetween(source, destination);
            if (time_to_destination < minimum_travel_time || minimum_travel_time == 0) {
                minimum_travel_time = time_to_destination;
                closest_destination = destination;
            }
        }

        LinkedList<Integer> greedy_path = getGreedyPath(closest_destination, home, attraction_database, visited, destination_count - 1);
        greedy_path.addFirst(source);

        return greedy_path;
    }

    public static LinkedList<Integer> optimisePathBySwap(LinkedList<Integer> path, AttractionDatabase attraction_database) {
        if (path.size() < 4)
            return path; // cannot perform swap on path with 3 nodes or less

        LinkedList<Integer> best_path = new LinkedList<Integer>(path);
        LinkedList<Integer> new_path;

        boolean found_better_path;
        int minimum_distance = attraction_database.travelTimeOf(path);
        int new_distance;

        while (true) {
            found_better_path = false;
            
            for (int i = 1; i != path.size() - 3; ++i) {
                for (int j = i + 2; j != path.size() - 1; ++j) {
                    new_path = new LinkedList<Integer>(best_path);
                    PathOptimiser.reverseInternalOfList(new_path, i, j);
                    new_distance = attraction_database.travelTimeOf(new_path);
                    if (new_distance < minimum_distance) {
                        minimum_distance = new_distance;
                        best_path = new_path;
                        found_better_path = true;
                        break;
                    }
                }
                if (found_better_path)
                    break;
            }
            
            if (!found_better_path) {
                for (int i = 1; i != path.size() - 3; ++i) {
                    for (int j = i + 2; j != path.size() - 1; ++j) {
                        new_path = new LinkedList<Integer>(best_path);
                        PathOptimiser.reverseInternalOfList(new_path, 0, new_path.size() - 1);
                        PathOptimiser.reverseInternalOfList(new_path, i, j);
                        new_distance = attraction_database.travelTimeOf(new_path);
                        if (new_distance < minimum_distance) {
                            minimum_distance = new_distance;
                            best_path = new_path;
                            found_better_path = true;
                            break;
                        }
                    }
                    if (found_better_path)
                        break;
                }
            }

            if (!found_better_path)
                break;
        }

        return best_path;
    }

    public static IncidentArray[] insertEdgeToPath(LinkedList<Integer> path, int path_index, double accumulated_cost, double budget, AttractionDatabase attraction_database, IncidentArray[] expected_best_path, IncidentArray[] best_path) {
        if (path_index == path.size() - 1) {
            return PathOptimiser.deepCopyIncidentArray(expected_best_path);
        }

        boolean branched = false;
        for (TransportMode transport_mode : TransportMode.values()) {
            double new_accumulated_cost = accumulated_cost + attraction_database.costBetween(path.get(path_index), path.get(path_index + 1), transport_mode);
            boolean within_budget = (new_accumulated_cost <= budget) || budget < 0;
            

            IncidentArray[] new_expected_best_path = PathOptimiser.deepCopyIncidentArray(expected_best_path);
            new_expected_best_path[path.get(path_index)].setTransportMode(transport_mode);
            boolean expected_better_than_best = attraction_database.travelTimeOf(new_expected_best_path) < attraction_database.travelTimeOf(best_path);

            if (within_budget && expected_better_than_best) {
                branched = true;
                IncidentArray[] better_path = insertEdgeToPath(path, path_index + 1, new_accumulated_cost, budget, attraction_database, new_expected_best_path, best_path);
                if (better_path != null)
                    best_path = PathOptimiser.deepCopyIncidentArray(better_path);
            }
        }
        if (branched)
            return PathOptimiser.deepCopyIncidentArray(best_path);
        else
            return null;
    }

    public static IncidentArray[] exhaustiveSearch(int home, double budget, AttractionDatabase attraction_database) {
        // create a starting path to permutate
        ArrayList<Integer> path = new ArrayList<>(attraction_database.size() + 1);
        path.add(home);
        for (int i = 0, j = 0; j != attraction_database.size(); ++j) {
            if (j != home) {
                path.add(j);
                ++i;
            }
        }
        path.add(home);

        // get all permutation of path
        ArrayList<ArrayList<Integer>> path_permutations = Permutation.getPermutations(path, 1, path.size() - 2);

        // for each path, find the best transport mode and keep the best from all path
        int minimum_travel_time = -1;
        IncidentArray[] best_route = null;

        for (int i = 0; i != path_permutations.size(); ++i) {
            IncidentArray[] expected_best_path = PathOptimiser.createIncidentArrayFromNodeList(path_permutations.get(i), TransportMode.TAXI);
            IncidentArray[] best_path = PathOptimiser.createIncidentArrayFromNodeList(path_permutations.get(i), TransportMode.FOOT);
            IncidentArray[] local_best_route = getBestTransportModeForPath(path_permutations.get(i), 0, 0, budget, attraction_database, expected_best_path, best_path);
            //IncidentArray[] local_best_route = null;
            int local_best_time = attraction_database.travelTimeOf(local_best_route);
            if (local_best_time < minimum_travel_time || minimum_travel_time < 0) {
                minimum_travel_time = local_best_time;
                best_route = local_best_route;
            }
        }
        return best_route;
    }

    public static IncidentArray[] getBestTransportModeForPath(ArrayList<Integer> path, int path_index, double accumulated_cost, double budget, AttractionDatabase attraction_data, IncidentArray[] expected_best_path, IncidentArray[] best_path) {
        if (path_index == path.size() - 1) {
            if (accumulated_cost <= budget) {
                if (attraction_data.travelTimeOf(expected_best_path) < attraction_data.travelTimeOf(best_path))
                    return PathOptimiser.deepCopyIncidentArray(expected_best_path);
            }
            return null;
        }

        for (TransportMode transport_mode : TransportMode.values()) {
            double new_accumulated_cost = accumulated_cost + attraction_data.costBetween(path.get(path_index), path.get(path_index + 1), transport_mode);
            IncidentArray[] new_expected_best_path = PathOptimiser.deepCopyIncidentArray(expected_best_path);
            new_expected_best_path[path.get(path_index)].setTransportMode(transport_mode);

            IncidentArray[] better_path = getBestTransportModeForPath(path, path_index + 1, new_accumulated_cost, budget, attraction_data, new_expected_best_path, best_path);
            if (better_path != null)
                best_path = PathOptimiser.deepCopyIncidentArray(better_path);
        }
        return PathOptimiser.deepCopyIncidentArray(best_path);
    }
}
