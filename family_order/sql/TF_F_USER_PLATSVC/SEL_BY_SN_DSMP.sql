SELECT * FROM TF_F_USER_PLATSVC
 WHERE  SERVICE_ID =:SERVICE_ID
   AND SYSDATE BETWEEN START_DATE AND END_DATE
   AND user_id=(select user_id from tf_f_user where serial_number=:SERIAL_NUMBER AND remove_tag='0')