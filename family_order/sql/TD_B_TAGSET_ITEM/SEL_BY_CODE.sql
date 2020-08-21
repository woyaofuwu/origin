--IS_CACHE=Y
SELECT tagset_code,tag_index,tag_name,tag_value,tag_valuecomment,rsrv_str1,rsrv_str2,remark 
  FROM td_b_tagset_item
 WHERE tagset_code=:TAGSET_CODE
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code ='ZZZZ')