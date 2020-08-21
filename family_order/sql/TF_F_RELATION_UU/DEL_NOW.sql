DELETE FROM tf_f_relation_uu
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND user_id_b = TO_NUMBER(:USER_ID_B)
   AND ((relation_type_code NOT IN (SELECT relation_type_code FROM td_s_relation WHERE relation_kind = 'F') --排除亲情关系
   		AND (end_date > SYSDATE OR start_date = TRUNC(SYSDATE)))
   		OR (relation_type_code='17'
   				AND SYSDATE<TO_DATE(TO_CHAR(ADD_MONTHS(start_date,orderno),'YYYY-MM')||'-01','YYYY-MM-DD')))