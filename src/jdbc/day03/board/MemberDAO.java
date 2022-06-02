package jdbc.day03.board;

import java.sql.*;
import java.util.*;

public class MemberDAO implements InterMemberDAO {

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
	
	
	// *** 회원가입시 사용가능한 아이디 인지 중복된 아이디 이라서 사용불가인 알려주는 메소드 구현하기 *** //
	@Override
	public boolean isUse_userid(String userid) {
		
		boolean isUse = false;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " select * "
					   + " from jdbc_member "
					   + " where userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				// userid 가 이미 존재하는 아이디인 경우이다.
				isUse = false;
			}
			else {
				// userid 가 이미 존재하지 않는 아이디인 경우이다.
				isUse = true;
			}
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}	
		
		return isUse;
	}// end of public boolean isUse_userid(String userid)-----------
	
	
	// *** 회원가입(insert)을 처리해주는 메소드 구현하기 *** //
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
			
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}	
		
		return result;
	}// end of public int memberRegister(MemberDTO member)----------------------
	
	
	// *** 로그인 처리(select) 메소드 구현하기 *** //
	@Override
	public MemberDTO login(Map<String, String> paraMap) {
		
		MemberDTO member = null;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = "select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd') "+
				         "from jdbc_member "+
				         "where status = 1 and userid = ? and passwd = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("passwd"));
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				member = new MemberDTO();
				
				member.setUserseq(rs.getInt(1));
				member.setUserid(rs.getString(2));
				member.setName(rs.getString(3));
				member.setMobile(rs.getString(4));
				member.setPoint(rs.getInt(5));
				member.setRegisterday(rs.getString(6));
			}
			
		
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return member;
		
	}// end of public MemberDTO login(Map<String, String> paraMap)-----------------


	// *** 관리자를 제외한 모든 회원들을 선택한 정렬기준으로 보여주는(select) 메소드 구현하기 *** //
	@Override
	public List<MemberDTO> selectAllMember(String sortChoice) {
		
		List<MemberDTO> memberList = new ArrayList<>();
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd hh24:mi:ss'), status "+ 
				         " from jdbc_member "+
				         " where userid != 'admin' ";
			
			switch (sortChoice) {
				case "1": // 회원명의 오름차순
					sql += " order by name asc ";
					break;
					 
				case "2": // 회원명의 내림차순 
					sql += " order by name desc ";
					break;
					
				case "3": // 가입일자의 오름차순
					sql += " order by 6 asc ";
					break;
					
				case "4": // 가입일자의 내림차순
					sql += " order by 6 desc ";
					break;					
			}// end of switch (sortChoice)---------------------------

			pstmt = conn.prepareStatement(sql);
						
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MemberDTO member = new MemberDTO();
				
				member.setUserseq(rs.getInt(1));
				member.setUserid(rs.getString(2));
				member.setName(rs.getString(3));
				member.setMobile(rs.getString(4));
				member.setPoint(rs.getInt(5));
				member.setRegisterday(rs.getString(6));
				member.setStatus(rs.getInt(7));
				
				memberList.add(member);
			}// end of while(rs.next())---------------------
			
		
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}		
		
		return memberList;
	}// end of public List<MemberDTO> selectAllMember(String sortChoice)----------
	

}
