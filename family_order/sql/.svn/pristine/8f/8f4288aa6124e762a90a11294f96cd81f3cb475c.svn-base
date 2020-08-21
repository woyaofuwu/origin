SELECT (select area_name from td_m_area where area_code =a.city_code) para_code1,a.serial_number para_code2,b.cust_name para_code3,(select sum(money) from tf_f_user_foregift where user_id=a.user_id) para_code4,'' para_code5, '' para_code6,'' para_code7,'' 
para_code8,'' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME,'' subsys_code,0 param_attr,'' param_code,'' param_name
 from tf_f_user a,tf_f_customer b
where a.city_code=:PARA_CODE1
      AND INSTR(a.user_state_codeset,:PARA_CODE5,1) > 0
      AND a.REMOVE_TAG = :PARA_CODE4      
      AND a.destroy_time >= to_date(:PARA_CODE2, 'yyyy-mm-dd') 
      AND a.destroy_time < to_date(:PARA_CODE3, 'yyyy-mm-dd')+1 
      AND a.cust_id = b.cust_id
      AND b.partition_id = mod(a.cust_id,10000)
      AND EXISTS(SELECT 1 FROM tf_f_user_foregift c 
                    where c.user_id=a.user_id
                    AND c.MONEY > 0
                 )
      AND  (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and
           (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
           (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
           (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
           (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)