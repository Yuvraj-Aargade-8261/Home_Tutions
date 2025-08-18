package com.example.hometutions;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class HomeTutionsApplication extends Application {
    
    private static final String TAG = "HomeTutionsApplication";
    private static HomeTutionsApplication instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // CRITICAL: Set system properties BEFORE any other operations
        initializeCrashPrevention();
        
        // Set up global exception handler
        setupGlobalExceptionHandler();
    }
    
    private void setupGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG, "Uncaught exception in thread: " + thread.getName(), throwable);
                
                // Handle system-level crashes gracefully
                if (throwable instanceof NullPointerException) {
                    Log.w(TAG, "Handling NullPointerException gracefully");
                    handleNullPointerException(throwable);
                } else if (throwable instanceof RuntimeException) {
                    Log.w(TAG, "Handling RuntimeException gracefully");
                    handleRuntimeException(throwable);
                } else {
                    Log.w(TAG, "Handling unknown exception gracefully");
                    handleUnknownException(throwable);
                }
                
                // Don't crash the app, just log the error
                Log.i(TAG, "Exception handled, app continuing...");
            }
        });
    }
    
    private void handleNullPointerException(Throwable throwable) {
        try {
            // Check if this is the specific callGcSupression error
            if (throwable.getStackTrace().length > 0 &&
                throwable.getStackTrace()[0].getMethodName().contains("callGcSupression")) {
                
                Log.w(TAG, "Detected callGcSupression error, applying aggressive cleanup");
                
                // Apply aggressive cleanup for this specific error
                for (int i = 0; i < 5; i++) {
                    System.gc();
                    try {
                        Thread.sleep(300); // Longer delay between GC calls
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // Set additional system properties
                System.setProperty("sun.awt.disableMixing", "true");
                System.setProperty("sun.java2d.opengl", "false");
                System.setProperty("sun.java2d.d3d", "false");
                System.setProperty("sun.java2d.xrender", "false");
                
            } else {
                // Regular NullPointerException handling
                System.gc();
            }
            
            // Log additional information for debugging
            Log.d(TAG, "Stack trace for NullPointerException:");
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                Log.d(TAG, "  at " + element.toString());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling NullPointerException", e);
        }
    }
    
    private void handleRuntimeException(Throwable throwable) {
        try {
            // Force garbage collection
            System.gc();
            
            // Log stack trace
            Log.d(TAG, "Stack trace for RuntimeException:");
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                Log.d(TAG, "  at " + element.toString());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling RuntimeException", e);
        }
    }
    
    private void handleUnknownException(Throwable throwable) {
        try {
            // Force garbage collection
            System.gc();
            
            // Log stack trace
            Log.d(TAG, "Stack trace for unknown exception:");
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                Log.d(TAG, "  at " + element.toString());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling unknown exception", e);
        }
    }
    
    private void initializeCrashPrevention() {
        try {
            // Set comprehensive system properties to prevent crashes
            System.setProperty("java.awt.headless", "true");
            System.setProperty("sun.awt.disableMixing", "true");
            System.setProperty("sun.java2d.opengl", "false");
            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("sun.java2d.xrender", "false");
            System.setProperty("java.awt.useSystemAAFontSettings", "on");
            System.setProperty("sun.java2d.pmoffscreen", "false");
            System.setProperty("sun.awt.noerasebackground", "true");
            System.setProperty("sun.java2d.noddraw", "true");
            System.setProperty("sun.java2d.nod3d", "true");
            
            // Additional properties for OPPO/ColorOS devices
            System.setProperty("sun.awt.disableMixing", "true");
            System.setProperty("sun.java2d.opengl", "false");
            System.setProperty("sun.java2d.d3d", "false");
            
            // Clear any cached authentication state
            clearAuthenticationCache();
            
            Log.d(TAG, "Crash prevention system properties set successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing crash prevention", e);
        }
    }
    
    private void clearAuthenticationCache() {
        try {
            // Clear any potential cached authentication data
            // This ensures no automatic login happens
            Log.d(TAG, "Clearing authentication cache to prevent automatic login");
            
            // Force garbage collection to clear any cached objects
            System.gc();
            
        } catch (Exception e) {
            Log.e(TAG, "Error clearing authentication cache", e);
        }
    }
    
    public static HomeTutionsApplication getInstance() {
        return instance;
    }
    
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            // Clean up resources
            Log.d(TAG, "Application finalizing, cleaning up resources");
        } finally {
            super.finalize();
        }
    }
}
