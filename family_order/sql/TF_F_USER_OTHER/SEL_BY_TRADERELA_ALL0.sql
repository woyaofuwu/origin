SELECT 0 partition_id,b.id_b user_id,'sn_b' rsrv_str5,b.short_code rsrv_str6,a.rsrv_value_code rsrv_value_code,a.rsrv_value rsrv_value,a.rsrv_str1 rsrv_str1,nvl(a.rsrv_str2,'0') rsrv_str2,nvl(a.rsrv_str3,'0') rsrv_str3,a.rsrv_str4 rsrv_str4,a.rsrv_str7 rsrv_str7,a.rsrv_str8 rsrv_str8,a.rsrv_str9 rsrv_str9,a.rsrv_str10 rsrv_str10,to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade_other a,tf_b_trade_relation b
 WHERE EXISTS(SELECT 1 FROM tf_b_trade WHERE user_id=b.id_b and trade_id=b.trade_id 
              AND accept_date<SYSDATE AND cancel_tag='0' AND trade_type_code=1025)
   AND a.rsrv_value_code(+)=:RSRV_VALUE_CODE
   AND a.trade_id(+)=b.trade_id
   AND b.relation_type_code='20'
   AND b.id_a=TO_NUMBER(:USER_ID)
   AND b.relation_attr = 0
   AND SYSDATE < a.end_date(+)
   AND SYSDATE < b.end_date