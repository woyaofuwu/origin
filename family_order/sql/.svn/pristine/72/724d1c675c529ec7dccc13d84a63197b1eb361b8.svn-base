INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,start_date,end_date,update_time)
SELECT MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),-1,discnt_code,'0',TRUNC(SYSDATE),TO_DATE('20501231','yyyymmdd'),sysdate
  FROM td_b_product_discnt
 WHERE product_id = :PRODUCT_ID
   AND force_tag = '1'
   AND SYSDATE BETWEEN start_date AND end_date
union
SELECT MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),-1,discnt_code,'0',TRUNC(SYSDATE),TO_DATE('20501231','yyyymmdd')
  FROM td_b_product_discnt a
 WHERE product_id = :PRODUCT_ID
   AND forcegroup_tag='1'
   AND force_tag != '1'
   AND SYSDATE BETWEEN start_date AND end_date
   AND exists (select 1 from tf_f_user_discnt
               where partition_id=mod(TO_NUMBER(:USER_ID),10000)
                 and user_id=TO_NUMBER(:USER_ID)
                 and discnt_code=a.discnt_code
                 and end_date=(select max(end_date) from tf_f_user_discnt c
                                where partition_id=mod(TO_NUMBER(:USER_ID),10000)
                                and user_id=TO_NUMBER(:USER_ID)
                                and exists (select 1 from td_b_product_discnt
                                                    where product_id = :PRODUCT_ID
                                                      AND forcegroup_tag='1'
                                                      AND SYSDATE BETWEEN start_date AND end_date
                                                      AND discnt_code=c.discnt_code)))