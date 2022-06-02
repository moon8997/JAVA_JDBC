package jdbc.day01;

import java.sql.*;

public class DDL_create_06 {

	public static void main(String[] args) {
		
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass"); 
			
						
			String sql_1 = " select * "
					     + " from user_tables "
					     + " where table_name = 'JDBC_TBL_EXAMTEST' ";  
			
			String sql_2 = " drop table JDBC_TBL_EXAMTEST cascade constraints purge ";
			
			String sql_3 = " create table jdbc_tbl_examtest "
					     + " (no    number(4) "
					     + " ,name  varchar2(40) "
					     + " ,msg   varchar2(200) "
					     + ")";
			
			String sql_4 = " select * "
					     + " from user_sequences "
					     + " where sequence_name = 'JDBC_SEQ_EXAMTEST' ";
			
			String sql_5 = " drop sequence JDBC_SEQ_EXAMTEST ";
			
			String sql_6 = " create sequence jdbc_seq_examtest "
						 + " start with 1 "
						 + " increment by 1 "
						 + " nomaxvalue "
						 + " nominvalue "
						 + " nocycle"
						 + " nocache ";
			
			String sql_7 = " insert into jdbc_tbl_examtest(no, name, msg) "
					     + " values(jdbc_seq_examtest.nextval, '이순신', '안녕하세요? 이순신 입니다.') ";
			
			String sql_8 = " select * "
					     + " from jdbc_tbl_examtest "
					     + " order by no asc ";
			
			pstmt = conn.prepareStatement(sql_1);
			// "JDBC_TBL_EXAMTEST" 테이블이 존재하는지 알아본다.
			
			rs = pstmt.executeQuery();
			
			int n = 0;
			if(rs.next()) {
				// "JDBC_TBL_EXAMTEST" 테이블이 존재하는 경우 
				// 먼저 "JDBC_TBL_EXAMTEST" 테이블을 drop 해야 한다.
				pstmt.close();
				pstmt = conn.prepareStatement(sql_2);
				
				n = pstmt.executeUpdate();
				/*	pstmt.executeUpdate()은  
	                DML(insert, update, delete, merge)문 및 DDL(create table, create view, drop table 등)문을 수행해주는 메소드로서 
		            결과값은 int 형태로서 DML문 이라면 적용된 행의 갯수가 나오고, DDL문 이라면 0이 나온다.
	            */
				System.out.println("drop table : " + n);
				// drop table : 0
			}
			
			
			// "JDBC_TBL_EXAMTEST" 테이블을 생성(create)한다. 
			pstmt = conn.prepareStatement(sql_3);
			n = pstmt.executeUpdate();
			/*	pstmt.executeUpdate()은  
	            DML(insert, update, delete, merge)문 및 DDL(create table, create view, drop table 등)문을 수행해주는 메소드로서 
	            결과값은 int 형태로서 DML문 이라면 적용된 행의 갯수가 나오고, DDL문 이라면 0이 나온다.
	        */
			System.out.println("create table : " + n);
			// create table : 0
			
			/////////////////////////////////////////////////////////
			
			pstmt = conn.prepareStatement(sql_4);
			// "JDBC_SEQ_EXAMTEST" 시퀀스가 존재하는지 알아본다.
			
			rs.close();
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				// "JDBC_SEQ_EXAMTEST" 시퀀스가 존재하는 경우 
				
				// 먼저 "JDBC_SEQ_EXAMTEST" 시퀀스를 drop 한다.
				pstmt = conn.prepareStatement(sql_5);
				n = pstmt.executeUpdate();
				/*	pstmt.executeUpdate()은  
	                DML(insert, update, delete, merge)문 및 DDL(create table, create view, drop table 등)문을 수행해주는 메소드로서 
		            결과값은 int 형태로서 DML문 이라면 적용된 행의 갯수가 나오고, DDL문 이라면 0이 나온다.
	            */
				System.out.println("drop sequence : " + n);
				// drop sequence : 0
			}
			
			// "JDBC_SEQ_EXAMTEST" 시퀀스를 생성(create)한다. 
			pstmt = conn.prepareStatement(sql_6);
			n = pstmt.executeUpdate();
			/*	pstmt.executeUpdate()은  
                DML(insert, update, delete, merge)문 및 DDL(create table, create view, drop table 등)문을 수행해주는 메소드로서 
	            결과값은 int 형태로서 DML문 이라면 적용된 행의 갯수가 나오고, DDL문 이라면 0이 나온다.
            */
			System.out.println("create sequence : " + n);
			// create sequence : 0
			
			pstmt = conn.prepareStatement(sql_7);
			n = pstmt.executeUpdate();
			System.out.println("insert 를 한 DML문의 n : " + n);
			// insert 를 한 DML문의 n : 1 
			
			pstmt = conn.prepareStatement(sql_8);
			rs = pstmt.executeQuery();
			
			StringBuilder sb = new StringBuilder();
			int cnt = 0;
			
			while(rs.next()) {
				cnt++;
				
				if(cnt==1) {
					System.out.println("---------------------------------------");
					System.out.println("일련번호\t성명\t글내용");
					System.out.println("---------------------------------------");
				}
				
				sb.append( rs.getInt("NO") + "\t" + rs.getString("NAME") + "\t" + rs.getString("MSG") + "\n" );
			}// end of while(rs.next())------------------------------
			
			if(cnt > 0) {
				System.out.println(sb.toString());
			}
			else {
				System.out.println(">> 입력된 데이터가 없습니다. <<");
			}
			
		} catch (ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			// >>> 6. 사용하였던 자원을 반납하기 <<< //
			// 반납의 순서는 생성순서의 역순으로 한다. 
			
			try {
				if(rs != null)
					rs.close();
				
				if(pstmt != null)
					pstmt.close();
				
				if(conn != null)
					conn.close();
			} catch (SQLException e) {

			}
			
		}

		System.out.println("~~~ 프로그램 종료 ~~~");
        
	}// end of main()----------------------------

}
