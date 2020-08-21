select operlog.* from TL_CONTENT_OPERATELOG operlog where 1 = 1
 and operlog.STAFF_ID = :STAFF_ID
 and operlog.OPER_TYPE = :OPER_TYPE
 and operlog.OPER_TAG = :OPER_TAG
 and operlog.MENU_ID = :MENU_ID
 and operlog.OPER_TIME >= to_date(:START_DATE, 'yyyy-mm-dd')
 and operlog.OPER_TIME <= to_date(:END_DATE, 'yyyy-mm-dd')
 order by operlog.SEQ_ID DESC