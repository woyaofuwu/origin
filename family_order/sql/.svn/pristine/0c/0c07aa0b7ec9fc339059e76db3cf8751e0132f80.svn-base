				 SELECT DISTINCT T.TRADE_ID,
												 T.USER_ID,
												 T.RSRV_VALUE_CODE,
												 T.RSRV_NUM1,
												 T.RSRV_STR11,
												 T.RSRV_STR21,
												 T.RSRV_STR22,
												 T.RSRV_STR23,
												 T.RSRV_TAG1,
												 T.RSRV_DATE1,
												 T.RSRV_DATE2,
												 T.START_DATE,
												 T.END_DATE,
												 T.MODIFY_TAG,
												 T.UPDATE_TIME,
												 T.UPDATE_STAFF_ID,
												 T.UPDATE_DEPART_ID,
												 T.REMARK,
												 TRADE.ORDER_ID,
												 CGROUP.CUST_NAME,
												 MERCH.MERCH_ORDER_ID,
												 MERCHP.PRODUCT_ORDER_ID
					      FROM TF_B_TRADE_OTHER      T,
								TF_B_TRADE            TRADE,
								TF_B_TRADE_GRP_MERCHP MERCHP,
								TF_B_TRADE_GRP_MERCH  MERCH ,
								TF_F_CUST_GROUP CGROUP
								WHERE T.RSRV_STR1 = 'BBOSS_MANAGE'
						   AND T.RSRV_NUM10 IS NULL	 
						AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(T.TRADE_ID, 5, 2))
						AND T.MODIFY_TAG = '0'
						AND T.TRADE_ID = MERCHP.TRADE_ID
						AND T.ACCEPT_MONTH = TRADE.ACCEPT_MONTH
						AND MERCHP.TRADE_ID = TRADE.TRADE_ID		 
						AND TRADE.CUST_ID = CGROUP.CUST_ID
						AND MERCH.TRADE_ID IN
								(SELECT TRADE_ID
									 FROM TF_B_TRADE
									WHERE ORDER_ID = TRADE.ORDER_ID)
						AND TRADE.CUST_ID = CGROUP.CUST_ID
						AND t.rsrv_str20= :IBSYSID 