package com.example.hometutions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometutions.R;

import java.util.List;

public class SubjectChipAdapter extends RecyclerView.Adapter<SubjectChipAdapter.SubjectChipViewHolder> {

    private List<String> subjects;

    public SubjectChipAdapter(List<String> subjects) {
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public SubjectChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject_chip, parent, false);
        return new SubjectChipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectChipViewHolder holder, int position) {
        String subject = subjects.get(position);
        holder.subjectChip.setText(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void updateSubjects(List<String> newSubjects) {
        this.subjects = newSubjects;
        notifyDataSetChanged();
    }

    static class SubjectChipViewHolder extends RecyclerView.ViewHolder {
        TextView subjectChip;

        SubjectChipViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectChip = itemView.findViewById(R.id.subjectChip);
        }
    }
}
