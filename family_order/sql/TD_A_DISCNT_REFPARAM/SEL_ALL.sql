--IS_CACHE=Y
SELECT id_type,product_id,product_name,to_char(trade_fee) trade_fee,to_char(exeminutes) exeminutes,to_char(high_fee) high_fee,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,to_char(rsrv_fee3) rsrv_fee3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,remark 
  FROM td_a_discnt_refparam
 WHERE product_id=:PRODUCT_ID