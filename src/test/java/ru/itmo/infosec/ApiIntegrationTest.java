package ru.itmo.infosec;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import ru.itmo.infosec.auth.UserAccountRepository;
import ru.itmo.infosec.data.DataItemRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    private static final String USERNAME = "student";
    private static final String PASSWORD = "StrongPass1!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository users;

    @Autowired
    private DataItemRepository dataItems;

    @BeforeEach
    void clearDatabase() {
        dataItems.deleteAll();
        users.deleteAll();
    }

    @Test
    void registrationLoginAndProtectedDataFlowWorks() throws Exception {
        register(USERNAME, PASSWORD);
        String token = login(USERNAME, PASSWORD);

        mockMvc.perform(post("/api/data")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"<b>Проверка</b>","content":"<script>alert(1)</script>"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("&lt;b&gt;Проверка&lt;/b&gt;"))
            .andExpect(jsonPath("$.content").value("&lt;script&gt;alert(1)&lt;/script&gt;"));

        mockMvc.perform(get("/api/data").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(header().string("X-Content-Type-Options", "nosniff"))
            .andExpect(header().string("Content-Security-Policy", containsString("default-src 'none'")));
    }

    @Test
    void protectedEndpointRejectsMissingToken() throws Exception {
        mockMvc.perform(get("/api/data"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordAndSqlInjectionPayloadDoNotAuthenticate() throws Exception {
        register(USERNAME, PASSWORD);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(USERNAME, "WrongPassword1!")))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials("' OR 1=1 --", PASSWORD)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void duplicateRegistrationIsRejected() throws Exception {
        register(USERNAME, PASSWORD);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(USERNAME, PASSWORD)))
            .andExpect(status().isConflict());
    }

    private void register(String username, String password) throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(username, password)))
            .andExpect(status().isCreated());
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(username, password)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andReturn()
            .getResponse()
            .getContentAsString();
        return JsonPath.read(body, "$.accessToken");
    }

    private String credentials(String username, String password) {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    }
}
