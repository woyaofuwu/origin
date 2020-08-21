update TL_B_USER_SCORE_GOODS t
 set t.remain_num=t.remain_num-1,t.get_num=t.get_num+1,t.get_quancode=:GET_QUANCODE,t.state=:STATE,T.GET_DATE=sysdate,t.update_time=sysdate,t.update_staff_id=:UPDATE_STAFF_ID,T.UPDATE_DEPART_ID=:UPDATE_DEPART_ID,T.rsrv_str1=:DEPART_NAME,t.rsrv_str2=:DEPART_AREA_CODE,t.rsrv_str3=:USER_CITY_CODE 
 where t.user_id=:USER_ID
 AND T.RULE_ID=:RULE_ID
 AND T.ACCEPT_MONTH=:ACCEPT_MONTH
 AND T.TRADE_ID=:TRADE_ID
 AND T.QUANCODE=:QUANCODE