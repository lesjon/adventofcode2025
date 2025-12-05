record Range(long start, long end) implements Comparable<Range>{
    public int compareTo(Range o){
        if(o.start != start){
            return Long.compare(start, o.start);
        }
        return Long.compare(end, o.end);
    }
};

List<Range> newRanges = new ArrayList<>();

void main() throws Exception {
    List<Range> ranges = Files.lines(Path.of("input.txt"))
        .filter(line -> line.contains("-"))
        .map((String line) -> {
            String[] parts = line.split("-");
            long start = Long.parseLong(parts[0]);
            long end = Long.parseLong(parts[1]);
            return new Range(start, end);
        }).sorted().toList();
    Range prev_r = null;
    for (Range r : ranges) {
        IO.println(r);
        if(prev_r == null || prev_r.end < r.start){
            newRanges.add(r);
        } else {
            newRanges.remove(newRanges.size()-1);
            assert prev_r.start <= r.start;
            newRanges.add(new Range(prev_r.start, Math.max(prev_r.end, r.end)));
        }
        prev_r = newRanges.get(newRanges.size()-1);
    }

    IO.println("calc total");
    long total = 0;
    for (Range r : newRanges) {
        IO.println(r);
        total += r.end - r.start +1;
    }
    IO.println(total);
}
