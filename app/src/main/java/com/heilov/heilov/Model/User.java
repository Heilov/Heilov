package com.heilov.heilov.Model;

import android.content.Context;

import com.heilov.heilov.Utils.EmailNotifier;
import com.heilov.heilov.Utils.InAppNotifier;
import com.heilov.heilov.Utils.Observable;
import com.heilov.heilov.Utils.Observer;

import java.util.ArrayList;
import java.util.List;

public class User implements Observable {
    private String name;
    private String email;
    private String profilePic;
    private int age;
    private String gender;
    private String location;
    private String uid;
    private ArrayList<Observer> observerList;

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



    @Override
    public void attachObserver(Observer o) {
        if(observerList == null){
            observerList = new ArrayList<>();
        }
        observerList.add(o);
    }

    @Override
    public void deattachObserver(Observer o) {
        observerList.remove(o);
    }

    @Override
    public void notify(Context context, String message) {
        for (Observer o : observerList) {
            o.update(context, message);
        }
    }
}
