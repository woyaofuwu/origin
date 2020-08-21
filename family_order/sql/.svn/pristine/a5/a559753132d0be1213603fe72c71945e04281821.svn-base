SELECT partition_id,to_char(user_id) user_id,vpn_name,vpn_no,to_char(user_id_a) user_id_a,group_area,
scp_code,vpmn_type,vpn_type,province,sub_state,func_tlags,feeindex,inter_feeindex,out_feeindex,
outgrp_feeindex,subgrp_feeindex,notdiscnt_feeindex,pre_ip_no,pre_ip_disc,othor_ip_disc,trans_no,
max_close_num,max_num_close,person_maxclose,max_outgrp_num,max_outgrp_max,max_inner_num,max_outnum,
max_users,max_linkman_num,max_telphonist_num,max_limit_users,
to_char(pkg_start_date,'yyyy-mm-dd hh24:mi:ss') pkg_start_date,pkg_type,discount,to_char(limit_fee) limit_fee,
zone_max,zonefree_num,to_char(zone_fee) zone_fee,mt_maxnum,aip_id,work_type_code,vpn_scare_code,vpn_time_code,
vpn_user_code,vpn_bundle_code,manager_no,call_net_type,call_area_type,over_fee_tag,limfee_type_code,
sinword_type_code,move_tag,trans_tag,lock_tag,cust_manager,link_man,to_char(month_fee_limit) month_fee_limit,
short_code_len,call_roam_type,bycall_roam_type,payitem_code,to_char(item_fee) item_fee,
to_char(usrgrp_id) usrgrp_id,to_char(open_date,'yyyy-mm-dd hh24:mi:ss') open_date,remove_tag,
to_char(remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,
to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,
to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3,:TRADE_ID TRADE_ID  
  FROM tf_f_user_vpn 
WHERE  USER_ID=to_number(:USER_ID)
       and PARTITION_ID=mod(to_number(:USER_ID),10000)
       and Remove_Tag <> '1'