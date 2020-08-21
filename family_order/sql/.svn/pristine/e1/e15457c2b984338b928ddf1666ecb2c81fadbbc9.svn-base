select '' SUBSYS_CODE, 0 PARAM_ATTR, '' PARAM_CODE, '' PARAM_NAME, a.serial_number PARA_CODE1, d.cust_name PARA_CODE2, d.POST_ADDRESS PARA_CODE3, d.phone PARA_CODE4, nvl( ( select to_char(decode( sign( leave_real_fee ), -1, round( abs( leave_real_fee ) / 100, 2 ), 0.00 ) ) from TF_O_LEAVEREALFEE where partition_id = mod( a.user_id, 10000) and user_id = a.user_id ), 0.00 ) PARA_CODE5, '' para_code6, '' PARA_CODE7, '' para_code8, '' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME
from tf_f_user a, tf_f_cust_person d
where a.user_id in 
    (
       select distinct b.user_id_b
       from tf_f_relation_uu b, tf_f_user_vpn c
       where b.user_id_a = c.user_id
             and c.vpn_no = :PARA_CODE1
             --and c.group_area = :PARA_CODE2 
             and ( c.cust_manager = :PARA_CODE3 or :PARA_CODE3 IS NULL )
     )
     and ( ( :PARA_CODE4 = '1' and a.remove_tag = :PARA_CODE4 and a.DESTROY_TIME between to_date( :PARA_CODE5, 'yyyy-mm-dd hh24:mi:ss') and to_date( :PARA_CODE6, 'yyyy-mm-dd hh24:mi:ss' ) ) 
           or ( :PARA_CODE4 = '3' and a.remove_tag = :PARA_CODE4  and a.PRE_DESTROY_TIME between to_date( :PARA_CODE5, 'yyyy-mm-dd hh24:mi:ss') and to_date( :PARA_CODE6, 'yyyy-mm-dd hh24:mi:ss' )  )
           or ( :PARA_CODE4 IS NULL and ( ( a.remove_tag = '1' and a.DESTROY_TIME between to_date( :PARA_CODE5, 'yyyy-mm-dd hh24:mi:ss') and to_date( :PARA_CODE6, 'yyyy-mm-dd hh24:mi:ss' ) ) or ( a.remove_tag = '3' and a.PRE_DESTROY_TIME between to_date( :PARA_CODE5, 'yyyy-mm-dd hh24:mi:ss') and to_date( :PARA_CODE6, 'yyyy-mm-dd hh24:mi:ss' ) ) ) ) )
     and d.partition_id = mod( a.cust_id, 10000 ) 
     and d.cust_id = a.cust_id
     and a.city_code = :PARA_CODE2
     and (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
     and (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
     and (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
     and (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)