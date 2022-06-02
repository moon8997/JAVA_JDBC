set hidden param parseThreshold = 150000;

show user;
-- USER이(가) "HR"입니다.

---- *** 회원 테이블 생성하기 *** ----
select *
from user_tables
where table_name = 'JDBC_MEMBER';

create table jdbc_member
(userseq       number        not null    -- 회원번호
,userid        varchar2(30)  not null    -- 회원아이디 
,passwd        varchar2(30)  not null    -- 회원암호
,name          varchar2(20)  not null    -- 회원명
,mobile        varchar2(20)              -- 연락처
,point         number(10) default 0      -- 포인트
,registerday   date default sysdate      -- 가입일자 
,status        number(1) default 1       -- status 컬럼의 값이 1 이면 정상, 0 이면 탈퇴 
,constraint PK_jdbc_member primary key(userseq)
,constraint UQ_jdbc_member unique(userid)
,constraint CK_jdbc_member check( status in(0,1) )
);

create sequence userseq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


select *
from jdbc_member
order by userseq asc;

update jdbc_member set status = 1;

commit;


---- *** 게시판 테이블 생성하기 *** ----
create table jdbc_board
(boardno       number        not null          -- 글번호
,fk_userid     varchar2(30)  not null          -- 작성자아이디
,subject       varchar2(100) not null          -- 글제목
,contents      varchar2(200) not null          -- 글내용
,writeday      date default sysdate not null   -- 작성일자
,viewcount     number default 0 not null       -- 조회수 
,boardpasswd   varchar2(20) not null           -- 글암호 
,constraint PK_jdbc_board primary key(boardno)
,constraint FK_jdbc_board foreign key(fk_userid) references jdbc_member(userid) 
);


create sequence board_seq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

desc jdbc_board;

select *
from jdbc_board
order by boardno desc;

select *
from jdbc_member
where userid = 'sdfsfaf';

update jdbc_member set status = 1
where userid = 'leess';

commit;

---------------------------------------------------------------------
insert into jdbc_board(boardno, fk_userid, subject, contents, boardpasswd)
values(board_seq.nextval, 'eomjh', '짜장면', '맛있어요~!!', '1234');

insert into jdbc_board(boardno, fk_userid, subject, contents, boardpasswd)
values(board_seq.nextval, 'leess', '돈까스', '좋아해요~!!', '1234');

insert into jdbc_board(boardno, fk_userid, subject, contents, boardpasswd)
values(board_seq.nextval, 'eomjh', '치킨', '맥주와 함께~~ 치맥!! 캬~~', '1234');

insert into jdbc_board(boardno, fk_userid, subject, contents, boardpasswd)
values(board_seq.nextval, 'leehr', '피자', '아주 좋아해요~~!!', '1234');

commit;


select *
from jdbc_board
order by boardno desc;


select B.boardno, B.subject, M.name
     , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss'), B.viewcount
from jdbc_board B JOIN jdbc_member M 
ON B.fk_userid = M.userid
order by boardno desc;


select boardno, fk_userid, subject, contents, writeday, viewcount
from jdbc_board
where boardno = 3;


select *
from jdbc_board
where boardno = '2';

select *
from jdbc_board
where boardno = 2 and fk_userid = 'leess';

select *
from jdbc_board
where boardno = 3;

select *
from jdbc_board
where boardno = 3 and fk_userid = 'leess';

select *
from jdbc_board
where boardno = '35434';

select *
from jdbc_board
where boardno = 'asfdsfsd';
-- ORA-01722: invalid number

select *
from jdbc_board
where boardno = 'ㄴㄹㄹㄴㅁㄹㄴㅇ';
-- ORA-01722: invalid number

select *
from jdbc_board
where boardno = 35434 and fk_userid = 'leess';


select *
from jdbc_member;

update jdbc_member set passwd = '5555'
where userid = 'leess';

rollback;


--- BOARD_SEQ 시퀀스를 사용했을때 다음에 들어올 값을 알고자 할 경우 ---
select last_number
from user_sequences
where sequence_name = 'BOARD_SEQ';

------------------------------------------------------------------------

---- *** 댓글 테이블 생성하기 *** ----
create table jdbc_comment 
(commentno   number        not null    -- 댓글번호 
,fk_boardno  number        not null    -- 원글의 글번호 
,fk_userid   varchar2(30)  not null    -- 사용자ID
,contents    varchar2(200) not null    -- 댓글내용 
,writeday    date default sysdate      -- 작성일자
,constraint  PK_jdbc_comment  primary key(commentno) 
,constraint  FK_jdbc_comment_fk_boardno foreign key(fk_boardno) 
             references jdbc_board(boardno) on delete cascade 
,constraint  FK_jdbc_comment_fk_userid  foreign key(fk_userid) 
             references jdbc_member(userid) 
);


create sequence seq_comment
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


select *
from jdbc_comment; 

insert into jdbc_comment(commentno, fk_boardno, fk_userid, contents)
values(1, '3453242', 'leess', '연습');
/*
오류 보고 -
ORA-02291: integrity constraint (HR.FK_JDBC_COMMENT_FK_BOARDNO) violated - parent key not found
*/

rollback;

select *
from jdbc_comment
where fk_boardno = 4;


select C.contents, M.name, C.writeday
from 
(select contents, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday, fk_userid
 from jdbc_comment 
 where fk_boardno = '4' ) C JOIN jdbc_member M 
ON C.fk_userid = M.userid; 


-------------------------------------------------------------------------
--- 글제목 다음에 딸린 댓글의 개수를 보여주고자 한다.

select B.boardno, B.subject, M.name 
     , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss'), B.viewcount 
from jdbc_board B JOIN jdbc_member M 
ON B.fk_userid = M.userid 
order by boardno desc;

