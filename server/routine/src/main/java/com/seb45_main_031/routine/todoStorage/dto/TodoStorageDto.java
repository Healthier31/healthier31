package com.seb45_main_031.routine.todoStorage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class TodoStorageDto {

    @Getter
    @Setter
    public static class Post{
        @NotBlank
        @Size(max = 20)
        private String category;

        private long memberId;

        @Valid
        private List<SavedTodoPost> savedTodoPosts;
    }

    @Getter
    @Setter
    public static class SavedTodoPost{
        @NotBlank
        @Size(max = 20)
        private String content;

        @NotBlank
        @Size(max = 50)
        private String emoji;

        private long tagId;
    }

    @Getter
    @Setter
    public static class Patch{
        private long todoStorageId;

        @Size(max = 20)
        private String category;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long todoStorageId;
        private String category;
        private UserInfo userInfo;
    }

    @Getter
    @Setter
    @Builder
    public static class UserInfo{
        private long memberId;
    }

    @Getter
    @Setter
    @Builder
    public static class StorageResponse{
        private long todoStorageId;
        private String category;
        private UserInfo userInfo;
        private List<SavedTodoResponse> savedTodoResponses;
    }

    @Getter
    @Setter
    @Builder
    public static class SavedTodoResponse{
        private long savedTodoId;
        private String content;
        private String emoji;

        private TagResponse tagResponse;

        private long memberId;
    }

    @Getter
    @Setter
    @Builder
    public static class TagResponse{
        private long tagId;
        private String tagName;
    }

}
