select to_char(USER_ID) user_id, RENT_TYPE_CODE, SERIAL_NUMBER, SIM_CARD_NO, IMSI, INST_ID,
		RENT_SIM_CARD, RENT_IMSI, RENT_DEVICE_NO, RENT_IMEI, RENT_SERIAL_NUMBER, 
		NATIONALITY_AREACODE, to_char(START_DATE,'YYYY-MM-DD HH24:MI:HH') start_date,
     to_char(END_DATE,'YYYY-MM-DD HH24:MI:HH') end_date, 
		RENT_TAG, OPEN_STAFF_ID, 
		OPEN_DEPART_ID, to_char(RSRV_NUM1) rsrv_num1, to_char(RSRV_NUM2) rsrv_num2,
     to_char(RSRV_NUM3) rsrv_num3, to_char(RSRV_NUM4) rsrv_num4, 
		to_char(RSRV_NUM5) rsrv_num5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, 
		RSRV_STR5, to_char(RSRV_DATE1,'YYYY-MM-DD HH24:MI:HH') rsrv_date1,
     to_char(RSRV_DATE2,'YYYY-MM-DD HH24:MI:HH') rsrv_date2, 
		to_char(RSRV_DATE3,'YYYY-MM-DD HH24:MI:HH') rsrv_date3
from TF_F_USER_RENT
where user_id = to_number(:USER_ID)
and sysdate between start_date and end_date