class Column{
    char op;
    long num;
    Column(char op){
        this.op = op;
        if(op == '*'){
            this.num = 1;
        }else{
            this.num = 0;
        }
    }

    void nextNum(long num){
        IO.println("nextNum( " + num + ") this.num: " + this.num + " this.op" + this.op);
        switch (op) {
            case '*' -> this.num *= num;
            case '+' -> this.num += num;
            default -> throw new RuntimeException();
        }
    }

}

List<Column> cols = new ArrayList<>();

void main() throws Exception {
    var lines = Files.lines(Path.of("input.txt"))
        .map(s -> s.trim().split("\s+"))
        .toList();
    for (int i = lines.size()-1; i >= 0; i--) {
        String[] line = lines.get(i);
        IO.println(Arrays.toString(line));
        for (int j = 0; j < line.length; j++) {
            String s = line[j];
            if (i == lines.size()-1) {
                cols.add(new Column(s.charAt(0)));
            } else {
                cols.get(j).nextNum(Long.parseLong(s));
            }
        }
    }
    long total = cols.stream().mapToLong(c -> c.num).sum();

    IO.println(total);
}
