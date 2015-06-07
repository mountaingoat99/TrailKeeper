package models;

/**
 * Created by Jeremey on 6/2/2015.
 */
import java.util.Date;

public class DemoModel {
    private static int nextId = 0;
    public String label;
    public String pathToImage;
    public int id = ++nextId;
}