select t.*,TO_CHAR(t.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') APPLY_TIME
  from tf_b_trade t
 where t.trade_type_code = '613'
   and t.cancel_tag = '0'
   and t.cust_id = :CUSTID_GROUP
   and t.serial_number = :KD_NUMBER