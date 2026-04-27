ALTER SESSION SET CURRENT_SCHEMA = PRISON_USER;


CREATE SEQUENCE PRISON_USER.SEQ_USUARIO   START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE PRISON_USER.SEQ_CELA      START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE PRISON_USER.SEQ_DETENTO   START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE PRISON_USER.USUARIOS (
    ID              NUMBER          NOT NULL,
    USERNAME        VARCHAR2(50)    NOT NULL,
    PASSWORD        VARCHAR2(255)   NOT NULL,
    NOME_COMPLETO   VARCHAR2(150)   NOT NULL,
    EMAIL           VARCHAR2(100),
    ROLE            VARCHAR2(30)    NOT NULL,
    ATIVO           NUMBER(1)       DEFAULT 1 NOT NULL,
    CRIADO_EM       TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_USUARIOS       PRIMARY KEY (ID),
    CONSTRAINT UQ_USER_USERNAME  UNIQUE (USERNAME),
    CONSTRAINT UQ_USER_EMAIL     UNIQUE (EMAIL),
    CONSTRAINT CK_USER_ROLE      CHECK (ROLE IN ('ROLE_ADMIN','ROLE_GESTOR','ROLE_AGENTE')),
    CONSTRAINT CK_USER_ATIVO     CHECK (ATIVO IN (0,1))
);

CREATE TABLE PRISON_USER.CELAS (
                                   ID              NUMBER          NOT NULL,
                                   NUMERO          VARCHAR2(10)    NOT NULL,
                                   BLOCO           VARCHAR2(10)    NOT NULL,
                                   CAPACIDADE      NUMBER(3)       NOT NULL,
                                   STATUS          VARCHAR2(20)    DEFAULT 'DISPONIVEL' NOT NULL,
                                   TIPO            VARCHAR2(20)    NOT NULL,
                                   CRIADO_EM       TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
                                   ATUALIZADO_EM   TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
                                   CONSTRAINT PK_CELAS         PRIMARY KEY (ID),
                                   CONSTRAINT UQ_CELA_NUMERO   UNIQUE (NUMERO),
                                   CONSTRAINT CK_CELA_STATUS   CHECK (STATUS IN ('DISPONIVEL','OCUPADA','LOTADA','MANUTENCAO')),
                                   CONSTRAINT CK_CELA_TIPO     CHECK (TIPO IN ('INDIVIDUAL','COLETIVA','ISOLAMENTO','SEGURANCA_MAXIMA')),
                                   CONSTRAINT CK_CELA_CAP      CHECK (CAPACIDADE > 0)
);

CREATE TABLE PRISON_USER.DETENTOS (
                                      ID                      NUMBER          NOT NULL,
                                      MATRICULA               VARCHAR2(20)    NOT NULL,
                                      NOME                    VARCHAR2(150)   NOT NULL,
                                      CPF                     VARCHAR2(11)    NOT NULL,
                                      DATA_NASCIMENTO         DATE            NOT NULL,
                                      DATA_ENTRADA            DATE            NOT NULL,
                                      DATA_PREVISAO_SAIDA     DATE,
                                      DATA_SAIDA              DATE,
                                      STATUS                  VARCHAR2(20)    DEFAULT 'ATIVO' NOT NULL,
                                      REGIME                  VARCHAR2(20)    NOT NULL,
                                      CRIME                   VARCHAR2(200),
                                      SENTENCA_ANOS           NUMBER(3),
                                      CELA_ID                 NUMBER,
                                      CRIADO_EM               TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
                                      ATUALIZADO_EM           TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
                                      CONSTRAINT PK_DETENTOS          PRIMARY KEY (ID),
                                      CONSTRAINT UQ_DET_MATRICULA     UNIQUE (MATRICULA),
                                      CONSTRAINT UQ_DET_CPF           UNIQUE (CPF),
                                      CONSTRAINT FK_DET_CELA          FOREIGN KEY (CELA_ID) REFERENCES PRISON_USER.CELAS(ID),
                                      CONSTRAINT CK_DET_STATUS        CHECK (STATUS IN ('ATIVO','LIBERADO','TRANSFERIDO','FORAGIDO','FALECIDO')),
                                      CONSTRAINT CK_DET_REGIME        CHECK (REGIME IN ('FECHADO','SEMIABERTO','ABERTO')),
                                      CONSTRAINT CK_DET_CPF_LEN       CHECK (LENGTH(CPF) = 11)
);

