UPDATE tf_b_discnt_end_notice a
   SET (a.refer_time,a.deal_state) = (SELECT b.refer_time,'2' FROM ti_o_sms b WHERE b.sms_notice_id = a.sms_notice_id)  
 WHERE a.bcyc_id = :BCYC_ID
   AND a.eparchy_code = :TRADE_EPARCHY_CODE
   AND a.discnt_code = :DISCNT_CODE
   AND TO_CHAR(a.end_date,'yyyy-mm') = :END_DATE
   AND a.deal_state = '1'
   AND EXISTS (SELECT 1 FROM ti_o_sms b WHERE b.sms_notice_id = a.sms_notice_id)