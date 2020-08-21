SELECT partition_id,to_char(user_id) user_id,to_char(inst_id) inst_id,serv_code,serial_number,group_id,biz_type_code,biz_code,
biz_attr,biz_name,biz_state_code,biz_status,biz_pri,usage_desc,intro_url,billing_type,price,
pre_charge,cs_tel,cs_url,access_mode,access_number,to_char(max_item_pre_day) max_item_pre_day,
to_char(max_item_pre_mon) max_item_pre_mon,is_text_ecgn,default_ecgn_lang,text_ecgn_en,
text_ecgn_zh,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
update_depart_id,update_staff_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
remark,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,
rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
rsrv_tag1,rsrv_tag2,rsrv_tag3 ,admin_num
  FROM tf_f_user_grp_platsvc
 WHERE partition_id=mod(:USER_ID,10000)
   AND sysdate BETWEEN start_date AND end_date
   AND user_id=TO_NUMBER(:USER_ID)