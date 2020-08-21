INSERT INTO TS_S_PURCHASE_REWARD
      (clct_day,date_id,staff_id,depart_id,trade_type_code,purchase_mode,
        purchase_attr,sale_num,reward_fee,cancel_tag)
SELECT :clct_day,TO_NUMBER(SUBSTR(:clct_day,5,2)),a.trade_staff_id,a.trade_depart_id,a.trade_type_code,b.purchase_mode,
        b.purchase_attr,COUNT(*),SUM(a.rsrv_str9),'0'
  FROM tf_bh_trade a,tf_b_trade_purchase b
 WHERE a.cancel_tag IN ('0','1')
   AND a.trade_depart_id = :depart_id
   AND (a.trade_staff_id = :staff_id OR :staff_id ='%')
   AND a.accept_date >= TO_DATE(:clct_day,'YYYYMMDD')
   AND a.accept_date <  TO_DATE(:clct_day,'YYYYMMDD') + 1
   AND a.trade_type_code = 240
   AND a.trade_id = b.trade_id
   AND b.purchase_mode = 'H1' 
 GROUP BY a.trade_staff_id,a.trade_depart_id,a.trade_type_code,b.purchase_mode,b.purchase_attr