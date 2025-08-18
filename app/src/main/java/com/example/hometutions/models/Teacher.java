package com.example.hometutions.models;

public class Teacher {
    private String id;
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String phoneNumber;
    private String address;
    private String subjects;
    private Object experience; // Can be String or Long
    private String qualification;
    private String location;
    private Object hourlyRate; // Can be String or Long
    private String rating;
    private String profileImageUrl;
    private String bio;
    private boolean isVerified;
    private String availability;
    
    // Legacy fields for compatibility
    private int age;
    private String gender;
    private String highestQualification;
    private String institution;
    private int yearsOfExperience;
    private java.util.List<String> subjectsTaught;
    private java.util.List<String> teachingStreams;
    private String aadharCardUrl;
    private String panCardUrl;
    private String degreeCertificateUrl;
    private boolean documentsVerified;
    
    // Additional fields that Firebase expects
    private String degree_image;
    private String aadhar_image;
    private String pan_image;
    private String userType;
    private Object createdAt; // Can be String or Long

    // Default constructor
    public Teacher() {
    }

    // Constructor with parameters
    public Teacher(String id, String fullName, String email, String phone, String subjects, 
                   String experience, String qualification, String location, String hourlyRate) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.subjects = subjects;
        this.experience = experience;
        this.qualification = qualification;
        this.location = location;
        this.hourlyRate = hourlyRate;
        this.rating = "4.5"; // Default rating
        this.isVerified = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public String getExperience() {
        if (experience instanceof Long) {
            return String.valueOf(experience);
        }
        return experience != null ? experience.toString() : "0";
    }

    public void setExperience(Object experience) {
        this.experience = experience;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHourlyRate() {
        if (hourlyRate instanceof Long) {
            return String.valueOf(hourlyRate);
        }
        return hourlyRate != null ? hourlyRate.toString() : "0";
    }

    public void setHourlyRate(Object hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    // Legacy getters and setters for compatibility
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getHighestQualification() {
        return highestQualification;
    }

    public void setHighestQualification(String highestQualification) {
        this.highestQualification = highestQualification;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public java.util.List<String> getSubjectsTaught() {
        return subjectsTaught;
    }

    public void setSubjectsTaught(java.util.List<String> subjectsTaught) {
        this.subjectsTaught = subjectsTaught;
    }

    public java.util.List<String> getTeachingStreams() {
        return teachingStreams;
    }

    public void setTeachingStreams(java.util.List<String> teachingStreams) {
        this.teachingStreams = teachingStreams;
    }

    public String getAadharCardUrl() {
        return aadharCardUrl;
    }

    public void setAadharCardUrl(String aadharCardUrl) {
        this.aadharCardUrl = aadharCardUrl;
    }

    public String getPanCardUrl() {
        return panCardUrl;
    }

    public void setPanCardUrl(String panCardUrl) {
        this.panCardUrl = panCardUrl;
    }

    public String getDegreeCertificateUrl() {
        return degreeCertificateUrl;
    }

    public void setDegreeCertificateUrl(String degreeCertificateUrl) {
        this.degreeCertificateUrl = degreeCertificateUrl;
    }

    public boolean isDocumentsVerified() {
        return documentsVerified;
    }

    public void setDocumentsVerified(boolean documentsVerified) {
        this.documentsVerified = documentsVerified;
    }

    // Additional getters and setters for Firebase compatibility
    public String getDegree_image() {
        return degree_image;
    }

    public void setDegree_image(String degree_image) {
        this.degree_image = degree_image;
    }

    public String getAadhar_image() {
        return aadhar_image;
    }

    public void setAadhar_image(String aadhar_image) {
        this.aadhar_image = aadhar_image;
    }

    public String getPan_image() {
        return pan_image;
    }

    public void setPan_image(String pan_image) {
        this.pan_image = pan_image;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCreatedAt() {
        if (createdAt instanceof Long) {
            return String.valueOf(createdAt);
        }
        return createdAt != null ? createdAt.toString() : "";
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }
}
