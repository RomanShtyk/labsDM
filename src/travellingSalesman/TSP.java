package travellingSalesman;

import java.util.ArrayList;
import java.util.List;

public class TSP {
    private static final double[][] distances = {{0, 129, 119, 43.6, 98.6, 98.6, 86.3, 52.2, 85.3, 44.5},
            {129, 0, 88.3, 149, 152, 57.4, 55.4, 141, 93.3, 86.3},
            {119, 88.3, 0, 97.4, 71.6, 72.6, 42.5, 71.6, 35.5, 92.1},
            {43.6, 149, 97.4, 0, 54, 119, 107, 28, 64.2, 60.7},
            {98.6, 152, 71.6, 54, 0, 138, 85.2, 39.9, 48.6, 90.7},
            {98.6, 57.4, 72.6, 119, 138, 0, 34.9, 111, 77.1, 56.3},
            {86.3, 55.4, 42.5, 107, 85.2, 34.9, 0, 80.9, 37.9, 44.7},
            {52.2, 141, 71.6, 28, 39.9, 111, 80.9, 0, 38.8, 52.4},
            {85.3, 93.3, 35.5, 64.2, 48.6, 77.1, 37.9, 38.8, 0, 47.4},
            {44.5, 86.3, 92.1, 60.7, 90.7, 56.3, 44.7, 52.4, 47.4, 0},};

    private static List<City> cities;

    private static List<Route> BFRoutePerms = new ArrayList<Route>();
    private static double BFcheapestCost = Double.MAX_VALUE;
    private static Route BFcheapestRoute;

    private static List<Route> BaBRoutePerms = new ArrayList<Route>();
    private static double BaBcheapestCost = Double.MAX_VALUE;
    private static Route BaBcheapestRoute;

    public static void main(String[] args) {
        long time1 = 0;
        long time2 = 0;
        long time3 = 0;
        int numIterations = 1;

        for (int i = 0; i < numIterations; i++) {
            long time = System.currentTimeMillis();
            bruteForce();
            System.out.println("\tTime:" + (System.currentTimeMillis() - time) + "ms");
            time1 += System.currentTimeMillis() - time;

            time = System.currentTimeMillis();
            nearestNeighbour();
            System.out.println("\tTime:" + (System.currentTimeMillis() - time) + "ms");
            time2 += System.currentTimeMillis() - time;

            time = System.currentTimeMillis();
            branchAndBound();
            System.out.println("\tTime:" + (System.currentTimeMillis() - time) + "ms");
            time3 += System.currentTimeMillis() - time;
        }

        System.out.println("\n\tBF:" + time1 / numIterations + "ms");
        System.out.println("\tNN:" + time2 / numIterations + "ms");
        System.out.println("\tBB:" + time3 / numIterations + "ms");
        System.out.println(
                "KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
    }


    public static void bruteForce() {
        System.out.println("bruteForce:");
        resetLists();

        List<Integer> cityNums = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            cityNums.add(i);
        }

        permute(new Route(), cityNums, true);
        System.out.println("\tComplete Permutations: " + BFRoutePerms.size());
        findShortestPermutation(BFRoutePerms);
    }

    private static void nearestNeighbour() {
        System.out.println("nearestNeighbour:");
        resetLists();

        double routeCost = 0;

        Route nearestRoute = new Route(cities.get(9));

        while (nearestRoute.getRoute().size() != cities.size()) {

            City neighbourCity = null;
            double neighbourDistance = Double.MAX_VALUE;

            for (int i = 0; i < 9; i++) {
                if (distances[nearestRoute.getCurrentCity().getID()][i] < neighbourDistance
                        && distances[nearestRoute.getCurrentCity().getID()][i] != 0
                        && cities.get(i).isVisited() == false) {

                    neighbourCity = cities.get(i);
                    neighbourDistance = distances[nearestRoute.getCurrentCity().getID()][i];
                }
            }

            if (neighbourCity != null) {
                nearestRoute.getRoute().add(neighbourCity);
                nearestRoute.setCurrentCity(neighbourCity);
                neighbourCity.setVisited(true);

                routeCost += neighbourDistance;
            }
        }

        routeCost += distances[nearestRoute.getStartCity().getID()][nearestRoute.getCurrentCity().getID()];

        nearestRoute.getRoute().add(cities.get(9));

        System.out.println("\t" + nearestRoute.toString() + "\n\tCost: " + routeCost);
    }


    private static void branchAndBound() {
        System.out.println("branchAndBound:");
        resetLists();

        List<Integer> cityNums = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            cityNums.add(i);
        }

        permute(new Route(), cityNums, false);
        System.out.println("\tComplete Permutations: " + BaBRoutePerms.size());
        System.out.println("\t" + BaBcheapestRoute.toString() + "\n\tCost: " + getRouteCost(BaBcheapestRoute));
    }


    private static void resetLists() {
        BFRoutePerms = new ArrayList<Route>();
        BaBRoutePerms = new ArrayList<Route>();

        cities = new ArrayList<City>();

        cities.add(new City("Birmingham", 0, false));
        cities.add(new City("Lancaster", 1, false));
        cities.add(new City("Leeds", 2, false));
        cities.add(new City("Leicester", 3, false));
        cities.add(new City("Lincoln", 4, false));
        cities.add(new City("Liverpool", 5, false));
        cities.add(new City("Manchester", 6, false));
        cities.add(new City("Nottingham", 7, false));
        cities.add(new City("Sheffield", 8, false));
        cities.add(new City("Stoke", 9, true));
    }


    private static void permute(Route r, List<Integer> notVisited, boolean isBruteForce) {
        if (!notVisited.isEmpty()) {

            for (int i = 0; i < notVisited.size(); i++) {
                int temp = notVisited.remove(0);

                Route newRoute = new Route();
                for (City c1 : r.getRoute()) {
                    newRoute.getRoute().add(c1);
                }

                newRoute.getRoute().add(cities.get(temp));

                if (isBruteForce) {
                    permute(newRoute, notVisited, true);
                } else {
                    if (BaBRoutePerms.isEmpty()) {
                        // Recursive call
                        permute(newRoute, notVisited, false);
                    } else if (getRouteCost(newRoute) < BaBcheapestCost) {
                        permute(newRoute, notVisited, false);
                    }
                }
                notVisited.add(temp);
            }
        } else {
            if (isBruteForce) {
                BFRoutePerms.add(r);
            } else {
                r.getRoute().add(0, cities.get(9));
                r.getRoute().add(cities.get(9));

                BaBRoutePerms.add(r);

                if (getRouteCost(r) < BaBcheapestCost) {
                    BaBcheapestRoute = r;
                    BaBcheapestCost = getRouteCost(r);
                }
            }
        }
    }

    private static void findShortestPermutation(List<Route> routeList) {
        for (Route r : routeList) {
            appendStoke(r);

            if (getRouteCost(r) < BFcheapestCost) {
                BFcheapestCost = getRouteCost(r);
                BFcheapestRoute = r;
            }
        }

        System.out.println("\t" + BFcheapestRoute.toString() + "\n\tCost: " + BFcheapestCost);
    }

    private static void appendStoke(Route r) {
        r.getRoute().add(0, cities.get(9));
        r.getRoute().add(cities.get(9));
    }


    private static Double getRouteCost(Route r) {
        double tempCost = 0;
        for (int i = 0; i < r.getRoute().size() - 1; i++) {
            tempCost += distances[r.getRoute().get(i).getID()][r.getRoute().get(i + 1).getID()];
        }
        return tempCost;
    }
}
