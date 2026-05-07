package cwchoiit.cleanecommerce.application.port.in.images;

import jakarta.validation.Valid;

/**
 * 이미지를 스토리지에 업로드하기 위한 UseCase 이미지를 서버가 직접 처리하지 않고, 클라이언트에서 올리고자 하는 이미지의 메타데이터(원본파일명, 타입, 길이,
 * 이미지유형(썸네일 등))만을 전달하면 해당 이미지를 올리기 위한 스토리지에 키와 업로드URL, 이미지 공개 URL만을 전달한다. 클라이언트는 전달받은 업로드 URL을 사용해
 * 이미지를 실제로 첨부한다.
 */
public interface IssueImageUploadUrlUseCase {
    IssueImageUploadUrlResult issue(@Valid IssueImageUploadUrlCommand command);
}
