import java.util.*;

class BusRoutes {
    //     Why BFS?
    // - We need to find shortest path (minimum number of buses)
    // - BFS processes level by level, ensuring minimum buses
    // - Each level represents one additional bus taken
    
    // a. HashMap<Integer, List<Integer>> stopToBuses:
    //    - Key: Bus stop number
    //    - Value: List of bus routes that visit this stop
    //    - Purpose: Quick lookup of which buses visit a particular stop
    
    // b. Queue<Integer>:
    //    - For BFS traversal
    //    - Stores bus stops to visit
    
    // c. Set<Integer> visitedBuses:
    //    - Keeps track of which bus routes we've already explored
    //    - Prevents revisiting same route
    
    // d. Set<Integer> visitedStops:
    //    - Keeps track of which stops we've already visited
    //    - Prevents cycles in our traversal
        public int numBusesToDestination(int[][] routes, int source, int target) {
            if ( source == target) {
                return 0;
            }
            Map<Integer, List<Integer>> stopToBusMap = new HashMap<>();
            for(int i=0;i<routes.length;i++){
                for(int stop: routes[i]) {
                    stopToBusMap.computeIfAbsent(stop, k->new ArrayList<>()).add(i);
                }
            }
            Queue<int []> q = new LinkedList<>();
            Set<Integer> visitedStops = new HashSet<>();
            Set<Integer> visitedBuses = new HashSet<>();
            q.offer(new int[]{source, 0});
            visitedStops.add(source);
            while(!q.isEmpty()) {
                int[] curr = q.poll();
                int currStop = curr[0];
                int busesTaken = curr[1];
                List<Integer> buses = stopToBusMap.getOrDefault(currStop, new ArrayList<>());
                for(int bus: buses) {
                    if(visitedBuses.contains(bus))
                        continue;
                    visitedBuses.add(bus);
                    for(int  nextStop:routes[bus]) {
                        if(nextStop== target) {
                            return busesTaken + 1;
                        }
                        if(!visitedStops.contains(nextStop)){
                            visitedStops.add(nextStop);
                            q.offer(new int[]{nextStop, busesTaken + 1});
                        }
                    }
                }
            }
            return -1;
        }
    }