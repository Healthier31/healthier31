package com.seb45_main_031.routine.savedTodoTest.controllerTest;

import com.google.gson.Gson;
import com.seb45_main_031.routine.member.service.MemberService;
import com.seb45_main_031.routine.savedTodo.dto.SavedTodoDto;
import com.seb45_main_031.routine.savedTodo.entity.SavedTodo;
import com.seb45_main_031.routine.savedTodo.mapper.SavedTodoMapper;
import com.seb45_main_031.routine.savedTodo.service.SavedTodoService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.seb45_main_031.routine.util.ApiDocumentUtils.getRequestPreprocessor;
import static com.seb45_main_031.routine.util.ApiDocumentUtils.getResponsePreprocessor;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class SavedTodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;

    @MockBean
    private SavedTodoService savedTodoService;
    @MockBean
    private SavedTodoMapper mapper;
    @MockBean
    private MemberService memberService;


    private static SavedTodo savedTodo;
    private static SavedTodo savedTodo2;
    private static SavedTodoDto.Post savedTodoPostDto;
    private static SavedTodoDto.Post savedTodoPostDto2;
    private static SavedTodoDto.PostList savedTodoPostListDto;
    private static SavedTodoDto.Patch savedTodoPatchDto;
    private static SavedTodoDto.Response response;

    @BeforeAll
    static void intiAll(){

        savedTodo = new SavedTodo();
        savedTodo.setSavedTodoId(1L);
        savedTodo.setContent("내용");
        savedTodo.setEmoji("이모지");

        savedTodo2 = new SavedTodo();
        savedTodo2.setSavedTodoId(2L);
        savedTodo2.setContent("내용2");
        savedTodo2.setEmoji("이모지2");

        savedTodoPostDto = new SavedTodoDto.Post();
        savedTodoPostDto.setContent("내용");
        savedTodoPostDto.setEmoji("이모지");
        savedTodoPostDto.setTodoStorageId(1L);
        savedTodoPostDto.setTagId(1L);
        savedTodoPostDto.setMemberId(1L);

        savedTodoPostDto2 = new SavedTodoDto.Post();
        savedTodoPostDto2.setContent("내용2");
        savedTodoPostDto2.setEmoji("이모지2");
        savedTodoPostDto2.setTodoStorageId(1L);
        savedTodoPostDto2.setTagId(1L);
        savedTodoPostDto2.setMemberId(1L);


        savedTodoPostListDto = new SavedTodoDto.PostList();
        savedTodoPostListDto.setPosts(List.of(savedTodoPostDto, savedTodoPostDto2));
        savedTodoPostListDto.setTodoStorageId(1L);


        savedTodoPatchDto = new SavedTodoDto.Patch();
        savedTodoPatchDto.setSavedTodoId(1L);
        savedTodoPatchDto.setContent("내용");
        savedTodoPatchDto.setEmoji("이모지");
        savedTodoPatchDto.setTagId(1L);


        response = SavedTodoDto.Response.builder()
                .savedTodoId(1L)
                .content("내용")
                .emoji("이모지")
                .build();

        SavedTodoDto.UserInfo userInfo = SavedTodoDto.UserInfo.builder()
                .memberId(1L)
                .build();

        SavedTodoDto.TodoStorageResponse todoStorageResponse = SavedTodoDto.TodoStorageResponse.builder()
                .todoStorageId(1L)
                .category("카테고리")
                .build();

        SavedTodoDto.TagResponse tagResponse = SavedTodoDto.TagResponse.builder()
                .tagId(1L)
                .tagName("태그이름")
                .build();

        response.setUserInfo(userInfo);
        response.setTodoStorageResponse(todoStorageResponse);
        response.setTagResponse(tagResponse);

    }


    @Test
    @WithMockUser(roles = "USER")
    void postSavedTodoTest() throws Exception{

        String requestBody = gson.toJson(savedTodoPostDto);

        given(memberService.findMemberId(Mockito.any(String.class))).willReturn(1L);
        given(mapper.savedTodoPostDtoToSavedTodo(Mockito.any(SavedTodoDto.Post.class))).willReturn(new SavedTodo());
        given(savedTodoService.createSavedTodo(Mockito.any(SavedTodo.class))).willReturn(savedTodo);

        ResultActions resultActions = mockMvc.perform(
                post("/savedTodos")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/savedTodos"))))
                .andDo(document(
                        "post-savedTodo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("emoji").type(JsonFieldType.STRING).description("이모지"),
                                        fieldWithPath("todoStorageId").type(JsonFieldType.NUMBER).description("투두 스토리지 식별자"),
                                        fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 식별자"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자").ignored()
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        )
                ));
    }

    @Test
    @WithMockUser(roles = "USER")
    void postSavedTodosTest() throws Exception{

        String requestBody = gson.toJson(savedTodoPostListDto);

        given(memberService.findMemberId(Mockito.any(String.class))).willReturn(1L);
        given(mapper.savedTodoPostListDtoToSavedTodos(
                Mockito.any(SavedTodoDto.PostList.class))).willReturn(List.of(savedTodo, savedTodo2));
        given(savedTodoService.createSavedTodos(Mockito.any(List.class))).willReturn(List.of(savedTodo, savedTodo2));

        ResultActions resultActions = mockMvc.perform(
                post("/savedTodos/lists")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andDo(document(
                        "post-savedTodos",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("posts").type(JsonFieldType.ARRAY).description("POST LIST"),
                                        fieldWithPath("posts[].content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("posts[].emoji").type(JsonFieldType.STRING).description("이모지"),
                                        fieldWithPath("posts[].todoStorageId")
                                                .type(JsonFieldType.NUMBER).description("투두 스토리지 식별자").ignored(),
                                        fieldWithPath("posts[].tagId").type(JsonFieldType.NUMBER).description("태그 식별자"),
                                        fieldWithPath("posts[].memberId")
                                                .type(JsonFieldType.NUMBER).description("회원 식별자").ignored(),
                                        fieldWithPath("todoStorageId").type(JsonFieldType.NUMBER).description("투두 스토리지 식별자")
                                )
                        )
                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void patchSavedTodoTest() throws Exception{

        String requestBody = gson.toJson(savedTodoPatchDto);

        given(mapper.savedTodoPatchDtoToSavedTodo(Mockito.any(SavedTodoDto.Patch.class))).willReturn(new SavedTodo());
        given(savedTodoService.updateSavedTodo(Mockito.any(SavedTodo.class), Mockito.any(String.class)))
                .willReturn(new SavedTodo());
        given(mapper.savedTodoToSavedTodoResponseDto(Mockito.any(SavedTodo.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/savedTodos/{savedTodo-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.savedTodoId").value(response.getSavedTodoId()))
                .andDo(document(
                        "patch-savedTodo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("savedTodo-id").description("저장 투두 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("savedTodoId").type(JsonFieldType.NUMBER).description("저장 투두 식별자").ignored(),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용").optional(),
                                        fieldWithPath("emoji").type(JsonFieldType.STRING).description("이모지").optional(),
                                        fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 식별자").optional()
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.savedTodoId").type(JsonFieldType.NUMBER).description("저장 투두 식별자"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.emoji").type(JsonFieldType.STRING).description("이모지"),
                                        fieldWithPath("data.userInfo").type(JsonFieldType.OBJECT).description("유저 정보"),
                                        fieldWithPath("data.userInfo.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data.todoStorageResponse").type(JsonFieldType.OBJECT)
                                                .description("투두 스토리지 정보"),
                                        fieldWithPath("data.todoStorageResponse.todoStorageId").type(JsonFieldType.NUMBER)
                                                .description("투두 스토리지 식별자"),
                                        fieldWithPath("data.todoStorageResponse.category").type(JsonFieldType.STRING)
                                                .description("투두 스토리지 카테고리"),
                                        fieldWithPath("data.tagResponse").type(JsonFieldType.OBJECT)
                                                .description("태그 정보"),
                                        fieldWithPath("data.tagResponse.tagId").type(JsonFieldType.NUMBER)
                                                .description("태그 식별자"),
                                        fieldWithPath("data.tagResponse.tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름")
                                )
                        )
                ));

    }


    @Test
    @WithMockUser(roles = "USER")
    void getSavedTodoTest() throws Exception{

        given(savedTodoService.findSavedTodo(Mockito.any(long.class), Mockito.any(String.class))).willReturn(new SavedTodo());
        given(mapper.savedTodoToSavedTodoResponseDto(Mockito.any(SavedTodo.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                get("/savedTodos/{savedTodo-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.savedTodoId").value(response.getSavedTodoId()))
                .andDo(document(
                        "get-savedTodo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("savedTodo-id").description("저장 투두 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.savedTodoId").type(JsonFieldType.NUMBER).description("저장 투두 식별자"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.emoji").type(JsonFieldType.STRING).description("이모지"),
                                        fieldWithPath("data.userInfo").type(JsonFieldType.OBJECT).description("유저 정보"),
                                        fieldWithPath("data.userInfo.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data.todoStorageResponse").type(JsonFieldType.OBJECT)
                                                .description("투두 스토리지 정보"),
                                        fieldWithPath("data.todoStorageResponse.todoStorageId").type(JsonFieldType.NUMBER)
                                                .description("투두 스토리지 식별자"),
                                        fieldWithPath("data.todoStorageResponse.category").type(JsonFieldType.STRING)
                                                .description("투두 스토리지 카테고리"),
                                        fieldWithPath("data.tagResponse").type(JsonFieldType.OBJECT)
                                                .description("태그 정보"),
                                        fieldWithPath("data.tagResponse.tagId").type(JsonFieldType.NUMBER)
                                                .description("태그 식별자"),
                                        fieldWithPath("data.tagResponse.tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름")
                                )
                        )
                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteSavedTodoTest() throws Exception{

        doNothing().when(savedTodoService).deleteSavedTodo(Mockito.any(long.class), Mockito.any(String.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/savedTodos/{savedTodo-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-savedTodo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("savedTodo-id").description("저장 투두 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));
    }


}
