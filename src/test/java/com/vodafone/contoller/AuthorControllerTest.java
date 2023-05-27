package com.vodafone.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.errorhandlling.NotFoundException;
import com.vodafone.model.Article;
import com.vodafone.model.Author;
import com.vodafone.service.ArticleServiceImpl;
import com.vodafone.service.AuthorService;
import com.vodafone.service.AuthorServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class AuthorControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorServiceImpl authorService;

    @InjectMocks
    public AuthorController authorController;

    private AutoCloseable mockClosable;

    @Before
    public void setup() {
        mockClosable = MockitoAnnotations.openMocks(this);
   /*     mockMvc = MockMvcBuilders.standaloneSetup(authorController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();*/
    }

    @After
    public void tearDown() throws Exception {
        mockClosable.close();
    }


    private String asJsonString(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }



    // Get All Authors
    @Test
    public void Should_GetAllAuthorsWithOKStatusCode_WhenGetAuthor() throws Exception {
        ResponseEntity<List<Author>> responseEntity = authorController.getAuthors();

        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        Author author1 = new Author();
        Author author2 = new Author();
        List<Author> authorList = Arrays.asList(author1, author2);

        when(authorService.getAllAuthors()).thenReturn(authorList);

        mockMvc.perform(get("/v1/authors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    public void Should_GetAllAuthorsWithEmptyResultSetWithOKStatusCode_WhenGetAuthorsButNoResult() throws Exception {
        ResponseEntity<List<Author>> responseEntity = authorController.getAuthors();
        List<Author> authorsList = new ArrayList<>();
        when(authorService.getAllAuthors()).thenReturn(authorsList);

        mockMvc.perform(get("/v1/authors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

    }

    // Get Author By ID Found In DataSource
    @Test
    public void should_GetAuthorByID_WhenIDIsFoundInDataSource() throws Exception {
        int validIDInDataSource = 1;
        ResponseEntity<Author> responseEntity = authorController.getAuthorById(validIDInDataSource);
        Author author1 = new Author();
        when(authorService.getAuthorById(validIDInDataSource)).thenReturn(author1);

        mockMvc.perform(get("/v1/authors/" + validIDInDataSource).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Verify mock service interaction
        verify(authorService, times(2)).getAuthorById(validIDInDataSource);
    }

    // Get Author By ID NOT found in Data Source
    @Test
    public void should_GetAuthorByID_WhenIDIsNOTFoundInDataSource() throws Exception {
        int invalidIdInDataSource = 1;
        when(authorService.getAuthorById(invalidIdInDataSource)).thenThrow(NotFoundException.class);


         mockMvc.perform(get("/v1/authors/" + invalidIdInDataSource)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

    }


    // Add Valid New Author
    @Test
    public void should_AddAuthor_WhenProvidedWithValidAuthor() throws Exception {
       Author validAuthor = new Author();
       validAuthor.setId(1);
       validAuthor.setName("Name of author");

        when(authorService.addAuthor(any(Author.class))).thenReturn(validAuthor);

        mockMvc.perform(post("/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validAuthor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(validAuthor.getName()))
                .andExpect(jsonPath("$.id").value(validAuthor.getId()));

    }

}