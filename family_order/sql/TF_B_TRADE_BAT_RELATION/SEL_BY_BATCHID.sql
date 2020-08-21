SELECT TO_CHAR(a.relation_batch_id) relation_batch_id,a.relation_type_code,a.relation_attr
  FROM tf_b_trade_bat_relation a
 WHERE a.batch_id = TO_NUMBER(:BATCH_ID)
   AND a.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) 
   AND SYSDATE BETWEEN a.start_date AND a.end_date
ORDER BY relation_batch_id