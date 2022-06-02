package jdbc.day03.board;

import java.util.*;

public interface InterBoardDAO {

	void close(); // 자원반납하기
	
	List<BoardDTO> boardList(); // 글목록보기

	BoardDTO viewContents(Map<String, String> paraMap); // 글내용보기

	int write(BoardDTO board); // 글쓰기 

	int write_comment(BoardCommentDTO comment); // 댓글쓰기 

	List<BoardCommentDTO> commentList(String boardno); // 원게시글 글번호에 딸린 댓글 보여주기 

	Map<String, Integer> statisticsByWeek(); // 최근 1주일간 일자별 게시글 작성건수를 select 해서 나오는 결과물 

	List<Map<String, String>> statisticsByPrevious_CurrentMonth(); // 저번달 및 이번달 일자별 게시글 작성건수

	int deleteBoard(Map<String, String> paraMap); // 글삭제하기

	int updateBoard(Map<String, String> paraMap); // 글수정하기

}
