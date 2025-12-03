void main() throws Exception {
    var ranges = Files.lines(Path.of("test.txt")).findFirst().get().split(",");
    long total = Arrays.stream(ranges)
        .map(r -> r.split("-"))
        .mapToLong(r -> invalidCount(r[0], r[1]))
        .sum();
    IO.println(total);
}

static long invalidCount(String start, String end) {
    long total = 0;
    for (int i = 1; i <= (end.length()+1)/2; i++) {
        total += invalidCount(start, end, i);
    }
    return total;
}

static long invalidCount(String start, String end, int seqSize) {
    IO.println("invalidCount " + start + ", " + end + ", " + seqSize);
    long total = 0;
    int startSize = start.length();
    int endSize = end.length();
    IO.println("startSize" + startSize);
    IO.println("endSize" + endSize);
    if(seqSize > (endSize+1)/2) {
        return 0;
    }
    long halfStart;
    long halfEnd;
    try{
        halfStart = Long.parseLong(start.substring(0, startSize-seqSize));
    } catch(Exception e){
        halfStart = 0;
    }
    halfEnd = Long.parseLong(end.substring(0, seqSize));
    IO.println("halfs: " + halfStart + ", " + halfEnd);
    Set<String> candidates = LongStream.range(halfStart, halfEnd+1)
        .mapToObj(l -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < end.length()/seqSize; i++) {
                sb.append(l);
            }
            return sb.toString();
        })
        .collect(Collectors.toSet());
    IO.println("candidates" + candidates);
    long startL = Long.parseLong(start);
    long endL = Long.parseLong(end);
    for(String candidate : candidates){ 
        // IO.println("candidate: "+candidate);
        long target = Long.parseLong(candidate);
        if(target >= startL && target <= endL){
            total += target;
        }
        // IO.readln();
    }
    IO.println("return: "+total);
    return total;
}
