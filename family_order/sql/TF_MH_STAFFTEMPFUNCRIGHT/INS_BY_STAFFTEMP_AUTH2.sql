INSERT INTO tf_mh_stafftempfuncright(staff_id,right_code,extend_value1,extend_value2,accredit_time,accredit_staff_id,use_tag,times,used_times,start_date,end_date,remark,rsvalue1,rsvalue2,update_time,update_staff_id,update_depart_id)
SELECT /*+INDEX(a PK_TF_M_STAFFTEMPFUNCRIGHT)*/ staff_id,right_code,extend_value1,extend_value2,accredit_time,accredit_staff_id,use_tag,times,used_times,start_date,SYSDATE,remark||:REMARK,rsvalue1,rsvalue2,SYSDATE,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID
FROM tf_m_stafftempfuncright a 
WHERE staff_id=:STAFF_ID
   AND (:RIGHT_CODE IS NULL OR right_code=:RIGHT_CODE)
   AND rsvalue2=:RSVALUE2