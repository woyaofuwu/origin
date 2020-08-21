SELECT to_char(trade_id) trade_id,
       accept_month,
       ATTR_CODE,
       ATTR_VALUE,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       rsrv_str7,
       rsrv_str8,
       rsrv_str9,
       rsrv_str10
  FROM tf_b_trade_ext
 WHERE ATTR_VALUE = TO_NUMBER(:ATTR_VALUE)
   AND attr_code = 'ESOP'
