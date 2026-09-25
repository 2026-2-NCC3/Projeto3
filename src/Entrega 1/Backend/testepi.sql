CREATE DATABASE IF NOT EXISTS testepi; 
USE testepi;
 
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

CREATE TABLE Aluno ( 
    id_aluno       INT AUTO_INCREMENT PRIMARY KEY, 
    nome_aluno     VARCHAR(150) NOT NULL, 
    telefone_aluno VARCHAR(20), 
    escola         VARCHAR(150), 
    cidade         VARCHAR(100), 
    grade          VARCHAR(20),
    senha TEXT NOT NULL
); 
 
CREATE TABLE Curso ( 
    id_curso         INT AUTO_INCREMENT PRIMARY KEY, 
    id_atividade     INT NOT NULL, 
    id_universidade  INT NOT NULL, 
    nome_curso       VARCHAR(150) NOT NULL, 
    descricao_curso  TEXT, 
    data_curso       DATE, 
    horario_curso    TIME, 
    local_curso      VARCHAR(150), 
    carga_horaria    INT, 
    quantidade_aula  INT, 
    FOREIGN KEY (id_atividade) REFERENCES Atividade(id_atividade), 
    FOREIGN KEY (id_universidade) REFERENCES Universidade(id_universidade) 
); 
 
CREATE TABLE Inscricao ( 
    id_inscricao     INT AUTO_INCREMENT PRIMARY KEY, 
    id_aluno         INT NOT NULL, 
    id_curso         INT NOT NULL, 
    status_inscricao VARCHAR(20) NOT NULL DEFAULT 'enrolled' 
        CHECK (status_inscricao IN ('enrolled', 'attended', 'no_show', 'cancelled')), 
    data_inscricao   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (id_aluno) REFERENCES Aluno(id_aluno), 
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso) 
); 
CREATE TABLE Presenca ( 
id_presenca     INT AUTO_INCREMENT PRIMARY KEY, 
id_aluno        
INT NOT NULL, 
id_atividade    INT NOT NULL, 
status_presenca VARCHAR(20), 
data_presenca   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
FOREIGN KEY (id_aluno) REFERENCES Aluno(id_aluno), 
FOREIGN KEY (id_atividade) REFERENCES Atividade(id_atividade) 
); 
CREATE INDEX idx_curso_atividade ON Curso(id_atividade); 
CREATE INDEX idx_curso_universidade ON Curso(id_universidade); 
CREATE INDEX idx_inscricao_aluno ON Inscricao(id_aluno); 
CREATE INDEX idx_inscricao_curso ON Inscricao(id_curso); 
CREATE INDEX idx_presenca_aluno ON Presenca(id_aluno); 
CREATE INDEX idx_presenca_atividade ON Presenca(id_atividade); -- Inserts -- Universidade 
INSERT INTO Universidade (nome_universidade, local_universidade) VALUES 
('Universidade FECAP', 'São Paulo, SP'), 
('Universidade FECAP', 'São Paulo, SP'), 
('Universidade FECAP', 'São Paulo, SP'), 
('Universidade FECAP', 'São Paulo, SP'), 
('Universidade FECAP', 'São Paulo, SP'); -- Atividade 
INSERT INTO Atividade (nome_atividade, data_atividade) VALUES 
('Semana de Orientação Profissional', '2026-10-05'), 
('Feira de Cursos Técnicos', '2026-10-12'), 
('Workshop de Tecnologia', '2026-10-19'), 
('Comunicação: Oratória', '2026-10-26'), 
('Oficina de Currículo', '2026-11-02'); -- Aluno  
INSERT INTO Aluno (nome_aluno, telefone_aluno, escola, cidade, grade, senha) VALUES 
('Aluno Teste 01', '(11) 90000-0001', 'Escola Estadual Modelo',  'São Paulo',   '9º ano', '123456'), 
('Aluno Teste 02', '(11) 90000-0002', 'Escola Estadual Exemplo', 'São Paulo',   '1º ano EM', '123456'), 
('Aluno Teste 03', '(11) 90000-0003', 'Escola Estadual Amostra', 'Santo André', '2º ano EM', '123456'), 
('Aluno Teste 04', '(11) 90000-0004', 'Escola Estadual Modelo',  'São Paulo',   '3º ano EM', '123456'), 
('Aluno Teste 05', '(11) 90000-0005', 'Escola Estadual Central', 'Campinas',    '9º ano', '123456'), 
('Aluno Teste 06', '(11) 90000-0006', 'Escola Estadual Exemplo', 'São Paulo',   '1º ano EM', '123456'), 
('Aluno Teste 07', '(11) 90000-0007', 'Escola Estadual Amostra', 'Santo André', '2º ano EM', '123456'), 
('Aluno Teste 08', '(11) 90000-0008', 'Escola Estadual Central', 'Campinas',    '3º ano EM', '123456'), 
('Aluno Teste 09', '(11) 90000-0009', 'Escola Estadual Modelo',  'Osasco', '9º ano', '123456'), 
('Aluno Teste 10', '(11) 90000-0010', 'Escola Estadual Exemplo', 'Guarulhos', '1º ano EM', '123456');
 -- Curso (vinculado a uma Atividade e a uma Universidade) 
