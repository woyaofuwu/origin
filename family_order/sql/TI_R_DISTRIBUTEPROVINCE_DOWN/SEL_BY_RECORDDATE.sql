SELECT CPORDERCODE CPORDER_CODE,MATERIALCODE MATERIAL_CODE,IMEITYPE USE_TYPE,DISTRIBUTEIMEI TERMINAL_ID,OPRNUMB OPER_TRADE_ID,OPRFLAG,'0' ARRIVAL_STATE 
FROM TI_R_DISTRIBUTEPROVINCE_DOWN
WHERE 1 = 1
AND DEALFLAG = :DEALFLAG
AND OPRFLAG = :OPRFLAG
AND RECORDDATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')