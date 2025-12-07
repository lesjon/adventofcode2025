void main() throws Exception {
    var lines = Files.lines(Path.of("input.txt"))
        .toList();
    Map<Integer, Long> beams = new HashMap<>();
    for (int y = 0; y < lines.size(); y++) {
        String line = lines.get(y);
        if(beams.isEmpty()) {
            for (int x = 0; x < line.length(); x++) {
                if(line.charAt(x) == 'S') { 
                    IO.println("start: " + x + ", " + y);
                    beams.put(x, 1L);
                }else{
                    beams.put(x, 0L);
                }
            }
        }
        Map<Integer, Long> nextBeams = new HashMap<>(beams);
        for(int beam : beams.keySet()) {
            if(line.charAt(beam) == '^'){
                nextBeams.merge(beam-1, beams.get(beam), (i,j) -> i+j);
                nextBeams.merge(beam+1, beams.get(beam), (i,j) -> i+j);
                nextBeams.put(beam, 0L);
            }
        }
        beams = nextBeams;
        showBeams(beams);
    }
    IO.println(" total: " + beams.values().stream().mapToLong(i -> i).sum());
}

void showBeams(Map<Integer, Long> beams){
    var sb = new StringBuilder();
    int maxBeam = beams.keySet().stream().mapToInt(i->i).max().getAsInt();
    for(int x = 0; x <= maxBeam; x++){
        sb.append((char) ('0' + beams.get(x)));
    }

    IO.println(sb.toString());
}


