static final boolean LOG = false;
static void log(Object msg) {
    if(LOG) IO.println(Objects.toString(msg));
}

record Machine(List<Integer> pos, List<int[]> buttons, int[] presses) implements Comparable<Machine> {
    public int compareTo(Machine o){
        return Long.compare(totalScore(), o.totalScore());
    }

    long totalScore() {
        return totalPresses() + distanceToOrigin();
    }
    long totalPresses() {
        long total = 0;
        for (int p : presses) {
            total += p;
        }
        return total;
    }

    long distanceToOrigin() {
        long total = 0;
        for (int i = 0; i < pos.size(); i++) {
            total += pos.get(i)*pos.get(i);
        }
        return total;
    }

    boolean overJoltage() {
        for (int i : pos) {
            if(i < 0) return true;
        }
        return false;
    }

    Machine press(int button) {
        List<Integer> newPos = new ArrayList<>(pos);
        int[] buttonArray = buttons.get(button);
        for (int i : buttonArray) {
            newPos.set(i, pos.get(i)-1);
        }
        int[] newPresses = presses.clone();
        newPresses[button]++;
        return new Machine(List.copyOf(newPos), buttons, newPresses);
    }
}

long fewestButtonPresses(Machine machine) {
    log("fewestButtonPresses(" + machine);
    PriorityQueue<Machine> machines = new PriorityQueue<>();
    Map<List<Integer>, Long> gScore = new HashMap<>();
    machines.add(machine);
    gScore.put(machine.pos, machine.totalPresses());
    while(!machines.isEmpty()) {
        var current = machines.poll();
        log("current: " + current.pos + " cost: " + current.totalScore());
        if(current.distanceToOrigin() == 0) {
            return current.totalPresses();
        }
        for (int i = 0; i < current.presses.length; i++) {
            Machine next = current.press(i);
            if(next.overJoltage()) continue;
            if(gScore.containsKey(next.pos)/*  && gScore.get(next.pos) < next.totalPresses()*/) {
                log("gScore of: " + next.pos + " was less then: "  + next.totalPresses());
                continue;
            }
            gScore.put(next.pos, next.totalPresses());
            machines.add(next);
        }
    }
    throw new IllegalStateException("No path to target found");
}


Machine parse(String line) {
    String[] parts = line.split(" ");

    String[] joltageParts = parts[parts.length-1].substring(1, parts[parts.length-1].length()-1).split(",");
    List<Integer> joltages = Arrays.stream(joltageParts).map(s -> Integer.parseInt(s)).toList();

    List<int[]> buttons = new ArrayList<>();
    for (int i = 1; i < parts.length-1; i++) {
        String p = parts[i].substring(1, parts[i].length()-1);
        String[] indicesStrings = p.split(",");
        int[] button = new int[indicesStrings.length];

        for (int j = 0; j < indicesStrings.length; j++) {
            button[j] = Integer.parseInt(indicesStrings[j]);
        }
        buttons.add(button);
    }
    var presses = new int[buttons.size()];

    return new Machine(joltages, buttons, presses);
}

void main() throws Exception {
    Stream<Machine> machines = Files.lines(Path.of("input.txt"))
        .map(l -> parse(l));

    long total = machines.parallel().mapToLong(machine -> fewestButtonPresses(machine)).peek(IO::println).sum();
    IO.println(total);

}
