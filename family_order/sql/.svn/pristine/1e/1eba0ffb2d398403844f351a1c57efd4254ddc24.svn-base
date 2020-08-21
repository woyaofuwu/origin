UPDATE tf_b_discnt_end_notice
   SET discnt_explain = :DISCNT_EXPLAIN,
       deal_staff_id = :TRADE_STAFF_ID,
       deal_depart_id = :TRADE_DEPART_ID,
       deal_time = SYSDATE,
       deal_state = '1',
       sms_notice_id = F_SYS_GETSEQID(eparchy_code,'seq_smssend_id')  
 WHERE bcyc_id = :BCYC_ID
   AND eparchy_code = :TRADE_EPARCHY_CODE
   AND discnt_code = :DISCNT_CODE
   AND TO_CHAR(end_date,'yyyy-mm') = :END_DATE
   AND deal_state = '0'