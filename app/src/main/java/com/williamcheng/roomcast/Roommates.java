package com.williamcheng.roomcast;
import java.util.ArrayList;
import java.util.List;

public class Roommates {
    private String name;
    private String joinCode;
    private String leaderUID;
    private List<String> usersUID;

    public Roommates(String name, String leaderUID) {
        this.name = name;
        this.leaderUID = leaderUID;
        usersUID = new ArrayList<>();
    }

    public Roommates() { }

    public void generateJoinCode() {
        StringBuilder newCode = new StringBuilder();

        /*Generate a random code of length 6 using 62 possible characters of
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

    public String getLeaderUID() {
        return leaderUID;
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

    public void setLeaderUID(String leaderUID) {
        this.leaderUID = leaderUID;
    }

    public void setUsersUID(List<String> usersUID) {
        this.usersUID = usersUID;
    }
}