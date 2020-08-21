SELECT a.trade_id,
       TO_CHAR(a.user_id) user_id,
       b.score_type_name,
       TO_CHAR(a.score_changed_sub) score_changed_sub,
       TO_CHAR(NVL(a.value_changed_sub / 100, 0)) value_changed_sub,
       TO_CHAR(a.action_count) action_count,
       a.consume_place,
       a.res_no,
       a.consume_goods,
       a.change_goods,
       a.partner_code,
       a.update_time,
       a.update_staff_id,
       a.update_depart_id,
       a.remark
  FROM tf_b_trade_scoresub a, td_s_scoretype b
 WHERE trade_id = TO_NUMBER(:PARA_CODE)
   AND a.score_type_code = b.score_type_code(+)