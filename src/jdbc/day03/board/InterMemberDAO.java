package jdbc.day03.board;

import java.util.List;
import java.util.Map;

public interface InterMemberDAO {

	// 자원반납 메소드 
	void close();
	
	// 회원가입시 사용가능한 아이디 인지 중복된 아이디 이라서 사용불가인 알려주는 메소드
	boolean isUse_userid(String userid);
	
	// 회원가입(insert) 메소드
	int memberRegister(MemberDTO member);
	
	// 로그인 처리(select) 메소드
	MemberDTO login(Map<String, String> paraMap);

	// 관리자를 제외한 모든 회원들을 선택한 정렬기준으로 보여주는(select) 메소드
	List<MemberDTO> selectAllMember(String sortChoice); 
	
}
