package phdhtl.khoa63.foodapp.Domain;

public class Location {
    private int Id;
    private String Loc;

    public Location(int id, String loc) {
        Id = id;
        Loc = loc;
    }

    @Override
    public String toString() {
        return Loc;
    }

    public Location() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLoc() {
        return Loc;
    }

    public void setLoc(String loc) {
        Loc = loc;
    }
}
