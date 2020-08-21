UPDATE TF_A_COMLOT
   SET rsrv_num1=nvl(rsrv_num1,0)+1  
 WHERE mconsign_id=TO_NUMBER(:CONSIGN_ID)