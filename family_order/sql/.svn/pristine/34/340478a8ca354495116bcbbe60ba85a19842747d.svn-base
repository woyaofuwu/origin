select a.vpn_no para_code1, a.vpn_name para_code2, a.rsrv_str5 para_code3, a.cust_manager para_code4, ( select staff_name from td_m_staff where staff_id = a.cust_manager ) para_code5, count(*) para_code6, sum( decode ( ( select class_id from tf_f_cust_vip where user_id = b.user_id_b and vip_type_code = '0' and remove_tag = '0' and rownum = 1), '4', 1, 0 ) ) para_code7, sum( decode ( ( select class_id from tf_f_cust_vip where user_id = b.user_id_b and vip_type_code = '0' and remove_tag = '0' and rownum = 1), '3', 1, 0 ) ) para_code8, sum( decode ( ( select class_id from tf_f_cust_vip where user_id = b.user_id_b and vip_type_code = '0' and remove_tag = '0' and rownum = 1), '2', 1, 0 ) ) para_code9, sum( decode ( ( select class_id from tf_f_cust_vip where user_id = b.user_id_b and vip_type_code = '0' and remove_tag = '0' and rownum = 1), '1', 1, 0 ) ) para_code10, sum( decode ( ( select class_id from tf_f_cust_vip where user_id = b.user_id_b and vip_type_code = '2' and remove_tag = '0' and rownum = 1), '', 0, 1 ) ) para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name  
from tf_f_user_vpn a, tf_f_relation_uu b
where a.cust_manager = :PARA_CODE1
      AND b.relation_type_code =  '20'
      and a.vpn_no = b.serial_number_a
      and a.open_date <= sysdate and ( a.remove_date > sysdate or a.remove_date is null )
      and b.start_date <= sysdate and b.end_date > sysdate
      AND (:PARA_CODE2 IS NOT NULL OR :PARA_CODE2 IS NULL)
      AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
      AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
      AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
      AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
      AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
group by a.vpn_no, a.vpn_name, a.rsrv_str5, a.cust_manager