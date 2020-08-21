SELECT a.cust_name,a.group_id,a.client_status,a.user_count,a.company_address,a.fax_nbr,a.email,f_sys_getcodename('calling_type',a.calling_type_code,'','') calling_type_code,a.trade_class1,a.main_trade,a.manager_staff_id 
  FROM (select a.*,rownum tmpnum from tf_fh_cust_group a
 WHERE a.remove_tag='0' 
   and backmonth = '200509'
   AND (:GROUP_ID is null  or a.group_id =:GROUP_ID)
  AND (:STARTDATE  is null  or a.prevalued2 >=to_date(:STARTDATE,'yyyy-mm-dd'))
  AND (:ENDDATE is null or a.prevalued2 <=to_date(:ENDDATE,'yyyy-mm-dd'))
  AND (:CLIENT_STATUS is null or a.client_status =:CLIENT_STATUS)
  AND (:CALLING_TYPE_CODE is null or a.calling_type_code =:CALLING_TYPE_CODE)
  AND (:CALLING_SUB_TYPE_CODE is null or a.calling_sub_type_code =:CALLING_SUB_TYPE_CODE)
  AND (:MANAGER_STAFF_ID is NULL  or a.manager_staff_id=:MANAGER_STAFF_ID)
  AND
    ( EXISTS
      ( select 1
         from td_m_staff e,td_m_depart f
         where a.manager_staff_id = e.staff_id
         and e.depart_id = f.depart_id
         and f.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :DEPART_ID)||'%'
         and (f.parent_depart_id = :PARENT_DEPART_ID OR :PARENT_DEPART_ID IS NULL ) )
     or :DEPART_ID is null)
) a
WHERE tmpnum >= :X_REMARK1 
AND (:X_REMARK2 is null OR tmpnum<=:X_REMARK2)