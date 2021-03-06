package unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websiteskeleton.products.ProductsController;
import org.junit.*;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ProductsControllerTest {
    @InjectMocks ProductsController controller;

    @Mock RestTemplate restTemplate;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ProductsController(restTemplate, new ObjectMapper());
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void testGetProducts() throws Exception {
        doReturn("Some External Product").when(restTemplate).getForObject(anyString(), eq(String.class));

        String expectedJson = "[" +
            "{\"name\":\"Super Glue\"}," +
            "{\"name\":\"Kool-Aide\"}," +
            "{\"name\":\"Some External Product\"}" +
        "]";

        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedJson))
            .andExpect(header().string("content-type", "application/json"));
    }

    @Test
    public void testGetProducts_ExternalServerNotFound() throws Exception {
        doThrow(new RestClientException("server unavailable")).when(restTemplate).getForObject(anyString(), eq(String.class));

        String expectedJson = "[" +
            "{\"name\":\"Super Glue\"}," +
            "{\"name\":\"Kool-Aide\"}" +
        "]";

        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedJson))
            .andExpect(header().string("content-type", "application/json"));
    }
}
