update TL_B_USER_COUPONS t
			 set t.spend_value=:SPEND_VALUE,T.TICKET_STATE='1',T.SPEND_TIME=sysdate,
			 t.spend_staff_id=:SPEND_STAFF_ID,t.spend_depart_id=:SPEND_DEPART_ID,t.RSRV_STR1=:REPAIR_NO,t.REMARK=:REMARK 
			 where t.ticket_code=:TICKET_CODE
			 and t.serial_number=:SERIAL_NUMBER