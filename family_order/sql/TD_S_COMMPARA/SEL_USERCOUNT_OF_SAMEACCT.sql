SELECT TO_CHAR(acct_id) para_code1,TO_CHAR(COUNT(1)) para_code2,
'' para_code3,
'' para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_a_payrelation
 WHERE acct_id = (SELECT acct_id FROM tf_a_payrelation
                   WHERE user_id = TO_NUMBER(:PARA_CODE1)
                     AND partition_id = MOD(TO_NUMBER(:PARA_CODE1),10000)
                     AND default_tag = '1' AND act_tag = '1'
                     AND (SELECT acyc_id FROM td_a_acycpara WHERE acyc_start_time <= SYSDATE AND acyc_end_time >= SYSDATE) BETWEEN start_acyc_id AND end_acyc_id)
   AND default_tag = '1' AND act_tag = '1'
   AND (SELECT acyc_id FROM td_a_acycpara WHERE acyc_start_time <= SYSDATE AND acyc_end_time >= SYSDATE) BETWEEN start_acyc_id AND end_acyc_id
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
 GROUP BY acct_id