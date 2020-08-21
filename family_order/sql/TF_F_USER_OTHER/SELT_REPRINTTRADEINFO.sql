SELECT 0 x_choice_tag,TO_CHAR(trade_id) trade_id,a.trade_type_code,trade_type,serial_number,cust_name,TO_CHAR(accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,trade_staff_id,accept_month
FROM (SELECT trade_id,serial_number,trade_type_code,cust_name,accept_date,trade_staff_id,accept_month
FROM tf_bh_trade
WHERE  accept_date >trunc(SYSDATE-7)
AND serial_number=:SERIAL_NUMBER
AND cancel_tag='0'  AND trade_eparchy_code=:TRADE_EPARCHY_CODE
UNION ALL
SELECT trade_id,serial_number,trade_type_code,cust_name,accept_date,trade_staff_id,accept_month
FROM tf_b_trade
WHERE  accept_date >TRUNC(SYSDATE-7)
AND serial_number=:SERIAL_NUMBER
AND cancel_tag='0'  AND trade_eparchy_code=:TRADE_EPARCHY_CODE
) a,td_s_tradetype b
WHERE  EXISTS (SELECT 1 FROM tf_b_tradefee_sub WHERE trade_id=a.trade_id   AND fee_mode IN ('0','1','2')
 and accept_month=a.accept_month)
AND a.trade_type_code=b.trade_type_code AND (b.eparchy_code=:TRADE_EPARCHY_CODE OR b.eparchy_code='ZZZZ')