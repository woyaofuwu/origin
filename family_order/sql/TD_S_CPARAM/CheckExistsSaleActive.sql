SELECT COUNT(1) RECORDCOUNT
  FROM TF_F_USER_SALE_ACTIVE A
 WHERE A.END_DATE > SYSDATE
   AND A.SERIAL_NUMBER = (SELECT REPLACE(U.SERIAL_NUMBER, 'KD_', '')
                              FROM TF_F_USER U
                             WHERE U.USER_ID = :USER_ID
                               AND U.REMOVE_TAG = '0')
   AND A.Product_Id = '66004002'