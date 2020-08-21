SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,inst_id,RSRV_STR3,rsrv_str5
  FROM TF_F_RELATION_UU t
 WHERE 1=1 
 	 AND user_id_a = TO_NUMBER(:USER_ID)
   AND relation_type_code = :RELATION_TYPE_CODE
   AND rsrv_str5 = :RSRV_STR5
   and (sysdate between t.START_DATE and t.END_DATE) 
   ORDER BY ORDERNO