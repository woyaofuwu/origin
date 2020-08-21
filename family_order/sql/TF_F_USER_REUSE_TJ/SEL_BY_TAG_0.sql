SELECT serial_number,to_char(user_id) user_id,tag,to_char(occupy_time,'yyyy-mm-dd hh24:mi:ss') occupy_time,occupy_staff_id,occupy_depart_id,rsrv_str1,rsrv_str2,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2 
  FROM tf_f_user_reuse_tj a
 WHERE (tag='0'
OR (tag='1' AND occupy_staff_id=:OCCUPY_STAFF_ID))
AND EXISTS (SELECT 1 FROM tf_f_user b 
           WHERE b.user_id=a.user_id
           AND b.partition_id=MOD(a.user_id,10000)
           AND b.user_state_codeset='7')
AND ROWNUM<501
ORDER BY serial_number