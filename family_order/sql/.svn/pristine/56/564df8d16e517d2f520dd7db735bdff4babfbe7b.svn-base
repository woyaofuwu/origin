SELECT SERIAL_NUMBER,
    CUST_NAME,
    LOCAL_NATIVE_CODE,
    PHONE,
    HOME_ADDRESS,
    WORK_NAME,
    EPARCHY_CODE,
    CITY_CODE,
    PSPT_TYPE_CODE,
    PSPT_ID PSPT_ID,
    PSPT_ADDR,
    SEX,
    POST_ADDRESS,
    POST_CODE,
    POST_PERSON,
    CONTACT,
    CONTACT_PHONE,
    REMOVE_TAG,
    OPEN_DATE,
    DESTROY_TIME,
    USER_ID,
    IN_STAFF_ID,
    IN_DEPART_ID,
    OPEN_STAFF_ID,      
    OPEN_DEPART_ID
FROM
(SELECT A.SERIAL_NUMBER SERIAL_NUMBER,
          B.CUST_NAME CUST_NAME,
          B.LOCAL_NATIVE_CODE,
          B.PHONE,
          B.HOME_ADDRESS,
          B.WORK_NAME,
          B.EPARCHY_CODE EPARCHY_CODE,
          B.CITY_CODE CITY_CODE,
          B.PSPT_TYPE_CODE PSPT_TYPE_CODE,
          B.PSPT_ID PSPT_ID,
          B.PSPT_ADDR,
          B.SEX,
          B.POST_ADDRESS,
          B.POST_CODE,
          B.POST_PERSON,
          B.CONTACT,
          B.CONTACT_PHONE,
          A.REMOVE_TAG REMOVE_TAG,
          TO_CHAR(A.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,
          TO_CHAR(A.DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS') DESTROY_TIME,
          TO_CHAR(A.USER_ID) USER_ID,
          A.IN_STAFF_ID,
          A.IN_DEPART_ID,
          A.OPEN_STAFF_ID,      
          A.OPEN_DEPART_ID
     FROM TF_F_CUST_PERSON   B,
          TF_F_USER          A,
					tf_f_customer      c
    WHERE A.CUST_ID = B.CUST_ID
		    and a.cust_id=c.cust_id
				and c.partition_id = mod(a.cust_id, 10000)
        AND B.CUST_NAME = :CUST_NAME
				AND B.PSPT_ID=:PSPT_ID
				and a.remove_tag = '0'
				and a.city_code not in ('HNSJ', 'HNHN')
				and c.cust_name = :CUST_NAME
				and c.pspt_id = :PSPT_ID
				and c.remove_tag = '0'
				and c.is_real_name = '1'
				and exists (SELECT 1
							 FROM tf_F_user_product p
							where p.user_id = a.user_id
								and p.partition_id = mod(a.user_id, 10000)
								and p.main_tag = '1'
								and p.brand_code in ('G001', 'G002', 'G010', 'G005', 'PWLW')
								and sysdate between p.start_date and p.end_date) 
   )    
ORDER BY OPEN_DATE