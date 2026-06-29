package k2web.module.enterCldrApply.service;

import k2web.com.util.K2Util;

public class EnterCldrApplyVo {

	/** 사이트아이디 */
	private String siteId;

	private Integer fnctNo;

	/** 현재페이지 */
	private Integer page = 1;

	/** 페이지갯수 */
	private Integer row = 10;

	/** 검색구분 */
	private String findType;

	/** 검색어 */
	private String findWord;

	/** 레이아웃 */
	private String layout;

	/** 설정 SEQ 검색 조건 */
	private Integer findSetupSeq;

	/** 신청 상태 검색 조건 (WAIT/APPROVED/REJECTED/CANCELED) */
	private String findArtclStatus;

	/** 신청 일자 시작 검색 조건 (yyyy-MM-dd) */
	private String findStartDt;

	/** 신청 일자 종료 검색 조건 (yyyy-MM-dd) */
	private String findEndDt;

	/**
	 * 사이트 아이디를 가져온다.
	 * 
	 * @return 사이트 아이디
	 */
	public String getSiteId() {
		return siteId;
	}

	/**
	 * 사이트 아이디를 설정한다.
	 * 
	 * @param siteId 사이트 아이디
	 * @see k2web.com.util.K2Util#replaceXSS(String)
	 */
	public void setSiteId(String siteId) {
		this.siteId = K2Util.replaceXSS(K2Util.replaceNull(siteId, ""));
	}

	/**
	 * 현재 페이지를 가져온다.
	 * 
	 * @return 현재 페이지
	 */
	public Integer getPage() {
		return page;
	}

	/**
	 * 현재 페이지를 설정한다.
	 * 
	 * @param page 현재 페이지
	 */
	public void setPage(Integer page) {
		this.page = page;
	}

	/**
	 * 목록 개수를 가져온다.
	 * 
	 * @return 목록 개수
	 */
	public Integer getRow() {
		return row;
	}

	/**
	 * 목록 개수를 설정한다.
	 * 
	 * @param row 목록 개수
	 */
	public void setRow(Integer row) {
		this.row = row;
	}

	/**
	 * 검색 타입을 가져온다.
	 * 
	 * @return 검색 타입
	 */
	public String getFindType() {
		return findType;
	}

	/**
	 * 검색 타입을 설정한다.
	 * 
	 * @param findType 검색타입
	 * @see k2web.com.util.K2Util#replaceXSS(String)
	 */
	public void setFindType(String findType) {
		this.findType = K2Util.replaceXSS(K2Util.replaceNull(findType, ""));
	}

	/**
	 * 검색어를 가져온다.
	 * 
	 * @return 검색어
	 */
	public String getFindWord() {
		return findWord;
	}

	/**
	 * 검색어를 설정한다.
	 * 
	 * @param findWord 검색어
	 * @see k2web.com.util.K2Util#replaceXSS(String)
	 */
	public void setFindWord(String findWord) {
		this.findWord = K2Util.replaceXSS(K2Util.replaceNull(findWord, ""));
	}

	/**
	 * Layout을 가져온다.
	 * 
	 * @return Layout
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * Layout을 설정한다.
	 * 
	 * @param layout Layout
	 * @see k2web.com.util.K2Util#replaceXSS(String)
	 */
	public void setLayout(String layout) {
		this.layout = K2Util.replaceXSS(K2Util.replaceNull(layout, ""));
		;
	}

	public Integer getFindSetupSeq() {
		return findSetupSeq;
	}

	public void setFindSetupSeq(Integer findSetupSeq) {
		this.findSetupSeq = findSetupSeq;
	}

	public String getFindArtclStatus() {
		return findArtclStatus;
	}

	public void setFindArtclStatus(String findArtclStatus) {
		this.findArtclStatus = findArtclStatus;
	}

	public String getFindStartDt() {
		return findStartDt;
	}

	public void setFindStartDt(String findStartDt) {
		this.findStartDt = findStartDt;
	}

	public String getFindEndDt() {
		return findEndDt;
	}

	public void setFindEndDt(String findEndDt) {
		this.findEndDt = findEndDt;
	}

	public Integer getFnctNo() {
		return fnctNo;
	}

	public void setFnctNo(Integer fnctNo) {
		this.fnctNo = fnctNo;
	}
}
