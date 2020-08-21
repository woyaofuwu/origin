--IS_CACHE=Y
SELECT ELEMENT_TYPE_CODE_A, ELEMENT_ID_A, ELEMENT_TYPE_CODE_B, ELEMENT_ID_B, LIMIT_TAG, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, EPARCHY_CODE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK
  FROM td_b_element_limit
 WHERE element_type_code_a=:ELEMENT_TYPE_CODE
   AND element_type_code_b=:ELEMENT_TYPE_CODE
   AND element_id_a=:ELEMENT_ID_A
   AND limit_tag=:LIMIT_TAG
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')