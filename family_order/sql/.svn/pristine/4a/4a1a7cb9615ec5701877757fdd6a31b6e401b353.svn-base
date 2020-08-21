SELECT a.costpriceid PARA_CODE1, b.agencyname PARA_CODE2, c.factoryname PARA_CODE3, d.device_model PARA_CODE4, e.color PARA_CODE5, a.costprice PARA_CODE6, decode(a.validateflag,'0','有效','1','无效') PARA_CODE7, to_char(a.effecttime,'yyyy-MM-dd') PARA_CODE8, to_char(a.uneffecttime,'yyyy-MM-dd') PARA_CODE9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name 
FROM TF_R_UNIFYCOSTPRICE a,TF_R_AGENCY b,TD_S_MANUFACTURER c, TD_S_DEVICE_MODEL d,TD_S_DEVICE_COLOR e 
where a.agencyid=b.agencyid
      AND a.factory_code=c.factory_code
      AND a.device_model_code=d.device_model_code
      AND a.color_code=e.color_code 
      AND d.device_type_code='0'
      AND e.device_type_code='0'
      AND ( a.agencyid=:PARA_CODE1 OR :PARA_CODE1 IS NULL )
      AND ( a.factory_code=:PARA_CODE2 OR :PARA_CODE2 IS NULL )
      AND ( a.device_model_code=:PARA_CODE3 OR :PARA_CODE3 IS NULL )
      AND ( a.color_code=:PARA_CODE4 OR :PARA_CODE4 IS NULL )
      AND ( a.validateflag=:PARA_CODE5 OR :PARA_CODE5 IS NULL )
      AND ( a.costprice>=:PARA_CODE6 OR :PARA_CODE6 IS NULL )
      AND ( a.costprice<=:PARA_CODE7 OR :PARA_CODE7 IS NULL )
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)