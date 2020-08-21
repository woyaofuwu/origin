select TO_CHAR(add_months(sysdate,1),'YYYY-MM-DD')||' 23:59:59' TICKET_END_DATE,t.* 
from tf_F_user t 
where t.SERIAL_NUMBER=:SERIAL_NUMBER 
AND T.REMOVE_TAG='0' 
and t.ACCT_TAG='0' 
--AND T.OPEN_MODE='0'