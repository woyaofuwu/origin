--IS_CACHE=Y
SELECT purchase_attr,eparchy_code,purchase_mode,to_char(curr_gift_fee) curr_gift_fee,delay_month,to_char(delay_month_fee) delay_month_fee,delay_month_evy,to_char(delay_month_evyfee) delay_month_evyfee,to_char(gift2deposit) gift2deposit,to_char(total_gift_month) total_gift_month,para_code1,para_code2,para_code3,para_code4,remark 
  FROM td_b_spc_purchasetrade
 WHERE purchase_attr=:PURCHASE_ATTR
   AND eparchy_code=:EPARCHY_CODE