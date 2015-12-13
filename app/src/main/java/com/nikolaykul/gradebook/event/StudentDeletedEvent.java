package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Student;

public class StudentDeletedEvent {
    public final Student student;

    public StudentDeletedEvent(final Student student) {
        this.student = student;
    }
}
