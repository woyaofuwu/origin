SELECT to_char(max(t.trade_id)) trade_id  FROM
(
SELECT a.trade_id trade_id FROM tf_bh_trade a
WHERE a.trade_staff_id = :TRADE_STAFF_ID
AND a.trade_type_code <> 520
AND a.accept_date > SYSDATE - 1/24
UNION
SELECT a.trade_id trade_id FROM tf_b_trade a
WHERE a.trade_staff_id = :TRADE_STAFF_ID
AND a.trade_type_code <> 520
AND a.accept_date > SYSDATE - 1/24
UNION
SELECT a.charge_id trade_id FROM tf_a_paylog a
WHERE  a.recv_staff_id = :TRADE_STAFF_ID
AND a.charge_source_code = '1'
AND a.recv_time > SYSDATE - 1/24
) t