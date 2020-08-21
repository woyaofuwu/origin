select C.USER_ID,
       C.SERIAL_NUMBER,
       TO_CHAR(C.OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE
  from TF_F_USER C
 where C.SERIAL_NUMBER = :SERIAL_NUMBER
   and C.REMOVE_TAG = :REMOVE_TAG
   and TRUNC(C.OPEN_DATE, 'mm') = TRUNC(sysdate, 'mm')
   and TO_NUMBER(TO_CHAR(C.OPEN_DATE, 'DD')) >= 25