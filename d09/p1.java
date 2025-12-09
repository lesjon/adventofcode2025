import static java.lang.Long.parseLong;

record Location(long x, long y){
    long size(Location o){
        return Math.abs(1+x-o.x)*Math.abs(1+y-o.y);
    }
}

record Line(Location start, Location end){}

void main() throws Exception {
    var squares = Files.lines(Path.of("test.txt"))
        .map(s -> s.split(","))
        .map(ss -> new Location(parseLong(ss[0]), parseLong(ss[1])))
        .toList();

    List<Line> lines = new ArrayList<>();
    lines.add(new Line(squares.get(squres.size()-1), squares.get(0)));
    for (int i = 1; i < squares.size(); i++) {
        lines.add(new Line(squares.get(i),squares.get(i-1)));

        for (int j = i+1; j < squares.size(); j++) {
            long size = squares.get(i).size(squares.get(j));
            if(size> largestSquare){
                largestSquare = size;
            }
        }
    }
    IO.println(largestSquare);
}
