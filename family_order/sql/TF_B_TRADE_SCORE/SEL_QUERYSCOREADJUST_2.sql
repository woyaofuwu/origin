SELECT 
TO_CHAR(a.trade_id) trade_id,
a.serial_number,
b.trade_type_code,
decode(b.trade_type_code,'6350','积分调整','352','M值调整') trade_type,
b.brand_code,
TO_CHAR(NVL(score,0)) score,
TO_CHAR(NVL(score_changed,0)) score_changed,
a.remark,
TO_CHAR(b.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,
b.trade_staff_id,
b.trade_depart_id,
DECODE(b.cancel_tag,'0','未返销','1','被返销','2','返销') cancel_tag
FROM (SELECT * FROM tf_bh_trade
UNION ALL
SELECT * FROM tf_b_trade) b,tf_b_trade_score a
WHERE a.trade_id=b.trade_id  AND a.accept_month=b.accept_month AND a.user_id=b.user_id
    AND b.accept_date BETWEEN TO_DATE(:START_DATE,'YYYY-MM-DD') AND TO_DATE(:END_DATE,'YYYY-MM-DD')+1
    AND b.trade_staff_id >= :TRADE_STAFF_ID_S 
    AND b.trade_staff_id <= :TRADE_STAFF_ID_E
    AND b.trade_type_code in ('6350','352')
    ORDER BY accept_date