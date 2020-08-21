select count(1) recordcount
from tf_a_payrelation  a
where a.acct_id=TO_NUMBER(:ACCT_ID)
and TO_CHAR(SYSDATE,'YYYYMM') BETWEEN  a.start_cycle_id and a.end_cycle_id
and rownum < 2