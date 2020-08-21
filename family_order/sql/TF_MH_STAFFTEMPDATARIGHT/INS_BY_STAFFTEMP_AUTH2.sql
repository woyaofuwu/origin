INSERT INTO tf_mh_stafftempdataright(staff_id,data_code,data_type,right_class,oper_special,accredit_time,accredit_staff_id,use_tag,times,used_times,start_date,end_date,remark,rsvalue1,rsvalue2,update_time,update_staff_id,update_depart_id)
 SELECT /*+INDEX(a PK_TF_M_STAFFTEMPDATARIGHT)*/ staff_id,data_code,data_type,right_class,oper_special,accredit_time,accredit_staff_id,use_tag,times,used_times,start_date,SYSDATE,remark||:REMARK,rsvalue1,rsvalue2,SYSDATE,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID
FROM tf_m_stafftempdataright a 
WHERE staff_id=:STAFF_ID
   AND (:DATA_CODE IS NULL OR data_code=:DATA_CODE)
   AND (:DATA_TYPE IS NULL OR data_type=:DATA_TYPE)
   AND rsvalue2=:RSVALUE2