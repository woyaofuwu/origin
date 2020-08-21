select * from(
select t.* 
  from TF_F_USER_DISCNT T, TD_S_COMMPARA T1
 where T.DISCNT_CODE = TO_NUMBER(T1.PARA_CODE1)
   AND T1.PARAM_ATTR = '532'
	 and t1.param_code='681'
   AND T.USER_ID = :USER_ID
	 order by t.end_date desc) where rownum=1