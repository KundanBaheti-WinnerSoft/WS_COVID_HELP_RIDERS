package com.ws.gms.Adapters;

public class ProfileDetails {
    int id, userId;
    String role, name, number, firstName, middleName, lastName, emailId, mobileno, address,area,city,pincode,state,country, imageName;
    Boolean activeDeactive;

    public ProfileDetails() {
    }

    public ProfileDetails(int id, int userId, String role, String name, String number, String firstName, String middleName, String lastName, String emailId, String mobileno, String address, String area, String city, String pincode, String state, String country, String imageName, Boolean activeDeactive) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.name = name;
        this.number = number;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.emailId = emailId;
        this.mobileno = mobileno;
        this.address = address;
        this.area = area;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.country = country;
        this.imageName = imageName;
        this.activeDeactive = activeDeactive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Boolean getActiveDeactive() {
        return activeDeactive;
    }

    public void setActiveDeactive(Boolean activeDeactive) {
        this.activeDeactive = activeDeactive;
    }
}
