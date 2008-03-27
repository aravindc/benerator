package org.databene.domain.address;

import java.util.*;

/**
 * Represents a city.<br/>
 * <br/>
 * Created: 11.06.2006 08:19:23
 */
public class City {

    // Ort;Zusatz;Plz;Vorwahl;Bundesland
    private String name;
    private String nameExtension;
    private SortedSet<String> zipCodes;
    private String phoneCode;
    private State state;
    private int inhabitants;
    private Locale language;

    public City(State state, String name, String addition, Collection<String> zipCodes, String phoneCode) {
        if (phoneCode == null)
            throw new IllegalArgumentException("Phone Code is null for " + name);
        this.state = state;
        this.name = name;
        this.nameExtension = addition;
        this.zipCodes = new TreeSet<String>(zipCodes);
        this.phoneCode = phoneCode;
    }

    public String getNameExtension() {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension) {
        this.nameExtension = nameExtension;
    }

    public Collection<String> getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(Collection<String> zipCodes) {
        this.zipCodes.clear();
        this.zipCodes.addAll(zipCodes);
    }

    public void addZipCode(String zipCode) {
        zipCodes.add(zipCode);
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public int getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(int inhabitants) {
        this.inhabitants = inhabitants;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return name + (nameExtension.length() > 0 && Character.isLetter(nameExtension.charAt(0)) ? " " : "") + nameExtension;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final City city = (City) o;
        if (nameExtension != null ? !nameExtension.equals(city.nameExtension) : city.nameExtension != null)
            return false;
        return name.equals(city.name);

    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + (nameExtension != null ? nameExtension.hashCode() : 0);
        return result;
    }

}
