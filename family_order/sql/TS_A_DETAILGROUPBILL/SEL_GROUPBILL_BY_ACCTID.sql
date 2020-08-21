select acct_id ,product_id,product_name,note_item_code,note_item ,
		sum(nvl(fee, 0)) FEE  ,
		sum(nvl(BALANCE, 0)) BALANCE  ,
		sum(nvl(PRINT_FEE, 0)) PRINT_FEE ,
		sum(nvl(B_DISCNT, 0)) B_DISCNT,
		sum(nvl(A_DISCNT, 0)) A_DISCNT,
		sum(nvl(ADJUST_BEFORE, 0)) ADJUST_BEFORE ,
		sum(nvl(ADJUST_AFTER, 0)) ADJUST_AFTER,
		sum(nvl(LATE_FEE, 0)) LATE_FEE,
		sum(nvl(LATE_BALANCE, 0)) LATE_BALANCE
  FROM ts_a_detailgroupbill
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND acyc_id=:ACYC_ID 
GROUP BY acct_id ,product_id,product_name,note_item_code,note_item 
order by product_id desc