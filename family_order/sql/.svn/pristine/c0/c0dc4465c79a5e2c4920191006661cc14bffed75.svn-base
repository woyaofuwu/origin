SELECT COUNT(1) RECORDCOUNT 
	FROM TF_F_USER_SALE_ACTIVE A
	WHERE A.END_DATE > SYSDATE
	   AND A.USER_ID = (SELECT R.USER_ID
	                      FROM TF_F_USER R
	                     WHERE R.SERIAL_NUMBER =
	                           (SELECT REPLACE(U.SERIAL_NUMBER, 'KD_', '')
	                              FROM TF_F_USER U
	                             WHERE U.USER_ID = :USER_ID
	                               AND U.REMOVE_TAG = '0')
	                       AND R.REMOVE_TAG = '0')
	   AND A.Product_Id = '99992832'
	   AND SYSDATE BETWEEN a.start_date AND a.start_date+365