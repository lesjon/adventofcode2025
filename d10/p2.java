static final boolean LOG = false;
static void log(Object msg){
    if(LOG) IO.println(Objects.toString(msg));
}

record Machine(List<Integer> target, List<int[]> buttons, int presses) implements Comparable<Machine> {

    static Machine parse(String line) {
        String[] parts = line.split(" ");
        String targetString = parts[parts.length-1].substring(1,parts[parts.length-1].length()-1);
        List<Integer> target = Arrays.stream(targetString.split(",")) 
            .map(Integer::parseInt)
            .toList();
        List<int[]> buttons = new ArrayList<>();
        for (int i = 1; i < parts.length-1; i++) {
            var buttonParts = parts[i].substring(1,parts[i].length()-1).split(",");
            var buttonIndices = Arrays.stream(buttonParts).mapToInt(Integer::parseInt).toArray();
            var button = new int[target.size()];
            for (int buttonIndex : buttonIndices) {
                button[buttonIndex] = 1;
            }
            buttons.add(button);
        }
        return new Machine(target, buttons, 0);
    }

    public String toString() {
        return String.format("Machine[target=%s, buttons=%s", target, buttons.stream().map(Arrays::toString).toList());
    }

    long dist() {
        return target.stream().mapToInt(i -> i*i).sum();
    }

    public int compareTo(Machine o) {
        return Long.compare(dist(), o.dist());
    }

    boolean atOrigin() {
        return target.stream().allMatch(i -> i==0);
    }

    boolean overShoot() {
        return target.stream().anyMatch(i -> i<0);
    }

    long solve() {
        IO.println("solve");
        var queue = new PriorityQueue<Machine>();
        Set<List<Integer>> seen = new HashSet<>();
        queue.add(this);
        seen.add(this.target);
        while(!queue.isEmpty()) {
            var machine = queue.poll();
            log(machine);
            if(machine.atOrigin()){
                return machine.presses;
            }
            for (int bi = 0; bi < buttons.size(); bi++) {
                var next = machine.nextPress(bi);
                if(seen.contains(next.target)){
                    continue;
                }
                if(next.overShoot()) {
                    log("overshoot");
                    continue;
                }
                queue.add(next);
                seen.add(next.target);
                log("seen: " + seen);
            }
        }
        throw new IllegalStateException("No path found");
    }

    Machine nextPress(int bi) {
        var button = buttons.get(bi);
        log("button: " + Arrays.toString(button));
        var nextTarget = new ArrayList<>(target);
        for (int i = 0; i < button.length; i++) {
            nextTarget.set(i, target.get(i)- button[i]);
        }
        log("nextTarget: " + nextTarget);
        return new Machine(nextTarget, buttons, 1+presses);
    }
}

void main() throws Exception {
    var lines = Files.lines(Path.of("input.txt")).toList();
    List<Machine> machines = new ArrayList<>();
    for (String line : lines) {
        machines.add(Machine.parse(line));
    }
    IO.println(machines.stream().mapToLong(Machine::solve).sum());
}
