--IS_CACHE=Y
SELECT staff_id,right_code,extend_value1,extend_value2,to_char(accredit_time,'yyyy-mm-dd hh24:mi:ss') accredit_time,accredit_staff_id,use_tag,times,used_times,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM tf_m_stafftempfuncright
 WHERE staff_id=:STAFF_ID
   AND right_code=:RIGHT_CODE
   AND use_tag=:USE_TAG
   AND (:START_DATE IS NULL OR  SYSDATE>=start_date)
   AND (:END_DATE IS NULL OR SYSDATE<=end_date)