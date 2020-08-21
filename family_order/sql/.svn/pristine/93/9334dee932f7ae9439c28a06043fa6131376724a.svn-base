select a.user_id,a.user_id_a,a.inst_id,a.discnt_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  from tf_f_user_discnt a 
 where a.user_id = :USER_ID
   and a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   and ((:ELEMENT_ID is null) or (a.discnt_code = :ELEMENT_ID))
   and sysdate between a.start_date and a.end_date