INSERT INTO Curso (id_atividade, id_universidade, nome_curso, descricao_curso, 
data_curso, horario_curso, local_curso, carga_horaria, quantidade_aula) VALUES 
(1, 1, 'Introdução à Programação',   'Curso introdutório de lógica e programação',   
'2026-10-05', '14:00:00', 'Auditório 1',   20, 8), 
(2, 2, 'Marketing Digital Básico',   'Fundamentos de marketing para redes sociais',  
'2026-10-12', '09:00:00', 'Sala 203',      
12, 4), 
(3, 3, 'Robótica para Iniciantes',   'Introdução a robótica educacional',
'2026-10-19', '10:00:00', 'Laboratório', 16, 6), 

(4, 4, 'Empreendedorismo Jovem',     'Fundamentos de empreendedorismo e inovação',   
'2026-10-26', '13:30:00', 'Sala 105',      
10, 5), 
(5, 5, 'Design Gráfico Essencial',   'Introdução a ferramentas de design gráfico',   
'2026-11-02', '15:00:00', 'Laboratório 3', 14, 7); 
-- Inscricao (Aluno + Curso) 
INSERT INTO Inscricao (id_aluno, id_curso, status_inscricao) VALUES 
(1, 1, 'enrolled'), 
(2, 1, 'attended'), 
(3, 2, 'enrolled'), 
(4, 3, 'no_show'), 
(1, 3, 'cancelled'), 
(5, 4, 'enrolled'), 
(6, 4, 'attended'), 
(7, 5, 'enrolled'), 
(8, 2, 'attended'), 
(9, 1, 'enrolled'), 
(10, 5, 'no_show'); -- Presenca (Aluno + Atividade) 
INSERT INTO Presenca (id_aluno, id_atividade, status_presenca) VALUES 
(1, 1, 'presente'), 
(2, 1, 'presente'), 
(3, 2, 'ausente'), 
(4, 3, 'presente'), 
(1, 3, 'ausente'), 
(5, 4, 'presente'), 
(6, 4, 'presente'), 
(7, 5, 'ausente'), 
(8, 2, 'presente'), 
(9, 1, 'presente'), 
(10, 5, 'ausente'); -- Queries rápidas pra conferir se deu certo 
SELECT * FROM Aluno; -- SELECT * FROM Curso; -- SELECT a.nome_aluno, c.nome_curso, i.status_inscricao 
--   FROM Inscricao i --   JOIN Aluno a ON a.id_aluno = i.id_aluno --   JOIN Curso c ON c.id_curso = i.id_curso; -- SELECT a.nome_aluno, at.nome_atividade, p.status_presenca -- FROM Presenca p -- JOIN Aluno a ON a.id_aluno = p.id_aluno -- JOIN Atividade at ON at.id_atividade = p.id_atividade;