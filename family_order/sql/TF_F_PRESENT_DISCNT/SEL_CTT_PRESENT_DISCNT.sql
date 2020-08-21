select partition_id,user_id,acct_id,present_serial_number,inst_id,
       present_money,to_char(present_date,'yyyy-mm-dd') present_date,
       discnt_code,discnt_inst_id,remark,rsrv_str1
from tf_f_present_discnt where 1=1
and acct_id = :ACCT_ID
and present_serial_number = :PRESENT_SERIAL_NUMBER
and present_date >= to_date(:START_DATE,'yyyy-mm-dd')
and present_date < trunc(to_date(:END_DATE,'yyyy-mm-dd'))+0.99999
order by present_date