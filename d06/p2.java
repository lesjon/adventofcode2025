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


void main() throws Exception {
    var lines = Files.lines(Path.of("input.txt"))
        .toList();
    var height = lines.get(0).length();
    var newLines = new ArrayList<String>();
    for (int y = 0; y < height; y++) {
        var sb = new StringBuilder();
        for (int x = 0; x < lines.size(); x++) {
            sb.append(lines.get(x).charAt(y));
        }
        newLines.add(sb.toString());
    }
    List<List<String>> sets = new ArrayList<>();
    sets.add(new ArrayList<>());
    for (String l : newLines) {
        if(l.isBlank()){
            sets.add(new ArrayList<>());
            continue;
        }
        if(l.endsWith("*") || l.endsWith("+")){
            sets.get(sets.size()-1).add(l.substring(0,l.length()-1).trim());
            sets.get(sets.size()-1).add(l.substring(l.length()-1).trim());
            continue;
        }
        sets.get(sets.size()-1).add(l.trim());
    }

    sets.forEach(s -> Collections.sort(s));
    IO.println(sets);

    List<Column> cols = new ArrayList<>();
    for (List<String> set : sets) {
        for (String s : set) {
            if (s.equals("+") || s.equals("*")) {
                cols.add(new Column(s.charAt(0)));
            } else {
                cols.get(cols.size()-1).nextNum(Long.parseLong(s));
            }
        }
    }
    long total = cols.stream().mapToLong(c -> c.num).sum();
    // for (int i = lines.size()-1; i >= 0; i--) {
    //     String[] line = lines.get(i);
    //     IO.println(Arrays.toString(line));
    //     for (int j = 0; j < line.length; j++) {
    //         String s = line[j];
    //         if (i == lines.size()-1) {
    //             cols.add(new Column(s.charAt(0)));
    //         } else {
    //             cols.get(j).nextNum(Long.parseLong(s));
    //         }
    //     }
    // }

    IO.println(total);
}
