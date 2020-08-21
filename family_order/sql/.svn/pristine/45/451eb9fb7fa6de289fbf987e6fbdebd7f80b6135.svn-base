SELECT (select bcyc_id from td_a_acycpara where acyc_id=a.acyc_id) para_code1,to_char(:PARA_CODE1) para_code2,
(select score_type_name from td_s_scoretype where score_type_code=a.integral_type_code) para_code3,
a.integral_fee para_code4,a.update_time para_code5,a.update_staff_id para_code6,a.update_depart_id para_code7,
(select nvl(score_value,0) from tf_f_user_score where partition_id=mod(to_number(:PARA_CODE2),10000) and user_id=a.user_id  and score_type_code='e') para_code8,
(select nvl(score_value,0) from tf_f_user_score where partition_id=mod(to_number(:PARA_CODE2),10000) and user_id=a.user_id  and score_type_code='f') PARA_CODE9 , 
'' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 ,'' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME,'' subsys_code,0 param_attr,'' param_code,'' param_name
 from tf_ah_integralbill a
where a.partition_id=mod(to_number(:PARA_CODE2),10000)
		AND a.user_id=to_number(:PARA_CODE2)
                AND a.integral_type_code in ('1','5','6','8')
		AND a.acyc_id BETWEEN 
                ( select b.acyc_id from td_a_acycpara b where b.bcyc_id = TO_NUMBER(:PARA_CODE3) ) AND 
                ( select c.acyc_id from td_a_acycpara c where c.bcyc_id = TO_NUMBER(:PARA_CODE4) )
   AND  (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) and
      (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and  
      (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
      (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
      (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
      (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
  ORDER BY a.acyc_id