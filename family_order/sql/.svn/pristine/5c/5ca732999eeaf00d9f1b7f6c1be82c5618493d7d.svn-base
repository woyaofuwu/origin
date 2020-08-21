INSERT INTO TI_O_YWTOCREDIT
  (SYNC_SEQUENCE,
   SYNC_DAY,
   MODIFY_TAG,
   TRADE_ID,
   USER_ID,
   TRADE_TYPE_CODE,
   REMARK,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID,
   UPDATE_TIME,
   EPARCHY_CODE,
   RSRV_STR1,
   RSRV_STR2,
   RSRV_STR3,
   RSRV_STR4,
   RSRV_STR5)
  SELECT :SYNC_SEQUENCE,
		 :SYNC_DAY,
		 DECODE(B.MODIFY_TAG, '0', '0', '1', '2', '2', '2', '2'),
		 :TRADE_ID,
		 USER_ID,
		 /* 除240业务以外，其他增加扣减信用度都需要传：7922 为信控业务类型:特殊信用额度重算，RSRV_STR1 填特殊额度，
		 * RSRV_STR2 填特殊额度服务类型 7908
		 * RSRV_STR3填特殊额度结束时间（时间格式使用 YYYYMMDDHH24MISS)*/
		 case when nvl(RSRV_STR10,'') = 'SUPERPOSED' then
		 to_number(7923)
		 else
		 to_number(DECODE(CREDIT_MODE,
				'addCredit',
				DECODE(:TRADE_TYPE_CODE,
					   240,
					   :TRADE_TYPE_CODE,
					   3814,
					   :TRADE_TYPE_CODE,
					   DECODE(NVL(:ATTR_VALUE,
								  'ABCD'),
							  'ABCD',
							  '7922',
							  '7960')),
				:TRADE_TYPE_CODE)) end TRADE_TYPE_CODE,
		 REMARK,
		 UPDATE_STAFF_ID,
		 UPDATE_DEPART_ID,
		 UPDATE_TIME,
		 :EPARCHY_CODE,
		 DECODE(CREDIT_MODE,
				'addCredit',
				DECODE(:CANCEL_TAG,
					   '2',
					   B.CREDIT_VALUE * (-1),
					   DECODE(:TRADE_TYPE_CODE,
							  240,
							  B.CREDIT_VALUE,
							  3814,
							  B.CREDIT_VALUE,
							  DECODE(NVL(:ATTR_VALUE,
										 'ABCD'),
									 'ABCD',
									 B.CREDIT_VALUE,
									 B.RSRV_STR4))),
				RSRV_STR1),
		case when nvl(RSRV_STR10,'') = 'SUPERPOSED' then
         to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss')
        else
		 DECODE(CREDIT_MODE,
				'addCredit',
				DECODE(:TRADE_TYPE_CODE,
					   240,
					   TO_CHAR(B.START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
					   3814,
					   TO_CHAR(B.START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
						  DECODE(NVL(:ATTR_VALUE,
								  'ABCD'),
							  'ABCD',
							  '7908',
							  '')),
				RSRV_STR2) end,
	    case when nvl(RSRV_STR10,'') = 'SUPERPOSED' and :CANCEL_TAG='2' and B.CREDIT_VALUE * (-1)>'0' then
         to_char(to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss')
		     when nvl(RSRV_STR10,'') = 'SUPERPOSED' and :CANCEL_TAG='2' and B.CREDIT_VALUE * (-1)<'0' then
         to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')
        else
		 DECODE(CREDIT_MODE,
				'addCredit',
				DECODE(RSRV_STR3,
					   'grpAddCredit',
					   RSRV_STR3,
					   'adcmasgrpAddCredit',
					   '',
					   TO_CHAR(B.END_DATE, 'YYYY-MM-DD HH24:MI:SS')),
				RSRV_STR3) end,
		 RSRV_STR4 ,
		 RSRV_STR5
	FROM TF_B_TRADE_CREDIT B
   WHERE B.TRADE_ID = :TRADE_ID
	 AND B.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))