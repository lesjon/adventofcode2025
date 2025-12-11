static final boolean LOG = true;
static void log(Object msg) {
    if(LOG) IO.println(Objects.toString(msg));
}
static final int SCALE = 1;

record Point(int x, int y) {
    static Point from(String line){
        var parts = line.split(",");
        return new Point(Integer.parseInt(parts[0])/SCALE, Integer.parseInt(parts[1])/SCALE);
    }

    Point subtract(int dx, int dy){
        return new Point(x-dx, y-dy);
    }
}

record Line(Point start, Point end) {
    int maxX() { return Math.max(start.x, end.x); }
    int minX() { return Math.min(start.x, end.x); }
    int maxY() { return Math.max(start.y, end.y); }
    int minY() { return Math.min(start.y, end.y); }
}

record Rectangle(Point start, Point end) implements Comparable<Rectangle> {
    long size() {
        return (maxX() - minX()+1)*(maxY() - minY()+1);
    }

    int maxX() { return Math.max(start.x, end.x); }
    int minX() { return Math.min(start.x, end.x); }
    int maxY() { return Math.max(start.y, end.y); }
    int minY() { return Math.min(start.y, end.y); }

    public int compareTo(Rectangle o) {
        return -Long.compare(size(), o.size());
    }

    boolean bordersSet(Grid grid) {
        for (int x = minX(); x <= maxX(); x++) {
            if(!grid.get(x, maxY()) || !grid.get(x,minY()) ){
                return false;
            }
        }
        for (int y = minY(); y <= maxY(); y++) {
            if(!grid.get(maxX(), y) || !grid.get(minX(),y)) {
                return false;
            }
        }
        return true;
    }
}

class Grid {
    BitSet[] bits;
    final int width;
    final int height;

    Grid(int width, int height) {
        this.width = width;
        this.height = height;
        bits = new BitSet[height+1];
        for (int i = 0; i <= height; i++) {
            bits[i] = new BitSet(width);
        }
    }

    void set(int x, int y) {
        bits[y].set(x);
    }

    void set(Point p) {
        set(p.x, p.y);
    }
    boolean get(int x, int y) {
        return bits[y].get(x);
    }

    boolean get(Point p) {
        return get(p.x, p.y);
    }

    void fill(int x, int y) {
        Queue<Point> queue = new LinkedBlockingQueue<>();
        queue.add(new Point(x,y));
        int largestX = 0;
        int largestY = y;
        while(!queue.isEmpty()){
            var curr = queue.poll();
            if(get(curr)) continue;
            set(curr);
            queue.add(new Point(curr.x-1, curr.y));
            queue.add(new Point(curr.x+1, curr.y));
            queue.add(new Point(curr.x, curr.y-1));
            queue.add(new Point(curr.x, curr.y+1));
            if(curr.x > largestX) {
                log(String.format("fill x progress: %.2f", 100*largestX / (double)width) + " %");
                largestX = curr.x;
            }
            if(curr.y > largestY) {
                log(String.format("fill y progress: %.2f", 100*((largestY-y) / (double)(height-y))) + " %");
                largestY = curr.y;
            }
        }
    }

    void display() {
        var sb = new StringBuilder();
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                if(get(x,y)){
                    sb.append('.');
                }else{
                    sb.append(' ');
                }
            }
            sb.append('\n');
        }
        IO.println(sb);
    }
}

void main() throws Exception {
    var points = Files.lines(Path.of("input.txt"))
        .map(Point::from)
        .toList();
    int gridStartX = points.stream().mapToInt(Point::x).min().getAsInt();
    long gridEndX= points.stream().mapToInt(Point::x).max().getAsInt();
    int gridStartY  = points.stream().mapToInt(Point::y).min().getAsInt();
    long gridEndY = points.stream().mapToInt(Point::y).max().getAsInt();
    int width = (int)(gridEndX - gridStartX);
    int height = (int)(gridEndY - gridStartY);

    log("width: " + width + " height:" + height + " gridStartX:" + gridStartX + " gridStartY:" + gridStartY);
    Grid grid = new Grid((int)width, (int)height);
    log("gridSize: " + Arrays.stream(grid.bits).mapToLong(BitSet::size).sum());
    var translatedPoints = points.stream().map(p -> p.subtract(gridStartX, gridStartY)).toList();
    PriorityQueue<Rectangle> queue = new PriorityQueue<>();
    for(int i = 0; i < translatedPoints.size(); i++) {
        var line = new Line(translatedPoints.get(i), translatedPoints.get((i+1)%translatedPoints.size()));
        log(line);
        if(line.start.x == line.end.x){
            for (int y = line.minY(); y <= line.maxY(); y++) {
                grid.set(line.start.x, y);
            }
        }else{
            for (int x = line.minX(); x <= line.maxX(); x++) {
                grid.set(x, line.start.y);
            }
        }
        for(int j = i+1; j < translatedPoints.size(); j++) {
            queue.add(new Rectangle(translatedPoints.get(i), translatedPoints.get(j)));
        }
    }
    log("lines set and queue created");
    int pointInsideY = (int)Math.round(translatedPoints.stream().filter(p -> p.x == 0).limit(2).mapToInt(Point::y).average().getAsDouble());
    log("start fill from: " + new Point(1, pointInsideY));
    grid.fill(1, pointInsideY);
    // grid.display();
    log("done fill");

    while(!queue.isEmpty()){
        var rect = queue.poll();
        log(rect);
        if(rect.bordersSet(grid)){
            IO.println(rect.size());
            return;
        }
    }

}
