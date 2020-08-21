SELECT ROWNUM id,res_type_code,staff_id,para_value2,rsrv_tag1,rsrv_tag2,para_value9 
  FROM
 (SELECT res_type_code,staff_id,para_value2,rsrv_tag1,rsrv_tag2,SUM(NVL(para_value9,0)) para_value9
    FROM tf_b_resdaystat_log
   WHERE res_type_code=:RES_TYPE_CODE
     AND (:CARD_TYPE_CODE IS NULL OR (:CARD_TYPE_CODE IS NOT NULL AND para_value2=:CARD_TYPE_CODE))
     AND depart_id||''=:DEPART_ID
     AND staff_id=:STAFF_ID
     AND (:RSRV_TAG1 IS NULL OR (:RSRV_TAG1 IS NOT NULL AND rsrv_tag1=:RSRV_TAG1))
     AND (:RSRV_TAG2 IS NULL OR (:RSRV_TAG2 IS NOT NULL AND rsrv_tag2=:RSRV_TAG2))
     AND (:PARA_VALUE1 IS NULL OR (:PARA_VALUE1 IS NOT NULL AND para_value1=:PARA_VALUE1))
     GROUP BY res_type_code,staff_id,para_value2,rsrv_tag1,rsrv_tag2)