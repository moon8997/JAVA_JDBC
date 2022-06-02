set hidden param parseThreshold = 150000;

show user;
-- USER이(가) "HR"입니다.

------------------------------------------------------------------------------
-- 1) 학급테이블 생성
create table jdbc_tbl_class
(classno        number(3)
,classname      varchar2(100)
,teachername    varchar2(20)
,constraint PK_jdbc_tbl_class_classno primary key(classno)
);

create sequence jdbc_seq_classno
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

insert into jdbc_tbl_class(classno, classname, teachername) 
values(jdbc_seq_classno.nextval, '자바웹프로그래밍A', '김샘'); 

insert into jdbc_tbl_class(classno, classname, teachername) 
values(jdbc_seq_classno.nextval, '자바웹프로그래밍B', '이샘');

insert into jdbc_tbl_class(classno, classname, teachername) 
values(jdbc_seq_classno.nextval, '자바웹프로그래밍C', '서샘');

commit;

select *
from jdbc_tbl_class;


-- 2) 학생테이블 생성 
create table jdbc_tbl_student
(stno           number(8)               -- 학번
,name           varchar2(20) not null   -- 학생명
,tel            varchar2(15) not null   -- 연락처
,addr           varchar2(100)           -- 주소
,registerdate   date default sysdate    -- 입학일자
,fk_classno     number(3) not null      -- 학급번호
,constraint PK_jdbc_tbl_student_stno primary key(stno)
,constraint FK_jdbc_tbl_student_classno foreign key(fk_classno) 
                                        references jdbc_tbl_class(classno)
);    

-- 학번에 사용할 시퀀스 생성
create sequence jdbc_seq_stno
start with 9001
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

insert into jdbc_tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(jdbc_seq_stno.nextval, '이순신', '02-234-5678', '서울시 강남구 역삼동', default, 1);

insert into jdbc_tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(jdbc_seq_stno.nextval, '김유신', '031-345-8876', '경기도 군포시', default, 2);

insert into jdbc_tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(jdbc_seq_stno.nextval, '안중근', '02-567-1234', '서울시 강서구 화곡동', default, 2);

insert into jdbc_tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(jdbc_seq_stno.nextval, '엄정화', '032-777-7878', '인천시 송도구', default, 3);

insert into jdbc_tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(jdbc_seq_stno.nextval, '박순신', '02-888-9999', '서울시 마포구 서교동', default, 3);

commit;

select * 
from jdbc_tbl_student;


/*
  >>>> Stored Procedure 란? <<<<<
  Query 문을 하나의 파일형태로 만들거나 데이터베이스에 저장해 놓고 함수처럼 호출해서 사용하는 것임.
  Stored Procedure 를 사용하면 연속되는 query 문에 대해서 매우 빠른 성능을 보이며, 
  코드의 독립성과 함께 보안적인 장점도 가지게 된다.
*/

select S.name, S.tel, S.addr, to_char(S.registerdate, 'yyyy-mm-dd hh24:mi:ss'),
       C.classname, C.teachername
from jdbc_tbl_student S JOIN jdbc_tbl_class C 
ON S.fk_classno = C.classno
where S.stno = '9001';

select count(*) 
from jdbc_tbl_student
where stno = 3432;


select count(*) 
from jdbc_tbl_student
where stno = 9001;


create or replace procedure pcd_student_select_one
(p_stno          IN   jdbc_tbl_student.stno%type
,o_name          OUT  jdbc_tbl_student.name%type
,o_tel           OUT  jdbc_tbl_student.tel%type
,o_addr          OUT  jdbc_tbl_student.addr%type
,o_registerdate  OUT  varchar2
,o_classname     OUT  jdbc_tbl_class.classname%type
,o_teachername   OUT  jdbc_tbl_class.teachername%type
)
is
    v_cnt  number(1);
begin
    select count(*) INTO v_cnt
    from jdbc_tbl_student
    where stno = p_stno;
    
    if v_cnt = 0 then
       o_name := null;
       o_tel := null;
       o_addr := null;
       o_registerdate := null;
       o_classname := null;
       o_teachername := null;
    
    else    
       select S.name, S.tel, S.addr, to_char(S.registerdate, 'yyyy-mm-dd hh24:mi:ss'),
              C.classname, C.teachername
              INTO
              o_name, o_tel, o_addr, o_registerdate, o_classname, o_teachername
       from jdbc_tbl_student S JOIN jdbc_tbl_class C 
       ON S.fk_classno = C.classno
       where S.stno = p_stno;
    end if;

end pcd_student_select_one;
-- Procedure PCD_STUDENT_SELECT_ONE이(가) 컴파일되었습니다.

---------------------------------------------------------------------------

select *
from jdbc_tbl_student;


select S.name, S.tel, S.addr, to_char(S.registerdate, 'yyyy-mm-dd hh24:mi:ss'),
       C.classname, C.teachername
from jdbc_tbl_student S JOIN jdbc_tbl_class C 
ON S.fk_classno = C.classno
where S.addr like '%'||'서울'||'%';


select S.stno, S.name, S.tel, S.addr, S.registerdate
     , C.classname, C.teachername
from ( select stno, name, tel, addr 
            , to_char(registerdate, 'yyyy-mm-dd hh24:mi:ss') AS registerdate 
            , fk_classno
       from jdbc_tbl_student
       where addr like '%'||'서울'||'%') S JOIN jdbc_tbl_class C 
ON S.fk_classno = C.classno;


create or replace procedure pcd_student_select_many
(p_addr_search    IN   varchar2
,o_data           OUT  SYS_REFCURSOR
)
is
begin
     OPEN o_data FOR
     select S.stno, S.name, S.tel, S.addr, S.registerdate
          , C.classname, C.teachername
     from ( select stno, name, tel, addr 
                 , to_char(registerdate, 'yyyy-mm-dd hh24:mi:ss') AS registerdate 
                 , fk_classno
            from jdbc_tbl_student
            where addr like '%'|| p_addr_search ||'%') S JOIN jdbc_tbl_class C 
     ON S.fk_classno = C.classno;
end pcd_student_select_many;
-- Procedure PCD_STUDENT_SELECT_MANY이(가) 컴파일되었습니다.


----------------------------------------------------------------------

/*
   === jdbc_tbl_student 테이블에 insert 할 수 있는 요일명과 시간을 제한하겠습니다. ===
   
   월,화,수,목,금 만 가능하며, 또한 월,화,수,목,금 이더라도 
   시간이 오전 9시 부터 오후 1시 59분 59초 까지만 가능하도록 한다.
   insert 할 수 없는 요일명 이거나 시간대 이라면 "영업마감 이므로 insert 를 할 수 없습니다." 이라는 오류메시지를 띄우도록 한다.
*/   

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






