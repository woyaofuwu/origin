SELECT /*+INDEX(A,IDX_TF_BH_TRADE_USERID)*/ 

       A.SERIAL_NUMBER

FROM TF_BH_TRADE A,TF_B_TRADE_OTHER T
WHERE 1=1
AND A.TRADE_ID=T.TRADE_ID
AND A.USER_ID=T.USER_ID
AND A.TRADE_TYPE_CODE= :TRADE_TYPE_CODE
AND T.RSRV_STR10 = :PSPT_ID
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