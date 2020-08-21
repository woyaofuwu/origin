SELECT distinct to_char(v.user_id) user_id,v.partition_id,v.vpn_name,v.vpn_no,to_char(v.user_id_a) user_id_a,v.group_area,
v.scp_code,v.vpmn_type,v.vpn_type,v.province,v.sub_state,v.func_tlags,v.feeindex,v.inter_feeindex,v.out_feeindex,
v.outgrp_feeindex,v.subgrp_feeindex,v.notdiscnt_feeindex,v.pre_ip_no,v.pre_ip_disc,v.othor_ip_disc,v.trans_no,
v.max_close_num,v.max_num_close,v.person_maxclose,v.max_outgrp_num,v.max_outgrp_max,v.max_inner_num,v.max_outnum,
v.max_users,v.max_linkman_num,v.max_telphonist_num,v.max_limit_users,
to_char(v.pkg_start_date,'yyyy-mm-dd hh24:mi:ss') pkg_start_date,v.pkg_type,v.discount,to_char(v.limit_fee) limit_fee,
v.zone_max,v.zonefree_num,to_char(v.zone_fee) zone_fee,v.mt_maxnum,v.aip_id,v.work_type_code,v.vpn_scare_code,v.vpn_time_code,
v.vpn_user_code,v.vpn_bundle_code,v.manager_no,v.call_net_type,v.call_area_type,v.over_fee_tag,v.limfee_type_code,
v.sinword_type_code,v.move_tag,v.trans_tag,v.lock_tag,v.cust_manager,v.link_man,to_char(v.month_fee_limit) month_fee_limit,
v.short_code_len,v.call_roam_type,v.bycall_roam_type,v.payitem_code,to_char(v.item_fee) item_fee,
to_char(v.usrgrp_id) usrgrp_id,to_char(v.open_date,'yyyy-mm-dd hh24:mi:ss') open_date,v.remove_tag,
to_char(v.remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,to_char(v.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
v.update_staff_id,v.update_depart_id,v.remark,v.rsrv_num1,v.rsrv_num2,v.rsrv_num3,to_char(v.rsrv_num4) rsrv_num4,
to_char(v.rsrv_num5) rsrv_num5,v.rsrv_str1,v.rsrv_str2,v.rsrv_str3,v.rsrv_str4,v.rsrv_str5,
to_char(v.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(v.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
to_char(v.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,v.rsrv_tag1,v.rsrv_tag2,v.rsrv_tag3
from 
   ucr_crm1.tf_b_trade_svc s,TF_F_USER_SVC u ,TF_F_USER_VPN v 
where   
           s.trade_id=:TRADE_ID  AND s.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
		 AND u.user_id=s.user_id   AND u.PARTITION_ID = MOD(s.USER_ID, 10000)   and u.service_id='860'
         AND v.user_id=u.user_id_a AND v.PARTITION_ID = MOD(u.user_id_a, 10000)
         AND v.Remove_Tag <> '1'
     AND sysdate BETWEEN u.start_date AND u.end_date