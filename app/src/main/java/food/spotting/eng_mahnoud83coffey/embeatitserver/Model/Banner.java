package food.spotting.eng_mahnoud83coffey.embeatitserver.Model;

public class Banner
{
    private String image,name ,id,menuId;

    public Banner() {
    }

    public Banner(String image, String name, String id) {
        this.image = image;
        this.name = name;
        this.id = id;
    }

    public Banner(String image, String name, String id, String menuId) {
        this.image = image;
        this.name = name;
        this.id = id;
        this.menuId = menuId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }





}
