SELECT to_char(trade_id) trade_id,accept_month,to_char(USER_ID) USER_ID, discnt_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_b_trade_discnt a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag=:MODIFY_TAG
   AND NOT EXISTS (SELECT 1 FROM td_s_commpara b 
   WHERE b.param_attr=970 AND (to_char(a.discnt_code)=b.para_code2 OR to_char(a.discnt_code)=b.para_code4) 
   AND b.end_date>SYSDATE)
