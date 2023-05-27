package com.vodafone.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.errorhandlling.ApiExceptionHandler;
import com.vodafone.errorhandlling.NotFoundException;
import com.vodafone.model.Article;
import com.vodafone.service.ArticleService;
import com.vodafone.service.ArticleServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ArticlesControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleServiceImpl articleService;

    @InjectMocks
    public ArticlesController articlesController;

    private AutoCloseable mockClosable;

    @Before
    public void setup() {
        mockClosable = MockitoAnnotations.openMocks(this);
   /*     mockMvc = MockMvcBuilders.standaloneSetup(articlesController)
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

    // Get All Articles (No Author)
    @Test
    public void Should_GetAllArticlesWithOKStatusCode_WhenGetArticlesWithNoAuthor() throws Exception {
        ResponseEntity<List<Article>> responseEntity = articlesController.getArticles(null);

        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        Article article1 = new Article();
        Article article2 = new Article();
        List<Article> articleList = Arrays.asList(article1, article2);

        when(articleService.getAllArticles()).thenReturn(articleList);

        mockMvc.perform(get("/v1/articles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    // Get All Articles From Author
    @Test
    public void Should_GetAllArticlesWithOKStatusCode_WhenGetArticlesWithAuthor() throws Exception {
        String authorName = "author of successful Name";
        ResponseEntity<List<Article>> responseEntity = articlesController.getArticles(authorName);
        Article article1 = new Article();
        List<Article> articleList = Arrays.asList(article1);

        when(articleService.getAllArticles()).thenReturn(articleList);

        mockMvc.perform(get("/v1/articles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Verify mock service interaction
        verify(articleService, times(1)).getAllArticles();

    }

    // Get All Articles From Author
    @Test
    public void Should_GetAllArticlesWithEmptyResultSetWithOKStatusCode_WhenGetArticlesWitAuthorButNoResult() throws Exception {
        String authorName = "author of successful Name";
        ResponseEntity<List<Article>> responseEntity = articlesController.getArticles(authorName);
        List<Article> articleList = new ArrayList<>();


        when(articleService.getAllArticles()).thenReturn(articleList);

        mockMvc.perform(get("/v1/articles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verify mock service interaction
        verify(articleService, times(1)).getAllArticles();

    }


    // Get Article By ID Found In DataSource
    @Test
    public void should_GetArticleByID_WhenIDIsFoundInDataSource() throws Exception {
        int validIDInDataSource = 1;
        ResponseEntity<Article> responseEntity = articlesController.getArticle(validIDInDataSource);
        Article article1 = new Article();
        when(articleService.getArticleById(validIDInDataSource)).thenReturn(article1);

        mockMvc.perform(get("/v1/articles/" + validIDInDataSource).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));

        // Verify mock service interaction
        verify(articleService, times(2)).getArticleById(validIDInDataSource);
    }

    // Get Article By ID NOT found in Data Source
    @Test
    public void should_GetArticleByID_WhenIDIsNOTFoundInDataSource() throws Exception {
        int invalidIdInDataSource = 1;
        when(articleService.getArticleById(invalidIdInDataSource)).thenThrow(NotFoundException.class);


        var vale = mockMvc.perform(get("/v1/articles/" + invalidIdInDataSource)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

    }


    // Add Valid New Article
    @Test
    public void should_AddArticle_WhenProvidedWithValidArticle() throws Exception {
        Article validArticle = new Article();
        validArticle.setId(1);
        validArticle.setName("name");
        validArticle.setAuthor("validNameOfAutohr");
        validArticle.setAuthorId(1);

        when(articleService.doesArticleExist(anyString())).thenReturn(false);
        when(articleService.addArticle(any(Article.class))).thenReturn(validArticle);

        mockMvc.perform(post("/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validArticle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(validArticle.getName()))
                .andExpect(jsonPath("$.id").value(validArticle.getId()));

    }

    // Add Existing Article
    @Test
    public void should_AddArticle_WhenProvidedWithDuplicateArticle() throws Exception {
        Article validArticle = new Article();
        validArticle.setId(1);
        validArticle.setName("name");
        validArticle.setAuthor("validNameOfAutohr");
        validArticle.setAuthorId(1);

        when(articleService.doesArticleExist(anyString())).thenReturn(true);

        mockMvc.perform(post("/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validArticle)))
                .andExpect(status().isConflict());
    }

    // ----------------------------
    // Update Article With WRONG ID
    @Test
    public void should_UpdateArticleThrowNotFoundException_WhenArticleIDIsNotValid() throws Exception {
        int invalidArticleId = 1;
        Article invalidArticle = new Article();
        invalidArticle.setId(invalidArticleId);
        invalidArticle.setName("name");
        invalidArticle.setAuthor("invalidNameOfAutohr");
        invalidArticle.setAuthorId(1);
        when(articleService.updateArticle(any(Integer.class), any(Article.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(put("/v1/articles/" + invalidArticleId)
                        .content(asJsonString(invalidArticle))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    // Update Article With Valid ID
    @Test
    public void should_UpdateArticle_WhenProvidedWithValidID() throws Exception {

        int validIDInDataSource = 1;
        Article article1 = new Article();
        article1.setId(validIDInDataSource);
        article1.setAuthor("auth");
        article1.setName("uesrName");
        when(articleService.updateArticle(eq(validIDInDataSource), any(Article.class))).thenReturn(article1);

        mockMvc.perform(post("/v1/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(article1)));

        article1.setName("NAAAAAAME");

        mockMvc.perform(put("/v1/articles/" + validIDInDataSource)
                        .content(asJsonString(article1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(article1.getName()));


    }


    // ----------------------------
    // Delete Article With Wrong ID
    @Test
    public void should_DeleteArticle_WhenArticleIDIsNotValid() throws Exception {
        int isNotValidID = 1;
        Article article1 = new Article();
        article1.setId(isNotValidID);
        article1.setAuthor("auth");
        article1.setName("uesrName");
        doThrow(NotFoundException.class).when(articleService).deleteArticle(isNotValidID);

        mockMvc.perform(delete("/v1/articles/" + isNotValidID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    // Delete Article With Valid ID
    @Test
    public void should_DeleteArticle_WhenArticleIDIsValid() throws Exception {
        int validIDInDataSource = 1;
        Article article1 = new Article();
        article1.setId(validIDInDataSource);
        article1.setAuthor("auth");
        article1.setName("uesrName");
        when(articleService.getArticleById(validIDInDataSource)).thenReturn(article1);
        articleService.addArticle(article1);

        mockMvc.perform(delete("/v1/articles/" + validIDInDataSource).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
}