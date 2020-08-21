--IS_CACHE=N
select a.*  from 
     (select t.* 
         from TD_B_WIDENET_ACCOUNT  t 
         where t.state = '0' 
         AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE
         order by dbms_random.value) a 
     where rownum  <=1
     