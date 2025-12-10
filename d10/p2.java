static final boolean LOG = true;
static void log(Object msg) {
    if(LOG) IO.println(Objects.toString(msg));
}

record Button(List<Integer> wires){}

record Machine(int[] joltages, List<Button> buttons, int[] presses) implements Comparable<Machine> {

    public int compareTo(Machine o) {
        return Long.compare(totalPresses()+totalJoltage(), o.totalPresses()+o.totalJoltage());
    }

    long totalJoltage() {
        return Arrays.stream(joltages).sum();
    }

    long totalPresses() {
        return Arrays.stream(presses).sum();
    }

    Machine press(int button){
        var buttonWires = buttons.get(button).wires;
        int[] joltages = this.joltages.clone();
        for (int wire : buttonWires) {
            joltages[wire]--;
        }
        int[] presses = this.presses.clone();
        presses[button]++;
        return new Machine(joltages, buttons, presses);
    }

    public String toString() {
        return String.format("Machine[joltages=%s, buttons=%s, presses=%s",  Arrays.toString(joltages), buttons.toString(), Arrays.toString(presses));
    }

    boolean overJoltage() {
        for (int i = 0; i < joltages.length; i++) {
            if(joltages[i] < 0) return true;
        }
        return false;
    }

    boolean overJoltage(int wire) {
        return joltages[wire] < 0;
    }

    boolean joltagesZero() {
        for (int i = 0; i < joltages.length; i++) {
            if(joltages[i] != 0) return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Machine other)){
            return false;
        }
        return Arrays.equals(presses, other.presses);
    }
}

Machine parse(String line) {
    String[] parts = line.split(" ");
    List<Button> buttons = new ArrayList<>();
    for (int i = 1; i < parts.length-1; i++) {
        assert parts[i].charAt(0) == '(';
        String p = parts[i].substring(1, parts[i].length()-1);
        var button = new Button(Arrays.stream(p.split(",")).map(Integer::parseInt).toList());
        buttons.add(button);
    }
    var presses = new int[buttons.size()];

    String[] joltageParts = parts[parts.length-1].substring(1, parts[parts.length-1].length()-1).split(",");
    int[] joltages = Arrays.stream(joltageParts).mapToInt(s -> Integer.parseInt(s)).toArray();
    return new Machine(joltages, buttons, presses);
}

long fewestButtonPresses(Machine machine){
    log("fewestButtonPresses( " + machine);
    PriorityQueue<Machine> machines = new PriorityQueue<>();
    Set<Machine> added = new HashSet<>();
    Map<int[], Long> bests = new HashMap<>();
    machines.add(machine);
    bests.put(machine.joltages, machine.totalPresses());
    while(!machines.isEmpty()) {
        var current = machines.poll();
        if(current.joltagesZero()) {
            return current.totalPresses();
        }
        for (int i = 0; i < current.presses.length; i++) {
            Machine next = current.press(i);
            if(next.overJoltage()) continue;
            if(bests.containsKey(next.joltages) && bests.get(next.joltages) < next.totalPresses()) {
                continue;
            }
            bests.put(next.joltages, next.totalPresses());
            machines.add(next);
        }
    }
    throw new IllegalStateException("No path to target found");
}

void main() throws Exception {
    Stream<Machine> machines = Files.lines(Path.of("input.txt"))
        .map(l -> parse(l));

    long total = machines.parallel().mapToLong(machine -> fewestButtonPresses(machine)).peek(IO::println).sum();
    IO.println(total);

}
