update TF_F_ACCOUNT_ACCTDAY
  set  end_date  = to_date('2050-12-31','YYYY-MM-DD hh24:mi:ss'),
       UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID = :UPDATE_DEPART_ID,
       UPDATE_TIME = SYSDATE
 WHERE acct_id=TO_NUMBER(:ACCT_ID) and partition_id=mod(TO_NUMBER(:ACCT_ID),10000)
   AND start_date=
   (select max(a.start_date) from TF_F_ACCOUNT_ACCTDAY a
    where a.acct_id = TO_NUMBER(:ACCT_ID)
      and a.partition_id=mod(TO_NUMBER(:ACCT_ID),10000))