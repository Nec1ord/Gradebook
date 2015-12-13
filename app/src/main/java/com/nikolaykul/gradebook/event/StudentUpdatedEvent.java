package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Student;

public class StudentUpdatedEvent {
    public final Student student;

    public StudentUpdatedEvent(final Student student) {
        this.student = student;
    }
}
