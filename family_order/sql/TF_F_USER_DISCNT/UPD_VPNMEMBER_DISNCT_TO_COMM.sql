update tf_f_user_discnt a
set spec_tag='0'
where user_id=to_number(:USER_ID)
 and user_id_a=to_number(:USER_ID_A)
 and partition_id=mod(to_number(:USER_ID),10000)
 and spec_tag='2'
 and exists (select 1 from tf_f_user_discnt
             where user_id=to_number(:USER_ID_A)
              and partition_id=mod(to_number(:USER_ID_A),10000)
              and user_id_a=-1
              and discnt_code=a.discnt_code
              and spec_tag='0')