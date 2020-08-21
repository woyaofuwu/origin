SELECT '0' trade_id,0 accept_month,to_char(user_id) user_id, to_char(user_id_a) user_id_a,discnt_code,'A' modify_tag, product_id, package_id, 
     TO_CHAR(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,TO_CHAR(end_date,'yyyy-mm-dd hh24:mi:ss') end_date, to_char(a.inst_id) inst_id
   FROM tf_f_user_discnt a
   WHERE user_id = TO_NUMBER(:USER_ID)
     AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
     AND end_date > SYSDATE
     AND NOT EXISTS (SELECT /*+ leading(c) */1 FROM tf_b_trade_discnt b, tf_b_trade c
                      WHERE c.user_id = TO_NUMBER(:USER_ID) 
                        AND c.cancel_tag='0'
                        AND c.accept_month = TO_NUMBER(:ACCEPT_MONTH)
                        AND b.trade_id = c.trade_id
                        AND b.accept_month = c.accept_month
                        AND b.user_id = TO_NUMBER(:USER_ID)
                        --AND b.product_id = a.product_id
                        --and b.package_id = a.package_id
                        AND b.discnt_code = a.discnt_code 
                        AND b.inst_id = a.inst_id)
  UNION
  SELECT /*+ leading(e) */TO_CHAR(d.trade_id) trade_id,d.accept_month,to_char(d.user_id) user_id, to_char(user_id_a) user_id_a, d.discnt_code,d.modify_tag, d.product_id, d.package_id, 
        TO_CHAR(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,TO_CHAR(end_date,'yyyy-mm-dd hh24:mi:ss') end_date, to_char(d.inst_id) inst_id
    FROM tf_b_trade_discnt d, tf_b_trade e
   WHERE e.user_id = TO_NUMBER(:USER_ID) 
     AND e.cancel_tag='0'
     AND e.accept_month = TO_NUMBER(:ACCEPT_MONTH)
     AND d.trade_id = e.trade_id
     AND d.accept_month = TO_NUMBER(:ACCEPT_MONTH)
     AND d.modify_tag <> '9'
     AND d.user_id = TO_NUMBER(:USER_ID)
     AND NOT EXISTS( SELECT 1 FROM tf_b_trade_discnt
                      WHERE trade_id = TO_NUMBER(:TRADE_ID)
                        AND accept_month = TO_NUMBER(:ACCEPT_MONTH) 
                        AND modify_tag='1'
                        --and package_id = d.package_id
                        --and product_id = d.product_id
                        AND user_id = TO_NUMBER(:USER_ID)
                        AND discnt_code = d.discnt_code 
                        AND inst_id = d.inst_id)
  UNION
  SELECT TO_CHAR(trade_id) trade_id,accept_month,TO_CHAR(user_id) user_id, to_char(user_id_a) user_id_a, discnt_code,modify_tag, product_id, package_id, 
     TO_CHAR(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,TO_CHAR(end_date,'yyyy-mm-dd hh24:mi:ss') end_date, to_char(t.inst_id) inst_id
  FROM tf_b_trade_discnt t
  WHERE trade_id = TO_NUMBER(:TRADE_ID)
    AND accept_month = TO_NUMBER(:ACCEPT_MONTH) 
    AND modify_tag='1'
    AND user_id = TO_NUMBER(:USER_ID)