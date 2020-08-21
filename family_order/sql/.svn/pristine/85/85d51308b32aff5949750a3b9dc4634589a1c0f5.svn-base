select  t.user_id,t.develop_depart_id,T2.DEPART_CODE||'|'||T2.DEPART_NAME DEPART_CODE,t.SERIAL_NUMBER,t1.res_code SIM_CARDNO,to_char(t.open_date,'YYYY-MM-DD hh24:mi:ss') OPEN_DATE
from tf_f_user t,tf_f_user_res t1 ,TD_M_DEPART t2 
where t.user_id=t1.user_id 
and t.develop_depart_id=t2.depart_id 
and t.open_mode='2' 
and t.acct_tag='2' 
and t.remove_tag='0'
and t1.res_type_code='1'  
and sysdate-1 between t1.start_date and t1.end_date 
and t2.depart_id=:DEPART_ID