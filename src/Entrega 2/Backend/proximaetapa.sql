CREATE DATABASE defaultdb;
USE defaultdb;

CREATE TABLE Usuario ( 
    cpf           CHAR(11) PRIMARY KEY, 
    nome          VARCHAR(150) NOT NULL, 
    email         VARCHAR(150) UNIQUE, 
    senha         VARCHAR(255) NOT NULL,
    data_nascimento DATE,
    biografia	VARCHAR(255),
    tipo_usuario  VARCHAR(20) NOT NULL
        CHECK (tipo_usuario IN ('aluno', 'professor')), 
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP 
); 
 
 
CREATE TABLE Aluno ( 
    cpf            CHAR(11) PRIMARY KEY, 
    telefone_aluno VARCHAR(20) NOT NULL, 
    escola         VARCHAR(150) NOT NULL, 
    cidade         VARCHAR(100) NOT NULL, 
    grade          VARCHAR(20) NOT NULL,
        CHECK (grade IN ('EM 1° ano', 'EM 2° ano', 'EM 3° ano', 'Graduação', 'Pós-Graduação', 'Mestrado', 'Doutorado', 'Pós-Doutorado')),
    FOREIGN KEY (cpf) REFERENCES Usuario(cpf),
    preferencia VARCHAR(255),
    vocacionalResultado VARCHAR(255),
    vocacionalDescricao VARCHAR(255)
); 


CREATE TABLE Professor ( 
    cpf                CHAR(11) PRIMARY KEY, 
    telefone_professor VARCHAR(20) NOT NULL, 
    especialidade      VARCHAR(150) NOT NULL,
    FOREIGN KEY (cpf) REFERENCES Usuario(cpf) 
); 
 

CREATE TABLE Blacklist ( 
id_blacklist INT AUTO_INCREMENT PRIMARY KEY, 
    cpf_aluno     CHAR(11) NOT NULL, 
    cpf_professor CHAR(11) NOT NULL, 
    motivo        TEXT NOT NULL,
    nivel_strike    INT NOT NULL
		CHECK (nivel_strike IN (1, 2, 3)),
    dias_bloqueio INT NOT NULL, 
    data_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    data_fim DATETIME GENERATED ALWAYS AS (DATE_ADD(data_inicio, INTERVAL 
dias_bloqueio DAY )) STORED,
    FOREIGN KEY (cpf_aluno) REFERENCES Aluno(cpf), 
    FOREIGN KEY (cpf_professor) REFERENCES Professor(cpf) 
); 
 
 
CREATE TABLE Universidade ( 
    id_universidade    INT AUTO_INCREMENT PRIMARY KEY, 
    nome_universidade  VARCHAR(150) NOT NULL, 
    local_universidade VARCHAR(150) 
); 

 
CREATE TABLE Atividade ( 
    id_atividade   INT AUTO_INCREMENT PRIMARY KEY, 
    nome_atividade VARCHAR(150) NOT NULL, 
    data_atividade DATE 
); 


CREATE TABLE Curso ( 
    id_curso         VARCHAR(150) PRIMARY KEY, 
    id_atividade     INT NOT NULL, 
    id_universidade  INT NOT NULL, 
    cpf_professor    CHAR(11), 
    nome_curso       VARCHAR(150) NOT NULL, 
    descricao_curso  TEXT NOT NULL, 
    local_curso      VARCHAR(150) NOT NULL,
    frequencia_minima DECIMAL(5,2) NOT NULL DEFAULT 75.00,
    dia_semana		 VARCHAR(100),
		CHECK (dia_semana IN ('Domingo', 'Segunda-Feira', 'Terça-Feira', 'Quarta-Feira', 'Quinta-Feira', 'Sexta-Feira', 'Sábado')),
    carga_horaria    INT NOT NULL, 
    quantidade_aula  INT NOT NULL, 
    FOREIGN KEY (id_atividade) REFERENCES Atividade(id_atividade), 
    FOREIGN KEY (id_universidade) REFERENCES Universidade(id_universidade), 
    FOREIGN KEY (cpf_professor) REFERENCES Professor(cpf) 
);

 
CREATE TABLE Encontro ( 
    id_encontro     VARCHAR(150) PRIMARY KEY, 
    id_curso        VARCHAR(150) NOT NULL,
    datas_encontro      JSON NOT NULL,
    horarios_encontro   JSON NOT NULL,
    numero_encontro INT NOT NULL, 
    data_encontro   DATE NOT NULL,  
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso) 
); 


