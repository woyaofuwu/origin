select 
       case
         when sysdate > t.end_date then
          'EXP'
         else
          'IN'
       end DATE_STATE, 
			 TO_CHAR(T.END_DATE,'YYYY-MM-DD HH24:MI:SS') DATE_LIMIT,t.ACCEPT_MONTH
,t.USER_ID
,t.RULE_ID
,t.RULE_NAME
,t.GOODS_NUM
,t.REMAIN_NUM
,t.GET_NUM
,t.SCORE
,t.QUANCODE
,t.GET_QUANCODE
,t.SERIAL_NUMBER
,t.STATE
,t.START_DATE
,t.END_DATE
,to_char(t.GET_DATE,'yyyy-mm-dd hh24:mi:ss') GET_DATE
,t.UPDATE_TIME
,t.UPDATE_STAFF_ID
,t.UPDATE_DEPART_ID
,t.EXCHANGE_TIME
,t.EXCHANGE_STAFF_ID
,t.EXCHANGE_DEPART_ID
,t.TRADE_ID
,t.REMARK
,t.RSRV_NUM1
,t.RSRV_NUM2
,t.RSRV_NUM3
,t.RSRV_NUM4
,t.RSRV_NUM5
,t.RSRV_STR1
,t.RSRV_STR2
,t.RSRV_STR3
,t.RSRV_STR4
,t.RSRV_STR5
,t.RSRV_DATE1
,t.RSRV_DATE2
,t.RSRV_DATE3
,t.RSRV_TAG1
,t.RSRV_TAG2
,t.RSRV_TAG3
  from tl_b_user_Score_GOODS t,tf_f_user u
 where t.serial_number = :SERIAL_NUMBER
 and t.user_id = u.user_id
 and t.serial_number = u.serial_number
 and u.remove_tag = '0'
 order by t.trade_id desc,t.rule_id,t.state