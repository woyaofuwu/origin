INSERT INTO TF_B_TRADE_VPN_MEB(TRADE_ID, ACCEPT_MONTH, USER_ID, USER_ID_A, LINK_MAN_TAG,
TELPHONIST_TAG, MAIN_TAG, ADMIN_FLAG, SERIAL_NUMBER, USER_PIN, 
SHORT_CODE, INNET_CALL_PWD, OUTNET_CALL_PWD, MON_FEE_LIMIT, CALL_NET_TYPE, 
CALL_AREA_TYPE, OVER_FEE_TAG, LIMFEE_TYPE_CODE, SINWORD_TYPE_CODE, LOCK_TAG, 
CALL_DISP_MODE, PERFEE_PLAY_BACK, NOT_BATCHFEE_TAG, PKG_START_DATE, PKG_TYPE, 
SELL_DEPART, OVER_RIGHT_TAG, CALL_ROAM_TYPE, BYCALL_ROAM_TYPE, OUTNETGRP_USE_TYPE, 
OUTGRP, MAX_POUT_NUM, FUNC_TLAGS, MEMBER_KIND, IS_TX,
VPN_TYPE, EXT_FUNC_TLAGS, OPEN_DATE, REMOVE_TAG, REMOVE_DATE, 
MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, 
RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, 
RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3)
select to_number(:TRADE_ID), :ACCEPT_MONTH, v.user_id, v.user_id_a, v.link_man_tag,
v.telphonist_tag, v.main_tag, v.admin_flag, v.serial_number, v.user_pin,
v.short_code, v.innet_call_pwd, v.outnet_call_pwd, v.mon_fee_limit, v.call_net_type,
v.call_area_type, v.over_fee_tag, v.limfee_type_code, v.sinword_type_code, v.lock_tag,
v.call_disp_mode, v.perfee_play_back, v.not_batchfee_tag, v.pkg_start_date, v.pkg_type,
v.sell_depart, v.over_right_tag, v.call_roam_type, v.bycall_roam_type, v.outnetgrp_use_type,
v.outgrp, v.max_pout_num, v.func_tlags, v.member_kind, v.is_tx,
v.vpn_type, v.ext_func_tlags,to_date(:OPEN_DATE,'yyyy-mm-dd hh24:mi:ss'), '0', v.remove_date,
'0', to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'), :UPDATE_STAFF_ID, :UPDATE_DEPART_ID, v.remark,
'0', v.rsrv_num2, v.rsrv_num3, v.rsrv_num4, v.rsrv_num5, v.rsrv_str1, v.rsrv_str2, v.rsrv_str3, v.rsrv_str4, v.rsrv_str5,
v.rsrv_date1, v.rsrv_date2, v.rsrv_date3, v.rsrv_tag1,  v.rsrv_tag2, '0'
from TF_F_USER_VPN_MEB v
where v.partition_id = MOD(to_number(:USER_ID), 10000)
and v.user_id = to_number(:USER_ID)
and v.rsrv_num1 = '0'
and open_date in (select max(open_date) open_date from TF_F_USER_VPN_MEB vt
where vt.partition_id = mod(to_number(:USER_ID), 10000)
and vt.user_id = to_number(:USER_ID)
and vt.remove_tag = '1'
)
and exists (select 1
from tf_f_cust_group g, tf_f_user u
where u.user_id = v.user_id_a
 and u.partition_id = mod(to_number(v.user_id_a), 10000)
 and g.cust_id = u.cust_id
 and g.remove_tag = '0')