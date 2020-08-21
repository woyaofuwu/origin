UPDATE tf_bh_trade
   SET remark = '行业网关处理失败'
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))