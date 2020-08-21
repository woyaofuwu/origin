SELECT a.trade_id,a.serial_number,a.user_id,to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,b.rule_id,b.res_id,b.goods_name,b.action_count,to_char(c.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(c.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,b.remark  FROM tf_bh_trade a, tf_b_trade_score b, td_b_exchange_rule c
WHERE a.user_id=to_number(:USER_ID)
AND a.trade_id = b.trade_id
AND a.accept_month=substr(a.trade_id,5,2)
AND b.accept_month=substr(b.trade_id,5,2)
AND a.user_id=b.user_id
AND b.rule_id=c.rule_id
--AND b.res_id=c.gift_type_code
AND a.trade_type_code=330
AND a.cancel_tag='0'
AND c.exchange_type_code='9'    --全球通积分助学兑换     
AND c.end_date > c.start_date
AND c.end_date > SYSDATE    
AND NOT EXISTS(
    SELECT 1 FROM tf_f_user_other d 
    WHERE d.user_id=b.user_id 
    AND d.rsrv_value=to_char(b.trade_id)         --积分兑换流水                                                           
    AND d.rsrv_str1=b.rule_id                --rule_id，兑换规则编码
    --AND d.rsrv_str2=b.res_id               --gift_type_code,兑换礼品类型编码
    AND d.rsrv_value_code='SI'
)