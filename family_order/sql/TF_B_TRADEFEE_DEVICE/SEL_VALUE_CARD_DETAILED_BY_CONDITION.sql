select  * from  TL_B_VALUECARD_DETAILED t 
   where 1=1
        AND t.TRADE_TYPE_CODE = '418'
	AND t.STATE_CODE='0'
	AND t.city_code = :CITY_CODE
	AND t.depart_code = :UPDATE_DEPART_ID
	AND t.auditordernumber = :STAFF_ID
	AND t.cust_number = :RSRV_STR7
	AND t.record_time >= :START_VALUECARD_SALE_TIME
	AND t.record_time <= :END_VALUECARD_SALE_TIME
	AND t.RSRV_STR5 >= :START_TIME5
	AND t.RSRV_STR5 <= :END_TIME5
	ORDER BY T.RSRV_STR5 DESC