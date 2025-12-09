record Location(int x, int y){
    long size(Location o){
        return (long)Math.abs(1+x-o.x)*(long)Math.abs(1+y-o.y);
    }

    static Location add(Location l, Location r){
        return new Location(l.x + r.x, l.y + r.y);
    }

    Location add(Location o){
        return add(this, o);
    }
}

record Line(Location start, Location end){
    boolean contains(Location target){
        if(target.x == start.x){
            if(start.y < target.y && target.y < end.y){
                return true;
            }
            if(end.y < target.y && target.y < start.y){
                return true;
            }
        }
        if(target.y == start.y){
            if(start.x < target.x && target.x < end.x){
                return true;
            }
            if(end.x < target.x && target.x < start.x){
                return true;
            }
        }
        return false;
    }
}

record Square(Location start, Location end, long size) implements Comparable<Square> {
    public int compareTo(Square o) {
        return Long.compare(o.size, size);
    }

    boolean tileIsInside(Location tile){
        if(tile.x > Math.min(start.x, end.x) && Math.max(start.x, end.x) > tile.x) {
            if(tile.y > Math.min(start.y, end.y) && Math.max(start.y, end.y) > tile.y) {
                return true;
            }
        }
        return false;
    }

    boolean tileIsOnEdge(Location tile){
        // IO.println("isTileOnEdge: " + tile);
        if(tile.x == start.x || tile.x == end.x && tile.y == start.y || tile.y == end.y) {
            return true;
        }
        return false;
    }
}

enum Direction {
    UP(new Location(0,-1)), DOWN(new Location(0,1)), LEFT(new Location(-1,0)), RIGHT(new Location(1,0));
    private Direction(Location vector){
        this.vector = vector;
    }
    Location vector;
}

void main() throws Exception {
    List<Location> tiles = Files.lines(Path.of("input.txt"))
        .map(s -> s.split(","))
        .map(ss -> new Location(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])))
        .toList();
    IO.println("tiles parsed");
    int width = tiles.stream().mapToInt(l -> l.x).max().getAsInt();
    int height = tiles.stream().mapToInt(l -> l.y).max().getAsInt();
    IO.println("width: " + width + " height: " + height);
    Map<Integer, List<Line>> xLines = new HashMap<>();
    Map<Integer, List<Line>> yLines = new HashMap<>();
    for (int i = 1; i <= tiles.size(); i++) {
        var line = new Line(tiles.get(i%tiles.size()), tiles.get(i-1));
        int firstIndex = i % tiles.size();
        if(tiles.get(firstIndex).x ==  tiles.get(i-1).x){
            List<Line> currentList  = new ArrayList<>();
            currentList.add(line);
            xLines.merge(tiles.get(i-1).x, currentList, (l,r) -> {
                l.addAll(r);
                return l;
            });
        }
        if(tiles.get(firstIndex).y ==  tiles.get(i-1).y){
            List<Line> currentList  = new ArrayList<>();
            currentList.add(line);
            yLines.merge(tiles.get(i-1).y, currentList, (l,r) -> {
                l.addAll(r);
                return l;
            });
        }
    }
    IO.println("xlines and ylines calculated");
    // Set<Location> fill = fill(width, height, xLines, yLines);
    // display(width, height, xLines, yLines, fill);
    // display(width, height, xLines, yLines, Set.of());

    Square largestSquare = largestSquare(tiles, xLines, yLines);
    IO.println(largestSquare.size);
    // display(largestSquare, xLines, yLines, Set.of());
}

Square largestSquare(List<Location> tiles, Map<Integer, List<Line>> xLines, Map<Integer, List<Line>> yLines){
    PriorityQueue<Square> squares = new PriorityQueue<>();
    for (int i = 1; i < tiles.size(); i++) {
        for (int j = i+1; j < tiles.size(); j++) {
            long size = tiles.get(i).size(tiles.get(j));
            squares.add(new Square(tiles.get(i), tiles.get(j), size));
        }
    }
    IO.println("calculated "+ squares.size() + " squares");
    while(!squares.isEmpty()){
        var square = squares.poll();
        IO.println(square);
        var start = square.start;
        var end = square.end;
        if(tiles.stream().parallel().anyMatch(tile -> {
            if(square.tileIsInside(tile)){
                // IO.println("tileIsInside");
                return true;
            }
            if(square.tileIsOnEdge(tile)) {
                for (Direction dir : getLineDirectionsOfTile(tile, xLines, yLines)) {
                    var nextLoc = Location.add(tile, dir.vector);
                    if(square.tileIsInside(nextLoc)) {
                        // IO.println("tileOnEdgePointsInside");
                        return true;
                    }
                }
            }
            return false;
        })) {
            continue;
        }
        if(linesGoesThrougSquare(square, xLines, yLines)){
            continue;
        }
        return square;
    }
    return null;
}

