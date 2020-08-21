SELECT RES_NO PARA_CODE1,
       
       '' PARA_CODE2,
       
       '' PARA_CODE3,
       
       '' PARA_CODE4,
       
       '' PARA_CODE5,
       
       '' PARA_CODE6,
       
       '' PARA_CODE7,
       
       '' PARA_CODE8,
       
       '' PARA_CODE9,
       
       '' PARA_CODE10,
       
       '' PARA_CODE11,
       
       '' PARA_CODE12,
       
       '' PARA_CODE13,
       
       '' PARA_CODE14,
       
       '' PARA_CODE15,
       
       '' PARA_CODE16,
       
       '' PARA_CODE17,
       
       '' PARA_CODE18,
       
       '' PARA_CODE19,
       
       '' PARA_CODE20,
       
       '' PARA_CODE21,
       
       '' PARA_CODE22,
       
       '' PARA_CODE23,
       
       '' PARA_CODE24,
       
       '' PARA_CODE25,
       
       '' PARA_CODE26,
       
       '' PARA_CODE27,
       
       '' PARA_CODE28,
       
       '' PARA_CODE29,
       
       '' PARA_CODE30,
       
       '' START_DATE,
       
       '' END_DATE,
       
       '' EPARCHY_CODE,
       
       '' REMARK,
       
       '' UPDATE_STAFF_ID,
       
       '' UPDATE_DEPART_ID,
       
       '' UPDATE_TIME,
       
       '' SUBSYS_CODE,
       
       0 PARAM_ATTR,
       
       '' PARAM_CODE,
       
       '' PARAM_NAME
  FROM TF_B_SIMCARD_FLOW A
 WHERE A.EPARCHY_CODE = :PARA_CODE1
      
   AND A.RES_NO=:PARA_CODE2
   
   AND (STOCK_POS_1='00004' OR STOCK_POS_2='00004' OR
        STOCK_POS_3='00004' OR STOCK_POS_4='00004' OR
        STOCK_POS_5='00004' OR STOCK_POS_6='00004' OR
        STOCK_POS_7='00004' OR STOCK_POS_8='00004' OR
        STOCK_POS_9='00004' OR STOCK_POS_10='00004' OR
        STOCK_POS_11='00004' OR STOCK_POS_12='00004' OR
        STOCK_POS_13='00004' OR STOCK_POS_14='00004' OR
        STOCK_POS_15='00004' OR STOCK_POS_16='00004' OR
        STOCK_POS_17='00004' OR STOCK_POS_18='00004' OR
        STOCK_POS_19='00004' OR STOCK_POS_20='00004' OR
        STOCK_POS_21='00004' OR STOCK_POS_22='00004' OR
        STOCK_POS_23='00004' OR STOCK_POS_24='00004' OR
        STOCK_POS_25='00004' OR STOCK_POS_26='00004' OR
        STOCK_POS_27='00004' OR STOCK_POS_28='00004' OR
        STOCK_POS_29='00004' OR STOCK_POS_30='00004'        
        )
      
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
      
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
      
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
      
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
      
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
      
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
      
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
      
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)