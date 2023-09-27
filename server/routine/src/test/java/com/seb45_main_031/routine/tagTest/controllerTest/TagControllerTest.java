package com.seb45_main_031.routine.tagTest.controllerTest;

import com.google.gson.Gson;
import com.seb45_main_031.routine.tag.dto.TagDto;
import com.seb45_main_031.routine.tag.entity.Tag;
import com.seb45_main_031.routine.tag.mapper.TagMapper;
import com.seb45_main_031.routine.tag.service.TagService;
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
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private TagService tagService;
    @MockBean
    private TagMapper mapper;


    private static Tag tag;
    private static Tag tag2;
    private static TagDto.Post tagPostDto;
    private static TagDto.Patch tagPatchDto;
    private static TagDto.Response response;
    private static TagDto.Response response2;

    @BeforeAll
    static void initAll(){

        tag = new Tag();
        tag.setTagId(1L);
        tag.setTagName("태그이름");

        tag2 = new Tag();
        tag2.setTagId(2L);
        tag2.setTagName("태그이름2");

        tagPostDto = new TagDto.Post();
        tagPostDto.setTagName("태그이름");

        tagPatchDto = new TagDto.Patch();
        tagPatchDto.setTagId(1L);
        tagPatchDto.setTagName("태그이름");

        response = TagDto.Response.builder()
                .tagId(1L)
                .tagName("태그이름")
                .build();

        response2 = TagDto.Response.builder()
                .tagId(2L)
                .tagName("태그이름2")
                .build();

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postTagTest() throws Exception{

        String requestBody = gson.toJson(tagPostDto);

        given(mapper.tagPostDtoToTag(Mockito.any(TagDto.Post.class))).willReturn(new Tag());
        given(tagService.createTag(Mockito.any(Tag.class))).willReturn(tag);

        ResultActions resultActions = mockMvc.perform(
                post("/tags")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/tags"))))
                .andDo(document(
                        "post-tag",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("tagName").type(JsonFieldType.STRING).description("태그 이름")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        )

                ));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchTagTest() throws Exception{

        String requestBody = gson.toJson(tagPatchDto);

        given(mapper.tagPatchDtoToTag(Mockito.any(TagDto.Patch.class))).willReturn(new Tag());
        given(tagService.updateTag(Mockito.any(Tag.class))).willReturn(new Tag());
        given(mapper.tagToTagResponseDto(Mockito.any(Tag.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/tags/{tag-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tagId").value(response.getTagId()))
                .andDo(document(
                        "patch-tag",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("tag-id").description("태그 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 식별자").ignored(),
                                        fieldWithPath("tagName").type(JsonFieldType.STRING).description("태그 이름").optional()
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.tagId").type(JsonFieldType.NUMBER).description("태그 식별자"),
                                        fieldWithPath("data.tagName").type(JsonFieldType.STRING).description("태그 이름")
                                )
                        )
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTagTest() throws Exception{

        given(tagService.findTag(Mockito.any(long.class))).willReturn(new Tag());
        given(mapper.tagToTagResponseDto(Mockito.any(Tag.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                get("/tags/{tag-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tagId").value(response.getTagId()))
                .andDo(document(
                        "get-tag",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("tag-id").description("태그 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.tagId").type(JsonFieldType.NUMBER).description("태그 식별자"),
                                        fieldWithPath("data.tagName").type(JsonFieldType.STRING).description("태그 이름")
                                )
                        )
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTagsTest() throws Exception{

        Page<Tag> pageTags = new PageImpl<>(List.of(tag, tag2),
                PageRequest.of(0, 5, Sort.by("tagId")), 2);

        given(tagService.findTags(Mockito.any(int.class), Mockito.any(int.class))).willReturn(pageTags);
        given(mapper.tagsToTagResponseDtos(Mockito.any(List.class))).willReturn(List.of(response, response2));

        ResultActions resultActions = mockMvc.perform(
                get("/tags")
                        .queryParam("page", "1")
                        .queryParam("size", "5")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].tagId").value(response.getTagId()))
                .andExpect(jsonPath("$.data[1].tagId").value(response2.getTagId()))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andDo(document(
                        "get-tags",
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
                                        fieldWithPath("data[].tagId").type(JsonFieldType.NUMBER).description("태그 식별자"),
                                        fieldWithPath("data[].tagName").type(JsonFieldType.STRING).description("태그 이름"),
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
    @WithMockUser(roles = "ADMIN")
    void deleteTagTest() throws Exception{

        doNothing().when(tagService).deleteTag(Mockito.any(long.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/tags/{tag-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-tag",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("tag-id").description("태그 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));

    }


}
