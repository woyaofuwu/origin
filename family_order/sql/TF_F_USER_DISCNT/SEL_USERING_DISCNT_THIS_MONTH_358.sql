SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code in (1285,1286,1391)
   AND end_date<SYSDATE
   AND end_date>to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   and user_id_a<>'-1'
   order by end_date desc