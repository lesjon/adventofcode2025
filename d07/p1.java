record Location(int x, int y){}

void main() throws Exception {
    var lines = Files.lines(Path.of("input.txt"))
        .toList();
    Set<Location> splitters = new HashSet<>();
    Set<Integer> beams = new HashSet<>();
    long total = 0;
    for (int y = 0; y < lines.size(); y++) {
        String line = lines.get(y);
        if(beams.isEmpty()) {
            for (int x = 0; x < line.length(); x++) {
                if(line.charAt(x) == 'S') { 
                    IO.println("start: " + x + ", " + y);
                    beams.add(x);
                }
            }
        }
        Set<Integer> nextBeams = new HashSet<>();
        for(int beam : beams) {
            if(line.charAt(beam) == '^'){
                total++;
                nextBeams.add(beam-1);
                nextBeams.add(beam+1);
            } else {
                nextBeams.add(beam);
            }
        }
        beams = nextBeams;
        showBeams(beams);
    }

    IO.println(total);
}

void showBeams(Set<Integer> beams){
    var sb = new StringBuilder();
    var maxBeam = beams.stream().mapToInt(i -> i).max().getAsInt();
    for(int i = 0; i <= maxBeam; i++){
        if(beams.contains(i)){
            sb.append("|");
        }else{
            sb.append(" ");
        }
    }
    IO.println(sb.toString());
}


