package com.heilov.heilov.Model;

public class User {
    private String name;
    private String email;
    private String profilePic;
    private int age;
    private String gender;
    private String location;
    private String uid;

    public User() {

    }

    public User(String name, String email, String profilePic, String uid) {
        this.age = 0;
        this.gender = "";
        this.location = "";
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
