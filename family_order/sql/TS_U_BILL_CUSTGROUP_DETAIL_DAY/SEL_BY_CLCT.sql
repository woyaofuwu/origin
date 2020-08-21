SELECT clct_day,bcyc_id,to_char(group_id) group_id,to_char(a.bill_item_code) fee_type,to_char(fee) fee,to_char(data_up) back_fee,to_char(data_down) real_fee,BILL_ITEM,item_type
  FROM ts_u_bill_custgroup_detail_day a,TD_SD_BILLITEM b
 WHERE a.BILL_ITEM_CODE = b.BILL_ITEM_CODE
   and (b.ITEM_TYPE = :ITEM_TYPE or :ITEM_TYPE is null)
   and (clct_day=:CLCT_DAY or :CLCT_DAY is null)
   AND ( group_id=TO_NUMBER(:GROUP_ID) or :GROUP_ID is null)
   AND FEE >= :FEE