static final boolean LOG = true;
static void log(Object msg) {
    if(LOG) IO.println(Objects.toString(msg));
}

record Point(long x, long y) {
    static Point from(String line){
        var parts = line.split(",");
        return new Point(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
    }
}

record Line(Point start, Point end) {
    boolean isXLine(){
        return start.x == end.x;
    }
    long maxX() { return Math.max(start.x, end.x); }
    long minX() { return Math.min(start.x, end.x); }
    long maxY() { return Math.max(start.y, end.y); }
    long minY() { return Math.min(start.y, end.y); }
}

record Rectangle(Point start, Point end) implements Comparable<Rectangle> {
    long size() {
        return (maxX() - minX()+1)*(maxY() - minY()+1);
    }

    long maxX() { return Math.max(start.x, end.x); }
    long minX() { return Math.min(start.x, end.x); }
    long maxY() { return Math.max(start.y, end.y); }
    long minY() { return Math.min(start.y, end.y); }

    public int compareTo(Rectangle o) {
        return -Long.compare(size(), o.size());
    }
    boolean linesCrossRectangle(Map<Long, List<Line>> xlines, Map<Long, List<Line>> ylines) {
        for(long x = minX()+1; x < maxX(); x++) {
            List<Line> candidates = xlines.get(x);
            if(null == candidates) continue;
            for(Line line : candidates) {
                log("x candi: " + line);
                if(xlineCrosses(line)) return true;
            }
        }
        for(long y = minY()+1; y < maxY(); y++) {
            List<Line> candidates = ylines.get(y);
            if(null == candidates) continue;
            for(Line line : candidates) {
                log("y candi: " + line);
                if(ylineCrosses(line)) return true;
            }
        }
        return false;
    }

    boolean ylineCrosses(Line line) {
        log("ylineCrosses(" + line);
        if(line.minX() <= minX() && line.maxX() >= minX()){
            return true;
        }
        if(line.minX() <= maxX() && line.maxX() >= maxX()){
            return true;
        }
        return false;
    }

    boolean xlineCrosses(Line line) {
        log("xlineCrosses(" + line);
        if(line.minY() <= minY() && line.maxY() >= minY()){
            return true;
        }
        if(line.minY() <= maxY() && line.maxY() >= maxY()){
            return true;
        }
        return false;
    }


}

void main() throws Exception {
    var points = Files.lines(Path.of("test.txt"))
        .map(Point::from)
        .toList();
    log("Parsed points: " + points);
    Map<Long, List<Line>> xlines = new HashMap<>();
    Map<Long, List<Line>> ylines = new HashMap<>();
    PriorityQueue<Rectangle> queue = new PriorityQueue<>();
    for(int i = 0; i < points.size(); i++) {
        var line = new Line(points.get(i), points.get((i+1)%points.size()));
        if(line.isXLine()) {
            if(xlines.containsKey(line.start.x)) {
                xlines.get(line.start.x).add(line);
            } else {
                xlines.put(line.start.x, new ArrayList<>(List.of(line)));
            }
        } else {
            if(ylines.containsKey(line.start.y)) {
                ylines.get(line.start.y).add(line);
            } else {
                ylines.put(line.start.y, new ArrayList<>(List.of(line)));
            }
        }
        for(int j = i+1; j < points.size(); j++) {
            queue.add(new Rectangle(points.get(i), points.get(j)));
        }
    }
    log("queue: " + queue);

    while(!queue.isEmpty()){
        var rect = queue.poll();
        log(rect);
        if(!rect.linesCrossRectangle(xlines,ylines)) {
            IO.println(rect.size());
            return;
        }
    }

}
