--IS_CACHE=Y
SELECT staff_id,data_code,data_type,right_class,oper_special,to_char(accredit_time,'yyyy-mm-dd hh24:mi:ss') accredit_time,accredit_staff_id,use_tag,times,used_times,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM tf_mh_stafftempdataright
 WHERE (:STAFF_ID is null or staff_id=:STAFF_ID)
   AND (:DATA_CODE is null or data_code=:DATA_CODE)
   AND accredit_time>=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND accredit_time<=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND (:ACCREDIT_STAFF_ID is null or accredit_staff_id=:ACCREDIT_STAFF_ID)