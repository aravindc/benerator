package org.databene.domain.address;

import org.databene.region.Country;
import org.databene.region.PhoneNumber;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 11.06.2006 08:05:00
 *
 */
public class Address {

    public String street;
    public String houseNumber;
    public String zipCode;
    public String city;
    public Country country;
    public PhoneNumber privatePhone;
    public PhoneNumber officePhone;
    public PhoneNumber mobilePhone;
    public PhoneNumber fax;

    public Address() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public Address(String street, String houseNumber, String zipCode, String city, Country country, PhoneNumber privatePhone, PhoneNumber officePhone, PhoneNumber mobilePhone, PhoneNumber fax) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.privatePhone = privatePhone;
        this.officePhone = officePhone;
        this.mobilePhone = mobilePhone;
        this.fax = fax;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public PhoneNumber getPrivatePhone() {
        return privatePhone;
    }

    public void setPrivatePhone(PhoneNumber privatePhone) {
        this.privatePhone = privatePhone;
    }

    public PhoneNumber getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(PhoneNumber officePhone) {
        this.officePhone = officePhone;
    }

    public PhoneNumber getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(PhoneNumber mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public PhoneNumber getFax() {
        return fax;
    }

    public void setFax(PhoneNumber fax) {
        this.fax = fax;
    }

    public String toString() {
        return street + " " + houseNumber + ", " + zipCode + " " + city + ", " + country
                + " (private:" + privatePhone + ", work:" + officePhone + ", mobile:" + mobilePhone + ", fax:" + fax + ')';
    }
}
