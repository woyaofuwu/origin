UPDATE tf_b_trade_batdeal a
   SET a.deal_state = '1',
       a.deal_time = NULL,
       a.deal_result = NULL,
       a.deal_desc = NULL
 WHERE a.batch_id = TO_NUMBER(:BATCH_ID)
   AND a.deal_state = '0'