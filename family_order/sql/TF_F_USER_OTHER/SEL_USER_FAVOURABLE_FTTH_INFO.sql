 Select t1.* From TF_F_USER t,TF_F_USER_OTHER T1
 where T.USER_ID=T1.USER_ID
 AND T.REMOVE_TAG='0'
 and t1.rsrv_value_code='FTTH'
 AND t1.rsrv_tag1='0'
 AND t1.rsrv_tag3='1'
 AND T.SERIAL_NUMBER=:SERIAL_NUMBER