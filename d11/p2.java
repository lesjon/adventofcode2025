static final boolean LOG = true;
static void log(Object msg) {
    if(LOG) IO.println(Objects.toString(msg));
}

record Graph(Node start, Set<Node> nodes, Node end ){}

record StringNode(String name, List<String> next){}
record Node(String name, List<Node> next){}

StringNode parse(String line) {
    log("parse( " + line);
    String[] parts = line.split(":");
    String name = parts[0];
    String[] nextNames = parts[1].trim().split(" ");
    return new StringNode(name, Arrays.asList(nextNames));
}

Map<List<String>, Long> cache = new HashMap<>();
long paths(String start, String end, Map<String, StringNode> nodes, List<String> path) {
    if(start.equals(end)){
        log(path);
        if(path.contains("fft") && path.contains("dac"))
            return 1L;
        return 0L;
    }
    // log("path" + path);
    List<String> cacheKey = path.stream().filter(s -> s.equals("fft") || s.equals("dac") || s.equals(start)).toList();;
    // log("cacheKey" + cacheKey);
    if(cache.containsKey(cacheKey)){
        log("cache.containsKey(cacheKey)");
        // log("cache:" + cache);
        return cache.get(cacheKey);
    }
    long result = 0;
    for (String next : nodes.get(start).next) {
        var nextPath = new ArrayList<>(path);
        nextPath.add(next);
        result += paths(next, end, nodes, nextPath);
    }
    cache.put(cacheKey, result);
    return result;
}

void main() throws Exception {
    Map<String, StringNode> nodes = Files.lines(Path.of("input.txt"))
        .map(l -> parse(l))
        .collect(Collectors.toMap(e -> e.name, e -> e));

    log(nodes);
    long total = paths("svr", "out", nodes, List.of("svr"));
    IO.println(total);

}
