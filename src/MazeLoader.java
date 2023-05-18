import java.io.File;

public class MazeLoader {

    public int[][] loadMaze(File mazeImage) {
        return new int[5][5];
    }

    private void processImage(File mazeImage) {
        // TODO ide:
        // Ta bort vita mellanrummet utanför labyrinten genom att räkna antal rader och kolumner tills den slår i svart
        // Ignorera rött och grönt
        // Ta resultatlabyrinten med svart border och downscalea så att pathsen bara blir 1 pixel och väggar 1 pixel
        // Generera maze som matrix utifrån deta
        // Gör funktionalitet för att markera start och finish cell och låt användaren sätta ut start och finish i GUI
    }

}
