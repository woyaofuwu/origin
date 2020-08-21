SELECT card_type_code,eparchy_code,
           SUM(NVL(para_value2, '0')) para_value2,
           SUM(NVL(para_value3, '0')) para_value3,
           SUM(NVL(para_value4, '0')) para_value4,
           SUM(NVL(para_value5, '0')) para_value5,
           SUM(NVL(para_value6, '0')) para_value6,
           SUM(NVL(para_value7, '0')) para_value7,
           SUM(NVL(para_value8, '0')) para_value8,
           SUM(NVL(to_char(para_value9), '0')) para_value9,
           SUM(NVL(to_char(para_value10), '0')) para_value10,
           SUM(NVL(to_char(para_value11), '0')) para_value11,
           SUM(NVL(to_char(para_value12), '0')) para_value12,
           SUM(NVL(to_char(para_value13), '0')) para_value13,
           SUM(NVL(to_char(para_value14), '0')) para_value14,
           SUM(NVL(to_char(para_value15), '0')) para_value15,
           SUM(NVL(to_char(para_value16), '0')) para_value16,
           SUM(NVL(to_char(para_value17), '0')) para_value17,
           SUM(NVL(to_char(para_value18), '0')) para_value18
      FROM tf_b_resdaystat_log a
     WHERE stat_type||''=:STAT_TYPE
       AND para_value1=:PARA_VALUE1
       AND rdvalue1>=TO_DATE(:RDVALUE_S, 'YYYY-MM-DD HH24:MI:SS')
       AND rdvalue1<=TO_DATE(:RDVALUE_E, 'YYYY-MM-DD HH24:MI:SS')
       AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE) 
  GROUP BY  card_type_code, eparchy_code