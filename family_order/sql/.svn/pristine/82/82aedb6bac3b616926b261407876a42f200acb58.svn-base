select to_char(m.log_time, 'dd') dd, to_char(m.log_time, 'hh24') hh24, to_char(m.log_time, 'mi') mi, sum(m.req_num) total 
 from tl_b_uipmonitor m where 1=1 
 and to_char(m.log_time, 'yyyy-mm-dd hh24:mi') >= :START_DATE
 and to_char(m.log_time, 'yyyy-mm-dd hh24:mi') <= :END_DATE 
 group by m.log_time
 order by m.log_time