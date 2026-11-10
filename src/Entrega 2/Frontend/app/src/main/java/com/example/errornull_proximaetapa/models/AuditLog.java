package com.example.errornull_proximaetapa.models;

import java.util.UUID;

public class AuditLog {

    private final String logId;
    private final String actorUserId;
    private final String actionType;
    private final String targetEntityId;
    private final String detalheAcao;
    private final long timestamp;

    public AuditLog(String actorUserId, String actionType, String targetEntityId, String detalheAcao) {
        this.logId = UUID.randomUUID().toString();
        this.actorUserId = actorUserId != null ? actorUserId : "SISTEMA";
        this.actionType = actionType != null ? actionType : "GERAL";
        this.targetEntityId = targetEntityId != null ? targetEntityId : "";
        this.detalheAcao = detalheAcao != null ? detalheAcao : "";
        this.timestamp = System.currentTimeMillis();
    }

    public String getLogId() {
        return logId;
    }

    public String getActorUserId() {
        return actorUserId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getTargetEntityId() {
        return targetEntityId;
    }

    public String getDetalheAcao() {
        return detalheAcao;
    }

    public long getTimestamp() {
        return timestamp;
    }
}