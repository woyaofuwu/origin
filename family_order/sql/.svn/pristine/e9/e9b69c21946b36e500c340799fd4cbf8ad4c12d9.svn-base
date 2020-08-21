SELECT count(1) recordcount
  FROM tf_b_trade_discnt a
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND a.modify_tag = '1'
   AND EXISTS (SELECT 1
   		FROM td_s_commpara b
   		WHERE b.subsys_code = 'CSM'
   		AND b.param_attr = 355
   		AND TO_NUMBER(TRIM(b.param_code)) = a.discnt_code
   		AND a.end_date-a.start_date < TO_NUMBER(b.para_code1)
   		AND SYSDATE < b.end_date
   		AND b.eparchy_code = :EPARCHY_CODE)