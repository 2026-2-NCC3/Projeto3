package com.example.errornull_proximaetapa.models;

import java.util.UUID;

public class Enrollment {

    private final String enrollmentId;
    private final String studentId;
    private final String courseId;
    private final long dataInscricao;
    private int progressoPercentual;
    private String statusEnrollment; // "RESERVA", "ATIVO", "CONCLUIDO"

    public Enrollment(String studentId, String courseId) {
        this.enrollmentId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseId = courseId;
        this.dataInscricao = System.currentTimeMillis();
        this.progressoPercentual = 0;
        this.statusEnrollment = "ATIVO";
    }

    public Enrollment(String enrollmentId, String studentId, String courseId, long dataInscricao, int progressoPercentual, String statusEnrollment) {
        this.enrollmentId = enrollmentId != null ? enrollmentId : UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseId = courseId;
        this.dataInscricao = dataInscricao > 0 ? dataInscricao : System.currentTimeMillis();
        this.progressoPercentual = Math.max(0, Math.min(100, progressoPercentual));
        this.statusEnrollment = statusEnrollment != null ? statusEnrollment : "ATIVO";
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public long getDataInscricao() {
        return dataInscricao;
    }

    public int getProgressoPercentual() {
        return progressoPercentual;
    }

    public void setProgressoPercentual(int progressoPercentual) {
        this.progressoPercentual = Math.max(0, Math.min(100, progressoPercentual));
        if (this.progressoPercentual >= 100) {
            this.statusEnrollment = "CONCLUIDO";
        }
    }

    public String getStatusEnrollment() {
        return statusEnrollment;
    }

    public void setStatusEnrollment(String statusEnrollment) {
        this.statusEnrollment = statusEnrollment;
    }
}