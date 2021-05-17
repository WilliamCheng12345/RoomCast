package com.williamcheng.roomcast.classes;
import java.util.ArrayList;
import java.util.List;

public class Roommates {
    private String name, joinCode;
    private List<String> usersUID = new ArrayList<>();

    public Roommates(String name) {
        this.name = name;
        generateJoinCode();
    }

    public Roommates() { }

    public void generateJoinCode() {
        StringBuilder newCode = new StringBuilder();

        /*Generate a random code of length 7 using 62 possible characters of
        26 uppercase letters, 26 lowercase letters and 10 digits.*/
        for(int i = 0; i < 7; i++) {
            int randomCharacter = (int)(Math.random()*62);

            if(randomCharacter <= 9) {
                randomCharacter += 48;
            }
            else if(randomCharacter <= 35) {
                randomCharacter += 55;
            }
            else {
                randomCharacter += 61;
            }

            newCode.append((char)randomCharacter);
        }

        joinCode = newCode.toString();
    }

    public void addUser(String uid) {
        usersUID.add(uid);
    }

    public String getName() {
        return name;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public List<String> getUsersUID() {
        return usersUID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public void setUsersUID(List<String> usersUID) {
        this.usersUID = usersUID;
    }
}
