select count(*) recordcount
from tf_a_payrelation
where exists (select count(*) from tf_a_payrelation
where acct_id =( select acct_id from tf_a_payrelation
                where user_id = to_number(:USER_ID)
                and DEFAULT_TAG='1'
                and end_acyc_id>=(select acyc_id from td_a_acycpara b
                where sysdate between b.acyc_start_time and b.acyc_end_time )
               )
AND DEFAULT_TAG='1'
and end_acyc_id>=(select acyc_id from td_a_acycpara b
                  where sysdate between b.acyc_start_time and b.acyc_end_time )
having count(*) = 1 )
and  acct_id=( select acct_id from tf_a_payrelation
                where user_id = to_number(:USER_ID)
                and DEFAULT_TAG='1'
                and end_acyc_id>=(select acyc_id from td_a_acycpara b
                where sysdate between b.acyc_start_time and b.acyc_end_time )
              )
AND END_ACYC_ID>=(select acyc_id from td_a_acycpara b
                  where sysdate between b.acyc_start_time and b.acyc_end_time  )
AND DEFAULT_TAG='0'
and payitem_code<>-1