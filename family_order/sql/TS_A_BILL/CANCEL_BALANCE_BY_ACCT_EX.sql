UPDATE ts_a_bill
   SET balance      = balance + :BALANCE,
       late_balance = :LATE_BALANCE,
       late_fee     = :LATE_FEE,
       latecal_date = TO_DATE(:LATECAL_DATE,'YYYY-MM-DD HH24:MI:SS'),
       pay_tag      = :PAY_TAG
 WHERE acct_id = :ACCT_ID
   AND partition_id = :PARTITION_ID
   AND user_id = :USER_ID
   AND bill_id = :BILL_ID
   AND integrate_item_code = :INTEGRATE_ITEM_CODE