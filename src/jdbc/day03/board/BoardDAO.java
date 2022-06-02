package jdbc.day03.board;

import java.sql.*;
import java.util.*;

public class BoardDAO implements InterBoardDAO {

	// attribute, field, property, 속성
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	
	// operation, method, 기능
	
	// *** 자원반납 메소드 구현하기 *** //
	@Override
	public void close() {
		
		try {
			if(rs != null) 		rs.close();
			if(pstmt != null) 	pstmt.close();
			if(conn != null) 	conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}// end of public void close()----------------------	
	
	
	
	// *** 글목록보기 메소드를 구현하기 *** //
	@Override
	public List<BoardDTO> boardList() {
		
		List<BoardDTO> boardList = new ArrayList<>();
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
		/*	
			String sql = " select B.boardno, B.subject, M.name "
					   + "     , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss'), B.viewcount "
					   + " from jdbc_board B JOIN jdbc_member M "
					   + " ON B.fk_userid = M.userid "
					   + " order by boardno desc ";
		*/			   
			
			String sql = " select B.boardno, B.subject, M.name "+
					     "      , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss'), B.viewcount "+
						 "      , nvl(C.COMMENTCNT, 0) "+
						 " from jdbc_board B JOIN jdbc_member M "+
						 " ON B.fk_userid = M.userid "+
						 " LEFT JOIN (select fk_boardno, count(*) AS COMMENTCNT "+
						 "            from jdbc_comment "+
						 "            group by fk_boardno) C "+
						 " ON B.boardno = C.fk_boardno "+
						 " order by 1 desc";
			
			pstmt = conn.prepareStatement(sql);
						
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				
				board.setBoardno(rs.getInt(1));
				board.setSubject(rs.getString(2));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString(3));
				board.setMember(member);
				
				board.setWriteday(rs.getString(4));
				board.setViewcount(rs.getInt(5));
				
				board.setCommentcnt(rs.getInt(6)); // 댓글의 개수
				
				boardList.add(board);
			}// end of while(rs.next())---------------------
			
		
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}		
		
		return boardList;
	}// end of public List<BoardDTO> boardList()---------------------


	
	// *** 글내용보기 메소드를 구현하기 *** //
	@Override
	public BoardDTO viewContents(Map<String, String> paraMap) {
	    
		BoardDTO board = null;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " select * "
					   + " from jdbc_board "
					   + " where boardno = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("boardno") );
						
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				// 입력한 글번호에 해당하는 글이 존재하는 경우
				
				// 로그인한 사용자가 쓴 글인지 (즉, 자신이 쓴 글을 자신이 보고자 하는 경우)
				// 로그인한 사용자가 쓴 글이 아닌 다른 사용자가 쓴 글인지 구분한다.
				sql = " select * "
					+ " from jdbc_board "
					+ " where boardno = ? and fk_userid = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("boardno") );
				pstmt.setString(2, paraMap.get("fk_userid") );
							
				rs = pstmt.executeQuery();
				
				if(!rs.next()) {
					// 로그인한 사용자가 쓴 글이 아닌 다른 사용자가 쓴 글이라면 
					
					sql = " update jdbc_board set viewcount = viewcount + 1 "
						+ " where boardno = ? ";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("boardno") );
					
					pstmt.executeUpdate();
				}
				
				sql = " select boardno, subject, contents, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss'), viewcount, M.name " 
					+ " from jdbc_board B JOIN jdbc_member M "
					+ " ON B.fk_userid = M.userid "
					+ " where boardno = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("boardno") );
				
				rs = pstmt.executeQuery();
				
				rs.next();
				
				board = new BoardDTO();
				board.setBoardno(rs.getInt(1));
				board.setSubject(rs.getString(2));
				board.setContents(rs.getString(3));
				board.setWriteday(rs.getString(4));
				board.setViewcount(rs.getInt(5));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString(6));
				
				board.setMember(member);
			}
			
			else {
				// 입력한 글번호에 해당하는 글이 존재하지 않는 경우
				System.out.println(">> 조회하고자 하는 글번호 "+paraMap.get("boardno")+"에 해당하는 글은 없습니다. << \n"); 
			}
		
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			if( e.getErrorCode() == 1722 ) {
				System.out.println(">> 조회하고자 하는 글번호는 정수로만 입력하세요 << \n");
			}
			else {
				e.printStackTrace();
			}
		} finally {
			close();
		}
		
		return board;
	}// end of public BoardDTO viewContents(Map<String, String> paraMap)---------------



	// *** 글쓰기 메소드를 구현하기 *** //
	/*
	   === Transaction 처리를 해야 하는 경우이다. ===
	   첫번째, jdbc_board(게시판) 테이블에 insert 가 성공되어지면 
	   두번째, jdbc_member(회원) 테이블에 있는 point 컬럼의 값을 10 증가(update)를 해야 한다.
	   즉, jdbc_board(게시판) 테이블에 insert 와 jdbc_member(회원) 테이블에 update 가 
	      둘 모두 성공해야만 commit을 해주고, 만약에 1개라도 실패하면 rollback 을 해주어야 한다.
	*/
	@Override
	public int write(BoardDTO board) {
		
		int result = 0;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			conn.setAutoCommit(false); // 트랜잭션 처리를 위해서 수동커밋으로 전환한다.
			
			String sql = " insert into jdbc_board(boardno, fk_userid, subject, contents, boardpasswd) "
					   + " values(board_seq.nextval, ?, ?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getFk_userid());
			pstmt.setString(2, board.getSubject());
			pstmt.setString(3, board.getContents());
			pstmt.setString(4, board.getBoardpasswd());
			
			int n = pstmt.executeUpdate();
			
			int m = 0;
			
			if(n==1) {
				sql = " update jdbc_member set point = point + 10 "
					+ " where userid = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, board.getFk_userid());
			
				m = pstmt.executeUpdate();
			}
			
			if(m == 1) {
				conn.commit(); // 트랜잭션 처리를 한 것임.
				result = m;
			}
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			try {
				conn.rollback(); // 트랜잭션 처리를 한 것임.
			} catch (SQLException e1) {	} 
		} finally {
			close();
		}		
		
		return result;
	}// end of public int write(BoardDTO board)--------------------------------



	// *** 댓글쓰기 메소드를 구현하기 *** //
	/*
	   === Transaction 처리를 해야 하는 경우이다. ===
	   첫번째, jdbc_comment(댓글) 테이블에 insert 가 성공되어지면 
	   두번째, jdbc_member(회원) 테이블에 있는 point 컬럼의 값을 5 증가(update)를 해야 한다.
	   즉, jdbc_comment(댓글) 테이블에 insert 와 jdbc_member(회원) 테이블에 update 가 
	      둘 모두 성공해야만 commit을 해주고, 만약에 1개라도 실패하면 rollback 을 해주어야 한다.
	*/
	@Override
	public int write_comment(BoardCommentDTO comment) {
		
		int result = 0;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			conn.setAutoCommit(false); // 트랜잭션 처리를 위해서 수동커밋으로 전환한다.
			
			String sql = " insert into jdbc_comment(commentno, fk_boardno, fk_userid, contents) "
					   + " values(seq_comment.nextval, to_number(?), ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, comment.getFk_boardno() ); 
			pstmt.setString(2, comment.getFk_userid() );
			pstmt.setString(3, comment.getContents() );
			
			int n = pstmt.executeUpdate();
			
			int m = 0;
			
			if(n==1) {
				sql = " update jdbc_member set point = point + 5 "
					+ " where userid = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, comment.getFk_userid() );
			
				m = pstmt.executeUpdate();
			}
			
			if(m == 1) {
				conn.commit(); // 트랜잭션 처리를 한 것임.
				result = m;
			}
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			try {
				 if(e.getErrorCode() == 2291) {
				 /*
					 오류 보고 -
					 ORA-02291: integrity constraint (HR.FK_JDBC_COMMENT_FK_BOARDNO) violated - parent key not found
				 */
					 System.out.println(">> [오류발생] 원글번호 "+comment.getFk_boardno()+"은 존재하지 않습니다. <<");
					 result = -1;
				 }
				
				 conn.rollback(); // 트랜잭션 처리를 한 것임.
			} catch (SQLException e1) {	} 
		} finally {
			close();
		}		
		
		return result;
		
	}// end of public int write_comment(BoardCommentDTO comment)---------------


	// *** 원게시글 글번호에 딸린 댓글을 보여주는 메소드 구현하기 *** // 
	@Override
	public List<BoardCommentDTO> commentList(String boardno) {
		
		List<BoardCommentDTO> commentList = new ArrayList<>();
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " select C.contents, M.name, C.writeday "+
					     " from "+
					     " (select contents, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday, fk_userid "+
					     "  from jdbc_comment "+
					     "  where fk_boardno = ? ) C JOIN jdbc_member M "+
					     " ON C.fk_userid = M.userid ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				BoardCommentDTO comment = new BoardCommentDTO();
				comment.setContents(rs.getString(1));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString(2));
				comment.setMember(member);
				
				comment.setWriteday(rs.getString(3));
				
				commentList.add(comment);
			}// end of while(rs.next())---------------------
			
		
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}		
		
		return commentList;
		
	}// end of public List<BoardCommentDTO> commentList(String boardno)-----------

	

	// *** 최근 1주일간 일자별 게시글 작성건수를 select 해서 나오는 결과물 메소드 구현하기 *** //
	@Override
	public Map<String, Integer> statisticsByWeek() {
		
		Map<String, Integer> resultMap = new HashMap<>();
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = "select count(*) AS TOTAL "+
					"     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 6, 1, 0)) AS PREVIOUS6 "+
					"     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 5, 1, 0)) AS PREVIOUS5 "+
					"     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 4, 1, 0)) AS PREVIOUS4 "+
					"     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 3, 1, 0)) AS PREVIOUS3 "+
					"     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 2, 1, 0)) AS PREVIOUS2 "+
					"     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 1, 1, 0)) AS PREVIOUS1 "+
					"     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 0, 1, 0)) AS TODAY "+
					"from jdbc_board "+
					"where func_midnight(sysdate) - func_midnight(writeday) < 7";
			
			pstmt = conn.prepareStatement(sql);
						
			rs = pstmt.executeQuery();
			
			rs.next();
			
			resultMap.put("TOTAL", rs.getInt(1));
			resultMap.put("PREVIOUS6", rs.getInt(2));
			resultMap.put("PREVIOUS5", rs.getInt(3));
			resultMap.put("PREVIOUS4", rs.getInt(4));
			resultMap.put("PREVIOUS3", rs.getInt(5));
			resultMap.put("PREVIOUS2", rs.getInt(6));
			resultMap.put("PREVIOUS1", rs.getInt(7));
			resultMap.put("TODAY", rs.getInt(8));
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return resultMap;
	}// end of public Map<String, Integer> statisticsByWeek()---------------------------------



	// *** 저번달 및 이번달 일자별 게시글 작성건수 메소드 구현하기 *** //
	@Override
	public List<Map<String, String>> statisticsByPrevious_CurrentMonth() {
		
		List<Map<String, String>> mapList = new ArrayList<>();
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = "select decode( grouping( to_char(writeday, 'yyyy-mm-dd') ), 0, to_char(writeday, 'yyyy-mm-dd'), '전체') AS WRITEDAY "+ 
						"     , count(*) AS CNT "+
						"from jdbc_board "+
						"where to_char(writeday, 'yyyy-mm') = to_char(sysdate, 'yyyy-mm') OR "+
						"      to_char(writeday, 'yyyy-mm') = to_char(add_months(sysdate, -1), 'yyyy-mm') "+
						"group by ROLLUP( to_char(writeday, 'yyyy-mm-dd') )";
			
			pstmt = conn.prepareStatement(sql);
						
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Map<String, String> map = new HashMap<>();
				
				map.put("WRITEDAY", rs.getString(1));
				map.put("CNT", String.valueOf(rs.getInt(2)));
				
				mapList.add(map);
			}// end of while(rs.next())-------------------------------------
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return mapList;
	}// end of public List<Map<String, String>> statisticsByPrevious_CurrentMonth()------------------------------

	


	// *** 글삭제하기 메소드 구현하기 *** //
	@Override
	public int deleteBoard(Map<String, String> paraMap) {
		
		int n = 0;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " select * "
					   + " from jdbc_board "
					   + " where boardno = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("boardno"));
						
			rs = pstmt.executeQuery();
			
			if(!rs.next()) {
				n = 1; // 존재하지 않는 글번호(boardno)를 가지고 글을 삭제하려는 경우 
			}
			else {
				// 존재하는 글번호(boardno)를 가지고 글을 삭제하려는 경우
				sql += " and fk_userid = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("boardno"));
				pstmt.setString(2, paraMap.get("userid"));  // paraMap.get("userid") 이 로그인 되어진 사용자의 아이디 값이다. 
				
				rs = pstmt.executeQuery();
				
				if(!rs.next()) {
					// 다른 사용자의 글을 삭제하려고 한 경우 
					n = 2;
				}
				else {
					// 로그인 되어진 사용자가 자신이 쓴 글을 삭제하려고 한 경우 
					sql += " and boardpasswd = ? ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("boardno"));
					pstmt.setString(2, paraMap.get("userid"));
					pstmt.setString(3, paraMap.get("boardpasswd"));
					
					rs = pstmt.executeQuery();
					
					if(!rs.next()) {
						// 삭제하려는 글의 글암호가 글삭제시 입력받은 글암호와 일치하지 않는 경우
						n = 3;
					}
					else {
						// 삭제하려는 글의 글암호가 글삭제시 입력받은 글암호와 일치하는 경우
						
						sql = " delete from jdbc_board "
							+ " where boardno = ? ";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, paraMap.get("boardno"));
						
						int m = pstmt.executeUpdate();
						if(m==1) {
							n = 4;
						}
						
					}
					
				}
				
			}
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
		//	e.printStackTrace();
			n = 5;
		} finally {
			close();
		}
		
		return n;
	}// end of public int deleteBoard(Map<String, String> paraMap)------------------------------


    
	// *** 글수정하기 메소드 구현하기 *** //
	@Override
	public int updateBoard(Map<String, String> paraMap) {

		int n = 0;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " select * "
					   + " from jdbc_board "
					   + " where boardno = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("boardno"));
						
			rs = pstmt.executeQuery();
			
			if(!rs.next()) {
				n = 1; // 존재하지 않는 글번호(boardno)를 가지고 글을 수정하려는 경우 
			}
			else {
				// 존재하는 글번호(boardno)를 가지고 글을 수정하려는 경우
				sql += " and fk_userid = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("boardno"));
				pstmt.setString(2, paraMap.get("userid"));  // paraMap.get("userid") 이 로그인 되어진 사용자의 아이디 값이다. 
				
				rs = pstmt.executeQuery();
				
				if(!rs.next()) {
					// 다른 사용자의 글을 수정하려고 한 경우 
					n = 2;
				}
				else {
					// 로그인 되어진 사용자가 자신이 쓴 글을 수정하려고 한 경우 
					sql += " and boardpasswd = ? ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("boardno"));
					pstmt.setString(2, paraMap.get("userid"));
					pstmt.setString(3, paraMap.get("boardpasswd"));
					
					rs = pstmt.executeQuery();
					
					if(!rs.next()) {
						// 수정하려는 글의 글암호가 글수정시 입력받은 글암호와 일치하지 않는 경우
						n = 3;
					}
					else {
						// 수정하려는 글의 글암호가 글수정시 입력받은 글암호와 일치하는 경우
						
						sql = " update jdbc_board set subject = ? , contents = ? "
							+ " where boardno = ? ";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, paraMap.get("subject"));
						pstmt.setString(2, paraMap.get("contents"));
						pstmt.setString(3, paraMap.get("boardno"));
						
						int m = pstmt.executeUpdate();
						if(m==1) {
							n = 4;
						}
						
					}
					
				}
				
			}
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
		//	e.printStackTrace();
			n = 5;
		} finally {
			close();
		}
		
		return n;		
	}// end of public int updateBoard(Map<String, String> paraMap)------------------------------
	
	
	
	

}
