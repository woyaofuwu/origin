SELECT out_group_id,phone_code,net_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_outnetgroup_phone
 WHERE out_group_id=:OUT_GROUP_ID
   AND SYSDATE BETWEEN start_date  AND end_date