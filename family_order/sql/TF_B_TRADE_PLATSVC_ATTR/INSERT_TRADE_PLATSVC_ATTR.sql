INSERT INTO TF_B_TRADE_PLATSVC_ATTR 
		(TRADE_ID, ACCEPT_MONTH, USER_ID, SERVICE_ID, SERIAL_NUMBER, OPER_CODE, 
		INFO_CODE, INFO_VALUE, INFO_NAME, START_DATE, END_DATE, MODIFY_TAG, 
		UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, 
		RSRV_STR1, RSRV_STR2, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3) 
VALUES (to_number(:TRADE_ID), to_number(:ACCEPT_MONTH), to_number(:USER_ID), 
		to_number(:SERVICE_ID), :SERIAL_NUMBER, :OPER_CODE, :INFO_CODE, 
		:INFO_VALUE, :INFO_NAME, to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'), 
		to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'), :MODIFY_TAG, 
		to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'), :UPDATE_STAFF_ID, 
		:UPDATE_DEPART_ID, :REMARK, to_number(:RSRV_NUM1), to_number(:RSRV_NUM2), 
		:RSRV_STR1, :RSRV_STR2, to_date(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss'), 
		to_date(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss'), 
		to_date(:RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss'))