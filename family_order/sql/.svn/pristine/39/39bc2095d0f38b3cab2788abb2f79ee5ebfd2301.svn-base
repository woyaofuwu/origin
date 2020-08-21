SELECT serial_number para_code1,b.action_code para_code2,c.action_name para_code3,TO_CHAR(action_count) para_code4,TO_CHAR(accept_date,'YYYY-MM-DD HH24:MI:SS') para_code5,trade_staff_id para_code6,trade_depart_id para_code7,TO_CHAR(score_changed_sub) para_code8,
'' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_bh_trade a,tf_b_trade_scoresub b,td_b_score_action c
WHERE accept_date BETWEEN TO_DATE(:PARA_CODE1,'YYYYMMDD') AND TO_DATE(:PARA_CODE2,'YYYYMMDD')+1
AND a.trade_id=b.trade_id AND b.action_code=c.action_code(+)
AND a.cancel_tag='0'
AND a.trade_type_code=330
AND a.trade_eparchy_code=:PARA_CODE3
AND (c.eparchy_code=:PARA_CODE3 OR  c.eparchy_code='ZZZZ')
AND c.end_date>sysdate
AND a.trade_staff_id LIKE :PARA_CODE4
AND a.trade_depart_id LIKE :PARA_CODE5
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL
UNION ALL
SELECT serial_number para_code1,b.action_code para_code2,c.action_name para_code3,TO_CHAR(action_count) para_code4,TO_CHAR(accept_date,'YYYY-MM-DD HH24:MI:SS') para_code5,trade_staff_id para_code6,trade_depart_id para_code7,TO_CHAR(score_changed_sub) para_code8,
'' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_b_trade a,tf_b_trade_scoresub b,td_b_score_action c
WHERE accept_date BETWEEN TO_DATE(:PARA_CODE1,'YYYYMMDD') AND TO_DATE(:PARA_CODE2,'YYYYMMDD')+1
AND a.trade_id=b.trade_id AND b.action_code=c.action_code(+)
AND a.cancel_tag='0'
AND a.trade_type_code=330
AND a.trade_eparchy_code=:PARA_CODE3
AND (c.eparchy_code=:PARA_CODE3 OR  c.eparchy_code='ZZZZ')
and c.end_date>sysdate
AND a.trade_staff_id LIKE :PARA_CODE4
AND a.trade_depart_id LIKE :PARA_CODE5
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL