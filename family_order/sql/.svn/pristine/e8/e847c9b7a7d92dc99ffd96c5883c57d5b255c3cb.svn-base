INSERT INTO tf_b_trade_scoresub(trade_id,accept_month,user_id,action_code,score_type_code,score_changed_sub,value_changed_sub,action_count,remark)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),TO_NUMBER(:USER_ID),-1,score_type_code,-score_value,0,1,'清积分'
  FROM tf_f_user_score
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND score_type_code in(SELECT TRIM(para_code1)
                            FROM td_s_commpara
                           WHERE subsys_code='CSM'
                             AND param_attr=350
                             AND param_code='0')