void main() throws Exception {
    var ranges = Files.lines(Path.of("input.txt")).findFirst().get().split(",");
    long total = Arrays.stream(ranges)
        .map(r -> r.split("-"))
        .mapToLong(r -> invalidCount(r[0], r[1]))
        .sum();
    IO.println(total);
}

static long invalidCount(String start, String end) {
    Set<String> invalids = new HashSet<>();
    if(start.length() != end.length()){
        assert end.length() - start.length() == 1;
        StringBuilder startEnd = new StringBuilder();
        for (int i = 0; i < start.length(); i++) {
            startEnd.append('9');
        }
        StringBuilder endStart = new StringBuilder("1");
        for (int i = 0; i < end.length()-1; i++) {
            endStart.append('0');
        }
        IO.println("split: " + start + " " +startEnd.toString() + " " + endStart.toString() + " " + end);
        return invalidCount(start, startEnd.toString()) + invalidCount(endStart.toString(), end);
    }
    for (int i = 1; i <= start.length()/2; i++) {
        if(start.length() % i != 0){
            continue;
        }
        invalidCount(start, end, i, invalids);
    }
    return invalids.stream().mapToLong(s -> Long.parseLong(s)).sum();
}

static Set<String> invalidCount(String start, String end, int seqSize, Set<String> invalids) {
    IO.println("invalidCount " + start + ", " + end + ", " + seqSize);
    long total = 0;
    int startSize = start.length();
    int endSize = end.length();
    IO.println("startSize" + startSize);
    IO.println("endSize" + endSize);
    long halfStart = Long.parseLong(start.substring(0, seqSize));
    long halfEnd = Long.parseLong(end.substring(0, seqSize));
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
            invalids.add(candidate);
        }
    }
    IO.println("return: "+total);
    return invalids;
}
