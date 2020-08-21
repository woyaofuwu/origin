SELECT t.stock_id para_code1, m.stock_name para_code2, t.FACTORY_CODE para_code3, t.DEVICE_MODEL_CODE para_code4, t.COLOR_CODE para_code5, sum(decode(t.device_state,'B',1,0)) para_code6, sum(decode(t.device_state,'C',1,0)) para_code7, h.ALERTVALVEMIN para_code8, h.ALERTVALVEMIN para_code9, '' PARA_CODE10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_R_MOBILEDEVICE t,TF_R_RESSTOCK_ARCH m,TF_R_AGENCY i,TF_R_STOCKALERTVALVE h 
WHERE t.DEVICE_STATE IN('B','C') 
      AND m.stock_id = t.stock_id 
      AND t.RSRV_STR3=i.AGENCYID
      AND h.STOCKID(+)=t.STOCK_ID 
      AND  h.FACTORY_CODE(+)=t.FACTORY_CODE  
      AND h.DEVICE_MODEL_CODE(+)=t.DEVICE_MODEL_CODE 
      AND h.COLOR_CODE(+)=t.COLOR_CODE and h.VALIDATEFLAG(+)='0'
      AND ( t.FACTORY_CODE = :PARA_CODE1 OR :PARA_CODE1 IS NULL )
      AND ( t.DEVICE_MODEL_CODE = :PARA_CODE2 OR :PARA_CODE2 IS NULL )
      AND ( t.COLOR_CODE = :PARA_CODE3 OR :PARA_CODE3 IS NULL )
      AND ( i.AGENCYNAME like '%'||:PARA_CODE4||'%' OR :PARA_CODE4 IS NULL )
      AND ( t.stock_id = :PARA_CODE5 OR :PARA_CODE5 IS NULL )
      AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
      AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
GROUP BY t.STOCK_ID,t.FACTORY_CODE,t.DEVICE_MODEL_CODE,t.COLOR_CODE,m.stock_name,h.ALERTVALVEMIN
order by t.STOCK_ID,t.FACTORY_CODE,t.DEVICE_MODEL_CODE,t.COLOR_CODE,m.stock_name,h.ALERTVALVEMIN