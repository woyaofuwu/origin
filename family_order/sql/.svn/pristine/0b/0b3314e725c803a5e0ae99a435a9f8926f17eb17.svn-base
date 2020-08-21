 select A.* from tf_f_relation_uu A where A.USER_ID_A IN (
  select t.User_Id_a from tf_f_relation_uu t where t.user_id_b=:USER_ID_CPE and t.relation_type_code='CP' AND T.ROLE_CODE_B='2' AND SYSDATE < t.END_DATE)
	AND A.relation_type_code='CP' AND A.ROLE_CODE_B='1'
	AND SYSDATE < A.END_DATE
	AND A.START_DATE<A.END_DATE