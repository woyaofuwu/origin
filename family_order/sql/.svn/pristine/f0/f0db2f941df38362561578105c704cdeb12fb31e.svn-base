SELECT COUNT(1) recordcount
FROM Tf_b_Trade_Batdeal
WHERE accept_date>trunc(sysdate)
  AND process_tag='0'
  AND batch_oper_type='SERVICECHG'
  AND RSRV_STR1='20'