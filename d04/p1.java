void main() throws Exception {

    var lines = Files.lines(Path.of("input.txt")).toList();
    int width = lines.get(0).length();
    int height = lines.size();
    IO.println(width + ", " + height);
    long total = 0;
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            if(lines.get(y).charAt(x) != '@') continue;
            int adjacents = 0;
            for (int dy = y-1; dy <= y+1; dy++) {
                for (int dx = x-1; dx <= x+1; dx++) {
                    if(dx == x && dy == y) continue;
                    if(dx < 0 || dy < 0 || dx >= width || dy >= height) continue;
                    if(lines.get(dy).charAt(dx) == '@')
                    adjacents++;
                }
            }
            if(adjacents < 4){
                IO.println("Found: " + x + ", "+ y);
                total++;
            }
        }
    }
    IO.println(total);
}
