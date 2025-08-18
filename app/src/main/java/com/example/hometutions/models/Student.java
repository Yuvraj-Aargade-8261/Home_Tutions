package com.example.hometutions.models;

import java.util.List;
import java.util.Map;

public class Student extends User {
    private int age;
    private String gender;
    private String currentClass;
    private String board;
    private String schoolName;
    private String parentContact;
    private List<String> subjectsNeeded;
    private List<String> tuitionStreams;
    private String preferredTeacherGender;
    private int minBudget;
    private int maxBudget;
    private String preferredTimeSlot;
    private String additionalRequirements;

    public Student() {
        super();
        setUserType("student");
    }

    public Student(String userId, String email, String fullName, String phoneNumber, String address) {
        super(userId, email, fullName, phoneNumber, address, "student");
    }

    // Getters and Setters
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCurrentClass() { return currentClass; }
    public void setCurrentClass(String currentClass) { this.currentClass = currentClass; }

    public String getBoard() { return board; }
    public void setBoard(String board) { this.board = board; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getParentContact() { return parentContact; }
    public void setParentContact(String parentContact) { this.parentContact = parentContact; }

    public List<String> getSubjectsNeeded() { return subjectsNeeded; }
    public void setSubjectsNeeded(List<String> subjectsNeeded) { this.subjectsNeeded = subjectsNeeded; }

    public List<String> getTuitionStreams() { return tuitionStreams; }
    public void setTuitionStreams(List<String> tuitionStreams) { this.tuitionStreams = tuitionStreams; }

    public String getPreferredTeacherGender() { return preferredTeacherGender; }
    public void setPreferredTeacherGender(String preferredTeacherGender) { this.preferredTeacherGender = preferredTeacherGender; }

    public int getMinBudget() { return minBudget; }
    public void setMinBudget(int minBudget) { this.minBudget = minBudget; }

    public int getMaxBudget() { return maxBudget; }
    public void setMaxBudget(int maxBudget) { this.maxBudget = maxBudget; }

    public String getPreferredTimeSlot() { return preferredTimeSlot; }
    public void setPreferredTimeSlot(String preferredTimeSlot) { this.preferredTimeSlot = preferredTimeSlot; }

    public String getAdditionalRequirements() { return additionalRequirements; }
    public void setAdditionalRequirements(String additionalRequirements) { this.additionalRequirements = additionalRequirements; }
}
