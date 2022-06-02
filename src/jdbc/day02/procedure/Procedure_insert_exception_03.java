/*
  >>>> Stored Procedure 란? <<<<<
  Query 문을 하나의 파일형태로 만들거나 데이터베이스에 저장해 놓고 함수처럼 호출해서 사용하는 것임.
  Stored Procedure 를 사용하면 연속되는 query 문에 대해서 매우 빠른 성능을 보이며, 
  코드의 독립성과 함께 보안적인 장점도 가지게 된다.


create or replace procedure pcd_jdbc_tbl_student_insert
(p_stno           IN  jdbc_tbl_student.stno%type
,p_name           IN  jdbc_tbl_student.name%type
,p_tel            IN  jdbc_tbl_student.tel%type
,p_addr           IN  jdbc_tbl_student.addr%type
,p_fk_classno     IN  jdbc_tbl_student.fk_classno%type
)
is
   v_day          varchar2(1);
   v_hour         varchar2(2);
   error_dayTime  exception;
begin
    -- 오늘의 요일명 알아오도록 한다.
    v_day := to_char(sysdate, 'd'); -- '1','2','3','4','5','6','7' 
                                    --  일  월  화  수  목  금  토 
    v_hour := to_char(sysdate, 'hh24');  
    
    if ( v_day in('1','7') OR 
         v_hour < '09' OR v_hour > '13' ) then
        raise error_dayTime;
    else
        insert into jdbc_tbl_student(stno, name, tel, addr, fk_classno)
        values(p_stno, p_name, p_tel, p_addr, p_fk_classno);
    end if;
    
    exception 
        when error_dayTime then
             raise_application_error(-20005,'>> 영업시간(월~금 09시-14시이전) 아니므로 insert 를 할 수 없습니다. <<');
    
end pcd_jdbc_tbl_student_insert;
-- Procedure PCD_JDBC_TBL_STUDENT_INSERT이(가) 컴파일되었습니다.

*/

package jdbc.day02.procedure;

import java.sql.*;
import java.util.Scanner;


public class Procedure_insert_exception_03 {

	public static void main(String[] args) {

		Connection conn = null;
	 // Connection conn 은 오라클 데이터베이스 서버와 연결을 맺어주는 객체
		
		CallableStatement cstmt = null;
	 // CallableStatement cstmt 은 Connection conn(연결한 오라클 서버)에 존재하는 Procedure 를 호출할 객체(우편배달부)이다. 
		
		String stno = "";
		String fk_classno = "";
		
		try {
			// >>> 1. 오라클 드라이버 로딩 <<<  //
			/*
			   === OracleDriver(오라클 드라이버)의 역할 ===
			   1). OracleDriver 를 메모리에 로딩시켜준다.
			   2). OracleDriver 객체를 생성해준다.
			   3). OracleDriver 객체를 DriverManager에 등록시켜준다.
			       --> DriverManager 는 여러 드라이버들을 Vector 에 저장하여 관리해주는 클래스이다.
			*/ 
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			
			// >>> 2. 어떤 오라클 서버와 연결을 할래? <<< //
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "cclass");
			
			
			// >>> 3. Connection conn 객체를 사용하여 prepareCall() 메소드를 호출함으로써 
			//        CallableStatement cstmt 객체를 생성한다.
			//        즉, CallableStatement cstmt 객체가 우편배달부(택배기사)쯤에 해당하는 것이다. <<< // 
			cstmt = conn.prepareCall("{call pcd_jdbc_tbl_student_insert(?,?,?,?,?)}");
			/*
			   오라클 서버에 생성한 프로시저  pcd_jdbc_tbl_student_insert 의 
			   매개변수 갯수가 5개 이므로 ? 를 5개 준다.
						  
			   다음으로 오라클의 프로시저를 수행( executeUpdate() ) 하기에 앞서서  
			   반드시 해야할 일은 IN mode 로 되어진 파라미터에 값을 넣어준다.
			*/
			
			Scanner sc = new Scanner(System.in);
			System.out.print("▷ 학번 : ");
			stno = sc.nextLine();
			
			System.out.print("▷ 성명 : ");
			String name = sc.nextLine();
			
			System.out.print("▷ 전화번호 : ");
			String tel = sc.nextLine();
			
			System.out.print("▷ 주소 : ");
			String addr = sc.nextLine();
			
			System.out.print("▷ 학급번호 : ");
			fk_classno = sc.nextLine();
			
			cstmt.setString(1, stno);       // 숫자 1은 프로시저 파라미터중 첫번째 파라미터인 IN 모드의 ? 를 말한다.
			cstmt.setString(2, name);       // 숫자 2은 프로시저 파라미터중 두번째 파라미터인 IN 모드의 ? 를 말한다.
			cstmt.setString(3, tel);        // 숫자 3은 프로시저 파라미터중 세번째 파라미터인 IN 모드의 ? 를 말한다.
			cstmt.setString(4, addr);       // 숫자 4은 프로시저 파라미터중 네번째 파라미터인 IN 모드의 ? 를 말한다.
			cstmt.setString(5, fk_classno); // 숫자 5은 프로시저 파라미터중 다섯번째 파라미터인 IN 모드의 ? 를 말한다.
			
			
			// >>> 4. CallableStatement cstmt 객체를 사용하여 오라클의 프로시저 실행하기 <<< //
			int n = cstmt.executeUpdate();  // 오라클 서버에게 해당 프로시저를 실행해라는 것이다.
			// 프로시저의 실행은 cstmt.executeUpdate(); 또는 cstmt.execute(); 이다. 
			
			if(n == 1) {
				System.out.println(">> 데이터 입력 성공!! <<");
			}
			
			sc.close();
		
		} catch(ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다. <<");
		} catch(SQLException e) {
			
			int errorCode = e.getErrorCode();
			
			if(errorCode == 20005) {
				System.out.println(e.getMessage());
			}
			else if(errorCode == 2291) {
				System.out.println(">> 학급번호 "+fk_classno+"은 우리학교에 존재하지 않는 학급번호 입니다. 학급번호를 올바르게 입력하세요 <<\n");
			}
			else if(errorCode == 1) {
				System.out.println(">> 학번 "+stno+"은 이미 사용중이므로 다른 학번을 입력하세요 <<\n");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			// >>> 6. 사용하였던 자원을 반납하기 <<< //
			// 반납의 순서는 생성순서의 역순으로 한다. 
			
			try {
				if(cstmt != null)
					cstmt.close();
				
				if(conn != null)
					conn.close();
			} catch (SQLException e) {

			}
			
		}
		
        System.out.println("\n~~~ 프로그램 종료 ~~~");
        
	}// end of main()----------------------------------------------

}
