package com.example.hometutions.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.OvershootInterpolator;

public class AnimationUtils {
    
    // Fade in animation
    public static void fadeIn(View view, long duration, Animation.AnimationListener listener) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);   
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        
        if (listener != null) {
            fadeIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(null);
                }
            });
        }
        
        fadeIn.start();
    }
    
    // Fade out animation
    public static void fadeOut(View view, long duration, Animation.AnimationListener listener) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(duration);
        fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());
        
        if (listener != null) {
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    listener.onAnimationEnd(null);
                }
            });
        } else {
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
        }
        
        fadeOut.start();
    }
    
    // Slide up animation
    public static void slideUp(View view, long duration, Animation.AnimationListener listener) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0f);
        slideUp.setDuration(duration);
        slideUp.setInterpolator(new OvershootInterpolator(0.8f));
        
        if (listener != null) {
            slideUp.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(null);
                }
            });
        }
        
        slideUp.start();
    }
    
    // Slide down animation
    public static void slideDown(View view, long duration, Animation.AnimationListener listener) {
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(view, "translationY", 0f, view.getHeight());
        slideDown.setDuration(duration);
        slideDown.setInterpolator(new AccelerateDecelerateInterpolator());
        
        if (listener != null) {
            slideDown.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    listener.onAnimationEnd(null);
                }
            });
        } else {
            slideDown.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
        }
        
        slideDown.start();
    }
    
    // Scale in animation with bounce
    public static void scaleIn(View view, long duration, Animation.AnimationListener listener) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        
        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.playTogether(scaleX, scaleY);
        scaleSet.setDuration(duration);
        scaleSet.setInterpolator(new BounceInterpolator());
        
        if (listener != null) {
            scaleSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(null);
                }
            });
        }
        
        scaleSet.start();
    }
    
    // Pulse animation
    public static void pulse(View view, long duration, int repeatCount) {
        ValueAnimator scaleX = ValueAnimator.ofFloat(1f, 1.1f, 1f);
        scaleX.setDuration(duration);
        scaleX.setRepeatCount(repeatCount);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        
        ValueAnimator scaleY = ValueAnimator.ofFloat(1f, 1.1f, 1f);
        scaleY.setDuration(duration);
        scaleY.setRepeatCount(repeatCount);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        
        scaleX.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            view.setScaleX(value);
            view.setScaleY(value);
        });
        
        scaleX.start();
    }
    
    // Floating animation for decorative elements
    public static void startFloatingAnimation(View view, long duration) {
        if (view == null) return;
        
        try {
            ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY", 0f, -20f, 0f);
            translateY.setDuration(duration);
            translateY.setRepeatCount(ValueAnimator.INFINITE);
            translateY.setRepeatMode(ValueAnimator.REVERSE);
            translateY.setInterpolator(new AccelerateDecelerateInterpolator());
            
            translateY.start();
        } catch (Exception e) {
            // If animation fails, just log the error
            e.printStackTrace();
        }
    }
    
    // Staggered animation for multiple views
    public static void staggerAnimation(View[] views, long delay, long duration, Animation.AnimationListener listener) {
        AnimatorSet[] animators = new AnimatorSet[views.length];
        
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f);
            
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(fadeIn, slideUp);
            animatorSet.setDuration(duration);
            animatorSet.setInterpolator(new OvershootInterpolator(0.8f));
            animatorSet.setStartDelay(i * delay);
            
            animators[i] = animatorSet;
        }
        
        AnimatorSet staggerSet = new AnimatorSet();
        staggerSet.playSequentially(animators);
        
        if (listener != null) {
            staggerSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(null);
                }
            });
        }
        
        staggerSet.start();
    }
    
    // Loading animation for progress bar
    public static void startLoadingAnimation(View progressBar, long duration) {
        if (progressBar == null) return;
        
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            ObjectAnimator rotation = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f);
            rotation.setDuration(duration);
            rotation.setRepeatCount(ValueAnimator.INFINITE);
            rotation.setInterpolator(new AccelerateDecelerateInterpolator());
            
            rotation.start();
        } catch (Exception e) {
            // If animation fails, just log the error
            e.printStackTrace();
        }
    }
    
    // Stop all animations on a view
    public static void stopAnimations(View view) {
        if (view == null) return;
        
        try {
            view.animate().cancel();
            view.clearAnimation();
        } catch (Exception e) {
            // If stopping animations fails, just log the error
            e.printStackTrace();
        }
    }
    
    // Scale in animation with overshoot
    public static void scaleInWithOvershoot(View view, long duration, Animation.AnimationListener listener) {
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f);
        
        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.playTogether(scaleX, scaleY);
        scaleSet.setDuration(duration);
        scaleSet.setInterpolator(new OvershootInterpolator(0.8f));
        
        if (listener != null) {
            scaleSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(null);
                }
            });
        }
        
        scaleSet.start();
    }
    
    // Slide up with delay animation
    public static void slideUpWithDelay(View view, long duration, long delay, Animation.AnimationListener listener) {
        view.setTranslationY(100f);
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideUp, fadeIn);
        animatorSet.setDuration(duration);
        animatorSet.setStartDelay(delay);
        animatorSet.setInterpolator(new OvershootInterpolator(0.8f));
        
        if (listener != null) {
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(null);
                }
            });
        }
        
        animatorSet.start();
    }
}
