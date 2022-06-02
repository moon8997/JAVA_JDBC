set hidden param parseThreshold = 150000;

show user;
-- USER이(가) "HR"입니다.


create table jdbc_tbl_memo
(no        number(4)
,name      varchar2(20) not null
,msg       varchar2(200) not null
,writeday  date default sysdate
,constraint PK_jdbc_tbl_memo_no primary key(no)
);
-- Table JDBC_TBL_MEMO이(가) 생성되었습니다.

create sequence jdbc_seq_memo
start with 1
increment by 1 
nomaxvalue
nominvalue
nocycle
nocache;
-- Table JDBC_TBL_MEMO이(가) 생성되었습니다.

/*
insert into jdbc_tbl_memo(no, name msg)
values(jdbc_seq_memo.nextval, 홍길동, 안녕하세요?);
*/

select *
from jdbc_tbl_memo
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from jdbc_tbl_memo
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday from jdbc_tbl_memo order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday 
from jdbc_tbl_memo 
where name = '문길'
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday 
from jdbc_tbl_memo 
where msg like '%'||'점심'||'%'
order by no desc;

create table JDBC_TBL_EXAMTEST
(no number);