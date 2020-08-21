UPDATE tf_a_acct_rec
   SET oper_date=sysdate,
       log_id=:LOG_ID,
       fee=:FEE
WHERE depart_id=:DEPART_ID
  AND acc_date=TO_DATE(:ACC_DATE, 'YYYYMMDD')