CREATE TABLE Inscricao ( 
    id_inscricao     VARCHAR(150) PRIMARY KEY, 
    cpf_aluno        CHAR(11) NOT NULL, 
    id_curso         VARCHAR(150) NOT NULL, 
    cpf_professor    CHAR(11), 
    status_inscricao VARCHAR(20) NOT NULL DEFAULT 'enrolled' 
        CHECK (status_inscricao IN ('enrolled', 'attended', 'no_show', 'cancelled')), 
    data_inscricao   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (cpf_aluno) REFERENCES Aluno(cpf), 
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso), 
    FOREIGN KEY (cpf_professor) REFERENCES Professor(cpf) 
); 
 
 
 CREATE TABLE QRCode (
	qr_code	VARCHAR(255) PRIMARY KEY DEFAULT (UUID()),
    cpf_aluno        CHAR(11) NOT NULL,
    id_encontro     VARCHAR(150) NOT NULL,
    FOREIGN KEY (cpf_aluno) REFERENCES Aluno(cpf),
    FOREIGN KEY (id_encontro) REFERENCES Encontro(id_encontro)
);
 
 
CREATE TABLE Presenca ( 
    id_presenca     VARCHAR(255) PRIMARY KEY,
    qr_code	VARCHAR(255) NOT NULL,
    id_inscricao    VARCHAR(150) NOT NULL, 
    id_encontro     VARCHAR(150) NOT NULL, 
    cpf_professor   CHAR(11), 
    status_presenca VARCHAR(20) NOT NULL DEFAULT 'presente' 
        CHECK (status_presenca IN ('presente', 'ausente')), 
    data_presenca   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (qr_code) REFERENCES QRCode(qr_code),
    FOREIGN KEY (id_inscricao) REFERENCES Inscricao(id_inscricao), 
    FOREIGN KEY (id_encontro) REFERENCES Encontro(id_encontro), 
    FOREIGN KEY (cpf_professor) REFERENCES Professor(cpf), 
    UNIQUE (id_inscricao, id_encontro) 
); 

 
CREATE TABLE Vaga ( 
    id_vaga         INT AUTO_INCREMENT PRIMARY KEY, 
    cpf_professor       CHAR(11) NOT NULL, 
    titulo          VARCHAR(150) NOT NULL, 
    empresa         VARCHAR(150) NOT NULL, 
    descricao       TEXT, 
    link_externo    VARCHAR(255), 
    data_publicacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (cpf_professor) REFERENCES Professor(cpf) 
); 
 
 
CREATE TABLE Notificacao ( 
    id_notificacao INT AUTO_INCREMENT PRIMARY KEY, 
    cpf_usuario    CHAR(11) NOT NULL, 
    titulo         VARCHAR(150) NOT NULL, 
    mensagem       TEXT NOT NULL, 
    tipo           VARCHAR(30) NOT NULL DEFAULT 'sistema' 
        CHECK (tipo IN ('vaga', 'curso', 'mensagem', 'sistema')), 
    lida           BOOLEAN NOT NULL DEFAULT FALSE, 
    data_criacao   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (cpf_usuario) REFERENCES Usuario(cpf) 
); 
 
 
 CREATE TABLE Certificado (
    id_certificado         VARCHAR(255) PRIMARY KEY DEFAULT (UUID()),
    id_inscricao           VARCHAR(150) NOT NULL UNIQUE,
    cpf_aluno              CHAR(11) NOT NULL,
    id_curso               VARCHAR(150) NOT NULL,
    carga_horaria          INT NOT NULL,
    percentual_frequencia  DECIMAL(5,2) NOT NULL,
    data_emissao           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codigo_validacao       VARCHAR(36) NOT NULL UNIQUE DEFAULT (UUID()),
    url_arquivo            VARCHAR(255),
    FOREIGN KEY (id_inscricao) REFERENCES Inscricao(id_inscricao),
    FOREIGN KEY (cpf_aluno)    REFERENCES Aluno(cpf),
    FOREIGN KEY (id_curso)     REFERENCES Curso(id_curso)
);


