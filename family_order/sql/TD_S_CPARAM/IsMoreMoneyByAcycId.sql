SELECT COUNT(1) recordcount
from dual
where (select nvl(sum(fee),0)
         from tf_o_credit_userbill
	 where user_id=to_number(:USER_ID)
	   and partition_id=mod(to_number(:USER_ID),10000)
	   and acyc_id in (310,311,312))>=24000