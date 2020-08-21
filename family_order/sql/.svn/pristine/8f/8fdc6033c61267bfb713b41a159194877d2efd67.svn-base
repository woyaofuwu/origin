select a.*  from Tf_f_User_Sale_Active a,td_s_commpara b
where a.user_id=:USER_ID
AND A.PRODUCT_ID=B.PARAM_CODE
AND B.PARAM_ATTR='7111'
AND SYSDATE < B.END_DATE
AND SYSDATE < A.END_DATE
and sysdate < a.start_date