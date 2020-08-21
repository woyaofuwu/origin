--IS_CACHE=Y
SELECT staff_id,data_code,data_type,right_class,oper_special,to_char(accredit_time,'yyyy-mm-dd hh24:mi:ss') accredit_time,accredit_staff_id,use_tag,times,used_times,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM tf_m_stafftempdataright
 WHERE staff_id=:STAFF_ID
   AND data_code=:DATA_CODE
   AND (:DATA_TYPE IS NULL OR data_type=:DATA_TYPE)
   AND use_tag=:USE_TAG
   AND (:START_DATE IS NULL OR SYSDATE>=start_date)
   AND (:END_DATE IS NULL OR SYSDATE<=end_date)