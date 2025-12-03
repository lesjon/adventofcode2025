void main() throws Exception {

    var lines = Files.lines(Path.of("input.txt"));
    long total = lines.mapToLong(l -> maximumJolts(l))
        .sum();
    IO.println(total);
}

static long maximumJolts(String line){
    IO.println("maximumJolts(" + line);
    long total = 0;
    int prevIndex = -1;
    for (int j = 11; j >= 0; j--) {
        int m = 0;
        for (int i = prevIndex+1; i < line.length()-j; i++) {
            if(line.charAt(i) > m){
                m = line.charAt(i);
                prevIndex = i;
            }
            if (m == '9') break;
        }
        total *= 10;
        total += (m - '0');
        IO.println("total: " + total);
    }
    return total;
}
