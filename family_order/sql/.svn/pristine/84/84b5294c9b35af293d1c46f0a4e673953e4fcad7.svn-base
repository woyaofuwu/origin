SELECT a.device_id para_code1,b.device_model para_code2,
j.namebrand_desc para_code3,'' para_code4,
'' para_code5,'' para_code6,'' para_code7,'' para_code8, '' PARA_CODE9 , 
'' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 ,'' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME,'' subsys_code,0 param_attr,'' param_code,'' param_name
 from tf_r_mobiledevice a,td_s_device_model b,td_s_namebrand_model_re i,td_s_namebrand j
where  a.device_id=:PARA_CODE1
   AND a.device_model_code=i.device_model_code(+)
  AND  a.device_model_code = b.device_model_code(+)
  AND  i.namebrand_code = j.namebrand_code(+)
   AND  (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)  and
        (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL) and
        (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL) and
        (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) and
        (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and
        (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
        (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
        (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
        (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)