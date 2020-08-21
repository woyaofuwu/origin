--IS_CACHE=Y
SELECT olcomserv_code,olcomserv_name,server_order,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_icolcomserv
 WHERE olcomserv_code=:OLCOMSERV_CODE