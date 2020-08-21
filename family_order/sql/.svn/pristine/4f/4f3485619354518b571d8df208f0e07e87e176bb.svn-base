SELECT a.card_type_code,a.value_code,a.eparchy_code,a.depart_id,to_char(sum(a.oper_num)) oper_num,to_char(sum(b.value_price*a.oper_num)) para_value1
  FROM tf_b_res_inout_log a,td_m_resvalue b
 WHERE a.res_type_code = b.res_type_code
   AND a.value_code = b.value_code
   AND oper_time>=TO_DATE(:OPER_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND oper_time<=TO_DATE(:OPER_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:OPER_FLAG is null or a.oper_flag=:OPER_FLAG)
   AND (:RES_TYPE_CODE is null or a.res_type_code=:RES_TYPE_CODE)
   AND (:CARD_TYPE_CODE is null or a.card_type_code=:CARD_TYPE_CODE)
   AND (:VALUE_CODE is null or a.value_code=:VALUE_CODE)
   AND (:EPARCHY_CODE is null or a.eparchy_code=:EPARCHY_CODE)
   AND (:DEPART_ID is null or a.depart_id=:DEPART_ID)
   AND (:DEPART_ID_MORE is null or a.depart_id in (select depart_id from td_s_assignrule b 
               where b.eparchy_code=:EPARCHY_CODE
                AND  b.res_type_code='1'
                AND  b.depart_frame like '%'||:DEPART_ID_MORE||'%'
                AND  a.depart_id=b.depart_id))
  GROUP BY a.eparchy_code,a.depart_id ,a.card_type_code,a.value_code