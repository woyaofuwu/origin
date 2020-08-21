--按市县兑奖情况查询积分兑奖汇总小计
SELECT action_code para_code1,action_name para_code2,sum(action_count) para_code3,
        sum(value_changed_sub) para_code4,'' para_code5,'' para_code6, 
        '' para_code7, '' para_code8, '' para_code9, '' para_code10,
        '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
        '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
        '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
        '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
        '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
        '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM 
     (
        SELECT /*+ index(a,PK_TF_B_TRADE_SCORESUB)*/ distinct a.trade_id,a.action_code,c.action_name,a.value_changed_sub,a.action_count
          FROM tf_b_trade_scoresub a,tf_bh_trade_staff b,td_b_score_action c,td_m_depart d
         WHERE a.trade_id = b.trade_id
           AND a.accept_month = b.accept_month
           AND a.action_code = c.action_code
           AND b.trade_depart_id = d.depart_id
           AND b.accept_date >= to_date(:PARA_CODE1,'yyyy-mm-dd hh24:mi:ss')
           AND b.accept_date <= to_date(:PARA_CODE2,'yyyy-mm-dd hh24:mi:ss')
           AND ((TRIM(:PARA_CODE3) IS NULL) OR a.action_code = TRIM(:PARA_CODE3))
           AND ((TRIM(:PARA_CODE4) IS NULL) OR b.trade_city_code = TRIM(:PARA_CODE4))
           AND ((TRIM(:PARA_CODE5) IS NULL) OR d.depart_kind_code = TRIM(:PARA_CODE5))
           AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
           AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
           AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
           AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
           AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
        )
       group by action_code,action_name