boolean linesGoesThrougSquare(Square square, Map<Integer, List<Line>> xLines, Map<Integer, List<Line>> yLines){
    // IO.println("linesGoesThrougSquare(" + square);
    for (int x = Math.min(square.start.x, square.end.x)+1; x < Math.max(square.start.x, square.end.x); x++) {
        var lines = xLines.get(x);
        if (lines == null) {
            continue;
        }
        // IO.println("x: " + x + " lines: " + lines);
        for (Line line : lines) {
            if(Math.min(line.start.y, line.end.y) <= Math.min(square.start.y, square.end.y) && Math.max(line.start.y, line.end.y) >= Math.max(square.start.y, square.end.y)){
                return true;
            }
        }
    }
    for (int y = Math.min(square.start.y, square.end.y)+1; y < Math.max(square.start.y, square.end.y); y++) {
        var lines = yLines.get(y);
        if (lines == null) {
            continue;
        }
        // IO.println("y: " + y + " lines: " + lines);
        for (Line line : lines) {
            if(Math.min(line.start.x, line.end.x) <= Math.min(square.start.x, square.end.x) && Math.max(line.start.x, line.end.x) >= Math.max(square.start.x, square.end.x)){
                return true;
            }
        }
    }
    return false;
}

List<Direction> getLineDirectionsOfTile(Location tile, Map<Integer, List<Line>> xLines, Map<Integer, List<Line>> yLines) {
    List<Direction> result = new ArrayList<>();
    var xline = xLines.get(tile.x).stream().filter(l -> l.start.y == tile.y || l.end.y == tile.y).findFirst().get();
    if(xline.start.y > tile.y || xline.end.y > tile.y){
        result.add(Direction.DOWN);
    }else{
        result.add(Direction.UP);
    }
    var yline = yLines.get(tile.y).stream().filter(l -> l.start.x == tile.x || l.end.x == tile.x).findFirst().get();
    if(yline.start.x > tile.x || yline.end.x > tile.x){
        result.add(Direction.RIGHT);
    }else{
        result.add(Direction.LEFT);
    }
    return result;
}

Set<Location> fill(int width, int height, Map<Integer, List<Line>> xLines, Map<Integer, List<Line>> yLines){
    Location fillStart = new Location(width-1, height-1);
    ArrayBlockingQueue<Location> fillQueue = new ArrayBlockingQueue<>(width*height);
    fillQueue.add(fillStart);
    Set<Location> fill = new HashSet<>();
    Set<Location> inQueue = new HashSet<>();
    while(!fillQueue.isEmpty()){
        Location l = fillQueue.poll();
        if(xLines.containsKey(l.x) && xLines.get(l.x).stream().anyMatch(line -> line.contains(l))){
            continue;
        }
        if(yLines.containsKey(l.y) && yLines.get(l.y).stream().anyMatch(line -> line.contains(l))){
            continue;
        }
        fill.add(l);
        var nexts = List.of(new Location(l.x-1,l.y), new Location(l.x+1,l.y), new Location(l.x+1,l.y), new Location(l.x,l.y-1), new Location(l.x,l.y+1));
        for (Location next : nexts) {
            if(inQueue.contains(next)){
                continue;
            }
            fillQueue.add(next);
            inQueue.add(next);
        }
    }
    return fill;
}


void display(Square square, Map<Integer, List<Line>> xlines, Map<Integer, List<Line>> ylines, Set<Location> fill){
    display(Math.min(square.start.x, square.end.x),Math.min(square.start.y, square.end.y), Math.max(square.start.x, square.end.x),Math.max(square.start.y, square.end.y), xlines, ylines, fill);
}

void display(int endX, int endY, Map<Integer, List<Line>> xlines, Map<Integer, List<Line>> ylines, Set<Location> fill){
    display(0,0, endX, endY, xlines, ylines, fill);
}
void display(int startX, int startY, int endX, int endY, Map<Integer, List<Line>> xlines, Map<Integer, List<Line>> ylines, Set<Location> fill){
    var sb = new StringBuilder();
    for (int x = startX; x <= endX; x++) {
        sb.setLength(0);
        for (int y = startY; y <= endY; y++) {
            var target = new Location(x,y);
            if(xlines.getOrDefault(target.x, List.of()).stream().anyMatch(l -> l.contains(target))){
                sb.append('X');
            } else if(ylines.getOrDefault(target.y, List.of()).stream().anyMatch(l -> l.contains(target))){
                sb.append('Y');
            } else if(fill.contains(target)) {
                sb.append('0');
            } else {
                sb.append('.');
            }
        }
        IO.println(sb.toString());
    }
}

