# 🏛️ Sistema de Gestão Prisional — Guia Completo

## Stack Utilizada

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework base |
| Spring Security + JWT | latest | Autenticação/Autorização |
| Spring Data JPA | latest | Acesso ao banco |
| Oracle Database Free | 23c | Banco de dados |
| Docker + Compose | latest | Containerização |
| Lombok | latest | Redução de boilerplate |

---

## 📁 Estrutura do Projeto

```
prison-management/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── init-db/
│   └── 01_schema.sql          ← DDL + dados iniciais
└── src/main/java/com/prison/
    ├── PrisonManagementApplication.java
    ├── config/
    │   └── SecurityConfig.java   ← JWT Filter + Security Chain
    ├── controller/
    │   ├── AuthController.java
    │   ├── CelaController.java
    │   └── DetentoController.java
    ├── dto/
    │   ├── AuthDTO.java
    │   ├── CelaDTO.java
    │   └── DetentoDTO.java
    ├── exception/
    │   ├── BusinessException.java
    │   ├── GlobalExceptionHandler.java
    │   └── ResourceNotFoundException.java
    ├── model/
    │   ├── Cela.java
    │   ├── Detento.java
    │   └── Usuario.java
    ├── repository/
    │   ├── CelaRepository.java
    │   ├── DetentoRepository.java
    │   └── UsuarioRepository.java
    └── service/
        ├── CelaService.java
        ├── DetentoService.java
        └── JwtService.java
```

---

## 🚀 Passo a Passo para Rodar o Projeto

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado
- [Git](https://git-scm.com/) instalado
- Java 21 + Maven instalados (apenas para desenvolvimento local)

---

### Passo 1 — Baixar o Docker

Certifique-se que o Docker Desktop está rodando. Confirme com:

```bash
docker --version
docker compose version
```

---

### Passo 2 — Estrutura de Pastas

Organize o projeto com esta estrutura e copie todos os arquivos nos locais corretos conforme mostrado acima.

---

### Passo 3 — Subir os containers

Na raiz do projeto (`prison-management/`), execute:

```bash
docker compose up --build
```

O que vai acontecer:
1. Docker vai baixar a imagem `gvenzl/oracle-free:23-slim` (~800MB)
2. Vai compilar o projeto Java com Maven (dentro do container)
3. Vai criar o schema e os dados iniciais no Oracle automaticamente
4. A aplicação Spring Boot vai subir na porta **8080**

> ⏳ Na **primeira execução**, aguarde de 3 a 5 minutos para o Oracle inicializar completamente.

---

### Passo 4 — Verificar se subiu

Acesse: http://localhost:8080/actuator/health

Resposta esperada:
```json
{ "status": "UP" }
```

---

### Passo 5 — Fazer login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "username": "admin",
  "role": "ROLE_ADMIN"
}
```

Guarde o `token` para usar nas próximas requisições.

---

## 🔑 Usuários Padrão

| Usuário | Senha | Role | Permissões |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | Tudo |
| `gestor` | `gestor123` | GESTOR | Criar/editar detentos e celas |
| `agente` | `agente123` | AGENTE | Apenas leitura |

---

## 📡 Endpoints da API

### 🔐 Auth

| Método | URL | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Login e obtenção do JWT |

### 🏠 Celas

| Método | URL | Role mínima | Descrição |
|---|---|---|---|
| GET | `/api/celas` | AGENTE | Listar celas (paginado) |
| GET | `/api/celas/{id}` | AGENTE | Buscar cela por ID |
| GET | `/api/celas/com-vaga` | AGENTE | Celas com vagas disponíveis |
| POST | `/api/celas` | GESTOR | Criar nova cela |
| PUT | `/api/celas/{id}` | GESTOR | Atualizar cela |
| PATCH | `/api/celas/{id}/manutencao` | ADMIN | Colocar em manutenção |

### 👤 Detentos

| Método | URL | Role mínima | Descrição |
|---|---|---|---|
| GET | `/api/detentos` | AGENTE | Listar detentos (paginado + filtros) |
| GET | `/api/detentos/{id}` | AGENTE | Buscar por ID |
| GET | `/api/detentos/matricula/{mat}` | AGENTE | Buscar por matrícula |
| POST | `/api/detentos` | GESTOR | Cadastrar detento |
| PUT | `/api/detentos/{id}` | GESTOR | Atualizar detento |
| PATCH | `/api/detentos/{id}/liberar` | GESTOR | Liberar detento |
| PATCH | `/api/detentos/{id}/transferir/{celaId}` | GESTOR | Transferir para outra cela |

---

## 📋 Exemplos de Uso com cURL

### Criar uma cela

```bash
curl -X POST http://localhost:8080/api/celas \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "C01",
    "bloco": "C",
    "capacidade": 4,
    "tipo": "COLETIVA"
  }'
```

### Cadastrar um detento

```bash
curl -X POST http://localhost:8080/api/detentos \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva",
    "cpf": "12345678901",
    "dataNascimento": "1985-06-15",
    "dataEntrada": "2024-01-10",
    "dataPrevisaoSaida": "2030-01-10",
    "regime": "FECHADO",
    "crime": "Roubo qualificado",
    "sentencaAnos": 6,
    "celaId": 1
  }'
```

### Listar detentos com filtro

```bash
curl "http://localhost:8080/api/detentos?nome=joao&status=ATIVO&page=0&size=10" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Liberar um detento

```bash
curl -X PATCH http://localhost:8080/api/detentos/1/liberar \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Transferir detento para outra cela

```bash
curl -X PATCH http://localhost:8080/api/detentos/1/transferir/3 \
  -H "Authorization: Bearer SEU_TOKEN"
```

---

## 🧠 Regras de Negócio Implementadas

1. **CPF único** — Não é possível cadastrar dois detentos com o mesmo CPF
2. **Matrícula automática** — Gerada no padrão `DETaaaa#####` (ex: `DET202400001`)
3. **Controle de capacidade** — Não permite alocar detento em cela lotada
4. **Status de cela automático** — DISPONÍVEL → OCUPADA → LOTADA conforme ocupação
5. **Manutenção bloqueada** — Cela com detentos não pode ir para manutenção
6. **Liberação registra data** — `dataSaida` é preenchida automaticamente
7. **Roles hierárquicas** — AGENTE só lê; GESTOR cria/edita; ADMIN controla tudo

---

## 🛑 Parar e Limpar

```bash
# Parar os containers (preserva dados)
docker compose stop

# Parar e remover containers (preserva volume Oracle)
docker compose down

# Parar e apagar TUDO (inclusive banco de dados!)
docker compose down -v
```

---

## 🔧 Desenvolvimento Local (sem Docker)

Se quiser rodar o projeto local com um Oracle já existente:

```bash
# 1. Configure o application.yml com seus dados de conexão

# 2. Execute o DDL manualmente no SQL*Plus ou SQL Developer:
#    init-db/01_schema.sql

# 3. Rode a aplicação
mvn spring-boot:run
```

---

## 📈 Próximos Passos Sugeridos

- [ ] Módulo de **Visitas** (visitantes, agendamento, controle)
- [ ] Módulo de **Ocorrências** (registros disciplinares)
- [ ] Módulo de **Trabalho/Remição** (redução de pena)
- [ ] **Relatórios** com export PDF/Excel
- [ ] **Audit trail** com Spring Data Envers
- [ ] **Swagger/OpenAPI** para documentação automática
- [ ] Testes unitários com JUnit 5 + Mockito
- [ ] CI/CD com GitHub Actions
