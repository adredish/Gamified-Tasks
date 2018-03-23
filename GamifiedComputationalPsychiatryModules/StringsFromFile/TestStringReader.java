package StringsFromFile;

class TestStringReader {

    public static void main(String[] args) throws Exception {
        ReadStringsFromFile srfp = new ReadStringsFromFile("C:\\Users\\adredish\\IdeaProjects\\WebSurfMain.WebSurf\\src\\StringsFromFile\\stringstest.txt");

        System.out.println(srfp.keySet());

        System.out.println(srfp.get("splashTEXT"));


    }
}
