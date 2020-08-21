select operlog.* from TL_CONTROL_OPERATELOG operlog where 1 = 1
 and operlog.STAFF_ID = :STAFF_ID
 and operlog.OPER_TYPE = :OPER_TYPE
 and operlog.OPER_TAG = :OPER_TAG
 and operlog.BIZ_CODE = :BIZ_CODE
 and operlog.TRANS_CODE = :TRANS_CODE
 and operlog.OPER_TIME >= to_date(:START_DATE, 'yyyy-mm-dd')
 and operlog.OPER_TIME <= to_date(:END_DATE, 'yyyy-mm-dd')
 order by operlog.SEQ_ID DESC