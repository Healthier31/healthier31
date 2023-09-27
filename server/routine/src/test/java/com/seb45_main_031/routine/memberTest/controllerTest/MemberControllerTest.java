package com.seb45_main_031.routine.memberTest.controllerTest;

import com.google.gson.Gson;
import com.seb45_main_031.routine.member.dto.MemberDto;
import com.seb45_main_031.routine.member.entity.Member;
import com.seb45_main_031.routine.member.mapper.MemberMapper;
import com.seb45_main_031.routine.member.service.MemberService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

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
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberMapper mapper;

    private static Member member;
    private static MemberDto.Post memberPostDto;
    private static MemberDto.Patch memberPatchDto;
    private static MemberDto.Response response;

    @BeforeAll
    static void initAll(){

        member = new Member();
        member.setMemberId(1L);
        member.setEmail("hgd@naver.com");
        member.setPassword("1234qwer");
        member.setNickname("홍길동");
        member.setImage("imageUrl");

        memberPostDto = new MemberDto.Post();
        memberPostDto.setEmail("hgd@naver.com");
        memberPostDto.setPassword("1234qwer");
        memberPostDto.setNickname("홍길동");

        memberPatchDto = new MemberDto.Patch();
        memberPatchDto.setMemberId(1L);
        memberPatchDto.setNickname("홍길동");
        memberPatchDto.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        response = MemberDto.Response.builder()
                        .memberId(1L)
                        .email("hgd@naver.com")
                        .nickname("홍길동")
                        .memberStatus(Member.MemberStatus.MEMBER_ACTIVE)
                        .exp(0)
                        .level(0)
                        .image("imageUrl")
                        .build();

    }

    @Test
    void postMemberTest() throws Exception{

        String requestBody = gson.toJson(memberPostDto);

        given(mapper.memberPostDtoToMember(Mockito.any(MemberDto.Post.class))).willReturn(new Member());
        given(memberService.createMember(Mockito.any(Member.class))).willReturn(member);

        ResultActions resultActions = mockMvc.perform(
                post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/members/myPage"))))
                .andDo(document(
                        "post-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestFields(
                                List.of(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        )

                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void postImageTest() throws Exception{

        MemberDto.ImageResponse imageResponse = MemberDto.ImageResponse.builder()
                        .memberId(1L)
                        .image("imageUrl")
                        .build();

        given(memberService.uploadImage(Mockito.any(MultipartFile.class), Mockito.any(String.class))).willReturn(new Member());

        given(mapper.memberToMemberImageResponseDto(Mockito.any(Member.class))).willReturn(imageResponse);

        MockMultipartFile multipartFile
                = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/members/image")
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.memberId").value(imageResponse.getMemberId()))
                .andExpect(jsonPath("$.data.image").value(imageResponse.getImage()))
                .andDo(document(
                        "post-image",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParts(
                                partWithName("file").description("이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("data.image").type(JsonFieldType.STRING).description("이미지")
                        )
                ));
    }


    @Test
    void renewAccessTokenTest() throws Exception{

        given(memberService.renewAccessToken(Mockito.any(String.class))).willReturn("Access Token");

        ResultActions resultActions = mockMvc.perform(
                post("/members/renewAccessToken")
                        .header("Refresh", "Refresh Token")
        );

        resultActions.andExpect(status().isOk())
                .andExpect(header().string("Authorization", is("Access Token")))
                .andDo(document(
                        "renew-accessToken",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName("Refresh").description("Refresh Token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));
    }


    @Test
    @WithMockUser(roles = "USER")
    void patchMemberTest() throws Exception{

        String requestBody = gson.toJson(memberPatchDto);

        given(memberService.updateMember(Mockito.any(Member.class), Mockito.any(String.class))).willReturn(new Member());
        given(mapper.memberPatchDtoToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(mapper.memberToMemberResponseDto(Mockito.any(Member.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/members/{member-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(response.getMemberId()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(response.getNickname()))
                .andDo(document(
                        "patch-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자").ignored(),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임").optional(),
                                        fieldWithPath("memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT").optional()
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                                        fieldWithPath("data.exp").type(JsonFieldType.NUMBER).description("경험치"),
                                        fieldWithPath("data.level").type(JsonFieldType.NUMBER).description("레벨"),
                                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("이미지")
                                )
                        )

                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void getMemberTest() throws Exception{

        given(memberService.findMember(Mockito.any(long.class), Mockito.any(String.class))).willReturn(new Member());
        given(mapper.memberToMemberResponseDto(Mockito.any(Member.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                get("/members/myPage/{member-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(response.getMemberId()))
                .andDo(document(
                        "get-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                                        fieldWithPath("data.exp").type(JsonFieldType.NUMBER).description("경험치"),
                                        fieldWithPath("data.level").type(JsonFieldType.NUMBER).description("레벨"),
                                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("이미지")
                                )
                        )
                ));

    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteMemberTest() throws Exception{

        MemberDto.Password password = new MemberDto.Password();
        password.setPassword("1234qwer");

        String requestBody = gson.toJson(password);

        doNothing().when(memberService).deleteMember(Mockito.any(long.class), Mockito.any(String.class), Mockito.any(String.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/members/{member-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        )
                ));

    }



}
