SELECT count(*) recordcount from dual
 WHERE
 ( EXISTS (select * from tf_f_user WHERE product_id IN (71,76,95)  AND remove_tag='0' AND user_Id =:USER_ID)
   AND
	 EXISTS(SELECT * FROM Tf_f_User_Infochange
	         where product_id IN(
	          select ID from td_a_transmode_limit
				       WHERE id_type='0' AND limit_tag='2'
               and para_code=:PARA_CODE
	          )
            and user_id=:USER_ID
						AND end_date>last_day(SYSDATE)+1
	 )
)
 OR EXISTS(select * from Tf_f_User_Infochange b where product_id IN (71,76,95)  AND  end_date>last_day(SYSDATE)+1 AND user_id=:USER_ID)