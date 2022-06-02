package jdbc.day03.board;

import java.text.SimpleDateFormat;
import java.util.*;

import jdbc.util.MyUtil;

public class TotalController {

	// field, attribute, property, 속성
	InterMemberDAO mdao = new MemberDAO();
	InterBoardDAO  bdao = new BoardDAO();
	
	// operation, method, 기능
	
	// *** 시작메뉴 메소드 생성하기 *** //
	public void menu_Start(Scanner sc) {
		
		MemberDTO member = null;
		String s_Choice = "";
		
		do {
			String loginName = "";
			String login_logout = "로그인";
			
			if(member != null) {
				loginName = "["+ member.getName() +"님(Point "+ member.getPoint() +") 로그인중..]";
				login_logout = "로그아웃";
			}
			
			System.out.println("\n>>> --------- 시작메뉴 "+ loginName +" --------- <<< \n"
			         + "1.회원가입    2."+login_logout+"    3.프로그램종료 \n"
			         + "-----------------------------------\n");
	
			System.out.print("▷ 메뉴번호 선택 : ");
			s_Choice = sc.nextLine();
			
			switch (s_Choice) {
				case "1":  // 회원가입
					member = memberRegister(sc);
					break;
				
					
				case "2":  
					 if("로그인".equals(login_logout)) {  
						 
						member = login(sc); // 로그인 처리 하기
						
						if(member != null) {           // 로그인이 성공한 경우
							menu_Board(member, sc);    // 게시판 메뉴에 들어간다.
						}
					 }	 
					 
					 else {
						 // 로그아웃
						 member = null;
						 System.out.println(">> 로그아웃 되었습니다. << \n");
					 }
					 
					break;	
				
					
				case "3":  
					   // 프로그램종료

					break;								
	
					
				default:
					System.out.println(">>> 메뉴에 없는 번호입니다. 다시 선택하세요!! <<< \n");
					break;
			}// end of switch (key)----------------
		
		} while( !("3".equals(s_Choice) ));
		
	}// end of public void menu_Start(Scanner sc)-----------------------------

	
	
	// *** 회원가입을 처리해주는 메소드 생성하기 *** //
	private MemberDTO memberRegister(Scanner sc) {
		
		System.out.println("\n >>> ---- 회원가입 ---- <<< ");
		
		String userid = "";
		
		do {
			System.out.print("1. 아이디 : ");
			userid = sc.nextLine();  
			
			boolean isUse = mdao.isUse_userid(userid);
			
			if( !isUse ) {
				System.out.println("\n >> "+userid+" 아이디가 이미 사용중이므로 다른 아이디값을 입력하세요!! << \n"); 
			}
			else {
				System.out.println("\n >> "+userid+" 아이디로 사용가능 합니다!! << \n"); 
				break;
			}
			
		} while(true);
		
		
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
			System.out.println("\n >> 회원가입 성공!! <<");
			menu_Board(member, sc);  // 게시판 메뉴에 들어간다.
			return member;
		}
		
