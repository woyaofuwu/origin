select u.serial_number, u.user_id, c.cust_name ,c.cust_id
  from tf_f_customer c, tf_f_user u
 where 1 = 1
   and c.cust_id = u.cust_id 
   and c.is_real_name = '1'
   and u.remove_tag = '0'
   and c.remove_tag = '0'
   and C.PARTITION_ID = MOD(TO_NUMBER(C.CUST_ID),10000)
   and U.PARTITION_ID = MOD(TO_NUMBER(U.USER_ID),10000)
   and C.PSPT_TYPE_CODE = (:PSPT_TYPE_CODE)
   and C.PSPT_ID = (:PSPT_ID)
   and not exists ( SELECT 1 FROM UCR_CRM1.TF_F_ONEPSPTMULTIUSER O WHERE O.USER_ID=U.USER_ID AND O.SERIAL_NUMBER=U.SERIAL_NUMBER AND O.REMOVE_TAG='0')