UPDATE tf_b_trade_batdeal t
   SET t.exec_time = SYSDATE,
       t.deal_time = SYSDATE,
       t.deal_state = DECODE(deal_state,'9','1','9'),
        t.deal_result = DECODE(deal_state,'9','','？？？？？？？？'),
        t.deal_desc = DECODE(deal_state,'9','','？？？？？？？？'),
        t.cancel_tag = '1',
        t.cancel_date = SYSDATE,
       t.cancel_staff_id = :TRADE_STAFF_ID,
       t.cancel_depart_id = :TRADE_DEPART_ID,
       t.cancel_city_code = :TRADE_CITY_CODE,
       t.cancel_eparchy_code = :TRADE_EPARCHY_CODE
 WHERE t.batch_id = TO_NUMBER(:BATCH_ID)
   AND t.deal_state IN ('0','1','3','6','9','A')
   AND t.cancel_tag = '0'
   and t.serial_number in (select a.serial_number
                           FROM tf_b_trade_batdeal a,uop_crm1.tf_f_user b
                           WHERE a.batch_id = TO_NUMBER(:BATCH_ID)
                           and a.serial_number=b.serial_number
                           and b.open_mode='1'
                           and b.acct_tag='2'
                           )