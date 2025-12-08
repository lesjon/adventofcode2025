import static java.lang.Long.parseLong;
record Location(long x, long y, long z){
    long dist(Location o){
        return (x-o.x)*(x-o.x) + (y-o.y)*(y-o.y) + (z-o.z)*(z-o.z);
    }
}

record Connection(Location left, Location right, long distance) implements Comparable<Connection> {

    public int compareTo(Connection o){
        return Long.compare(distance, o.distance);
    }
}


void main() throws Exception {
    var boxes = Files.lines(Path.of("input.txt"))
    // var boxes = Files.lines(Path.of("test.txt"))
        .map(s -> s.split(","))
        .map(ss -> new Location(parseLong(ss[0]), parseLong(ss[1]), parseLong(ss[2])))
        .toList();
    Map<Location, Set<Location>> circuits = new HashMap<>();
    for (Location box : boxes) {
        circuits.put(box, new HashSet<>(Set.of(box)));
    }
    PriorityQueue<Connection> connections = new PriorityQueue<>();
    for (int i = 0; i < boxes.size(); i++) {
        for (int j = i+1; j < boxes.size(); j++) {
            long dist = boxes.get(i).dist(boxes.get(j));
            var con = new Connection(boxes.get(i), boxes.get(j), dist);
            connections.add(con);
        }
    }
    IO.println(connections.size());
    while (true) {
        var con = connections.poll();
        // IO.println("i: " + i + " con: " + con);
        var left = circuits.get(con.left);
        var right = circuits.get(con.right);
        left.addAll(right);
        for (var l : right) {
            circuits.put(l, left);
        }
        if(left.size() == boxes.size()){
            IO.println(con.left.x * con.right.x);
            break;
        }
        // circuits.entrySet().stream().filter(e -> e.getValue().size() > 1).forEach(IO::println);
    }
}
