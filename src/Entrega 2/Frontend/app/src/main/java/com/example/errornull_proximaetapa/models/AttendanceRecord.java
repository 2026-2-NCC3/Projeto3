package com.example.errornull_proximaetapa.models;

import java.util.UUID;

public class AttendanceRecord {

    private final String attendanceId;
    private final String sessionId;
    private final String studentId;
    private final String courseId;
    private long horarioEntrada;
    private long horarioSaida;
    private String statusPresenca; // "PRESENTE", "AUSENTE", "PENDENTE"

    public AttendanceRecord(String sessionId, String studentId, String courseId) {
        this.attendanceId = UUID.randomUUID().toString();
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.horarioEntrada = System.currentTimeMillis();
        this.statusPresenca = "PRESENTE";
    }

    public AttendanceRecord(String attendanceId, String sessionId, String studentId, String courseId, long horarioEntrada, long horarioSaida, String statusPresenca) {
        this.attendanceId = attendanceId != null ? attendanceId : UUID.randomUUID().toString();
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.horarioEntrada = horarioEntrada;
        this.horarioSaida = horarioSaida;
        this.statusPresenca = statusPresenca != null ? statusPresenca : "PRESENTE";
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public long getHorarioEntrada() {
        return horarioEntrada;
    }

    public void setHorarioEntrada(long horarioEntrada) {
        this.horarioEntrada = horarioEntrada;
    }

    public long getHorarioSaida() {
        return horarioSaida;
    }

    public void setHorarioSaida(long horarioSaida) {
        this.horarioSaida = horarioSaida;
    }

    public String getStatusPresenca() {
        return statusPresenca;
    }

    public void setStatusPresenca(String statusPresenca) {
        this.statusPresenca = statusPresenca;
    }
}