SELECT a.device_id para_code1,a.rsrv_str1 para_code2,a.oper_time para_code3,b.device_model para_code4,c.color para_code5,d.stock_name para_code6,a.rsrv_str4 para_code7, '' para_code8, '' para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_rh_mobiledevice a,td_s_device_model b ,
    td_s_device_color c,tf_r_resstock_arch d
WHERE a.device_model_code=b.device_model_code
      AND b.device_type_code='0'
      AND a.color_code=c.color_code
      AND c.device_type_code='0'
      AND a.stock_id=d.stock_id
      AND ( a.device_id = :PARA_CODE1 OR :PARA_CODE1 IS NULL)
      AND ( a.rsrv_str3 = :PARA_CODE2 OR :PARA_CODE2 IS NULL ) 
      AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
      AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
      AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
      AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
      AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)