--IS_CACHE=Y
SELECT a.staff_id,a.data_code,a.data_type,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,a.update_staff_id,a.update_depart_id 
  FROM td_m_tempdataright a
 WHERE a.staff_id=:STAFF_ID_IN
   AND a.data_code in (select b.data_code from tf_m_staffdataright b where  b.staff_id=:STAFF_ID and right_tag='1')
   AND a.start_date<=sysdate 
   AND a.end_date>sysdate