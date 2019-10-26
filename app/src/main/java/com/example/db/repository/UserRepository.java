package com.example.db.repository;

import androidx.annotation.NonNull;

import com.example.db.entity.ExperianceLevel;
import com.example.db.entity.Topic;
import com.example.db.entity.TopicNames;
import com.example.db.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        List<User> users = new ArrayList<>();
        List<Topic> topics = new ArrayList<>();

        users.add(new User("id", "full name", "email", "username"));
        users.add(new User("ID", "FULL NAME", "EMAIL", "USERNAME"));
        topics.add(new Topic(TopicNames.DATA_STRUCTURE, ExperianceLevel.COMPETENT));
        topics.add(new Topic(TopicNames.OOP, ExperianceLevel.BEGINNER));

        user.setEducation("");
        user.setProfilePictureUrl("");
        user.setCityLocation("");
        user.setCountryLocation("");
        user.setNrOfFollowers(0);
        user.setNrOfFollowing(0);
        user.setFollowersList(users);
        user.setTopicsList(topics);
        userDatabaseReference.child(user.getId()).setValue(user);
    }

    @Override
    public void update(User user) {

        User u;
        u = user;

        System.out.println(user.getId() + "---------------------------!!!!!!!");

        userDatabaseReference.
                child(user.getId()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapShot : dataSnapshot.getChildren()) {
                            switch (Objects.requireNonNull(singleSnapShot.getKey())) {
                                case "cityLocation":
                                    String cityLocation = (String) singleSnapShot.getValue();
                                    if (u.getCityLocation().equals("") || u.getCityLocation() == null)
                                        u.setCityLocation("");
                                    if (cityLocation == null || cityLocation.equals("") || !cityLocation.equals(u.getCityLocation()))
                                        u.setCityLocation((String) singleSnapShot.getValue());
                                    break;
                                case "countryLocation":
                                    String countryLocation = (String) singleSnapShot.getValue();
                                    if (u.getCountryLocation().equals("") || u.getCountryLocation() == null)
                                        u.setCountryLocation("");
                                    if (countryLocation == null || countryLocation.equals("") || !countryLocation.equals(u.getCountryLocation()) || !u.getCountryLocation().equals(""))
                                        u.setCountryLocation(countryLocation);
                                    break;
                                case "education":
                                    String education = (String) singleSnapShot.getValue();
                                    if (u.getEducation().equals("") || u.getEducation() == null)
                                        u.setEducation("");
                                    if (education == null || education.equals("") || !education.equals(u.getEducation()) || !u.getEducation().equals(""))
                                        u.setEducation(education);
                                    break;
                                case "fullName":
                                    String fullName = (String) singleSnapShot.getValue();
                                    if (u.getFullName().equals("") || u.getEducation() == null)
                                        u.setEducation("");
                                    if (fullName == null || fullName.equals("") || !fullName.equals(u.getFullName()) || !u.getFullName().equals(""))
                                        u.setFullName(fullName);
                                    break;
                                case "nrOfFollowers":
                                    Integer nrOfFollowers = (Integer) singleSnapShot.getValue();
                                    if (u.getNrOfFollowers() == 0 || u.getNrOfFollowers() == null)
                                        u.setNrOfFollowers(0);
                                    if (nrOfFollowers == null || nrOfFollowers == 0 || !nrOfFollowers.equals(u.getNrOfFollowers()) || u.getNrOfFollowers() != 0)
                                        u.setNrOfFollowers(nrOfFollowers);
                                    break;
                                case "nrOfFollowing":
                                    Integer nrOfFollowing = (Integer) singleSnapShot.getValue();
                                    if (u.getNrOfFollowing() == 0 || u.getNrOfFollowing() == null)
                                        u.setNrOfFollowing(0);
                                    if (nrOfFollowing == null || nrOfFollowing == 0 || !nrOfFollowing.equals(u.getNrOfFollowing()) || u.getNrOfFollowing() != 0)
                                        u.setNrOfFollowers(nrOfFollowing);
                                    break;
                                case "profilePictureUrl":
                                    String profilePictureUrl = (String) singleSnapShot.getValue();
                                    if (u.getProfilePictureUrl().equals("") || u.getProfilePictureUrl() == null)
                                        u.setProfilePictureUrl("");
                                    if (profilePictureUrl == null || profilePictureUrl.equals("") || !profilePictureUrl.equals(u.getProfilePictureUrl()) || !u.getProfilePictureUrl().equals(""))
                                        u.setProfilePictureUrl(profilePictureUrl);
                                    break;
                                case "username":
                                    String username = (String) singleSnapShot.getValue();
                                    if (u.getUsername().equals("") || u.getUsername() == null)
                                        u.setUsername("");
                                    if (username == null || username.equals("") || !username.equals(u.getUsername()) || !u.getUsername().equals(""))
                                        u.setUsername(username);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getId());
        userDatabaseReference.setValue(u);
    }
}
