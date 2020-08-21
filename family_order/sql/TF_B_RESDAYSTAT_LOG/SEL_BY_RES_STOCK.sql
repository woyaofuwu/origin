SELECT SUM(NVL(para_value9,0)) para_value9 
  FROM tf_b_resdaystat_log
 WHERE oper_time>=TRUNC(SYSDATE-1)
   AND res_type_code=:RES_TYPE_CODE
   AND oper_flag=:OPER_FLAG
   AND stat_type=:STAT_TYPE
   AND eparchy_code=:EPARCHY_CODE
   AND depart_id||''=(SELECT depart_id FROM td_m_staff where staff_id=:STAFF_ID)
   AND staff_id||''=:STAFF_ID
   AND card_type_code IN (SELECT res_kind_code FROM td_s_reskind
                        WHERE  eparchy_code=:EPARCHY_CODE
                          AND res_type_code=:RES_TYPE_CODE)