SELECT PAY_MODE_CODE 
  FROM tf_f_account
 WHERE CUST_ID = (
                    SELECT CUST_ID
                      FROM TF_F_USER
                     WHERE SERIAL_NUMBER = :SERIAL_NUMBER
                       AND REMOVE_TAG = '0'
                 )