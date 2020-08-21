SELECT (select area_name from td_m_area where area_code =a.city_code) para_code1,a.serial_number para_code2,b.cust_name para_code3,a.cust_name para_code4,to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') para_code5, '' para_code6,'' para_code7,'' 
para_code8,'' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME,'' subsys_code,0 param_attr,'' param_code,'' param_name
 from tf_bh_trade a,tf_b_trade_cust_bak b
where a.city_code=:PARA_CODE1
      AND a.accept_date >= to_date(:PARA_CODE2, 'yyyy-mm-dd') 
      AND a.accept_date  < to_date(:PARA_CODE3, 'yyyy-mm-dd') +1 
      AND a.trade_type_code =TO_NUMBER(:PARA_CODE4)
      AND a.trade_id = b.trade_id
      AND a.cancel_tag = '0'
      AND  (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) and
           (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and
           (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
           (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
           (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
           (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)