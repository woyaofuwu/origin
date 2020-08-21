SELECT to_char(user_id_a) User_id,to_char(user_id_b) user_id_b,
      a.serial_number_b RSRV_STR11,
      decode(relation_type_code,'20','智能网VPMN集团','21','普通虚拟网集团','22','校园网(VPUN)','26','集团彩铃',relation_type_code)  RSRV_STR12,
      short_code RSRV_STR13,
      to_char(start_date,'yyyy-mm-dd hh24:mi:ss') RSRV_STR14,
      to_char(end_date,'yyyy-mm-dd hh24:mi:ss') RSRV_STR15 ,
      c.cust_name RSRV_STR16,
      c.RSRV_STR1,
      c.RSRV_STR2,
      c.RSRV_STR3,
      c.RSRV_STR4, 
      c.RSRV_STR5,
      c.RSRV_STR6,
      c.RSRV_STR7,
      c.RSRV_STR8,
      c.RSRV_STR9,
      c.RSRV_STR10,
      '' vpn_no,''vpn_name,      
      '' group_area, '' scp_code, '' vpmn_type, '' province, '' sub_state, '' func_tlags, 0 feeindex, 0 inter_feeindex, 0 out_feeindex, 0 outgrp_feeindex, 0 subgrp_feeindex, 0 notdiscnt_feeindex,
      '' pre_ip_no,0 pre_ip_disc,0 othor_ip_disc, '' trans_no,0 max_close_num,0 max_num_close, 0 person_maxclose, 0 max_outgrp_num, 0 max_outgrp_max, 0 max_inner_num, 0 max_outnum,0 max_users,
      0 max_linkman_num ,0 max_telphonist_num, 0 max_limit_users, ''  pkg_start_date,
      0 pkg_type,0 discount,0  limit_fee,0 zone_max,0 zonefree_num, 0 zone_fee, 0 mt_maxnum,0 aip_id,'' work_type_code, '' vpn_scare_code, '' vpn_time_code, '' vpn_user_code, '' vpn_bundle_code, '' manager_no, '' call_net_type, '' call_area_type, '' over_fee_tag,
      '' limfee_type_code, '' sinword_type_code, '' move_tag,''trans_tag, '' lock_tag, '' cust_manager, '' link_man,0 month_fee_limit,0 short_code_len, '' call_roam_type, '' bycall_roam_type, 0 payitem_code,0  item_fee, ''  open_date,
      ''  remove_date,'' rsrv_str17,''rsrv_str18,''rsrv_str19,''rsrv_str20       
  FROM tf_f_relation_uu a,tf_f_user b,tf_f_customer c
 Where 
    a.user_id_b=b.user_id
    And b.cust_id=c.cust_id 
    And a.partition_id=mod(a.user_id_b,10000)
    And c.partition_id=mod(c.cust_id,10000)
    And user_id_a=TO_NUMBER(:USER_ID_A)
    And  relation_type_code In ('20','21','22','26')
    And (b.remove_tag=:REMOVE_TAG or :REMOVE_TAG is null)  
   AND sysdate BETWEEN start_date AND end_date