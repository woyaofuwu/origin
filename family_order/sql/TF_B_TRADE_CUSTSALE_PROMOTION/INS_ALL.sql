insert into TF_B_TRADE_CUSTSALE_PROMOTION
(TRADE_ID,ACCEPT_MONTH,OPER_NUMBER,VERIFY_TIME,ORDER_TIME,ORDER_CODE,BIZ_CATALOG,OPER_IDENTIFY,SERIAL_NUMBER,PROMOTION_CODE,START_DATE,END_DATE)
values(to_number(:TRADE_ID),to_number(:ACCEPT_MONTH),:OPER_NUMBER,to_date(:VERIFY_TIME,'YYYYMMDDhh24miss'),to_date(:ORDER_TIME,'YYYYMMDDhh24miss'),:ORDER_CODE,:BIZ_CATALOG,
:OPER_IDENTIFY,:SERIAL_NUMBER,:PROMOTION_CODE,to_date(:START_DATE,'YYYY-MM-DD hh24:mi:ss'),to_date(:END_DATE,'YYYY-MM-DD hh24:mi:ss'))