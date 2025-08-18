# Firebase Realtime Database Schema

This document describes the database structure for the HomeTutions application using Firebase Realtime Database.

## Database Structure Overview

```
hometutions-db/
├── users/                    # Basic user information
│   └── {userId}/
│       ├── userId
│       ├── email
│       ├── fullName
│       ├── phoneNumber
│       ├── address
│       ├── userType          # "student" or "teacher"
│       ├── profilePhotoUrl
│       ├── createdAt
│       └── isVerified
│
├── students/                 # Student-specific information
│   └── {userId}/
│       ├── userId            # Reference to users table
│       ├── age
│       ├── gender
│       ├── currentClass
│       ├── board
│       ├── schoolName
│       ├── parentContact
│       ├── subjectsNeeded    # Array of subjects
│       ├── tuitionStreams    # Array of streams
│       ├── preferredTeacherGender
│       ├── minBudget
│       ├── maxBudget
│       ├── preferredTimeSlot
│       └── additionalRequirements
│
├── teachers/                 # Teacher-specific information
│   └── {userId}/
│       ├── userId            # Reference to users table
│       ├── age
│       ├── gender
│       ├── highestQualification
│       ├── institution
│       ├── yearsOfExperience
│       ├── subjectsTaught    # Array of subjects
│       ├── teachingStreams   # Array of streams
│       ├── aadharCardUrl
│       ├── panCardUrl
│       ├── degreeCertificateUrl
│       ├── documentsVerified
│       ├── hourlyRate
│       ├── availability
│       └── bio
│
├── sessions/                 # Tuition sessions (future feature)
│   └── {sessionId}/
│       ├── sessionId
│       ├── studentId
│       ├── teacherId
│       ├── subject
│       ├── startTime
│       ├── endTime
│       ├── status            # "scheduled", "ongoing", "completed", "cancelled"
│       ├── rate
│       └── notes
│
├── messages/                 # Chat messages (future feature)
│   └── {messageId}/
│       ├── messageId
│       ├── senderId
│       ├── receiverId
│       ├── content
│       ├── timestamp
│       ├── isRead
│       └── messageType       # "text", "image", "document"
│
└── notifications/            # Push notifications (future feature)
    └── {notificationId}/
        ├── notificationId
        ├── userId
        ├── title
        ├── message
        ├── timestamp
        ├── isRead
        └── type              # "session", "message", "system"
```

## Data Models

### User Model
```json
{
  "userId": "string",
  "email": "string",
  "fullName": "string",
  "phoneNumber": "string",
  "address": "string",
  "userType": "string",
  "profilePhotoUrl": "string",
  "createdAt": "number",
  "isVerified": "boolean",
  "additionalData": "object"
}
```

### Student Model
```json
{
  "userId": "string",
  "age": "number",
  "gender": "string",
  "currentClass": "string",
  "board": "string",
  "schoolName": "string",
  "parentContact": "string",
  "subjectsNeeded": ["string"],
  "tuitionStreams": ["string"],
  "preferredTeacherGender": "string",
  "minBudget": "number",
  "maxBudget": "number",
  "preferredTimeSlot": "string",
  "additionalRequirements": "string"
}
```

### Teacher Model
```json
{
  "userId": "string",
  "age": "number",
  "gender": "string",
  "highestQualification": "string",
  "institution": "string",
  "yearsOfExperience": "number",
  "subjectsTaught": ["string"],
  "teachingStreams": ["string"],
  "aadharCardUrl": "string",
  "panCardUrl": "string",
  "degreeCertificateUrl": "string",
  "documentsVerified": "boolean",
  "hourlyRate": "number",
  "availability": "string",
  "bio": "string"
}
```

## Field Descriptions

### Common Fields
- **userId**: Unique identifier from Firebase Authentication
- **email**: User's email address (unique)
- **fullName**: User's full name
- **phoneNumber**: Contact phone number
- **address**: Complete address
- **profilePhotoUrl**: URL to profile photo in Firebase Storage
- **createdAt**: Timestamp when user was created
- **isVerified**: Whether the user account is verified

