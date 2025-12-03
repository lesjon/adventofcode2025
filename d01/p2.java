void main() throws Exception {
    var lines = Files.lines(Path.of("input.txt")).toList();
    int total = 0;
    int pos = 50;
    for (int i = 0; i < lines.size(); i++) {
        var line = lines.get(i);
        IO.println(line);
        switch (line.charAt(0)) {
            case 'L' -> {
                int dist = Integer.parseInt(line.substring(1));
                IO.println("dist: "+dist);
                total += dist / 100;
                IO.println("total: "+total);
                int prev_pos = pos;
                IO.println("prev_pos: "+prev_pos);
                pos -= dist%100;
                IO.println("pos: "+pos);
                if(prev_pos > 0 && pos < 0){
                    total++;
                }
                IO.println("total: "+total);
            }
            case 'R' -> {
                int dist = Integer.parseInt(line.substring(1));
                total += dist / 100;
                pos += dist%100;
                if(pos > 100){
                    total++;
                }
            }
            default -> throw new RuntimeException();
        }
        if (pos < 0) {
            pos = 100+pos;
        }
        pos %= 100;
        IO.println("pos: "+pos + " total: " + total);
        if(pos == 0){ 
            total++;
        }  
    }
    IO.println(total);
}

