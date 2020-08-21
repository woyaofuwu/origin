SELECT  INFO_RECV_ID,FORB_BADNESS_NUMBER,FORB_REPORT_NUMBER,FORB_OPERATE_TYPE,FORB_STAFF_ID,FORB_UPDATE_TIME,RECV_CONTENT FROM TF_F_FORBIDDEN_POINT T
 WHERE 1=1
 and (T.INFO_RECV_ID = :INFO_RECV_ID OR :INFO_RECV_ID IS NULL)
 and T.FORB_BADNESS_NUMBER like  :BADNESS_NUMBER
 and T.FORB_REPORT_NUMBER like :REPORT_NUMBER
 and  trim(T.FORB_OPERATE_TYPE)=:OPERATE_TYPE
 and T.FORB_UPDATE_TIME >= to_date(:START_DATE, 'yyyy-mm-dd')
 and T.FORB_UPDATE_TIME <=(to_date(:END_DATE, 'yyyy-mm-dd') + 1)
 order by T.FORB_UPDATE_TIME desc