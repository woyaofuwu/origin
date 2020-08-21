SELECT to_char(user_id) user_id,serv_code,SERV_TYPE,CONFIRMFLAG,
serial_number,group_id,t.Inst_Id PRODUCT_ID,biz_type_code,biz_code,biz_attr RB_LIST,biz_name PRODUCT_NAME,
biz_in_code,biz_state_code,biz_status,biz_pri,oper_state,auth_code,usage_desc,intro_url,billing_type,
billing_mode,price,pre_charge,cs_tel,cs_url,access_mode ACCESSMODEL,access_number,si_base_in_code,si_base_in_code_a,
ec_base_in_code BASE_ACCESSNO,ec_base_in_code_a,to_char(max_item_pre_day) max_item_pre_day,
to_char(max_item_pre_mon) max_item_pre_mon,to_char(deliver_num) deliver_num,
forbid_start_time_a,forbid_end_time_a,forbid_start_time_b,forbid_end_time_b,forbid_start_time_c,
forbid_end_time_c,forbid_start_time_d,forbid_end_time_d,is_text_ecgn TEXT_SIGN_FLAG,
decode(default_ecgn_lang,'1','0','2','1') TEXT_SIGN_DEFAULT,
text_ecgn_en TEXT_SIGN_EN,text_ecgn_zh TEXT_SIGN_ZH,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date,
to_char(opr_eff_time,'yyyy-mm-dd hh24:mi:ss') opr_eff_time,opr_seq_id,plat_sync_state,
admin_num,mas_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_depart_id,update_staff_id 
  FROM tf_f_user_grp_platsvc T
 WHERE t.GROUP_ID = :GROUP_ID 
   and sysdate between t.start_date and t.end_date
