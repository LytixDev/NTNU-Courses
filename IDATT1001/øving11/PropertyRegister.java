import java.util.ArrayList;

/**
 * Class PropertyRegister.
 * This class purpose is to hold properties in an ArrayList and act as an interface for the client
 * I chose to use an ArrayList as it's mutable, and therefore I can add elements to it without dealing with
 * creating a new table with a larger size.
 */
public class PropertyRegister {
    private ArrayList<Property> propertyRegister;

    /**
     * Creates an instance of PropertyRegister
     */
    public PropertyRegister() {
        propertyRegister = new ArrayList<>();
    }

    /**
     * Creates a new property given the parameters and adds it to the PropertyRegister ArrayList
     * @param municipalityNr int
     * @param municipalityName String
     * @param lotNr int
     * @param sectionNr int
     * @param name String
     * @param area float
     * @param ownerName String
     */
    public void addProperty(int municipalityNr, String municipalityName, int lotNr, int sectionNr, String name,
                            float area, String ownerName) {
        Property toAdd = new Property(municipalityNr, municipalityName, lotNr, sectionNr, name, area, ownerName);
        if (!propertyRegister.contains(toAdd))
            propertyRegister.add(toAdd);
    }

    /**
     * Loops over the properties to find the average area
     * @return float, average area
     */
    public float avgArea() {
        float totalArea = 0f;
        for (Property property : propertyRegister) {
            totalArea += property.getArea();
        }
        return  totalArea / propertyRegister.size();
    }

    /**
     * Finds a property given the three class variables of property to find uniqueness.
     * Instead of returning the Property object, I instead return the String representation.
     * This is because the client has no use for the Property object, and only requires the String representation.
     * This minimizes coupling by not having the Client class connect with the Property class.
     * @param municipalityNr int
     * @param lotNr int
     * @param sectionNr int
     * @return String, String representation of the found property
     */
    public String findProperty(int municipalityNr, int lotNr, int sectionNr) {
        for (Property property : propertyRegister) {
            if (property.getMunicipalityNr() == municipalityNr && property.getLotNr() == lotNr &&
            property.getSectionNr() == sectionNr) {

                //Property found = new Property(property.getMunicipalityNr(), property.getMunicipalityName(),
                //        property.getLotNr(), property.getSectionNr(), property.getName(), property.getArea(),
                //        property.getOwnerName());
                return property.toString();
            }
        }

        return "Property not found";
    }

    /**
     * Finds all properties that match the given lotNr.
     * Could also return an ArrayList that contain the properties, however same is in findProperty method, I
     * argue it's unnecessary as the client only needs to see the String representation.
     * @param lotNr int
     * @return String, will be set to "Found no properties" if out is blank.
     */
    public String findPropertyByLotNr(int lotNr) {
        String out = "";
        for (Property property : propertyRegister) {
            if (property.getLotNr() == lotNr)
                out += property + "\n";
        }
        return (out.isBlank()) ? "Found no properties" : out;
    }

    /**
     * Removes a property from the propertyRegister
     * @param municipalityNr int
     * @param lotNr int
     * @param sectionNr int
     */
    public void removeProperty(int municipalityNr, int lotNr, int sectionNr) {
        Property toRemove = null;
        for (Property property : propertyRegister) {
           if (property.getMunicipalityNr() == municipalityNr && property.getLotNr() == lotNr &&
                   property.getSectionNr() == sectionNr) {
               toRemove = property;
               break;
           }
       }
        if (toRemove != null)
            propertyRegister.remove(toRemove);
    }

    /**
     * @return int, size of propertyRegister
     */
    public int getAmountProperties() {
        return propertyRegister.size();
    }

    @Override
    public String toString() {
        return "PropertyRegister{" +
                "propertyRegister=" + propertyRegister +
                '}';
    }
}
