--IS_CACHE=Y
SELECT CORP_NO PARA_CODE,CORP_NO||'|'||CORP_NAME PARA_NAME 
	FROM TD_R_TERMINAL_CORP WHERE 1=1 
	AND COOP_TYPE_CODE = :COOP_TYPE_CODE 
	AND VALID_FLAG = :VVALID_FLAG 
	ORDER BY CORP_NAME