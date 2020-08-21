SELECT (select area_name from td_m_area where area_code =a.city_code) para_code1,a.serial_number para_code2,b.cust_name para_code3,decode(a.REMOVE_TAG,'4','欠费销号','0','欠费停机','未知') para_code4,to_char(a.destroy_time,'yyyy-mm-dd hh24:mi:ss') para_code5, '' para_code6,'' para_code7,'' 
para_code8,'' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME,'' subsys_code,0 param_attr,'' param_code,'' param_name
 from tf_f_user a,tf_f_customer b
where     ((:PARA_CODE1 is not null and b.pspt_type_code = :PARA_CODE1) or :PARA_CODE1 is null)
     AND ((:PARA_CODE2 is not null and b.pspt_id = :PARA_CODE2) or :PARA_CODE2 is null)
      AND ((:PARA_CODE3 is not null and b.cust_name =:PARA_CODE3) or :PARA_CODE3 is null)
      AND (INSTR(a.user_state_codeset,'9',1) > 0 or INSTR(a.user_state_codeset,'5',1) > 0)
      AND (a.REMOVE_TAG = '4' or a.REMOVE_TAG = '0')   
      AND a.cust_id = b.cust_id
      AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL) and
          (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) and
          (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and
          (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
          (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
          (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
          (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)