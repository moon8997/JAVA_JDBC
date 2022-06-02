package jdbc.day03;

import java.util.*;


public class MemberCtrl {

	// attribute, field, property, 속성
	InterMemberDAO mdao = new MemberDAO();
	
	
	// operation, method, 기능
	
	// *** 시작메뉴를 보여주는 메소드 *** //
	public void menu_Start(Scanner sc) {
		
		MemberDTO member = null;
		String s_Choice = "";
		
		do {
        //   String loginName = (member!=null)?"["+member.getName()+" 로그인중..]":"";
        //   String login_logout = (member!=null)?"로그아웃":"로그인";
            
			String loginName = "";
			String login_logout = "로그인";
			String menu_myInfo = "";
			
            if(member!=null) {
            	loginName = "["+member.getName()+" 로그인중..]";
            	login_logout = "로그아웃";
            	menu_myInfo = "4.나의정보보기     5.회원탈퇴하기\n";
            }
			
			System.out.println("\n >>> ----- 시작메뉴 "+ loginName +" ----- <<< \n"
					         + "1.회원가입    2."+login_logout+"    3.프로그램종료\n"
					         + menu_myInfo
					         + "---------------------------------\n");
			
			System.out.print("▷ 메뉴번호 선택 : ");
			s_Choice = sc.nextLine();
			
			switch (s_Choice) {
				case "1": // 회원가입
                    memberRegister(sc);
					break;
					
				case "2": // 로그인 or 로그아웃
					if("로그인".equals(login_logout)) {
						member = login(sc); // 로그인 시도하기
					}
					else {
						member = null; // 로그아웃하기
						System.out.println(">>> 로그아웃 되었습니다. <<< \n");
					}
					
					break;	
					
				case "3": // 프로그램종료
					
					break;						
					
				case "4":	
				     if(member!=null) { // 나의정보보기
			            	System.out.println(member);
			            }
				     else 
				    	 System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<< \n");
					break;
				
				case "5":	
				     if(member!=null) { // 회원탈퇴하기
			            int n = mdao.memberDelete( member.getUserseq() );	
			            
			            if(n==1) {
			            	System.out.println(">> 회원탈퇴를 성공했습니다. <<<");
			            	member = null;
			            }
			            }
				     else 
				    	 System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<< \n");
					break;
					
				default:
					System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<< \n");
					break;
					
			}// end of switch (key)------------------
			
		} while( !("3".equals(s_Choice)) );
		
	}// end of public void munu_Start(Scanner sc)---------
	
	
	// *** 회원가입을 해주는 메소드 *** //
	private void memberRegister(Scanner sc) {
		
		System.out.println("\n >>> ---- 회원가입 ---- <<<");
		
		System.out.print("1. 아이디 : ");
		String userid = sc.nextLine();
		
		System.out.print("2. 비밀번호 : ");
		String passwd = sc.nextLine();
		
		System.out.print("3. 회원명 : ");
		String name = sc.nextLine();
		
		System.out.print("4. 연락처(휴대폰) : ");
		String mobile = sc.nextLine();
		
		MemberDTO member = new MemberDTO();
		member.setUserid(userid);
		member.setPasswd(passwd);
		member.setName(name);
		member.setMobile(mobile);
		
		int n = mdao.memberRegister(member);
		
		if(n==1) {
			System.out.println("\n >>> 회원가입을 축하드립니다. <<<");
		}
		
	}// end of private void memberRegister(Scanner sc)-------------------------
	
	
	
	// *** 로그인을 처리해주는 메소드 생성하기 *** //
	private MemberDTO login(Scanner sc) {
		
		MemberDTO member = null;
		
		System.out.println("\n >>> ---- 로그인 ---- <<< ");
		
		System.out.print("▷ 아이디 : ");
		String userid = sc.nextLine();  
		
		System.out.print("▷ 비밀번호 : ");
		String passwd = sc.nextLine();  
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("passwd", passwd);
		
		member = mdao.login(paraMap);
		
		if(member != null) {
			System.out.println("\n >>> 로그인 성공!! <<< \n");
		}
		
		else {
			System.out.println("\n >>> 로그인 실패!! <<< \n");
		}
		
		
		return member;
		
	}// end of private MemberDTO login(Scanner sc)-----------------------------
	

}
