SELECT a.trade_id,a.partition_id,a.user_id,a.biz_code,a.pre_charge,a.max_item_pre_day,a.max_item_pre_mon,a.is_text_ecgn,a.default_ecgn_lang,a.text_ecgn_en,a.text_ecgn_zh,a.start_date,a.end_date,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,b.serial_number rsrv_str1,b.brand_code rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.oper_code
FROM tf_b_trade_grpmbmp_info a, tf_bh_trade b
WHERE a.trade_id = b.trade_id
AND b.trade_staff_id =:TRADE_STAFF_ID
AND b.accept_date between TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS') and TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')