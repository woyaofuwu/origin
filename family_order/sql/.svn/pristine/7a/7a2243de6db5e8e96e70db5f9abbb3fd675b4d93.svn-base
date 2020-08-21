select nvl(sum(adjust_fee),0) recordcount
 from tf_a_adjustblog where  acyc_id
>=(select min(acyc_id) from td_a_acycpara where use_tag = '0'
) AND user_id=:USER_ID