import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("run");

        //TODO maze generator som laddar en maze
        //TODO passa maze till en maze solver, som ett game engine
        //TODO maze solver har metoder för att lösa mazen och returnerar

        var maze = new MazeLoader(new File("src/maze-image.jpg"));
    }

}
