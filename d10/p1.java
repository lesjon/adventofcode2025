static final boolean LOG = true;
static void log(Object msg) {
    if(LOG) IO.println(Objects.toString(msg));
}

record Button(List<Integer> lights){}

record Machine(boolean[] target, boolean[] lights, List<Button> buttons, int[] presses) implements Comparable<Machine> {
    public int compareTo(Machine o){
        return Long.compare(totalPresses(), o.totalPresses());
    }

    long totalPresses() {
        return Arrays.stream(presses).sum();
    }

    Machine press(int button){
        log("press(" + button);
        log("lights:" + Arrays.toString(this.lights));
        boolean[] lights = this.lights.clone();
        var buttonLights = buttons.get(button).lights;
        log("buttonLights:" + buttonLights);
        for (int light : buttonLights) {
            lights[light] = !lights[light];
        }
        int[] presses = this.presses.clone();
        log("presses:" + Arrays.toString(presses) + " totalPresses: " + totalPresses());
        presses[button]++;
        log("presses:" + Arrays.toString(presses) + " totalPresses: " + totalPresses());
        return new Machine(target, lights, buttons, presses);
    }
    public String toString() {
        return String.format("Machine[target=%s, lights=%s, buttons=%s, presses=%s", Arrays.toString(target), Arrays.toString(lights), buttons.toString(), Arrays.toString(presses));
    }

}

Machine parse(String line) {
    // log("parse(" + line);
    String[] parts = line.split(" ");
    // log("parts:" + Arrays.toString(parts));
    String[] lights = parts[0].substring(1, parts[0].length()-1).split("");
    // log("lights:" + Arrays.toString(lights));
    boolean[] target = new boolean[lights.length];
    for (int i = 0; i < lights.length; i++) {
        target[i] = lights[i].equals(".") ? false : true;
    }
    // log("target:" + Arrays.toString(target));
    List<Button> buttons = new ArrayList<>();
    for (int i = 1; i < parts.length-1; i++) {
        assert parts[i].charAt(0) == '(';
        String p = parts[i].substring(1, parts[i].length()-1);
        var button = new Button(Arrays.stream(p.split(",")).map(Integer::parseInt).toList());
        buttons.add(button);
    }
    // log("buttons:" + buttons);
    var presses = new int[buttons.size()];
    return new Machine(target, new boolean[target.length], buttons, presses);
}

boolean arrayCompare(boolean[] l, boolean[] r) {
    log("arrayCompare");
    if(l.length != r.length) {
        log("l.length != r.length");
        return false;
    }
    for (int i = 0; i < l.length; i++) {
        log(i);
        if(l[i] != r[i]) {
            log("l[i] != r[i]" + l[i] + r[i]);
            return false;
        }
    }
    return true;
}

long fewestButtonPresses(Machine machine){
    log("fewestButtonPresses( " + machine);
    PriorityQueue<Machine> machines = new PriorityQueue<>();
    Set<Machine> added = new HashSet<>();
    machines.add(machine);
    added.add(machine);
    while(!machines.isEmpty()){
        var current = machines.poll();
        log("current: " + current);
        if(Arrays.equals(current.target, current.lights)) {
            return current.totalPresses();
        }
        log(current);
        for (int i = 0; i < current.presses.length; i++) {
            var next = current.press(i);
            log("next:" + next + " from i: " + i);
            log("next.lights:" + Arrays.toString(next.lights));
            if(added.contains(next)){
                continue;
            }
            added.add(next);
            machines.add(next);
        }

    }
    throw new IllegalStateException("No path to target found");

}

void main() throws Exception {
    List<Machine> machines = Files.lines(Path.of("input.txt"))
        .map(l -> parse(l))
        .toList();

    log(machines);
    long total = 0;
    for (Machine machine : machines) {
        IO.println(machine);
        total += fewestButtonPresses(machine);
    }
    IO.println(total);

}
