SELECT /*+INDEX(A,IDX_TF_BH_TRADE_USERID)*/ 
       A.SERIAL_NUMBER

  FROM TF_BH_TRADE A
 WHERE 1=1
AND A.USER_ID = TO_NUMBER(:USER_ID)
AND A.SERIAL_NUMBER = TO_NUMBER(:SERIAL_NUMBER)
AND A.TRADE_TYPE_CODE=:TRADE_TYPE_CODE
AND A.ACCEPT_DATE>(SYSDATE-183)
AND (
    A.ACCEPT_MONTH = (select to_char(add_months(trunc(sysdate),-0),'mm') from dual)
 or A.ACCEPT_MONTH = (select to_char(add_months(trunc(sysdate),-1),'mm') from dual)
 or A.ACCEPT_MONTH = (select to_char(add_months(trunc(sysdate),-2),'mm') from dual)
 or A.ACCEPT_MONTH = (select to_char(add_months(trunc(sysdate),-3),'mm') from dual)
 or A.ACCEPT_MONTH = (select to_char(add_months(trunc(sysdate),-4),'mm') from dual)
 or A.ACCEPT_MONTH = (select to_char(add_months(trunc(sysdate),-5),'mm') from dual)
 or A.ACCEPT_MONTH = (select to_char(add_months(trunc(sysdate),-6),'mm') from dual)
 )