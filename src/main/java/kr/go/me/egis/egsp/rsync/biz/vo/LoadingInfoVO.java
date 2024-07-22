package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class LoadingInfoVO {
    /** 로딩 ID */
    private long loadingId;
    /** 수집 ID */
    private long colcId;
    /** 인터페이스 ID */
    private String intrfcId;
    /** 수신파일 */
    private String recpFile;
    /** 수신파일 사이즈 */
    private long recpFileSize;
    /** 수신파일 라인 카운트 */
    private long recpFileLineCt;
    /** 수신파일 저장 카운트 */
    private long recpFileSkipCt;
    /** 정의된 컬럼 카운트 */
    private long defineColumnCt;
    /** 수신파일 헤더 존재 유무 */
    private String defineHeaderYn;
    /** 로딩 테이블 */
    private String loadingTableNm;
    /** 로딩 유형 */
    private String loadingTy;
    /** 로딩 상태 */
    private String loadingSttus;
    /** 로딩 전 Row 카운트 */
    private long loadingBfRowCt;
    /** 로딩 후 Row 카운트 */
    private long loadingAfRowCt;
    /** 로딩 시간 */
    private String loadingDt;
    /** 오류파일 */
    private String errFile;
    /** 오류로그파일 */
    private String errLogFile;
    /** 오류정보 */
    private String errInfo;
}
