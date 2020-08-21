select SEQ_ID,
       ACCEPT_MONTH,
       SERIAL_NUMBER,
       OPR_NUMBER,
       CARD_TYPE,
       WLAN_CARD_SEQ,
       OPR_TYPE,
       OPR_TYPE,
       STATE,
       to_char(t.OPR_DATE, 'yyyy-mm-dd hh24:mi:ss') OPR_DATE,
       OPR_STAFF_ID,
       OPR_DEPART_ID,
       PAY_LOG_ID,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3
  from TF_B_WLAN_FEE_CARD_LOG t
 where 1 = 1
   and t.opr_type = :OPR_TYPE
   and t.state = :STATE
   and t.serial_number = :SERIAL_NUMBER
   and (t.opr_date > to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') or
       :START_DATE is null)
   and (t.opr_date < to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') or
       :END_DATE is null)
   and (t.WLAN_CARD_SEQ = :WLAN_CARD_SEQ or :WLAN_CARD_SEQ is null)
 order by opr_date desc