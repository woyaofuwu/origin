SELECT COUNT(1) NUM FROM TF_F_USER_PLATSVC A                                              
WHERE a.user_id = TO_NUMBER(:USER_ID)                                                                 
  AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)                                                                                                                    
  AND a.sp_code = :SP_CODE                                                                        
  AND a.biz_code = :BIZ_CODE
  AND A.BIZ_STATE_CODE = 'A'
   AND TO_CHAR(a.first_date_mon,'mm')=TO_CHAR(SYSDATE,'mm')