select fk_boardno, count(*) AS COMMENTCNT
from jdbc_comment
group by fk_boardno;


select B.boardno, B.subject, M.name 
     , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss'), B.viewcount
     , nvl(C.COMMENTCNT, 0)
from jdbc_board B JOIN jdbc_member M 
ON B.fk_userid = M.userid 
LEFT JOIN (select fk_boardno, count(*) AS COMMENTCNT
           from jdbc_comment
           group by fk_boardno) C
ON B.boardno = C.fk_boardno  
order by 1 desc;


-- *** 최근 1주일간 일자별 게시글 작성건수 *** --
select boardno, fk_userid, writeday, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss'),
       sysdate - writeday,
       to_date( to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd' ) - to_date( to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd' )
from jdbc_board
order by boardno desc;


select *
from jdbc_board
where to_date( to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd' ) - to_date( to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd' ) < 7;


--- *** 특정 날짜를 입력받아서 그 날짜의 자정(0시0분0초)의 값을 반환시켜주는 함수를 생성해봅니다. *** ---
create or replace function func_midnight
(p_date  IN  date)
return  date
is
begin
    return to_date( to_char(p_date, 'yyyy-mm-dd'), 'yyyy-mm-dd' );
end func_midnight;
-- Function FUNC_MIDNIGHT이(가) 컴파일되었습니다.


--- *** 생성되어진 함수의 원본소스를 조회해본다. *** ---
select text
from user_source
where type = 'FUNCTION' and name = 'FUNC_MIDNIGHT';


select *
from jdbc_board
where func_midnight(sysdate) - func_midnight(writeday) < 7;

/*
  ----------------------------------------------------------------------------------
   TOTAL   PREVIOUS6  PREVIOUS5  PREVIOUS4  PREVIOUS3  PREVIOUS2  PREVIOUS1   TODAY
  ----------------------------------------------------------------------------------
     3         3          0          0         0          0          0          0
*/

select writeday 
     , decode( func_midnight(sysdate) - func_midnight(writeday), 6, 1, 0)
     , decode( func_midnight(sysdate) - func_midnight(writeday), 5, 1, 0)
     , decode( func_midnight(sysdate) - func_midnight(writeday), 4, 1, 0)
     , decode( func_midnight(sysdate) - func_midnight(writeday), 3, 1, 0)
     , decode( func_midnight(sysdate) - func_midnight(writeday), 2, 1, 0)
     , decode( func_midnight(sysdate) - func_midnight(writeday), 1, 1, 0)
     , decode( func_midnight(sysdate) - func_midnight(writeday), 0, 1, 0)
from jdbc_board
where func_midnight(sysdate) - func_midnight(writeday) < 7;


select count(*) AS TOTAL
     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 6, 1, 0)) AS PREVIOUS6
     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 5, 1, 0)) AS PREVIOUS5
     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 4, 1, 0)) AS PREVIOUS4
     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 3, 1, 0)) AS PREVIOUS3
     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 2, 1, 0)) AS PREVIOUS2
     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 1, 1, 0)) AS PREVIOUS1
     , sum(decode( func_midnight(sysdate) - func_midnight(writeday), 0, 1, 0)) AS TODAY
from jdbc_board
where func_midnight(sysdate) - func_midnight(writeday) < 7;



--- *** 저번달 및 이번달 일자별 게시글 작성건수 *** ---
select *
from jdbc_board;

update jdbc_board set writeday = add_months(writeday, -1)
where boardno = 1;
-- 1 행 이(가) 업데이트되었습니다.

commit;

select *
from jdbc_board
where to_char(writeday, 'yyyy-mm') = to_char(sysdate, 'yyyy-mm') OR
      to_char(writeday, 'yyyy-mm') = to_char(add_months(sysdate, -1), 'yyyy-mm');
      

select decode( grouping( to_char(writeday, 'yyyy-mm-dd') ), 0, to_char(writeday, 'yyyy-mm-dd'), '전체') AS WRITEDAY 
     , count(*) AS CNT
from jdbc_board
where to_char(writeday, 'yyyy-mm') = to_char(sysdate, 'yyyy-mm') OR
      to_char(writeday, 'yyyy-mm') = to_char(add_months(sysdate, -1), 'yyyy-mm')
group by ROLLUP( to_char(writeday, 'yyyy-mm-dd') );    


------------------------------------------------------------------------------
select *
from jdbc_board;

delete from jdbc_board
where boardno = 'ㄴㅇㄹㄴㅇㄹㄹ'; -- 삭제하려는 글번호에 문자를 입력한 경우 
/*
오류 보고 -
ORA-01722: invalid number
*/

delete from jdbc_board
where boardno = '337'; -- 존재하지 않는 글번호를 삭제하려는 경우 
-- 0개 행 이(가) 삭제되었습니다.


delete from jdbc_board
where boardno = '1' and fk_userid = 'leess'; -- 존재하는 글번호 이지만 다른사용자가 작성한 글을 삭제하려는 경우
-- 0 행 이(가) 삭제되었습니다.


delete from jdbc_board
where boardno = '1' and fk_userid = 'eomjh' and boardpasswd = '234234'; -- 존재하는 글번호 이면서 자신이 작성한 글인데 글암호가 틀린 경우에 삭제하려는 경우 
-- 0 행 이(가) 삭제되었습니다.


delete from jdbc_board
where boardno = '1' and fk_userid = 'eomjh' and boardpasswd = '1234'; -- 존재하는 글번호 이면서 자신이 작성한 글인데 글암호가 올바른 경우에 삭제하려는 경우 
-- 1 행 이(가) 삭제되었습니다.


rollback;
-- 롤백 완료.


      















