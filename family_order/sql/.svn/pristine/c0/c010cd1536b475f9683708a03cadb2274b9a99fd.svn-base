UPDATE ts_a_bill
   SET balance      = balance + :BALANCE,
       late_balance = decode(sign(late_balance - NVL(:LATE_BALANCE,0)), -1,0,late_balance - NVL(:LATE_BALANCE,0)),
       late_fee     = decode(sign(late_fee - NVL(:LATE_FEE,0)),-1,0,late_fee - NVL(:LATE_FEE,0)),
       latecal_date = to_date(:LATECAL_DATE,'YYYY-MM-DD HH24:MI:SS'),
       pay_tag      = :PAY_TAG
 WHERE acct_id = :ACCT_ID
   AND partition_id = :PARTITION_ID
   AND user_id = :USER_ID
   AND bill_id = :BILL_ID
   AND integrate_item_code = :INTEGRATE_ITEM_CODE