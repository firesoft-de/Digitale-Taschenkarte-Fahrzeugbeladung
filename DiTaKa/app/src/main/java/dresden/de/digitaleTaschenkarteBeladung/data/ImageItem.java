package dresden.de.digitaleTaschenkarteBeladung.data;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

@Entity(tableName = "image")
public class ImageItem {

    @PrimaryKey
    private int id;

    private String path;
    private int categoryID;

//    @Ignore
//    private Bitmap image;

    public ImageItem(int id, String path, int categoryID) {
        this.id = id;
        this.path = path;
        this.categoryID = categoryID;
    }

    public void setId(int id) {}
    public void setPath(String path) {this.path = path;}
    public void setCategoryID(int categoryID) {this.categoryID = categoryID;}
//    public void setImage(Bitmap bitmap) {this.image = bitmap;}

    public int getId() {return id;}
    public String getPath() {return path;}
    public int getCategoryID() {return categoryID;}


//    public Bitmap getImage() {return image;}
}
