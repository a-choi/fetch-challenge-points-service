package pointsservice.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Date;
import javax.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pointsservice.model.request.TransactionRequest;
import pointsservice.model.request.UserSpendRequest;
import pointsservice.service.PointsService;

@ActiveProfiles({"unit"})
@SpringBootTest
@AutoConfigureMockMvc
class PointsControllerIntegrationTest {

  public static final String POINTS_URL = "/points/user";

  @Autowired
  private WebApplicationContext applicationContext;
  private ObjectMapper mapper;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp () {
    mockMvc = MockMvcBuilders.webAppContextSetup(this.applicationContext).build();
    mapper = new ObjectMapper();
  }

  @Test
  void contextLoads() {
    ServletContext servletContext = applicationContext.getServletContext();
    assertNotNull(servletContext);
    assertTrue(servletContext instanceof MockServletContext);
    assertNotNull(applicationContext.getBean("pointsController"));
  }

  @Test
  void getPointsBalance_Valid_200() throws Exception {
    mockMvc.perform(get(POINTS_URL))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void getPointsBalance_EntityNotFound_404() throws Exception {
    mockMvc.perform(get("/points/user/99999"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void spendPoints_Valid_200() throws Exception {
    final var requestBody = new UserSpendRequest(0L);
    mockMvc.perform(patch(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk());
  }

  @Test
  void spendPoints_InsufficientBalance_418() throws Exception {
    final var requestBody = new UserSpendRequest(9000L);
    mockMvc.perform(patch(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isIAmATeapot());
  }

  @Test
  void spendPoints_NullPoints_400() throws Exception {
    var requestBody = new UserSpendRequest(null);
    mockMvc.perform(patch(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void spendPoints_NegativePoints_400() throws Exception {
    var requestBody = new UserSpendRequest(-1L);
    mockMvc.perform(patch(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addTransaction_Valid_200() throws Exception {
    var requestBody = new TransactionRequest("DANNON", 1000L, Date.from(Instant.parse("2020-11-02T14:00:00Z" )));
    mockMvc.perform(post(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk());
  }

  @Test
  void addTransaction_UserNotFound_404() throws Exception {
    var requestBody = new TransactionRequest("DANNON", 1000L, Date.from(Instant.parse("2020-11-02T14:00:00Z" )));
    mockMvc.perform(post(POINTS_URL + "/9999")
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isNotFound());
  }

  @Test
  void addTransaction_PayerNotFound_404() throws Exception {
    var requestBody = new TransactionRequest("NOT_FOUND", 1000L, Date.from(Instant.parse("2020-11-02T14:00:00Z" )));
    mockMvc.perform(post(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isNotFound());
  }

  @Test
  void addTransaction_NullPayer_400() throws Exception {
    var requestBody = new TransactionRequest(null, 1000L, Date.from(Instant.parse("2020-11-02T14:00:00Z" )));
    mockMvc.perform(post(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addTransaction_BlankPayer_400() throws Exception {
    var requestBody = new TransactionRequest("", 1000L, Date.from(Instant.parse("2020-11-02T14:00:00Z" )));
    mockMvc.perform(post(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addTransaction_NullTransactionPoints_400() throws Exception {
    var requestBody = new TransactionRequest("DANNON", null, Date.from(Instant.parse("2020-11-02T14:00:00Z" )));
    mockMvc.perform(post(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addTransaction_NullTimestamp_400() throws Exception {
    var requestBody = new TransactionRequest("DANNON", 1000L, null);
    mockMvc.perform(post(POINTS_URL)
        .contentType(APPLICATION_JSON).content(mapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
  }
}
