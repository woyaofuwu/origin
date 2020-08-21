--IS_CACHE=Y
SELECT TRADE_TYPE_CODE, EPARCHY_CODE, BRAND_CODE, PRODUCT_ID, RULE_TYPE_CODE, RULE_ID, EXEC_ORDER, RIGHT_CODE, 
		(case when sysdate between START_DATE and END_DATE then 'F0A' else 'F0X' end) STATE, 
		to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, 
		LOCK_FLAG, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID,
		REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5
 FROM TD_RULE_FLOW t
where (t.trade_type_code = -1 or t.trade_type_code = :TRADE_TYPE_CODE)
 and (t.brand_code = 'ZZZZ' or t.brand_code = :BRAND_CODE)
 and (t.eparchy_code = 'ZZZZ' or t.eparchy_code = :EPARCHY_CODE)
 and (t.product_id = -1 or t.product_id = :PRODUCT_ID)
 and t.rule_id like '%' || :RULE_ID || '%'
 and t.rule_type_code = :RULE_TYPE_CODE     
 order by rule_type_code, exec_order