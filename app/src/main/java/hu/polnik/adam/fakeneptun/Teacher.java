package hu.polnik.adam.fakeneptun;

public class Teacher {
    private String id;
    private String name;
    private String info;
    private String organization;
    private float rateInfo;
    private int imageResource;

    public Teacher(String name, String info, String organization, float rateInfo, int imageResource) {
        this.name = name;
        this.info = info;
        this.organization = organization;
        this.rateInfo = rateInfo;
        this.imageResource = imageResource;
    }

    public Teacher() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public String getOrganization() {
        return organization;
    }

    public float getRateInfo() {
        return rateInfo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
