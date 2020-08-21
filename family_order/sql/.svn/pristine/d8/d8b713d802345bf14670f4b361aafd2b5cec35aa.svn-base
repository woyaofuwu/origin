SELECT B.PRODUCT_ID FROM TF_F_USER_PRODUCT B
                         WHERE 1 = 1
                           AND B.USER_ID = :USER_ID
                           AND B.PARTITION_ID = MOD(:USER_ID, 10000)                          
                           AND ROWNUM =1