select '' SUBSYS_CODE ,0 PARAM_ATTR , '' PARAM_CODE , '' PARAM_NAME , a.vpn_no para_code1, 
a.vpn_name para_code2, 
a.rsrv_str5 para_code3,
a.cust_manager para_code4,
( select staff_name from td_m_staff where staff_id = a.cust_manager ) para_code5, '' para_code6, '' para_code7, '' para_code8, '' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME
from tf_f_user_vpn a
where a.cust_manager = :PARA_CODE1
and not exists(select 1 from tf_f_relation_uu b
      where b.relation_type_code = '20'
      and a.user_id = b.user_id_a
      and b.start_date <= sysdate 
      and b.end_date > sysdate )
and a.open_date <= sysdate 
and ( a.remove_date > sysdate or a.remove_date is null )
and (:PARA_CODE2 IS NOT NULL OR :PARA_CODE2 IS NULL)
and (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
and (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
and (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
and (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
and (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
and (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
and (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
and (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)