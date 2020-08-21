SELECT '1' trade_id,'1' rsrv_value_code,'1' rsrv_value,a.rsrv_str1 rsrv_str1,to_char(COUNT(*)) rsrv_str2,'' rsrv_str3,'' rsrv_str4,'' rsrv_str5,a.rsrv_str6 rsrv_str6,'' rsrv_str7,'' rsrv_str8,'' rsrv_str9,'' rsrv_str10,'0' modify_tag,'' start_date,'' end_Date 
  FROM tf_b_trade_other a,tf_bh_trade b
 WHERE a.trade_id = b.trade_id
   AND a.modify_tag = 0
   AND b.trade_type_code=:TRADE_TYPE_CODE
   AND b.cancel_tag='0'
   AND b.trade_staff_id=:RSRV_STR1 
   AND b.finish_date BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND b.accept_month = to_number(substr(:START_DATE,6,2))
   and a.rsrv_value_code='TSUB'
   GROUP BY a.rsrv_str6,a.rsrv_str1