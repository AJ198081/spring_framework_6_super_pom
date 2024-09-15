package dev.aj.spring_6.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.spring_6.exceptionHandlers.NotFoundException;
import dev.aj.spring_6.model.BeerDTO;
import dev.aj.spring_6.model.BeerStyle;
import dev.aj.spring_6.services.BeerCsvService;
import dev.aj.spring_6.services.BeerService;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * When testing any non-safe HTTP methods and using Spring Security’s CSRF protection, you must include a valid CSRF Token in the request.
 * https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/csrf.html
 * */

@WebMvcTest(BeerController.class)
@AutoConfigureMockMvc
//@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class BeerControllerIT {

//    @Autowired //You can simply autowire 'mockMvc' or alternatively if you want to provide mocked springSecurity implementation then use webApplicationContext
    private MockMvc mockMvc;

    @MockBean
    private BeerService beerService;

    @MockBean
    private BeerCsvService beerCsvService;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<UUID> idCaptor;

    @Captor
    private ArgumentCaptor<BeerDTO> beerCaptor;

    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity()) // This is only required if you are not using any of SpringBoot Test slices, otherwise it is by default applied
                .build();
    }

    @SneakyThrows
    @Test
    void testUnAuthenticated401() {
        mockMvc.perform(get("/v1/beer/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testIsSuccessfulWith2xx() {
        mockMvc.perform(get("/v1/beer/all")
//                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @SneakyThrows
    @Test
    void getBeerById() {

//        Authentication authentication = new PreAuthenticatedAuthenticationToken("AJ", "Password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Authentication usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken("AJ", "Password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        TestSecurityContextHolder.setAuthentication(usernamePasswordAuthentication);

        List<BeerDTO> beerDTOS = List.of(
                BeerDTO.builder().id(UUID.randomUUID()).beerStyle(BeerStyle.LAGER).build(),
                BeerDTO.builder().id(UUID.randomUUID()).beerStyle(BeerStyle.IPA).build());

        Mockito.when(beerService.getBeerById(Mockito.any(UUID.class)))
                .thenAnswer(invocationOnMock -> {
                    UUID id = invocationOnMock.getArgument(0);
                    return beerDTOS.stream().filter(beerDTO -> beerDTO.getId().equals(id)).findFirst().orElse(null);
                });

        mockMvc.perform(get("/v1/beer/{id}", beerDTOS.get(1).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(containsString("IPA")),
                        jsonPath("$").isNotEmpty(),
                        jsonPath("$.id", is(beerDTOS.get(1).getId().toString())),
                        jsonPath("$.beerStyle", is("IPA")));

        TestSecurityContextHolder.clearContext();
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void getBeerByIdNotFound() {
        UUID id = UUID.randomUUID();
        String formattedResponse = String.format("BeerDTO with id %s not found", id);

        Mockito.when(beerService.getBeerById(id))
                .thenThrow(new NotFoundException(formattedResponse));

        mockMvc.perform(get("/v1/beer/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        content().string(is(formattedResponse))
                );

    }

    @SneakyThrows
    @Test
    @WithMockUser
    void getAllBeers() {
        Mockito.when(beerService.getAllBeers())
                .thenReturn(List.of(
                        BeerDTO.builder().id(UUID.randomUUID()).beerStyle(BeerStyle.LAGER).build(),
                        BeerDTO.builder().id(UUID.randomUUID()).beerStyle(BeerStyle.IPA).build()
                ));

        MvcResult mvcResult = mockMvc.perform(get("/v1/beer/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").isArray(),
                        jsonPath("$.[0].beerStyle", is("LAGER")),
                        jsonPath("$.[1].beerStyle", is("IPA")),
                        jsonPath("$.length()", is(2)))
                .andReturn();

        List<BeerDTO> beerDTOList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertThat(beerDTOList).hasSize(2);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void saveBeer() {

        UUID generatedId = UUID.randomUUID();
        Mockito.when(beerService.saveBeer(Mockito.any(BeerDTO.class))).thenAnswer(
                invocation -> {
                    BeerDTO beerDTO = invocation.getArgument(0);
                    beerDTO.setId(generatedId);
                    return beerDTO;
                });

        BeerDTO beerDTOToSave = BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("1.99"))
                .beerName("Galaxy Cat")
                .upc("289238kl")
                .quantityOnHand(1)
                .build();

        mockMvc.perform(post("/v1/beer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTOToSave)))
                .andExpectAll(
                        status().isCreated(),
                        header().exists("Location"),
                        header().string("Location", is("/v1/beer/" + generatedId)));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void throwsMethodArgumentsInvalidExceptionWhenFieldValidationsFail() {

        UUID generatedId = UUID.randomUUID();
        Mockito.when(beerService.saveBeer(Mockito.any(BeerDTO.class))).thenAnswer(
                invocation -> {
                    BeerDTO beerDTO = invocation.getArgument(0);
                    beerDTO.setId(generatedId);
                    return beerDTO;
                });

        BeerDTO beerDTOToSave = BeerDTO.builder()
                .id(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/v1/beer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTOToSave)))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$").isArray(),
                        jsonPath("$.length()", is(2)));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void updateBeer() {

        BeerDTO beerDTOToUpdate = BeerDTO.builder()
                .id(UUID.randomUUID())
                .beerName("Galaxy Cat")
                .build();

        mockMvc.perform(put("/v1/beer/{id}", beerDTOToUpdate.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTOToUpdate)))
                .andExpectAll(
                        status().isNoContent());

        Mockito.verify(beerService).updateBeer(idCaptor.capture(), beerCaptor.capture());
        Assertions.assertThat(idCaptor.getValue()).isEqualTo(beerDTOToUpdate.getId());
        Assertions.assertThat(beerCaptor.getValue().getBeerName()).isEqualTo(beerDTOToUpdate.getBeerName());
    }

    @SneakyThrows
    @Test
    @WithMockUser(authorities = {"DELETE"})
    void deleteBeer() {
        BeerDTO beerDTOToDelete = BeerDTO.builder()
                .id(UUID.randomUUID())
                .beerName("Galaxy Cat")
                .build();

        mockMvc.perform(delete("/v1/beer/{id}", beerDTOToDelete.getId())
                        .with(csrf()))
                .andExpectAll(
                        status().isAccepted());

        Mockito.verify(beerService).deleteBeerById(idCaptor.capture());
        Assertions.assertThat(idCaptor.getValue()).isEqualTo(beerDTOToDelete.getId());
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void patchBeer() {
        UUID beerId = UUID.randomUUID();
        String beerName = "Galaxy Cat";

        Map<String, Object> beerPropertiesToPatch = new HashMap<>();
        beerPropertiesToPatch.put("beerName", beerName);
        beerPropertiesToPatch.put("id", beerId);

        mockMvc.perform(patch("/v1/beer/{id}", beerId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerPropertiesToPatch)))
                .andExpectAll(
                        status().isNoContent());

        Mockito.verify(beerService).patchBeerById(idCaptor.capture(), beerCaptor.capture());

        Assertions.assertThat(idCaptor.getValue()).isEqualTo(beerId);
        Assertions.assertThat(beerCaptor.getValue().getBeerName()).isEqualTo(beerName);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListBeersByName() {
        mockMvc.perform(get("/v1/beer/all/csv")
                        .queryParam("beerName", "IPA"))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.size()", is(0))
                );
    }
}

