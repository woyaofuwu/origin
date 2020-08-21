SELECT to_char(user_id) user_id,partition_id,year_id,acyc_id,id_type,score_type_code,to_char(score) score,rsrv_str1,rsrv_str2,to_char(rsrv_num1) rsrv_num1,to_char(rsrv_num2) rsrv_num2,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2 
  FROM tf_f_user_newscore
 WHERE user_id=to_number(:USER_ID)
   AND partition_id=MOD(to_number(:USER_ID), 10000)
   AND id_type=:ID_TYPE
   AND score_type_code=:SCORE_TYPE_CODE
   AND year_id IN (SELECT para_code1 From td_s_commpara WHERE param_attr=3300 AND end_date>SYSDATE)