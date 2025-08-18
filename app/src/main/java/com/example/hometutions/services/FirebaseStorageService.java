package com.example.hometutions.services;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

public class FirebaseStorageService {
    private static final String TAG = "FirebaseStorageService";
    
    private FirebaseStorage storage;
    private StorageReference storageRef;
    
    public interface StorageCallback {
        void onSuccess(String downloadUrl);
        void onFailure(String error);
        void onProgress(double progress);
    }
    
    public FirebaseStorageService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    
    /**
     * Upload profile photo
     */
    public void uploadProfilePhoto(Uri imageUri, String userId, StorageCallback callback) {
        String fileName = "profile_photos/" + userId + "_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference photoRef = storageRef.child(fileName);
        
        UploadTask uploadTask = photoRef.putFile(imageUri);
        
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            callback.onProgress(progress);
        }).addOnSuccessListener(taskSnapshot -> {
            // Get download URL
            photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                callback.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                callback.onFailure("Failed to get download URL: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            callback.onFailure("Upload failed: " + e.getMessage());
        });
    }
    
    /**
     * Upload document (Aadhar, PAN, Degree, etc.)
     */
    public void uploadDocument(Uri documentUri, String userId, String documentType, StorageCallback callback) {
        try {
            Log.d(TAG, "Starting upload for document: " + documentType + " for user: " + userId);
            Log.d(TAG, "Document URI: " + documentUri);
            
            String fileName = "documents/" + userId + "/" + documentType + "_" + UUID.randomUUID().toString() + ".pdf";
            Log.d(TAG, "Storage path: " + fileName);
            
            StorageReference docRef = storageRef.child(fileName);
            Log.d(TAG, "Storage reference created: " + docRef.getPath());
            
            UploadTask uploadTask = docRef.putFile(documentUri);
            
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload progress: " + progress + "%");
                callback.onProgress(progress);
            }).addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Upload successful for: " + fileName);
                // Get download URL
                docRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Download URL obtained: " + uri.toString());
                    callback.onSuccess(uri.toString());
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get download URL for: " + fileName, e);
                    callback.onFailure("Failed to get download URL: " + e.getMessage());
                });
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Upload failed for: " + fileName, e);
                Log.e(TAG, "Error details: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                
                // Check if it's a storage rules issue
                if (e.getMessage() != null && e.getMessage().contains("404")) {
                    Log.e(TAG, "404 error detected - this usually means storage rules are too restrictive or bucket doesn't exist");
                    callback.onFailure("Storage configuration error: Please check Firebase Storage setup and rules");
                } else {
                    callback.onFailure("Upload failed: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Exception during upload setup", e);
            callback.onFailure("Upload setup failed: " + e.getMessage());
        }
    }
    
    /**
     * Upload image document
     */
    public void uploadImageDocument(Uri imageUri, String userId, String documentType, StorageCallback callback) {
        try {
            Log.d(TAG, "Starting upload for document: " + documentType + " for user: " + userId);
            Log.d(TAG, "Image URI: " + imageUri);
            
            String fileName = "documents/" + userId + "/" + documentType + "_" + UUID.randomUUID().toString() + ".jpg";
            Log.d(TAG, "Storage path: " + fileName);
            
            StorageReference docRef = storageRef.child(fileName);
            Log.d(TAG, "Storage reference created: " + docRef.getPath());
            
            UploadTask uploadTask = docRef.putFile(imageUri);
            
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload progress: " + progress + "%");
                callback.onProgress(progress);
            }).addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Upload successful for: " + fileName);
                // Get download URL
                docRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Download URL obtained: " + uri.toString());
                    callback.onSuccess(uri.toString());
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get download URL for: " + fileName, e);
                    callback.onFailure("Failed to get download URL: " + e.getMessage());
                });
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Upload failed for: " + fileName, e);
                Log.e(TAG, "Error details: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                
                // Check if it's a storage rules issue
                if (e.getMessage() != null && e.getMessage().contains("404")) {
                    Log.e(TAG, "404 error detected - this usually means storage rules are too restrictive or bucket doesn't exist");
                    callback.onFailure("Storage configuration error: Please check Firebase Storage setup and rules");
                } else {
                    callback.onFailure("Upload failed: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Exception during upload setup", e);
            callback.onFailure("Upload setup failed: " + e.getMessage());
        }
    }
    
    /**
     * Delete file from storage
     */
    public void deleteFile(String fileUrl, StorageCallback callback) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            callback.onSuccess("No file to delete");
            return;
        }
        
        StorageReference fileRef = storage.getReferenceFromUrl(fileUrl);
        fileRef.delete().addOnSuccessListener(aVoid -> {
            callback.onSuccess("File deleted successfully");
        }).addOnFailureListener(e -> {
            callback.onFailure("Failed to delete file: " + e.getMessage());
        });
    }
    
    /**
     * Get file size
     */
    public void getFileSize(String fileUrl, StorageCallback callback) {
        StorageReference fileRef = storage.getReferenceFromUrl(fileUrl);
        fileRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            long size = storageMetadata.getSizeBytes();
            callback.onSuccess(String.valueOf(size));
        }).addOnFailureListener(e -> {
            callback.onFailure("Failed to get file size: " + e.getMessage());
        });
    }
    
    /**
     * Check if file exists
     */
    public void fileExists(String fileUrl, StorageCallback callback) {
        StorageReference fileRef = storage.getReferenceFromUrl(fileUrl);
        fileRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            callback.onSuccess("File exists");
        }).addOnFailureListener(e -> {
            callback.onFailure("File does not exist: " + e.getMessage());
        });
    }
    
    /**
     * Upload multiple files
     */
    public void uploadMultipleFiles(java.util.List<Uri> fileUris, String userId, String folder, StorageCallback callback) {
        final int totalFiles = fileUris.size();
        final int[] uploadedFiles = {0};
        final java.util.List<String> downloadUrls = new java.util.ArrayList<>();
        
        for (int i = 0; i < fileUris.size(); i++) {
            Uri fileUri = fileUris.get(i);
            String fileName = folder + "/" + userId + "_" + i + "_" + UUID.randomUUID().toString() + ".jpg";
            StorageReference fileRef = storageRef.child(fileName);
            
            UploadTask uploadTask = fileRef.putFile(fileUri);
            
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrls.add(uri.toString());
                    uploadedFiles[0]++;
                    
                    if (uploadedFiles[0] == totalFiles) {
                        // All files uploaded successfully
                        callback.onSuccess("All files uploaded successfully");
                    }
                }).addOnFailureListener(e -> {
                    callback.onFailure("Failed to get download URL for file " + uploadedFiles[0]);
                });
            }).addOnFailureListener(e -> {
                callback.onFailure("Failed to upload file " + uploadedFiles[0] + ": " + e.getMessage());
            });
        }
    }

    /**
     * Test Firebase Storage connectivity
     */
    public void testStorageConnection(StorageCallback callback) {
        try {
            Log.d(TAG, "Testing Firebase Storage connection...");
            Log.d(TAG, "Storage instance: " + (storage != null ? "Created" : "Null"));
            Log.d(TAG, "Storage reference: " + (storageRef != null ? "Created" : "Null"));
            
            if (storage != null && storageRef != null) {
                // Try to get storage metadata to test connection
                storageRef.getMetadata().addOnSuccessListener(metadata -> {
                    Log.d(TAG, "Storage connection successful. Bucket: " + metadata.getBucket());
                    callback.onSuccess("Storage connection successful. Bucket: " + metadata.getBucket());
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Storage connection test failed", e);
                    callback.onFailure("Storage connection test failed: " + e.getMessage());
                });
            } else {
                callback.onFailure("Storage not properly initialized");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during storage connection test", e);
            callback.onFailure("Storage connection test exception: " + e.getMessage());
        }
    }
}
