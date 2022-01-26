import java.util.Objects;

/**
 * Class Property.
 * The property can share municipalityNr, lotNr, sectionNr with other properties.
 * Therefore, I have chosen multiple variables who together become the unique identifier.
 * This is because a property can not share municipalityNr, lotNr and sectionNr with another property.
 */

public class Property {
    private final int municipalityNr;
    private final String municipalityName;
    private final int lotNr;
    private final int sectionNr;
    private String name;  // name of Property, can be blank
    private float area;
    private String ownerName;

    /**
     * Creates an instance of class Property
     * @param municipalityNr int
     * @param municipalityName String
     * @param lotNr int
     * @param sectionNr int
     * @param name String, name of property can be blank
     * @param area float
     * @param ownerName String
     * @throws IllegalArgumentException if municipalityName is empty
     * @throws IllegalArgumentException if area is not greater than 0
     * @throws IllegalArgumentException if ownerName is empty
     */
    public Property(int municipalityNr, String municipalityName, int lotNr, int sectionNr, String name, float area,
                    String ownerName) {
        this.municipalityNr = municipalityNr;
        if (municipalityName.isBlank())
            throw new IllegalArgumentException("Municipality must have a name");
        else
            this.municipalityName = municipalityName;
        this.lotNr = lotNr;
        this.sectionNr = sectionNr;
        this.name = name;
        if (area < 0)
            throw new IllegalArgumentException("Area must be greater than 0");
        else
            this.area = area;
        if (ownerName.isBlank())
            throw new IllegalArgumentException("Owner must have a name");
        else
            this.ownerName = ownerName;
    }

    // getters
    public int getMunicipalityNr() {
        return municipalityNr;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public int getLotNr() {
        return lotNr;
    }

    public int getSectionNr() {
        return sectionNr;
    }

    public String getName() {
        return name;
    }

    public float getArea() {
        return area;
    }

    public String getOwnerName() {
        return ownerName;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Generates a String in the following format:
     *   "municipalityNr-lotNr/sectionNr"
     *   "1445-54/73"
     * @return String
     */
    public String getID() {
        return municipalityNr + "-" + lotNr + "/" + sectionNr;
    }

    /**
     * toString
     * @return String
     */
    @Override
    public String toString() {
        return "Property{" +
                "municipalityNr=" + municipalityNr +
                ", municipalityName='" + municipalityName + '\'' +
                ", lotNr=" + lotNr +
                ", sectionNr=" + sectionNr +
                ", name='" + name + '\'' +
                ", area=" + area +
                ", ownerName='" + ownerName + '\'' +
                '}' + "\n";
    }

    /**
     * Checks if two objects of Property are equal
     * @param o Object, object to be checked for equality
     * @return boolean, true if the objects are equal and vice versa
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property that = (Property) o;
        return municipalityNr == that.getMunicipalityNr() && lotNr == that.getLotNr() && sectionNr == that.getSectionNr();
    }
}