		else {
			return null;
		}
		
	}// end of private void memberRegister(Scanner sc)-------------------------



	// *** 로그인 처리하기 메소드 생성하기 *** //
	private MemberDTO login(Scanner sc) {
		
		MemberDTO member = null;
		
		System.out.println("\n >>> ---- 로그인 ---- <<<");
		
		System.out.print("▷ 아이디 : ");
		String userid = sc.nextLine();
		
		System.out.print("▷ 비밀번호 : ");
		String passwd = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("passwd", passwd);
		
		member = mdao.login(paraMap);
		
		if(member != null) {
			System.out.println("\n >> 로그인 성공!! << \n");
		}
		else {
			System.out.println("\n >> 로그인 실패!! << \n");
		}
		
		return member;
	}// end of private MemberDTO login(Scanner sc)-----------------------------

	
	
	// *** 게시판 메뉴 메소드 생성하기 *** //
	private void menu_Board(MemberDTO member, Scanner sc) {
		
		String menuNo = "";
		
		String adminOnly_menu = ("admin".equals(member.getUserid()))?"10.모든회원정보보기":"";
		
		do {
			System.out.println("--------------- 게시판 메뉴["+member.getName() +"님(Point "+member.getPoint()+") 로그인중..] -----------------\n" 
					         + " 1.글목록보기    2.글내용보기    3.글쓰기     4.댓글쓰기 \n"
					         + " 5.글수정하기    6.글삭제하기    7.최근 1주일간 일자별 게시글 작성건수 \n"
					         + " 8.저번달 및 이번달 일자별 게시글 작성건수   9.나가기     "+ adminOnly_menu + "\n"
					         + "-----------------------------------------------------------"); 
			
			System.out.print("▷ 메뉴번호 선택 : ");
			menuNo = sc.nextLine();
			
			switch (menuNo) {
				
				case "1": // 글목록보기
					boardList();
					break;
					
					
				case "2": // 글내용보기
					viewContents(sc, member.getUserid());
					break;
					
					
				case "3": // 글쓰기 (jdbc_board 테이블에 insert 및 jdbc_member 테이블의 point 컬럼 update)
					write(sc, member);
					break;
					
					
				case "4": // 댓글쓰기 (jdbc_comment 테이블에 insert 및 jdbc_member 테이블의 point 컬럼 update)
					write_comment(sc, member);
					break;
					
					
				case "5": // 글수정하기
					int n = updateBoard(sc, member);
					
					if(n==0) {
						System.out.println(">> [경고] 수정할 글번호 입력값은 숫자만 가능합니다. << \n");
					}
					else if(n==1) {
						System.out.println(">> [경고] 수정할 글번호는 존재하지 않는 글번호 입니다. << \n");
					}
					else if(n==2) {
						System.out.println(">> [경고] 다른 사용자의 글은 수정불가 합니다. << \n");
					}
					else if(n==3) {
						System.out.println(">> [경고] 입력하신 글 암호가 글작성시 입력한 것과 일치하지 않습니다. << \n");
					}
					else if(n==4) {
						System.out.println(">> 글수정이 성공되었습니다. << \n");
					}
					else if(n==5) {
						System.out.println(">> [장애발생] DB에 장애가 발생하여 글수정이 불가 합니다. << \n");
					}
					else if(n==6) {
						System.out.println(">> 글수정을 취소하였습니다. << \n");
					}
					break;
					
					
				case "6": // 글삭제하기
					n = deleteBoard(sc, member);
					
					if(n==0) {
						System.out.println(">> [경고] 삭제할 글번호 입력값은 숫자만 가능합니다. << \n");
					}
					else if(n==1) {
						System.out.println(">> [경고] 삭제할 글번호는 존재하지 않는 글번호 입니다. << \n");
					}
					else if(n==2) {
						System.out.println(">> [경고] 다른 사용자의 글은 삭제불가 합니다. << \n");
					}
					else if(n==3) {
						System.out.println(">> [경고] 입력하신 글 암호가 글작성시 입력한 것과 일치하지 않습니다. << \n");
					}
					else if(n==4) {
						System.out.println(">> 글삭제가 성공되었습니다. << \n");
					}
					else if(n==5) {
						System.out.println(">> [장애발생] DB에 장애가 발생하여 글삭제가 불가 합니다. << \n");
					}
					else if(n==6) {
						System.out.println(">> 글삭제를 취소하였습니다. << \n");
					}
					
					break;
					
					
				case "7": // 최근 1주일간 일자별 게시글 작성건수
					statisticsByWeek();
					break;
				
					
				case "8": // 저번달 및 이번달 일자별 게시글 작성건수
					statisticsByPrevious_CurrentMonth();
					break;
				
					
				case "9": // 나가기
					
					break;	
					
					
				case "10": // 10.모든회원정보보기(관리자전용)
					
					if( "admin".equals(member.getUserid()) ) {
						
						System.out.println("[1:회원명의 오름차순  / 2:회원명의 내림차순 / \n "
								         + "3:가입일자의 오름차순 / 4:가입일자의 내림차순]");
						System.out.print("▷ 정렬선택 : ");
						String sortChoice = sc.nextLine();
						
						// 1 또는 2 또는 3 또는 4 를 제외한 나머지가 sortChoice 에 입력되면 1 로 보겠다.
						if( !("1".equals(sortChoice) || "2".equals(sortChoice) ||
						      "3".equals(sortChoice) || "4".equals(sortChoice)) ) {
							sortChoice = "1";
						}
						
						selectAllMember(sortChoice);
						// 관리자를 제외한 모든 회원들을 선택한 정렬기준으로 보여주는 메소드 호출하기  
					}
					
					else {
						System.out.println(">> 메뉴에 없는 번호 입니다. << \n");
					}
					
					break;	
					
	
				default:
					System.out.println(">> 메뉴에 없는 번호 입니다. << \n");
					
					break;
			}//
			
		} while( !("9".equals(menuNo)) );
		
	}// end of private void menu_Board()-----------------
	
	



	// *** 글목록보기 메소드 생성하기 *** //
	private void boardList() {
		
		List<BoardDTO> boardList = bdao.boardList();
		
		if( boardList.size() > 0 ) {
			// 게시글이 존재하는 경우
			
			System.out.println("\n----------------------------- [게시글 목록] -----------------------------");
			System.out.println("글번호\t글제목\t\t작성자\t작성일자\t\t조회수");
			System.out.println("-------------------------------------------------------------------------");
			
			StringBuilder sb = new StringBuilder();
			
			for(BoardDTO board : boardList) {
				String subject = board.getSubject();
				
				if(subject.length() > 8) {
					subject = subject.substring(0, 8)+"..";
				}
				else {
					int cnt = 10-subject.length();
					
					String blank = "";
					for(int i=0; i<cnt; i++) {
						blank += " ";
					}//end of for-------------------
					
					subject += blank;
				}
				
				String commentcnt = (board.getCommentcnt() > 0)?"["+board.getCommentcnt()+"]":"";
				// 해당 원글에 딸린 댓글의 개수 
				
				sb.append( board.getBoardno()+"\t"+
						   subject+commentcnt+"\t"+  
			               board.getMember().getName()+"\t"+  
						   board.getWriteday()+"\t"+ 
						   board.getViewcount()+"\n"
						 );
			}// end of for------------------------------
			
			System.out.println(sb.toString());
		}
		else {
			// 게시글이 1개도 존재하지 않을 경우 
			System.out.println(">> 글목록이 없습니다. \n <<");
		}
		
	}// end of private void boardList()-----------------------------


	// *** 글내용보기 메소드 생성하기 *** //
	private void viewContents(Scanner sc, String login_userid) {
		
		System.out.println("\n>>> 글내용 보기 <<<");
		
		System.out.print("▷ 글번호 : ");
		String boardno = sc.nextLine();  
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("boardno", boardno);
		paraMap.put("fk_userid", login_userid);
		
		BoardDTO board = bdao.viewContents(paraMap);
		
		if(board != null) {
			// 존재하는 글번호를 입력한 경우
			System.out.println("\n>> ==== [글내용 보기] ==== <<");
			System.out.println("▷ 글번호 : " + board.getBoardno() + "\n"
					         + "▷ 작성자 : " + board.getMember().getName() + "\n"
					         + "▷ 글제목 : " + board.getSubject() + "\n"
					         + "▷ 글내용 : " + board.getContents() + "\n"
					         + "▷ 작성일자 : " + board.getWriteday() + "\n"
					         + "▷ 글조회수 : " + board.getViewcount() + "\n");
		
		
		    ////////////////////////////////////////////////////////////////////////
		
			System.out.println(">> [댓글내용보기] <<");
			System.out.println("----------------------------------------------------");
			
			List<BoardCommentDTO> commentList = bdao.commentList(boardno);
			
		    if( commentList.size() > 0 ) {
		    	// 딸린 댓글이 존재하는 경우 
		    	
		    	System.out.println("댓글내용\t\t작성자\t작성일자");
		    	System.out.println("----------------------------------------------------");
		    	
		    	StringBuilder sb = new StringBuilder();
		    	for(BoardCommentDTO comment : commentList) {
		    		sb.append( comment.getContents() + "\t" + comment.getMember().getName() + "\t" + comment.getWriteday() + "\n" );  
		    	}// end of for-------------------
		    	
		    	System.out.println(sb.toString());
		    }
		    
		    else {
		    	// 딸린 댓글이 존재하지 않는 경우
		    	System.out.println(" == 댓글내용 없음 == ");
		    	System.out.println("----------------------------------------------------\n");
		    }
		    
		}
		
		
	}// end of private void viewContents(Scanner sc, String userid)-----------
	
	
	
	// *** 글쓰기 메소드 생성하기 *** //
	private void write(Scanner sc, MemberDTO member) {
		
		System.out.println("\n>>> 글쓰기 <<<");
		
		System.out.println("1. 작성자명 : " + member.getName());
		
		System.out.print("2. 글제목 : ");
		String subject = sc.nextLine();
		
		System.out.print("3. 글내용 : ");
		String contents = sc.nextLine();
		
		System.out.print("4. 글암호 : ");
		String boardpasswd = sc.nextLine();
		
		String yn = "";
		
		do {
			System.out.print(">> 정말로 글쓰기를 하시겠습니까?[Y/N] : ");
			yn = sc.nextLine();
			
			if( "y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn) ) {
				break;
			}
			else {
				System.out.println(">> [경고] Y 또는 N 만 입력하세요!! << \n");
			}
		} while(true);
		
		
		if( "n".equalsIgnoreCase(yn) ) {
			System.out.println(">> 글쓰기를 취소하셨습니다. << \n");
		}
		else {
			// 사용자가 "y"를 입력한 경우
			BoardDTO board = new BoardDTO();
			board.setFk_userid(member.getUserid());
			board.setSubject(subject);
			board.setContents(contents);
			board.setBoardpasswd(boardpasswd);
			
			int n = bdao.write(board);
			
			if(n==1) {
				System.out.println(">> 글쓰기가 성공적으로 완료되었습니다. << \n");
				member.setPoint( member.getPoint() + 10 ); // !!!!!! 글쓰기가 성공되어지면 보여주는 포인트를 10 증가 해준다. !!!!!!! 
			}
			else {
				System.out.println(">> 장애가 발생되어 글쓰기가 실패되었습니다. << \n");
			}
			
		}
		
	}// end of private void write(Scanner sc, String userid)-------------------
	
	
	
	// *** 댓글쓰기 메소드 생성하기 *** //
	private void write_comment(Scanner sc, MemberDTO member) {
		
		System.out.println("\n>>> 댓글쓰기 <<<");
		
		System.out.println("1. 작성자명 : " + member.getName());
		
		String fk_boardno = "";
		do {
			System.out.print("2. 원글의 글번호 : ");
			fk_boardno = sc.nextLine(); // 존재하는 원글번호를 입력하는 경우
			                            // 존재하지 않는 원글번호를 입력하는 경우 "35343"
			                            // "ㄴㄹㄴㅇㄹㄴㅇ" "sdfsdfdsf" 와 같이 장난치는 경우
			try {
			     Integer.parseInt(fk_boardno);
			     break;
			} catch (NumberFormatException e) {
				System.out.println(">> [경고] 원글의 글번호는 정수로만 입력하세요!! <<");
			}
		} while(true);
		
		
		
		String contents = "";
		do {
			System.out.print("3. 댓글내용 : "); // "안녕하세요" 와 같이 잘 입력해주는 경우 
			                                  // "       " 와 같이 공백만 입력해주는 경우 
			                                  // 그냥 엔터만 하는 경우 
			contents = sc.nextLine();
			
			if(contents.trim().isEmpty()) {
				// "       " 와 같이 공백만 입력해주는 경우 
	            // 그냥 엔터만 하는 경우
				System.out.println(">> [경고] 댓글내용은 그냥엔터나 공백만으로는 안되요!! <<");
			}
			else {
				break;
			}
			
		} while(true);
		
		
		BoardCommentDTO comment = new BoardCommentDTO();
		comment.setFk_boardno(fk_boardno);        // 원글의 글번호   
		comment.setFk_userid(member.getUserid()); // 댓글을 작성하고자 하는 사용자(==현재 로그인 되어진 사용자)의 userid  
		comment.setContents(contents);            // 댓글내용 
		
		int n = bdao.write_comment(comment); 
		
		if(n==1) {
			// 댓글쓰기가 성공한 경우 
			System.out.println(">> 댓글쓰기 성공!! << \n");
			member.setPoint( member.getPoint() + 5 ); // !!!!!! 댓글쓰기가 성공되어지면 보여주는 포인트를 5 증가 해준다. !!!!!!! 
		}
		else if(n == -1) {
			// 사용자가 댓글을 쓸때 존재하지 않는 원글번호를 입력한 경우 
			System.out.println(">> 올바른 원글번호를 입력하세요!! \n");
		}
		else { // n == 0
			// 기타 장애가 발생한 경우 
			System.out.println(">> [장애발생] DB에 장애로 인하여 댓글쓰기 실패 !! \n");
		}
		
	}// end of private void write_comment(Scanner sc, MemberDTO member)---------

	
	
	
	// *** 글수정하기 메소드 생성하기 *** //
	private int updateBoard(Scanner sc, MemberDTO member) {
		
		int n = 0;
		
		System.out.println("\n>>> 글 수정 하기 <<<");
		
		System.out.print("▷ 수정할 글번호 : "); 
		String boardno = sc.nextLine();
		
		try {
			Integer.parseInt(boardno);
			
			System.out.print("▷ 수정할 글제목 : "); 
			String subject = sc.nextLine();
			
			System.out.print("▷ 수정할 글내용 : "); 
			String contents = sc.nextLine();
			
			System.out.print("▷ 수정할 글의 글암호 : "); 
			String boardpasswd = sc.nextLine();
			
			do {
				System.out.print(">> 정말로 글을 수정하시겠습니까?[Y/N] : ");
				String yn = sc.nextLine();
				
				if("n".equalsIgnoreCase(yn)) {
				    n = 6;
				//  n ==> 6 이라면 글 수정을 사용자가 취소한 경우 	
				 
				    break;
				}
				else if("y".equalsIgnoreCase(yn)) {
					
					Map<String, String> paraMap = new HashMap<>();
					paraMap.put("boardno", boardno);
					paraMap.put("boardpasswd", boardpasswd);
					paraMap.put("userid", member.getUserid());
					
					paraMap.put("subject", subject);
					paraMap.put("contents", contents);
					
					n = bdao.updateBoard(paraMap);
				/*
				    n ==> 1 이라면 존재하지 않는 글번호(boardno)를 가지고 글을 수정하려는 경우 	
				    n ==> 2 이라면 다른 사용자의 글을 수정하려고 한 경우 
				    n ==> 3 이라면 수정하려는 글의 글암호가 글수정시 입력받은 글암호와 일치하지 않는 경우 
				    n ==> 4 이라면 글수정이 성공한 경우
				    n ==> 5 이라면 SQL문에 장애가 발생한 경우 
				*/

					break;
				}
				else {
					System.out.println(">> [경고] Y 또는 N 만 입력하세요!! << \n");
				}
			} while(true);
			
		} catch(NumberFormatException e) {
			// str_boardno 값이 숫자로 변환될 수 없는 경우
			n = 0;
		}
		
		return n;
	}// end of private int updateBoard(Scanner sc, MemberDTO member)-----------------------------------
	
	
	
	// *** 글삭제하기 메소드 생성하기 *** //
	private int deleteBoard(Scanner sc, MemberDTO member) {
		
		int n = 0;
		
		System.out.println("\n>>> 글 삭제 하기 <<<");
		
		System.out.print("▷ 삭제할 글번호 : "); 
		String boardno = sc.nextLine();
		
		try {
			Integer.parseInt(boardno);
			
			System.out.print("▷ 삭제할 글의 글암호 : "); 
			String boardpasswd = sc.nextLine();
			
			do {
				System.out.print(">> 정말로 글을 삭제하시겠습니까?[Y/N] : ");
				String yn = sc.nextLine();
				
				if("n".equalsIgnoreCase(yn)) {
				    n = 6;
				//  n ==> 6 이라면 글 삭제를 사용자가 취소한 경우 	
				 
				    break;
				}
				else if("y".equalsIgnoreCase(yn)) {
					
					Map<String, String> paraMap = new HashMap<>();
					paraMap.put("boardno", boardno);
					paraMap.put("boardpasswd", boardpasswd);
					paraMap.put("userid", member.getUserid());
					
					n = bdao.deleteBoard(paraMap);
				/*
				    n ==> 1 이라면 존재하지 않는 글번호(boardno)를 가지고 글을 삭제하려는 경우 	
				    n ==> 2 이라면 다른 사용자의 글을 삭제하려고 한 경우 
				    n ==> 3 이라면 삭제하려는 글의 글암호가 글삭제시 입력받은 글암호와 일치하지 않는 경우 
				    n ==> 4 이라면 글삭제가 성공한 경우
				    n ==> 5 이라면 SQL문에 장애가 발생한 경우 
				*/

					break;
				}
				else {
					System.out.println(">> [경고] Y 또는 N 만 입력하세요!! << \n");
				}
			} while(true);
			
		} catch(NumberFormatException e) {
			// str_boardno 값이 숫자로 변환될 수 없는 경우
			n = 0;
		}
		
		return n;
	}// end of private int deleteBoard(Scanner sc, MemberDTO member)-----------------------------------
	
	
	
	
	
	// **** 최근 1주일간 일자별 게시글 작성건수 메소드 생성하기 **** //
	private void statisticsByWeek() {
		
		System.out.println("\n-------------------- [최근 1주일간 일자별 게시글 작성건수] --------------------");
		
		String title = "전체\t";
		
		// 만약 오늘이 2022-02-03 이라면 
		// 전체   2022-01-28   2022-01-29   2022-01-30   2022-01-31   2022-02-01   2022-02-02   2022-02-03
		// 와 같이 제목을 나타내고자 한다.
		for(int i=0; i<7; i++) {
			title += MyUtil.addDay(i-6) + "   ";  // -6  -5  -4  -3  -2  -1   0
		}// end of for----------------------------------------------------------
		
		System.out.println(title);
		// 전체   2022-01-28   2022-01-29   2022-01-30   2022-01-31   2022-02-01   2022-02-02   2022-02-03
		
		System.out.println("-------------------------------------------------------------------------");
		
		Map<String, Integer> resultMap = bdao.statisticsByWeek(); // 최근 1주일간 일자별 게시글 작성건수를 select 해서 나오는 결과물 
		
		String result = resultMap.get("TOTAL") + "\t" + 
				        resultMap.get("PREVIOUS6") + "\t" + 
				        resultMap.get("PREVIOUS5") + "\t" + 
				        resultMap.get("PREVIOUS4") + "\t" + 
				        resultMap.get("PREVIOUS3") + "\t" + 
				        resultMap.get("PREVIOUS2") + "\t" + 
				        resultMap.get("PREVIOUS1") + "\t" + 
				        resultMap.get("TODAY");
		
		System.out.println(result);
		System.out.println("");
		
	}// end of private void statisticsByWeek()-----------------------------------
	
	
	
	
	
	// *** 저번달 및 이번달 일자별 게시글 작성건수 *** //
	private void statisticsByPrevious_CurrentMonth() {
		
		Calendar currentDate = Calendar.getInstance(); 
		// 현재날짜와 시간을 얻어온다.
		
		currentDate.add(Calendar.MONTH, -1); // currentDate 가 1달전으로 변경되어진다.
		
		SimpleDateFormat sdateFmt = new SimpleDateFormat("yyyy년 MM월");
		String previous_1_month = sdateFmt.format(currentDate.getTime());
		
		currentDate = Calendar.getInstance(); 
		// 현재날짜와 시간을 얻어온다.
		String current_month = sdateFmt.format(currentDate.getTime());
		
		System.out.println("\n>>>> [ "+previous_1_month+" , "+current_month+" 일자별 게시글 작성건수] <<<<");
	//	System.out.println("\n>>>> [ 2022년 01월, 2022년 02월 일자별 게시글 작성건수] <<<<");
		
		System.out.println("------------------------------");
		System.out.println(" 작성일자\t작성건수");
		System.out.println("------------------------------");
		
		List<Map<String, String>> mapList = bdao.statisticsByPrevious_CurrentMonth();
		
		if(mapList.size() > 0) {
		
			StringBuilder sb = new StringBuilder();
			
			for(Map<String, String> map : mapList) {
				sb.append( map.get("WRITEDAY") + "\t" + map.get("CNT") + "\n" );
			}// end of for----------------------
			
			System.out.println(sb.toString());
		}
		else {
			System.out.println(" 작성된 게시글이 없습니다.");
		}
		
	}// end of private void statisticsByPrevious_CurrentMonth()-------------------------------------------
	
	
	
	

	// *** 관리자를 제외한 모든 회원들을 선택한 정렬기준으로 보여주는 메소드 생성하기 *** //
	private void selectAllMember(String sortChoice) {
		
		System.out.println("\n>>> ============== 모든 회원정보 ============== <<<");
		System.out.println("------------------------------------------------------------------");
		System.out.println("회원번호   아이디   성명   연락처   포인트   가입일자               탈퇴유무 ");
		System.out.println("------------------------------------------------------------------");
		
		List<MemberDTO> memberList = mdao.selectAllMember(sortChoice);
		
		int memberCount = memberList.size(); // "admin" 을 제외한 회원수
		
		if(memberCount > 0) {
			
			StringBuilder sb = new StringBuilder();
			
			for(MemberDTO member : memberList) {
				
				String str_status = ( member.getStatus() == 1 )?"가입중":"탈퇴함";
				
				sb.append( member.getUserseq() + "  " + 
						   member.getUserid() + "  " + 
						   member.getName() + "  " + 
						   member.getMobile() + "  " + 
						   member.getPoint() + "  " + 
						   member.getRegisterday() + "  " + 
						   str_status + "\n" );
				
			}// end of for---------------------
			
			System.out.println(sb.toString());
			
		}
		else {
			System.out.println("가입된 회원이 없습니다.");
		}
		
	}// end of private void selectAllMember(String sortChoice) ---------------
	
	
	
}
