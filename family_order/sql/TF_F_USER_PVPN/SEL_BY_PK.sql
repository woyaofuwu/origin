SELECT to_char(user_id) user_id,to_char(user_id_a) user_id_a,link_man_tag,telphonist_tag,main_tag,admin_flag,serial_number,user_pin,short_code,innet_call_pwd,outnet_call_pwd,to_char(mon_fee_limit) mon_fee_limit,call_net_type,call_area_type,over_fee_tag,limfee_type_code,sinword_type_code,lock_tag,call_disp_mode,perfee_play_back,not_batchfee_tag,to_char(pkg_start_date,'yyyy-mm-dd hh24:mi:ss') pkg_start_date,pkg_type,sell_depart,over_right_tag,call_roam_type,bycall_roam_type,outnetgrp_use_type,outgrp,max_pout_num,func_tlags,to_char(open_date,'yyyy-mm-dd hh24:mi:ss') open_date,to_char(remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_str11,rsrv_str12,rsrv_str13,rsrv_str14,rsrv_str15,rsrv_str16,rsrv_str17,rsrv_str18,rsrv_str19,rsrv_str20 
  FROM tf_f_user_pvpn
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND user_id_a=TO_NUMBER(:USER_ID_A)
   AND open_date = (select max(open_date) from tf_f_user_pvpn
	         WHERE user_id=TO_NUMBER(:USER_ID)
             AND user_id_a=TO_NUMBER(:USER_ID_A))