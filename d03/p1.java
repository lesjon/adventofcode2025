void main() throws Exception {

    var lines = Files.lines(Path.of("input.txt"));
    long total = lines.mapToLong(l -> maximumJolts(l))
        .sum();
    IO.println(total);
}

static long maximumJolts(String line){
    IO.println("maximumJolts" + line);
    int max1 = 0, max2 = 0;
    int max1Index = -1;
    for (int i = 0; i < line.length()-1; i++) {
        if(line.charAt(i) > max1){
            max1 = line.charAt(i);
            max1Index = i;
        }
        if (max1 == '9') break;
    }

    for (int i = max1Index+1; i < line.length(); i++) {
        if(line.charAt(i) > max2){
            max2 = line.charAt(i);
        }
        if (max2 == '9') break;
    }
    IO.println("max1,max2: " + (char) max1 + ", " +(char) max2);
    return (max1 - '0')*10 + (max2 - '0');
}
