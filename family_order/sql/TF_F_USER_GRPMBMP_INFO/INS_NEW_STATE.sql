INSERT INTO tf_f_user_grpmbmp_info(partition_id,user_id,biz_code,pre_charge,max_item_pre_day,max_item_pre_mon,
is_text_ecgn,default_ecgn_lang,text_ecgn_en,text_ecgn_zh,start_date,end_date,update_time,rsrv_num1,rsrv_num2,rsrv_num3,
rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3)
SELECT MOD(user_id,10000),user_id,biz_code,pre_charge,max_item_pre_day,max_item_pre_mon,is_text_ecgn,
default_ecgn_lang,text_ecgn_en,text_ecgn_zh,start_date,end_date,sysdate,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,
rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3 
  FROM tf_b_trade_grpmbmp_info
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND oper_code <> '70'