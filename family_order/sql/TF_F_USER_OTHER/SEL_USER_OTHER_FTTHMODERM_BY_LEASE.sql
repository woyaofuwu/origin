Select t1.* From TF_F_USER t,TF_F_USER_OTHER T1
 where T.USER_ID=T1.USER_ID
 AND T.REMOVE_TAG='0'
 AND t1.rsrv_value_code='FTTH'
 AND t1.rsrv_tag2 = '1'
 AND t1.rsrv_tag1 = '0'
 and to_number(t1.rsrv_str2)>0
 AND SYSDATE BETWEEN T1.START_DATE AND T1.END_DATE
 AND t1.trade_id = :TRADE_ID
 AND T.SERIAL_NUMBER=:SERIAL_NUMBER