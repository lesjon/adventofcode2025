record Location(int x, int y){
    int size(Location o){
        return Math.abs(1+x-o.x)*Math.abs(1+y-o.y);
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

record Square(Location start, Location end, int size) implements Comparable<Square> {
    public int compareTo(Square o) {
        return o.size - size;
    }

    boolean tileIsInside(Location tile){
        if(tile.x > Math.min(start.x, end.x) && Math.max(start.x, end.x > tile.x){
            if(tile.y > Math.min(start.y, end.y) && Math.max(start.y, end.y > tile.y){
                return true;
            }
        }
        return false;
    }
}

void main() throws Exception {
    List<Location> squares = Files.lines(Path.of("test.txt"))
        .map(s -> s.split(","))
        .map(ss -> new Location(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])))
        .toList();
    IO.println("tiles parsed");
    int width = squares.stream().mapToInt(l -> (int)l.x).max().getAsInt();
    int height = squares.stream().mapToInt(l -> (int)l.y).max().getAsInt();
    IO.println("width: " + width + " height: " + height);
    Map<Integer, List<Line>> xLines = new HashMap<>();
    Map<Integer, List<Line>> yLines = new HashMap<>();
    for (int i = 1; i <= squares.size(); i++) {
        var line = new Line(squares.get(i%squares.size()), squares.get(i-1));
        int firstIndex = i % squares.size();
        if(squares.get(firstIndex).x ==  squares.get(i-1).x){
            List<Line> currentList  = new ArrayList<>();
            currentList.add(line);
            xLines.merge(squares.get(i-1).x, currentList, (l,r) -> {
                l.addAll(r);
                return l;
            });
        }
        if(squares.get(firstIndex).y ==  squares.get(i-1).y){
            List<Line> currentList  = new ArrayList<>();
            currentList.add(line);
            yLines.merge(squares.get(i-1).y, currentList, (l,r) -> {
                l.addAll(r);
                return l;
            });
        }
    }
    IO.println("xlines and ylines calculated");
    // Set<Location> fill = fill(width, height, xLines, yLines);
    // display(width, height, xLines, yLines, fill);

    Square largestSquare = largestSquare(squares, xLines, yLines);
    IO.println(largestSquare.size);
    display(largestSquare, xLines, yLines, Set.of());
}

Square largestSquare(List<Location> tiles, Map<Integer, List<Line>> xLines, Map<Integer, List<Line>> yLines){
    PriorityQueue<Square> squares = new PriorityQueue<>();
    for (int i = 1; i < tiles.size(); i++) {
        for (int j = i+1; j < tiles.size(); j++) {
            int size = tiles.get(i).size(tiles.get(j));
            squares.add(new Square(tiles.get(i), tiles.get(j), size));
        }
    }
    SQUARES: while(!squares.isEmpty()){
        var square = squares.poll();
        display(square, xLines, yLines, Set.of());
        IO.println(square);
        var start = square.start;
        var end = square.end;
        for (int x = Math.min(start.x, end.x)+1; x < Math.max(start.x, end.x); x++) {
            IO.println("x: " + x);
            var lines = xLines.get(x);
            IO.println("lines: " + lines);
            if(lines == null) continue;
            int minY = Math.min(start.y, end.y); 
            var minYLoc = new Location(x,minY);
            IO.println("minYLoc: " + minYLoc);
            if(lines.stream().anyMatch(l -> l.contains(minYLoc))) {
                IO.println("lines matched minYLoc");
                continue SQUARES;
            }
            int maxY = Math.max(start.y, end.y); 
            var maxYLoc = new Location(x, maxY);
            IO.println("maxYLoc: " + maxYLoc);
            if(lines.stream().anyMatch(l -> l.contains(maxYLoc))) {
                IO.println("lines matched maxYLoc");
                continue SQUARES;
            }
        }
        for (int y = Math.min(start.y, end.y)+1; y < Math.max(start.y, end.y); y++) {
            IO.println("y: " + y);
            var lines = yLines.get(y);
            IO.println("lines: " + lines);
            if(lines == null) continue;
            int minx = Math.min(start.x, end.x); 
            Location minXLoc = new Location(minx, y);
            IO.println("minXLoc: " + minXLoc);
            if(lines.stream().anyMatch(l -> l.contains(minXLoc))) {
                IO.println("lines matched minXLoc");
                continue SQUARES;
            }
            int maxx = Math.max(start.x, end.x); 
            var maxXLoc = new Location(maxx, y);
            IO.println("maxXLoc: " + maxXLoc);
            if(lines.stream().anyMatch(l -> l.contains(maxXLoc))) {
                IO.println("lines matched maxXLoc");
                continue SQUARES;
            }
        }
        return square;
    }
    return null;
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

