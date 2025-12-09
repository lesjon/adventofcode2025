import static java.lang.Long.parseLong;

record Location(long x, long y){
    long size(Location o){
        return Math.abs(1+x-o.x)*Math.abs(1+y-o.y);
    }
}

record Line(Location start, Location end){
    boolean contains(Location target){
        if(target.x == start.x){
            if(start.y <= target.y && target.y <= end.y){
                return true;
            }
            if(end.y <= target.y && target.y <= start.y){
                return true;
            }
        }
        if(target.y == start.y){
            if(start.x <= target.x && target.x <= end.x){
                return true;
            }
            if(end.x <= target.x && target.x <= start.x){
                return true;
            }
        }
        return false;
    }
}

void main() throws Exception {
    var squares = Files.lines(Path.of("input.txt"))
        .map(s -> s.split(","))
        .map(ss -> new Location(parseLong(ss[0]), parseLong(ss[1])))
        .toList();

    
    int width = squares.stream().mapToInt(l -> (int)l.x).max().getAsInt();
    int height = squares.stream().mapToInt(l -> (int)l.y).max().getAsInt();
    IO.println("width: " + width + " height: " + height);
    List<Line> lines = new ArrayList<>();
    Map<Long, List<Line>> xLines = new HashMap<>();
    Map<Long, List<Line>> yLines = new HashMap<>();
    for (int i = 1; i <= squares.size(); i++) {
        var line = new Line(squares.get(i%squares.size()), squares.get(i-1));
        lines.add(line);
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
    // Set<Location> fill = fill(width, height, xLines, yLines);
    // display(width, height, xLines, yLines, fill);

}

Set<Location> fill(int width, int height, Map<Long, List<Line>> xLines, Map<Long, List<Line>> yLines){
    Location fillStart = new Location(width-1, height-1);
    ArrayBlockingQueue<Location> fillQueue = new ArrayBlockingQueue<>(width*height);
    fillQueue.add(fillStart);
    Set<Location> fill = new HashSet<>();
    Set<Location> inQueue = new HashSet<>();
    while(!fillQueue.isEmpty()){
        IO.println("fillQueue.size(): " + fillQueue.size());
        IO.println("fill.size(): " + fill.size());
        Location l = fillQueue.poll();
        if(xLines.containsKey(l.x) && xLines.get(l.x).stream().anyMatch(line -> line.contains(l))){
            continue;
        }
        if(yLines.containsKey(l.y) && yLines.get(l.y).stream().anyMatch(line -> line.contains(l))){
            continue;
        }
        // IO.println("adding to fill: " + fill);
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

void display(int width, int height, Map<Long, List<Line>> xlines, Map<Long, List<Line>> ylines, Set<Location> fill){
    var sb = new StringBuilder();
    for (int x = 0; x <= width; x++) {
        sb.setLength(0);
        for (int y = 0; y <= height; y++) {
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

