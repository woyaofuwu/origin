 select s.param_code,s.param_name,s.para_code1,s.para_code2,s.para_code20, TO_CHAR(t.USER_ID_A) USER_ID_A,t.DISCNT_CODE
 from (SELECT TO_CHAR(A.USER_ID) USER_ID,  
       TO_CHAR(A.USER_ID_A) USER_ID_A,                                                                                  
       A.DISCNT_CODE                                                                                               
   FROM TF_F_USER_DISCNT A                           
   WHERE A.USER_ID = TO_NUMBER(:USER_ID)                    
   AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)                                  
   AND A.START_DATE + 0 < A.END_DATE                            
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE 
     ) t,TD_S_COMMPARA s 
 where  TO_CHAR(t.DISCNT_CODE )=s.param_code 
 and    s.subsys_code=:SUBSYS_CODE
 and    s.param_attr=:PARAM_ATTR
 and    s.para_code1=:PARA_CODE1