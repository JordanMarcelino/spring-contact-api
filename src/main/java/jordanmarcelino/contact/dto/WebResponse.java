package jordanmarcelino.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {

    private String message;

    private T data;

    private List<Error> errors;

    private PageMetaData paging;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Error {

        private String field;

        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PageMetaData {

        private Integer totalPage;

        private Integer size;

        private boolean hasNext;
    }
}
