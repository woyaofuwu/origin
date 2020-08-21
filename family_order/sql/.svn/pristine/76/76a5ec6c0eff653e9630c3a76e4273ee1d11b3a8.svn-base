INSERT INTO tf_f_user_svcallowance(partition_id,user_id,service_type,deal_flag,start_date,end_date,update_date)   
SELECT MOD(TO_NUMBER(:USER_ID), 10000),:USER_ID,a.para_code2 ,1,SYSDATE,
           TO_DATE('2050-12-31 23:59:59','YYYY-MM-DD HH24:MI:SS'),SYSDATE 
  FROM td_s_commpara a
 WHERE (a.param_attr='1') AND (a.para_code1='80')