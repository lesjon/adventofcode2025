static final boolean LOG = true;
static void log(Object msg) {
    if(LOG) IO.println(Objects.toString(msg));
}

record Graph(Node start, Set<Node> nodes, Node end ){}

record StringNode(String name, List<String> next){}
record Node(String name, List<Node> next){}

StringNode parse(String line) {
    log("parse(" + line);
    String[] parts = line.split(":");
    String name = parts[0];
    String[] nextNames = parts[1].trim().split(" ");
    return new StringNode(name, Arrays.asList(nextNames));
}

Map<String, Long> cache = new HashMap<>();
long paths(String start, String end, Map<String, StringNode> nodes) {
    if(start.equals(end)){
        return 1L;
    }
    if(cache.containsKey(start)){
        return cache.get(start);
    }
    long result = 0;
    for (String next : nodes.get(start).next) {
        result += paths(next, end, nodes);
    }
    cache.put(start, result);
    return result;
}

void main() throws Exception {
    Map<String, StringNode> nodes = Files.lines(Path.of("input.txt"))
        .map(l -> parse(l))
        .collect(Collectors.toMap(e -> e.name, e -> e));

    log(nodes);
    long total = paths("you", "out", nodes);
    IO.println(total);

}
