package br.com.devsuperior.dev_xp_ai;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class DevControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @BeforeEach
    void limparTabela() {
        jdbcTemplate.execute("DELETE FROM tb_developer_experience");
        jdbcTemplate.execute("DELETE FROM tb_developer_user");
    }
    // ---------------------------------------------------------------
    // Utilitarios
    // ---------------------------------------------------------------
    private String bodyValido(String email, String nickname) {
        return """
                {
                    "fullName": "Carlos Silva",
                    "email": "%s",
                    "nickname": "%s",
                    "uf": "SP",
                    "yearsOfExperience": 5,
                    "primaryLanguage": "Java",
                    "interestedInAi": true,
                    "skills": ["Spring", "Docker"]
                }
                """.formatted(email, nickname);
    }
    private long criarDesenvolvedor(String email, String nickname) throws Exception {
        MvcResult result = mockMvc.perform(post("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(email, nickname)))
                .andExpect(status().isCreated())
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        Matcher matcher = Pattern.compile("/developers/(\\d+)").matcher(location);
        matcher.find();
        return Long.parseLong(matcher.group(1));
    }
    // ---------------------------------------------------------------
    // POST /developers
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve criar desenvolvedor e retornar 201 com Location quando dados validos")
    void deveCriarDesenvolvedorQuandoDadosValidos() throws Exception {
        String body = """
                {
                    "fullName": "Carlos Silva",
                    "email": "carlos@email.com",
                    "nickname": "carlosdev",
                    "uf": "SP",
                    "yearsOfExperience": 5,
                    "primaryLanguage": "Java",
                    "interestedInAi": true,
                    "skills": ["Spring", "Docker"]
                }
                """;
        mockMvc.perform(post("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName").value("Carlos Silva"))
                .andExpect(jsonPath("$.email").value("carlos@email.com"))
                .andExpect(jsonPath("$.nickname").value("carlosdev"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.yearsOfExperience").value(5))
                .andExpect(jsonPath("$.primaryLanguage").value("Java"))
                .andExpect(jsonPath("$.interestedInAi").value(true))
                .andExpect(jsonPath("$.skills", hasSize(2)));
    }
    @Test
    @DisplayName("Deve retornar 400 com estrutura de erro quando campos invalidos no POST")
    void deveRetornar400QuandoCamposInvalidosNoCadastro() throws Exception {
        String body = """
                {
                    "fullName": "AB",
                    "email": "email-invalido",
                    "nickname": "x",
                    "uf": "XX",
                    "yearsOfExperience": -1,
                    "primaryLanguage": "",
                    "interestedInAi": null,
                    "skills": []
                }
                """;
        mockMvc.perform(post("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Falha de validacao"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", not(empty())));
    }
    @Test
    @DisplayName("Deve retornar 409 quando email ja cadastrado")
    void deveRetornar409QuandoEmailJaCadastrado() throws Exception {
        criarDesenvolvedor("dup@email.com", "nick-unico-1");
        mockMvc.perform(post("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido("dup@email.com", "outro-nick")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email ja cadastrado"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", not(empty())));
    }
    @Test
    @DisplayName("Deve retornar 409 quando nickname ja cadastrado")
    void deveRetornar409QuandoNicknameJaCadastrado() throws Exception {
        criarDesenvolvedor("primeiro@email.com", "nick-repetido");
        mockMvc.perform(post("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido("segundo@email.com", "nick-repetido")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Nickname ja cadastrado"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", not(empty())));
    }
    @Test
    @DisplayName("Deve retornar 400 quando correlationId ausente no POST")
    void deveRetornar400QuandoCorrelationIdAusenteNoCadastro() throws Exception {
        mockMvc.perform(post("/developers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido("sem-header@email.com", "sem-header-nick")))
                .andExpect(status().isBadRequest());
    }
    // ---------------------------------------------------------------
    // GET /developers
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve retornar 200 e lista de desenvolvedores quando existem registros")
    void deveRetornarListaDeDesenvolvedoresQuandoExistemRegistros() throws Exception {
        criarDesenvolvedor("dev1@email.com", "dev-um");
        criarDesenvolvedor("dev2@email.com", "dev-dois");
        mockMvc.perform(get("/developers")
                        .header("correlationId", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }
    @Test
    @DisplayName("Deve retornar 200 e lista vazia quando nao ha desenvolvedores")
    void deveRetornarListaVaziaQuandoNaoHaDesenvolvedores() throws Exception {
        mockMvc.perform(get("/developers")
                        .header("correlationId", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    @Test
    @DisplayName("Deve retornar 400 quando filtro UF invalido na listagem")
    void deveRetornar400QuandoFiltroUfInvalidoNaListagem() throws Exception {
        mockMvc.perform(get("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .param("uf", "XX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Filtro invalido"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", not(empty())));
    }
    @Test
    @DisplayName("Deve filtrar por UF e retornar somente desenvolvedores do estado informado")
    void deveRetornarApenasDesenvolvedoresDaUfFiltrada() throws Exception {
        criarDesenvolvedor("sp@email.com", "dev-sp");
        jdbcTemplate.update(
                "INSERT INTO tb_developer_user (full_name, email, nickname, uf) VALUES (?, ?, ?, ?)",
                "Ana Minas", "mg@email.com", "dev-mg", "MG"
        );
        Long userIdMg = jdbcTemplate.queryForObject(
                "SELECT id FROM tb_developer_user WHERE email = ?", Long.class, "mg@email.com"
        );
        jdbcTemplate.update(
                "INSERT INTO tb_developer_experience (developer_id, years_of_experience, primary_language, interested_in_ai, skills) VALUES (?, ?, ?, ?, ?)",
                userIdMg, 3, "Python", false, "Django,Flask"
        );
        mockMvc.perform(get("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .param("uf", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uf").value("SP"));
    }
    @Test
    @DisplayName("Deve filtrar por linguagem e retornar apenas desenvolvedores correspondentes")
    void deveRetornarApenasDesenvolvedoresDaLinguagemFiltrada() throws Exception {
        criarDesenvolvedor("java@email.com", "dev-java");
        jdbcTemplate.update(
                "INSERT INTO tb_developer_user (full_name, email, nickname, uf) VALUES (?, ?, ?, ?)",
                "Pedro Python", "python@email.com", "dev-py", "RJ"
        );
        Long userIdPython = jdbcTemplate.queryForObject(
                "SELECT id FROM tb_developer_user WHERE email = ?", Long.class, "python@email.com"
        );
        jdbcTemplate.update(
                "INSERT INTO tb_developer_experience (developer_id, years_of_experience, primary_language, interested_in_ai, skills) VALUES (?, ?, ?, ?, ?)",
                userIdPython, 2, "Python", false, "Django"
        );
        mockMvc.perform(get("/developers")
                        .header("correlationId", UUID.randomUUID().toString())
                        .param("language", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].primaryLanguage").value("Java"));
    }
    @Test
    @DisplayName("Deve retornar 400 quando correlationId ausente na listagem")
    void deveRetornar400QuandoCorrelationIdAusenteNaListagem() throws Exception {
        mockMvc.perform(get("/developers"))
                .andExpect(status().isBadRequest());
    }
    // ---------------------------------------------------------------
    // GET /developers/{id}
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve retornar 200 com dados do desenvolvedor quando id existe")
    void deveRetornarDesenvolvedorQuandoIdExiste() throws Exception {
        long id = criarDesenvolvedor("busca@email.com", "dev-busca");
        mockMvc.perform(get("/developers/{id}", id)
                        .header("correlationId", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("busca@email.com"))
                .andExpect(jsonPath("$.nickname").value("dev-busca"));
    }
    @Test
    @DisplayName("Deve retornar 404 quando desenvolvedor nao encontrado pelo id")
    void deveRetornar404QuandoIdInexistente() throws Exception {
        mockMvc.perform(get("/developers/{id}", 999999L)
                        .header("correlationId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Desenvolvedor nao encontrado"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", not(empty())));
    }
    @Test
    @DisplayName("Deve retornar 400 quando correlationId ausente na busca por id")
    void deveRetornar400QuandoCorrelationIdAusenteNaBuscaPorId() throws Exception {
        mockMvc.perform(get("/developers/{id}", 1L))
                .andExpect(status().isBadRequest());
    }
    // ---------------------------------------------------------------
    // PUT /developers/{id}/experience
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve atualizar anos de experiencia e retornar 200 quando dados validos")
    void deveAtualizarExperienciaQuandoDadosValidos() throws Exception {
        long id = criarDesenvolvedor("exp@email.com", "dev-exp");
        String body = """
                { "yearsOfExperience": 10 }
                """;
        mockMvc.perform(put("/developers/{id}/experience", id)
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.yearsOfExperience").value(10));
    }
    @Test
    @DisplayName("Deve retornar 400 quando yearsOfExperience invalido na atualizacao")
    void deveRetornar400QuandoExperienciaInvalidaNaAtualizacao() throws Exception {
        long id = criarDesenvolvedor("expinv@email.com", "dev-expinv");
        String body = """
                { "yearsOfExperience": 99 }
                """;
        mockMvc.perform(put("/developers/{id}/experience", id)
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Falha de validacao"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", not(empty())));
    }
    @Test
    @DisplayName("Deve retornar 404 quando desenvolvedor nao encontrado na atualizacao de experiencia")
    void deveRetornar404QuandoIdInexistenteNaAtualizacaoDeExperiencia() throws Exception {
        String body = """
                { "yearsOfExperience": 5 }
                """;
        mockMvc.perform(put("/developers/{id}/experience", 999999L)
                        .header("correlationId", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Desenvolvedor nao encontrado"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", not(empty())));
    }
    @Test
    @DisplayName("Deve retornar 400 quando correlationId ausente na atualizacao de experiencia")
    void deveRetornar400QuandoCorrelationIdAusenteNaAtualizacaoDeExperiencia() throws Exception {
        String body = """
                { "yearsOfExperience": 5 }
                """;
        mockMvc.perform(put("/developers/{id}/experience", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