CREATE TABLE Card (
    id_card        INT AUTO_INCREMENT PRIMARY KEY,
    cpf_aluno      CHAR(11) NOT NULL UNIQUE,
    codigo_card    VARCHAR(36) NOT NULL UNIQUE DEFAULT (UUID()),
    data_emissao   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_validade  DATE,
    status_card    VARCHAR(20) NOT NULL DEFAULT 'ativo'
        CHECK (status_card IN ('ativo', 'inativo', 'expirado')),
    FOREIGN KEY (cpf_aluno) REFERENCES Aluno(cpf)
);


CREATE TABLE Mensagem (
    id_mensagem       INT AUTO_INCREMENT PRIMARY KEY,
    cpf_remetente     CHAR(11) NOT NULL,
    cpf_destinatario  CHAR(11),
    id_curso          VARCHAR(150),
    tipo_mensagem     VARCHAR(20) NOT NULL DEFAULT 'chat'
        CHECK (tipo_mensagem IN ('comunicado', 'chat')),
    assunto           VARCHAR(150),
    conteudo          TEXT NOT NULL,
    data_envio        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lida              BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (cpf_remetente)    REFERENCES Usuario(cpf),
    FOREIGN KEY (cpf_destinatario) REFERENCES Usuario(cpf),
    FOREIGN KEY (id_curso)         REFERENCES Curso(id_curso),
    CHECK (tipo_mensagem = 'comunicado' OR cpf_destinatario IS NOT NULL)
);


CREATE OR REPLACE VIEW vw_frequencia_inscricao AS
SELECT
    i.id_inscricao,
    i.cpf_aluno,
    i.id_curso,
    c.frequencia_minima,
    (SELECT COUNT(*) FROM Encontro e
      WHERE e.id_curso = i.id_curso) AS total_encontros,
    (SELECT COUNT(*) FROM Presenca p
      WHERE p.id_inscricao = i.id_inscricao
        AND p.status_presenca = 'presente') AS total_presencas,
    ROUND(100 * (SELECT COUNT(*) FROM Presenca p
                  WHERE p.id_inscricao = i.id_inscricao
                    AND p.status_presenca = 'presente')
              / NULLIF((SELECT COUNT(*) FROM Encontro e
                         WHERE e.id_curso = i.id_curso), 0), 2) AS percentual_frequencia
FROM Inscricao i
JOIN Curso c ON c.id_curso = i.id_curso;
 
 
CREATE INDEX idx_curso_universidade ON Curso(id_universidade); 
CREATE INDEX idx_curso_professor ON Curso(cpf_professor); 
CREATE INDEX idx_encontro_curso ON Encontro(id_curso); 
CREATE INDEX idx_inscricao_aluno ON Inscricao(cpf_aluno); 
CREATE INDEX idx_inscricao_curso ON Inscricao(id_curso); 
CREATE INDEX idx_inscricao_professor ON Inscricao(cpf_professor); 
CREATE INDEX idx_presenca_inscricao ON Presenca(id_inscricao); 
CREATE INDEX idx_presenca_encontro ON Presenca(id_encontro); 
CREATE INDEX idx_presenca_professor ON Presenca(cpf_professor); 
CREATE INDEX idx_blacklist_professor ON Blacklist(cpf_professor); 
CREATE INDEX idx_vaga_prof ON Vaga(cpf_professor); 
CREATE INDEX idx_notificacao_usuario ON Notificacao(cpf_usuario);
CREATE INDEX idx_certificado_aluno    ON Certificado(cpf_aluno);
CREATE INDEX idx_certificado_curso    ON Certificado(id_curso);
CREATE INDEX idx_mensagem_remetente   ON Mensagem(cpf_remetente);
CREATE INDEX idx_mensagem_dest        ON Mensagem(cpf_destinatario);
CREATE INDEX idx_mensagem_curso       ON Mensagem(id_curso);