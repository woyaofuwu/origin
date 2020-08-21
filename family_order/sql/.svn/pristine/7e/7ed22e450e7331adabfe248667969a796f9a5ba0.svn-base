UPDATE tf_b_trade_batdeal a
   SET a.exec_time = SYSDATE,
       a.deal_time = SYSDATE,
       a.deal_state = DECODE(deal_state,'9','1','9'),
   	   a.deal_result = DECODE(deal_state,'9','','已取消执行'),
   	   a.deal_desc = DECODE(deal_state,'9','','已取消执行'),
   	   a.cancel_tag = '1',
   	   a.cancel_date = SYSDATE,
       a.cancel_staff_id = :TRADE_STAFF_ID,
       a.cancel_depart_id = :TRADE_DEPART_ID,
       a.cancel_city_code = :TRADE_CITY_CODE,
       a.cancel_eparchy_code = :TRADE_EPARCHY_CODE
 WHERE a.operate_id = TO_NUMBER(:OPERATE_ID)
   AND a.deal_state||NULL IN ('0','1','3','6','9','A')
   AND a.cancel_tag = '0'