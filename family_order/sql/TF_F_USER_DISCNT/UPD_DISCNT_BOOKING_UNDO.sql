UPDATE tf_f_user_discnt a 
SET a.product_id = a.rsrv_str3,a.package_id = a.rsrv_str4
where a.product_id = :PRODUCT_ID
  and a.user_id = :USER_ID
  and a.rsrv_str3 is not null
  and a.rsrv_str4 is not null  
  and  not Exists (SELECT 1 FROM tf_b_trade_discnt_bak b
                 WHERE b.trade_id=to_number(:TRADE_ID)
                 AND b.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                 And a.inst_id = b.inst_id)