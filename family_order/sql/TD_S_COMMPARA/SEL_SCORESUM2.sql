SELECT /*+ leading(a) use_nl(a,b) */ '['||b.action_code||']'||c.action_name para_code1,SUM(action_count) para_code2,SUM(NVL(value_changed_sub,0)) para_code3,
'' para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM (SELECT trade_id,trade_depart_id from tf_bh_trade
			WHERE  trade_city_code LIKE :PARA_CODE4
			 AND cancel_tag='0'
			 AND trade_eparchy_code=:PARA_CODE6
			 AND accept_date BETWEEN TO_DATE(:PARA_CODE1,'YYYYMMDD') AND TO_DATE(:PARA_CODE2,'YYYYMMDD')+1
			 AND trade_type_code=330
			UNION ALL
			SELECT trade_id,trade_depart_id from tf_b_trade
			WHERE  trade_city_code LIKE :PARA_CODE4
			 AND cancel_tag='0'
			 AND trade_eparchy_code=:PARA_CODE6
			 AND accept_date BETWEEN TO_DATE(:PARA_CODE1,'YYYYMMDD') AND TO_DATE(:PARA_CODE2,'YYYYMMDD')+1
			 AND trade_type_code=330
			 )a,tf_b_trade_scoresub b,td_b_score_action c,td_m_depart d,td_m_departkind e
WHERE  a.trade_id=b.trade_id AND b.action_code=c.action_code(+)
  AND e.eparchy_code= c.eparchy_code
	AND a.trade_depart_id=d.depart_id
	AND d.depart_kind_code=e.depart_kind_code(+)
	AND b.action_code like :PARA_CODE3
	AND e.depart_kind_code LIKE :PARA_CODE5
	AND e.eparchy_code=:PARA_CODE6
        AND c.end_date>sysdate
	AND :PARA_CODE7 IS NULL
  AND :PARA_CODE8 IS NULL
  AND :PARA_CODE9 IS NULL
  AND :PARA_CODE10 IS NULL
GROUP BY b.action_code,c.action_name