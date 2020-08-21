SELECT depart_id,depart_code,oper_type,reason_code,to_char(recv_date,'yyyy-mm-dd hh24:mi:ss') recv_date,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,remark,rsrv_tag1,rsrv_tag2,rsrv_str1,rsrv_str2 
  FROM tf_a_blackdepartlog
 WHERE depart_code=:DEPART_CODE
   AND oper_type=:OPER_TYPE