SELECT to_char(log_id) log_id,serial_number,back_type_code,eparchy_code,sim_card_no,moffice_id,code_type_code,to_char(destory_time,'yyyy-mm-dd hh24:mi:ss') destory_time,remove_tag,city_code_o,stock_id_o,to_char(user_id) user_id,to_char(back_time,'yyyy-mm-dd hh24:mi:ss') back_time,back_staff_id,back_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2, rsrv_str3 
  FROM tf_b_reuse_log a
 WHERE serial_number>=:RES_NO_S
   and serial_number<=:RES_NO_E
   and (:OPER_TAG is NULL or ((:OPER_TAG='1' and rsrv_tag1='1') or (rsrv_tag1='0' or rsrv_tag1 is null)) )
   AND (:END_DATE IS NULL OR TO_DATE(:END_DATE,'yyyy-mm-dd')+1>=back_time)
   AND (:BEGIN_DATE IS NULL OR TO_DATE(:BEGIN_DATE,'yyyy-mm-dd')<=back_time)
   AND (:BACK_STAFF_ID is null or back_staff_id=:BACK_STAFF_ID)
   AND (:PARA_VALUE4 IS NULL OR TO_DATE(:PARA_VALUE4,'yyyy-mm-dd')+1>=rsrv_date1)
   AND (:PARA_VALUE3 IS NULL OR TO_DATE(:PARA_VALUE3,'yyyy-mm-dd')<=rsrv_date1)
   AND NOT EXISTS (SELECT 1 FROM tf_r_mphonecode_idle b WHERE b.serial_number=a.serial_number and b.precode_tag='1')