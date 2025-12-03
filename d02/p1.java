void main() throws Exception {

    var ranges = Files.lines(Path.of("input.txt")).findFirst().get().split(",");
    long total = Arrays.stream(ranges)
        .map(r -> r.split("-"))
        .mapToLong(r -> invalidCount(r[0], r[1]))
        .sum();
    IO.println(total);
}

static long invalidCount(String start, String end) {
    IO.println("invalidCount " + start + ", " + end);
    long total = 0;
    int startSize = start.length();
    int endSize = end.length();
    IO.println("startSize" + startSize);
    IO.println("endSize" + endSize);
    int halfEndLen = (endSize+1)/2;
    long halfStart;
    long halfEnd;
    try{
        halfStart = Long.parseLong(start.substring(0, startSize-halfEndLen));
    } catch(Exception e){
        halfStart = 0;
    }
    halfEnd = Long.parseLong(end.substring(0, halfEndLen));
    IO.println("halfs: " + halfStart + ", " + halfEnd);
    Set<String> candidates = LongStream.range(halfStart, halfEnd+1)
        .mapToObj(l -> String.format("%d%d",l,l))
        .collect(Collectors.toSet());
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