### Student-Specific Fields
- **age**: Student's age (5-100)
- **gender**: Gender (Male/Female/Other)
- **currentClass**: Current academic class/standard
- **board**: Educational board (CBSE/ICSE/State Board)
- **schoolName**: Name of current school/college
- **parentContact**: Parent/guardian contact number
- **subjectsNeeded**: Array of subjects requiring help
- **tuitionStreams**: Array of academic streams
- **preferredTeacherGender**: Preferred teacher gender
- **minBudget**: Minimum monthly budget
- **maxBudget**: Maximum monthly budget
- **preferredTimeSlot**: Preferred time for tuition
- **additionalRequirements**: Any additional requirements

### Teacher-Specific Fields
- **age**: Teacher's age (18-100)
- **gender**: Gender (Male/Female/Other)
- **highestQualification**: Highest educational qualification
- **institution**: Institution where qualification was obtained
- **yearsOfExperience**: Years of teaching experience
- **subjectsTaught**: Array of subjects they can teach
- **teachingStreams**: Array of academic streams they teach
- **aadharCardUrl**: URL to Aadhar card document
- **panCardUrl**: URL to PAN card document
- **degreeCertificateUrl**: URL to degree certificate
- **documentsVerified**: Whether documents are verified by admin
- **hourlyRate**: Hourly teaching rate
- **availability**: Teaching schedule and availability
- **bio**: Teacher's bio and introduction

## Data Validation Rules

### Firebase Database Rules
```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid",
        ".validate": "newData.hasChildren(['userId', 'email', 'fullName', 'userType'])"
      }
    },
    "students": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid",
        ".validate": "newData.hasChildren(['userId', 'age', 'gender'])"
      }
    },
    "teachers": {
      "$uid": {
        ".read": "auth != null",
        ".write": "$uid === auth.uid",
        ".validate": "newData.hasChildren(['userId', 'highestQualification', 'yearsOfExperience'])"
      }
    }
  }
}
```

### Validation Constraints
- **Email**: Must be valid email format
- **Phone**: Must be 10-digit Indian mobile number
- **Age**: Must be between 5-100 for students, 18-100 for teachers
- **Experience**: Must be between 0-50 years
- **Budget**: Must be positive numbers with min ≤ max
- **Required Fields**: All marked fields must be present

## Indexing Strategy

### Primary Indexes
- **users**: userId (primary key)
- **students**: userId (primary key)
- **teachers**: userId (primary key)

### Secondary Indexes
- **teachers**: subjectsTaught, teachingStreams, documentsVerified
- **students**: subjectsNeeded, tuitionStreams, preferredTimeSlot
- **users**: userType, isVerified

### Query Optimization
- Use compound indexes for complex queries
- Index frequently searched fields
- Consider denormalization for read-heavy operations

## Data Migration

### Version Control
- Database schema versioning
- Migration scripts for schema changes
- Backward compatibility considerations

### Backup Strategy
- Regular automated backups
- Point-in-time recovery
- Cross-region replication

## Security Considerations

### Authentication
- Firebase Authentication required for all operations
- User can only access their own data
- Teachers' public data readable by authenticated users

### Data Privacy
- Sensitive documents stored securely
- Personal information protected
- GDPR compliance considerations

### Access Control
- Role-based access control
- Document verification workflow
- Admin-only operations

## Performance Optimization

### Query Optimization
- Limit query results
- Use pagination for large datasets
- Implement caching strategies

### Storage Optimization
- Compress large text fields
- Optimize image storage
- Regular data cleanup

## Monitoring and Analytics

### Metrics to Track
- User registration rates
- Document verification times
- Query performance
- Storage usage

### Error Handling
- Comprehensive error logging
- User-friendly error messages
- Fallback mechanisms

## Future Enhancements

### Planned Features
- Real-time chat system
- Session booking system
- Payment processing
- Rating and review system
- Push notifications
- Offline support

### Schema Extensions
- Payment records
- Session history
- User preferences
- Analytics data
- Admin management
