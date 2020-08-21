SELECT count(1) recordcount
  FROM tf_f_relation_uu a
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND user_id_b = TO_NUMBER(:USER_ID_B)
   AND EXISTS(SELECT 1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND para_code1=a.relation_type_code
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
   AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
   AND end_date+0 > sysdate