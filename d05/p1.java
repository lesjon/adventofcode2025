record Range(long start, long end){
    boolean inRange(long id){
        return start <= id && id <= end;
    }
};

List<Long> ingredientIds = new ArrayList<>();
List<Range> ranges = new ArrayList<>();

void main() throws Exception {
    var lines = Files.lines(Path.of("input.txt")).toList();
    boolean split = false;
    long total = 0;
    for (int i = 0; i < lines.size(); i++) {
        var line =lines.get(i);
        if(line.isEmpty()) {
            split = true;
            continue;
        }
        if(split){
            if(isFresh(Long.parseLong(line))){
                total++;
            }
            continue;
        }
        String[] parts = line.split("-");
        ranges.add(new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1])));
    }
    IO.println(total);
}

boolean isFresh(long id){
    for (Range r : ranges) {
        if(r.inRange(id)){
            return true;
        }
    }
    return false;
}
