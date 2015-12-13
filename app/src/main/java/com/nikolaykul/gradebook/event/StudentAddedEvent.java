package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Student;

public class StudentAddedEvent {
    public final Student student;

    public StudentAddedEvent(final Student student) {
        this.student = student;
    }
}
