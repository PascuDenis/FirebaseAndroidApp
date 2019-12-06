package com.example.db.repository;

import com.example.db.entity.ExperianceLevel;
import com.example.db.entity.Topic;
import com.example.db.entity.TopicNames;
import com.example.db.entity.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements ICRUDRepository<User, String> {
    private DatabaseReference userDatabaseReference;

    public UserRepository(String tablename) {
        this.userDatabaseReference = FirebaseDatabase.getInstance().getReference(tablename);
    }

    @Override
    public User get(String id) {
        return null;
    }

    @Override
    public boolean exist(String id) {
        return false;
    }

    @Override
    public void create(User user) {
        List<String> users = new ArrayList<>();
        List<String> reputations = new ArrayList<>();
        List<Topic> topics = new ArrayList<>();
        user.setEducation("");
        user.setProfilePictureUrl("");
        user.setCityLocation("");
        user.setCountryLocation("");
        user.setNrOfFollowers(0);
        user.setReputationNumber(0);
        user.setFollowersList(users);
        user.setReputationList(reputations);
        user.setTopicsList(topics);
        userDatabaseReference.child(user.getId()).setValue(user);
    }

    @Override
    public void update(User user) {
//        List<Topic> topics = new ArrayList<>();
//        topics.add(new Topic(TopicNames.OOP, ExperianceLevel.EXPERT));
//        topics.add(new Topic(TopicNames.DESIGN_PATTERNS, ExperianceLevel.NOVICE));
//
//        user.setCityLocation("Sighisoara");
//        user.setCountryLocation("Hop Germania");
//        user.setEducation("UBBB");
//        user.setFullName( "DP");
//        user.setUsername("DDPP");
//        user.setProfilePictureUrl("");
//        user.setTopicsList(topics);


        if (user.getCityLocation() != null || !user.getCityLocation().equals("")) {
            userDatabaseReference.child(user.getId()).child("cityLocation").setValue(user.getCityLocation());
        }
        if (user.getCountryLocation() != null || !user.getCountryLocation().equals("")) {
            userDatabaseReference.child(user.getId()).child("countryLocation").setValue(user.getCountryLocation());
        }
        if (user.getEducation() != null || !user.getEducation().equals("")) {
            userDatabaseReference.child(user.getId()).child("education").setValue(user.getEducation());
        }
        if (user.getFullName() != null || !user.getFullName().equals("")) {
            userDatabaseReference.child(user.getId()).child("fullName").setValue(user.getFullName());
        }
        if (user.getUsername() != null || !user.getUsername().equals("")) {
            userDatabaseReference.child(user.getId()).child("username").setValue(user.getUsername());
        }
        if (user.getTopicsList().size() != 0) {
            userDatabaseReference.child(user.getId()).child("topicsList").setValue(user.getTopicsList());
        }
    }

    public void updateUserProfilePicture(User user) {
        userDatabaseReference.child(user.getId()).child("profilePictureUrl").setValue(user.getProfilePictureUrl());
    }

}
