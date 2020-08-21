select a.user_id,vpn_no,c.cust_name vpn_name,max_users, group_area,scp_code,to_char(b.open_date,'yyyy-mm-dd hh24:mi:ss') open_date,
     to_char(c.cou) RSRV_STR1,
      b.serial_number RSRV_STR2,
      '' user_id_b,
      '' RSRV_STR11,
      '' RSRV_STR12,
      '' RSRV_STR13,
      '' RSRV_STR14,
      '' RSRV_STR15 ,
      '' RSRV_STR16,     
      '' RSRV_STR3,
      '' RSRV_STR4, 
      '' RSRV_STR5,
      '' RSRV_STR6,
      '' RSRV_STR7,
      '' RSRV_STR8,
      '' RSRV_STR9,
      '' RSRV_STR10,
      '' RSRV_STR11, 
      '' vpmn_type, '' province, '' sub_state, '' func_tlags, 0 feeindex, 0 inter_feeindex, 0 out_feeindex, 0 outgrp_feeindex, 0 subgrp_feeindex, 0 notdiscnt_feeindex,
      '' pre_ip_no,0 pre_ip_disc,0 othor_ip_disc, '' trans_no,0 max_close_num,0 max_num_close, 0 person_maxclose, 0 max_outgrp_num, 0 max_outgrp_max, 0 max_inner_num, 0 max_outnum,
      0 max_linkman_num ,0 max_telphonist_num, 0 max_limit_users, ''  pkg_start_date,
      0 pkg_type,0 discount,0  limit_fee,0 zone_max,0 zonefree_num, 0 zone_fee, 0 mt_maxnum,0 aip_id,'' work_type_code, '' vpn_scare_code, '' vpn_time_code, '' vpn_user_code, '' vpn_bundle_code, '' manager_no, '' call_net_type, '' call_area_type, '' over_fee_tag,
      '' limfee_type_code, '' sinword_type_code, '' move_tag,''trans_tag, '' lock_tag, '' cust_manager, '' link_man,0 month_fee_limit,0 short_code_len, '' call_roam_type, '' bycall_roam_type, 0 payitem_code,0  item_fee,
      ''  remove_date,'' rsrv_str17,''rsrv_str18,''rsrv_str19,''rsrv_str20  
From tf_f_user_vpn a,
   tf_f_user b,
   tf_f_customer c,
   (Select Count(*) cou From tf_f_relation_uu Where user_id_a=to_number(:USER_ID) And end_date>Sysdate And start_date<Sysdate) c
   Where b.user_id=a.user_id(+)
   And b.partition_id=Mod(b.user_id,10000)
   And b.remove_tag='0'
   And b.user_id=to_number(:USER_ID)
   And b.cust_id=c.cust_id
   And c.partition_id=mod(c.cust_id,10000)