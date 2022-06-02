package jdbc.day03;

import java.sql.*;
import java.util.Map;

// DAO(Database Access Object) ==> 데이터베이스에 연결하여 SQL구문을 실행시켜주는 객체

public class MemberDAO implements InterMemberDAO {

	// attribute, field, property, 속성
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	
	// operation, method, 기능
	
	// === 자원반납을 해주는 메소드 === //
	private void close() {
		try {
			  if(rs != null)	rs.close();
			  if(pstmt != null)	pstmt.close();
			  if(conn != null)	conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}// end of private void close()-------------------------------


	// === 회원가입(insert) 메소드 구현하기 === //
	@Override
	public int memberRegister(MemberDTO member) {
		
		int result = 0;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " insert into jdbc_member(userseq, userid, passwd, name, mobile) "
					   + " values(userseq.nextval, ?, ?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, member.getPasswd());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getMobile());
			
			result = pstmt.executeUpdate();
			
		} catch (ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLIntegrityConstraintViolationException e) {
		  //	System.out.println("에러코드번호 : " + e.getErrorCode());
		  //	System.out.println("에러메시지 : " + e.getMessage());
			
			if( e.getErrorCode() == 1 ) {
				System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	}// end of public int memberRegister(MemberDTO member)---------------------


	
	// === 로그인처리(select) 메소드 구현하기 === //
	@Override
	public MemberDTO login(Map<String, String> paraMap) {
		
		MemberDTO member = null;
		
		try {
			 Class.forName("oracle.jdbc.driver.OracleDriver");
			 
			 conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass"); 
			 
			 String sql = "select userseq, name, mobile, point, to_char(registerday, 'yyyy-mm-dd') AS RegisterDay "+
					      "from jdbc_member "+
					      "where status = 1 and userid = ? and passwd = ? ";
			 
			 pstmt = conn.prepareStatement(sql);
			 pstmt.setString(1, paraMap.get("userid") ); // paraMap 의 key인 "userid"는 MemberCtrl 클래스에서 정의해둔 것이다.(98 라인) 
			 pstmt.setString(2, paraMap.get("passwd") ); // paraMap 의 key인 "passwd"는 MemberCtrl 클래스에서 정의해둔 것이다.(99 라인)
			
			 rs = pstmt.executeQuery();
			 
			 if( rs.next() ) {
				 member = new MemberDTO();
			 //	 member.setName( rs.getString(1) );
			 //  또는	
				 member.setUserseq(rs.getInt("USERSEQ"));
				 member.setName( rs.getString("NAME") );
				 member.setMobile(rs.getString("MOBILE"));
				 member.setPoint(rs.getInt("POINT"));
				 member.setRegisterday(rs.getString("RegisterDay"));
			}
		
		} catch (ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return member;
	}// end of public MemberDTO login(Map<String, String> paraMap)-------------


	// === 회원탈퇴(update)메소드 구현하기 === //
	@Override
	public int memberDelete(int userseq) {
		
		int result = 0;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " update jdbc_member set status = 0 "
						+ " where userseq = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userseq);
			
			result = pstmt.executeUpdate();
			
		} catch (ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLIntegrityConstraintViolationException e) {
		  //	System.out.println("에러코드번호 : " + e.getErrorCode());
		  //	System.out.println("에러메시지 : " + e.getMessage());
			
			if( e.getErrorCode() == 1 ) {
				System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	} // end of public int memberDelte(int userseq)------------------------
	
	
	
	
}
