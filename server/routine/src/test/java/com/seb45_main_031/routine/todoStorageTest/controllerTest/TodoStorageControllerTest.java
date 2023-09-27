package com.seb45_main_031.routine.todoStorageTest.controllerTest;

import com.google.gson.Gson;
import com.seb45_main_031.routine.member.service.MemberService;
import com.seb45_main_031.routine.todoStorage.dto.TodoStorageDto;
import com.seb45_main_031.routine.todoStorage.entity.TodoStorage;
import com.seb45_main_031.routine.todoStorage.mapper.TodoStorageMapper;
import com.seb45_main_031.routine.todoStorage.service.TodoStorageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class TodoStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private TodoStorageService todoStorageService;
    @MockBean
    private TodoStorageMapper mapper;
    @MockBean
    private MemberService memberService;

    private static TodoStorage todoStorage;
    private static TodoStorage todoStorage2;
    private static TodoStorageDto.Post todoStoragePostDto;
    private static TodoStorageDto.Patch todoStoragePatchDto;
    private static TodoStorageDto.Response response;
    private static TodoStorageDto.Response response2;
    private static TodoStorageDto.StorageResponse storageResponse;

    @BeforeAll
    static void initAll(){

        todoStorage = new TodoStorage();
        todoStorage.setTodoStorageId(1L);
        todoStorage.setCategory("카테고리");

        todoStorage2 = new TodoStorage();
        todoStorage2.setTodoStorageId(2L);
        todoStorage2.setCategory("카테고리2");


        todoStoragePostDto = new TodoStorageDto.Post();
        todoStoragePostDto.setCategory("카테고리");
        todoStoragePostDto.setMemberId(1L);

        TodoStorageDto.SavedTodoPost savedTodoPostDto = new TodoStorageDto.SavedTodoPost();
        savedTodoPostDto.setContent("내용");
        savedTodoPostDto.setEmoji("이모지");
        savedTodoPostDto.setTagId(1L);

        todoStoragePostDto.setSavedTodoPosts(List.of(savedTodoPostDto));


        todoStoragePatchDto = new TodoStorageDto.Patch();
        todoStoragePatchDto.setTodoStorageId(1L);
        todoStoragePatchDto.setCategory("카테고리");


        response = TodoStorageDto.Response.builder()
                .todoStorageId(1L)
                .category("카테고리")
                .build();

        TodoStorageDto.UserInfo userInfo = TodoStorageDto.UserInfo.builder()
                .memberId(1L).build();

        response.setUserInfo(userInfo);

        response2 = TodoStorageDto.Response.builder()
                .todoStorageId(2L)
                .category("카테고리2")
                .userInfo(userInfo)
                .build();


        storageResponse = TodoStorageDto.StorageResponse.builder()
                .todoStorageId(1L)
                .category("카테고리")
                .userInfo(userInfo)
                .build();

        TodoStorageDto.TagResponse tagResponse = TodoStorageDto.TagResponse.builder()
                .tagId(1L)
                .tagName("태그이름")
                .build();

        TodoStorageDto.SavedTodoResponse savedTodoResponse = TodoStorageDto.SavedTodoResponse.builder()
                .savedTodoId(1L)
                .content("내용")
                .emoji("이모지")
                .tagResponse(tagResponse)
                .memberId(1L)
                .build();

        TodoStorageDto.SavedTodoResponse savedTodoResponse2 = TodoStorageDto.SavedTodoResponse.builder()
                .savedTodoId(2L)
                .content("내용2")
                .emoji("이모지2")
                .tagResponse(tagResponse)
                .memberId(1L)
                .build();

        storageResponse.setSavedTodoResponses(List.of(savedTodoResponse, savedTodoResponse2));

    }

    @Test
    @WithMockUser(roles = "USER")
    void postTodoStorageTest() throws Exception{

        String requestBody = gson.toJson(todoStoragePostDto);

        given(memberService.findMemberId(Mockito.any(String.class))).willReturn(1L);
        given(mapper.todoStoragePostDtoToTodoStorage(Mockito.any(TodoStorageDto.Post.class))).willReturn(new TodoStorage());
        given(todoStorageService.createTodoStorage(Mockito.any(TodoStorage.class))).willReturn(todoStorage);

        ResultActions resultActions = mockMvc.perform(
                post("/todoStorages")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/todoStorages"))))
                .andDo(document(
                        "post-todoStorage",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자").ignored(),
                                        fieldWithPath("savedTodoPosts").type(JsonFieldType.ARRAY)
                                                .description("저장 투두 리스트").optional(),
                                        fieldWithPath("savedTodoPosts[].content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("savedTodoPosts[].emoji").type(JsonFieldType.STRING).description("이모지"),
                                        fieldWithPath("savedTodoPosts[].tagId").type(JsonFieldType.NUMBER).description("태그 식별자")
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        )
                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void patchTodoStorageTest() throws Exception{

        String requestBody = gson.toJson(todoStoragePatchDto);

        given(mapper.todoStoragePatchDtoToTodoStorage(Mockito.any(TodoStorageDto.Patch.class))).willReturn(new TodoStorage());
        given(todoStorageService.updateTodoStorage(Mockito.any(TodoStorage.class), Mockito.any(String.class)))
                .willReturn(new TodoStorage());
        given(mapper.todoStorageToTodoStorageResponseDto(Mockito.any(TodoStorage.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/todoStorages/{todoStorage-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todoStorageId").value(response.getTodoStorageId()))
                .andDo(document(
                        "patch-todoStorage",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("todoStorage-id").description("투두 스토리지 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("todoStorageId").type(JsonFieldType.NUMBER)
                                                .description("투두 스토리지 식별자").ignored(),
                                        fieldWithPath("category").type(JsonFieldType.STRING)
                                                .description("카테고리").optional()
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.todoStorageId").type(JsonFieldType.NUMBER)
                                                .description("투두 스토리지 식별자"),
                                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.userInfo").type(JsonFieldType.OBJECT).description("유저 정보"),
                                        fieldWithPath("data.userInfo.memberId").type(JsonFieldType.NUMBER).description("회원 식별자")
                                )
                        )
                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void getTodoStorageTest() throws Exception{

        given(todoStorageService.findTodoStorage(Mockito.any(long.class), Mockito.any(String.class)))
                .willReturn(new TodoStorage());
        given(mapper.todoStorageToTodoStorageResponseDto(Mockito.any(TodoStorage.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                get("/todoStorages/{todoStorage-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todoStorageId").value(response.getTodoStorageId()))
                .andDo(document(
                        "get-todoStorage",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("todoStorage-id").description("투두 스토리지 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.todoStorageId").type(JsonFieldType.NUMBER)
                                                .description("투두 스토리지 식별자"),
                                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.userInfo").type(JsonFieldType.OBJECT).description("유저 정보"),
                                        fieldWithPath("data.userInfo.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자")
                                )
                        )
                ));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTodoStoragesTest() throws Exception{

        Page<TodoStorage> pageTodoStorages = new PageImpl<>(List.of(todoStorage, todoStorage2),
                PageRequest.of(0, 5, Sort.by("todoStorageId")), 2);

        given(todoStorageService.findTodoStorages(Mockito.any(int.class), Mockito.any(int.class), Mockito.any(String.class)))
                .willReturn(pageTodoStorages);
        given(mapper.todoStoragesToTodoStorageResponseDtos(Mockito.any(List.class)))
                .willReturn(List.of(response, response2));

        ResultActions resultActions = mockMvc.perform(
                get("/todoStorages")
                        .queryParam("page", "1")
                        .queryParam("size", "5")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].todoStorageId").value(response.getTodoStorageId()))
                .andExpect(jsonPath("$.data[1].todoStorageId").value(response2.getTodoStorageId()))
                .andDo(document(
                        "get-todoStorages",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestParameters(
                                List.of(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기")
                                )
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("데이터"),
                                        fieldWithPath("data[].todoStorageId").type(JsonFieldType.NUMBER)
                                                .description("투두 스토리지 식별자"),
                                        fieldWithPath("data[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data[].userInfo").type(JsonFieldType.OBJECT).description("유저 정보"),
                                        fieldWithPath("data[].userInfo.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER)
                                                .description("총 데이터 개수"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER)
                                                .description("총 페이지 개수")
                                )
                        )
                ));

    }


    @Test
    @WithMockUser(roles = "USER")
    void getTodosByStorageTest() throws Exception{

        given(todoStorageService.findTodoStorage(Mockito.any(long.class), Mockito.any(String.class)))
                .willReturn(new TodoStorage());
        given(mapper.todoStorageToTodoStorageResponseDtos(Mockito.any(TodoStorage.class)))
                .willReturn(storageResponse);

        ResultActions resultActions = mockMvc.perform(
                get("/todoStorages/storages/{todoStorage-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todoStorageId").value(storageResponse.getTodoStorageId()))
                .andDo(document(
                        "get-todosByStorage",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("todoStorage-id").description("투두 스토리지 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.todoStorageId").type(JsonFieldType.NUMBER)
                                                .description("투두 스토리지 식별자"),
                                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.userInfo").type(JsonFieldType.OBJECT).description("유저 정보"),
                                        fieldWithPath("data.userInfo.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data.savedTodoResponses").type(JsonFieldType.ARRAY)
                                                .description("저장 투두 리스트 정보"),
                                        fieldWithPath("data.savedTodoResponses[].savedTodoId").type(JsonFieldType.NUMBER)
                                                .description("저장 투두 식별자"),
                                        fieldWithPath("data.savedTodoResponses[].content").type(JsonFieldType.STRING)
                                                .description("내용"),
                                        fieldWithPath("data.savedTodoResponses[].emoji").type(JsonFieldType.STRING)
                                                .description("이모지"),
                                        fieldWithPath("data.savedTodoResponses[].tagResponse").type(JsonFieldType.OBJECT)
                                                .description("태그 정보"),
                                        fieldWithPath("data.savedTodoResponses[].tagResponse.tagId").type(JsonFieldType.NUMBER)
                                                .description("태그 식별자"),
                                        fieldWithPath("data.savedTodoResponses[].tagResponse.tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름"),
                                        fieldWithPath("data.savedTodoResponses[].memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자")

                                )
                        )
                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteTodoStorageTest() throws Exception{

        doNothing().when(todoStorageService).deleteTodoStorage(Mockito.any(long.class), Mockito.any(String.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/todoStorages/{todoStorage-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-todoStorage",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("todoStorage-id").description("투두 스토리지 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));

    }



}
