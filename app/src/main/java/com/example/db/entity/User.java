package com.example.db.entity;

import java.util.List;
import java.util.Objects;

public class User {
    private String id;
    private String email;
    private String username;
    private String password;
    private String fullName;
    private String education;
    private String profilePictureUrl;
    private String cityLocation;
    private String countryLocation;
    private Integer nrOfFollowers;
    private Integer reputationNumber;
    private List<String> followersList;
    private List<Topic> topicsList;

    public User() {
    }

    public User(String id, String profilePictureUrl) {
        this.id = id;
        this.profilePictureUrl = profilePictureUrl;
    }

    public User(String id, String fullName, String username, String email) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
    }

    public User(String id,
                String fullName,
                String username,
                String email,
                String cityLocation,
                String countryLocation,
                String education,
                String profilePictureUrl,
                Integer nrOfFollowers,
                Integer nrOfFollowing,
                List<String> followersList,
                List<Topic> topicsList) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.education = education;
        this.profilePictureUrl = profilePictureUrl;
        this.cityLocation = cityLocation;
        this.countryLocation = countryLocation;
        this.nrOfFollowers = nrOfFollowers;
        this.reputationNumber = nrOfFollowing;
        this.followersList = followersList;
        this.topicsList = topicsList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getCityLocation() {
        return cityLocation;
    }

    public void setCityLocation(String cityLocation) {
        this.cityLocation = cityLocation;
    }

    public String getCountryLocation() {
        return countryLocation;
    }

    public void setCountryLocation(String countryLocation) {
        this.countryLocation = countryLocation;
    }

    public Integer getNrOfFollowers() {
        return nrOfFollowers;
    }

    public void setNrOfFollowers(Integer nrOfFollowers) {
        this.nrOfFollowers = nrOfFollowers;
    }

    public Integer getReputationNumber() {
        return reputationNumber;
    }

    public void setReputationNumber(Integer reputationNumber) {
        this.reputationNumber = reputationNumber;
    }

    public List<String> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(List<String> followersList) {
        this.followersList = followersList;
    }

    public List<Topic> getTopicsList() {
        return topicsList;
    }

    public void setTopicsList(List<Topic> topicsList) {
        this.topicsList = topicsList;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", cityLocation='" + cityLocation + '\'' +
                ", countryLocation='" + countryLocation + '\'' +
                ", nrOfFollowers=" + nrOfFollowers +
                ", reputationNumber=" + reputationNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
