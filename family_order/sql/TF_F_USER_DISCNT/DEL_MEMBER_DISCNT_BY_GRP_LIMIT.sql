UPDATE tf_f_user_discnt
   SET end_date=TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) - 1/24/3600  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND user_id_a=TO_NUMBER(:USER_ID_A)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND end_date > sysdate
   AND NOT EXISTS (
      SELECT 1 FROM TD_S_COMMPARA
      WHERE SUBSYS_CODE='BMS'
        AND PARAM_ATTR='12'
        AND PARAM_CODE=:RELATION_TYPE_CODE
        AND INSTR(para_code1||para_code2||para_code3||para_code4,'|'||discnt_code||'|')>0
        AND SYSDATE BETWEEN start_date AND end_date
        AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
   )