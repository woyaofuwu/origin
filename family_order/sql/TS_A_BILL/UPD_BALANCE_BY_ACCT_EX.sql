UPDATE ts_a_bill
   SET balance      = :BALANCE,
       late_balance = :LATE_BALANCE,
       late_fee = late_fee +:LATE_FEE,
       pay_tag      = :PAY_TAG
 WHERE acct_id = :ACCT_ID
   AND partition_id = :PARTITION_ID
   AND user_id = :USER_ID
   AND bill_id = :BILL_ID
   AND integrate_item_code = :INTEGRATE_ITEM_CODE
   AND balance = :OLD_BALANCE
   AND pay_tag = :OLD_PAYTAG