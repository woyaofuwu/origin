insert into TF_SM_BI_BUSIMANAGE 
(BP_SERIAL_NO, 
BP_NAME, 
START_TIME, 
END_TIME, 
DATA_TIME, 
ATT_STR1, 
ATT_STR2, 
SALE_ACT_SCRIPT, 
SMS_SCRIPT, 
MATCH_CODE, 
SALE_ACT_TYPE)
values 
(
:BP_SERIAL_NO, 
:BP_NAME, 
TO_DATE(:START_TIME, 'yyyy-mm-dd'), 
TO_DATE(:END_TIME, 'yyyy-mm-dd'), 
TO_DATE(:DATA_TIME, 'yyyy-mm-dd'), 
:ATT_STR1, 
:ATT_STR2, 
:SALE_ACT_SCRIPT, 
:SMS_SCRIPT, 
:MATCH_CODE, 
:SALE_ACT_TYPE)