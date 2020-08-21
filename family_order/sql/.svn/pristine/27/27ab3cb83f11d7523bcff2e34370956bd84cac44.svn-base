UPDATE ts_a_bill_rc SET late_fee = 0,late_balance = 0,latecal_date = null
WHERE acct_id = :ACCT_ID AND partition_id = :PARTITION_ID AND bill_id = :BILL_ID
AND (SELECT abs(SUM(fee) + SUM(adjust_after) - SUM(balance)) aa FROM ts_a_bill
WHERE acct_id = :SUBACCT_ID AND partition_id = :SUBPARTITION_ID AND bill_id = :SUBBILL_ID) = 0