-- ============ ÍNDICES ============

CREATE INDEX PRISON_USER.IDX_DETENTO_NOME    ON PRISON_USER.DETENTOS (NOME);
CREATE INDEX PRISON_USER.IDX_DETENTO_STATUS  ON PRISON_USER.DETENTOS (STATUS);
CREATE INDEX PRISON_USER.IDX_DETENTO_CELA    ON PRISON_USER.DETENTOS (CELA_ID);
CREATE INDEX PRISON_USER.IDX_CELA_BLOCO      ON PRISON_USER.CELAS (BLOCO);
CREATE INDEX PRISON_USER.IDX_CELA_STATUS     ON PRISON_USER.CELAS (STATUS);

-- ============ DADOS INICIAIS ============

-- Senha: admin123 (BCrypt)
INSERT INTO PRISON_USER.USUARIOS (ID, USERNAME, PASSWORD, NOME_COMPLETO, EMAIL, ROLE)
VALUES (PRISON_USER.SEQ_USUARIO.NEXTVAL, 'admin',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Administrador do Sistema', 'admin@prison.gov.br', 'ROLE_ADMIN');

-- Senha: gestor123 (BCrypt)
INSERT INTO PRISON_USER.USUARIOS (ID, USERNAME, PASSWORD, NOME_COMPLETO, EMAIL, ROLE)
VALUES (PRISON_USER.SEQ_USUARIO.NEXTVAL, 'gestor',
        '$2a$10$dGh1GZM.1lnKzIX38hQcaORCTGq5lsL.dBrCF4L.YQcQnGfZZPcYi',
        'Gestor Penitenciário', 'gestor@prison.gov.br', 'ROLE_GESTOR');

-- Senha: agente123 (BCrypt)
INSERT INTO PRISON_USER.USUARIOS (ID, USERNAME, PASSWORD, NOME_COMPLETO, EMAIL, ROLE)
VALUES (PRISON_USER.SEQ_USUARIO.NEXTVAL, 'agente',
        '$2a$10$p6ezEQQJFfMiAbT0OoYGxuXVQl7giyOVW5dFwUKHH0q.PvV29nRr6',
        'Agente Penitenciário', 'agente@prison.gov.br', 'ROLE_AGENTE');

-- Celas de exemplo
INSERT INTO PRISON_USER.CELAS (ID, NUMERO, BLOCO, CAPACIDADE, STATUS, TIPO)
VALUES (PRISON_USER.SEQ_CELA.NEXTVAL, 'A01', 'A', 4, 'DISPONIVEL', 'COLETIVA');

INSERT INTO PRISON_USER.CELAS (ID, NUMERO, BLOCO, CAPACIDADE, STATUS, TIPO)
VALUES (PRISON_USER.SEQ_CELA.NEXTVAL, 'A02', 'A', 4, 'DISPONIVEL', 'COLETIVA');

INSERT INTO PRISON_USER.CELAS (ID, NUMERO, BLOCO, CAPACIDADE, STATUS, TIPO)
VALUES (PRISON_USER.SEQ_CELA.NEXTVAL, 'B01', 'B', 1, 'DISPONIVEL', 'INDIVIDUAL');

INSERT INTO PRISON_USER.CELAS (ID, NUMERO, BLOCO, CAPACIDADE, STATUS, TIPO)
VALUES (PRISON_USER.SEQ_CELA.NEXTVAL, 'ISO01', 'ISO', 1, 'DISPONIVEL', 'ISOLAMENTO');

COMMIT;