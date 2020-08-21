SELECT a.group_id,a.class_id,a.client_status,a.calling_type_code,a.calling_sub_type_code,to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date 
  FROM (select a.*,rownum tmpnum from tf_b_trade_group a
 WHERE (:GROUP_ID is null  or a.group_id =:GROUP_ID)
  AND (:STARTDATE  is null  or a.accept_date >=to_date(:STARTDATE,'yyyy-mm-dd'))
  AND (:ENDDATE is null or a.accept_date<=to_date(:ENDDATE,'yyyy-mm-dd'))
  AND (:CLIENT_STATUS is null or a.client_status =:CLIENT_STATUS)
  AND (:CALLING_TYPE_CODE is null or a.calling_type_code =:CALLING_TYPE_CODE)
  AND (:CALLING_SUB_TYPE_CODE is null or a.calling_sub_type_code =:CALLING_SUB_TYPE_CODE)
  AND (:MANAGER_STAFF_ID is NULL  or a.manager_staff_id=:MANAGER_STAFF_ID)
  AND (a.trade_type_code='5005')
  AND EXISTS
      ( select 1
         from td_m_staff e,td_m_depart f
         where a.manager_staff_id = e.staff_id
         and e.depart_id = f.depart_id
         and f.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :DEPART_ID)||'%'
         and (f.parent_depart_id = :PARENT_DEPART_ID OR :PARENT_DEPART_ID IS NULL ) )
) a
WHERE tmpnum >= :X_REMARK1 
AND (:X_REMARK2 is null OR tmpnum<=:X_REMARK2)