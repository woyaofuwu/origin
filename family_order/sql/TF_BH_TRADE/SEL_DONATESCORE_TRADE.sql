SELECT serial_number,
       accept_date,
       rsrv_str2 double_score,
       trade_id,
       accept_month,
       order_id,
       trade_type_code,
       cust_id,
       cust_name,
       user_id,
       acct_id,
       accept_date,
       cancel_tag,
       cancel_date
from tf_bh_trade
where (serial_number=:SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND accept_date >= to_date(:START_DATE,'yyyy-mm-dd')
   AND accept_date <= to_date(:END_DATE,'yyyy-mm-dd')
   AND eparchy_code = :EPARCHY_CODE
   AND trade_type_code = 5040 
   AND rsrv_str1 = '1'