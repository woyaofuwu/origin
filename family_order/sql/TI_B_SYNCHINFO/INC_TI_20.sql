INSERT INTO TI_B_POSTINFO (SYNC_SEQUENCE,MODIFY_TAG,TRADE_ID,PARTITION_ID,ID,ID_TYPE,POST_NAME,POST_TAG,POST_CONTENT,POST_TYPESET,POST_CYC,POST_ADDRESS,POST_CODE,EMAIL,FAX_NBR,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 )
        SELECT            :SYNC_SEQUENCE,:MODIFY_TAG,:TRADE_ID,  a.PARTITION_ID,a.ID,a.ID_TYPE,a.POST_NAME,a.POST_TAG,a.POST_CONTENT,a.POST_TYPESET,a.POST_CYC,a.POST_ADDRESS,a.POST_CODE,a.EMAIL,a.FAX_NBR,a.START_DATE,a.END_DATE,a.UPDATE_TIME,a.UPDATE_STAFF_ID,a.UPDATE_DEPART_ID,a.REMARK,a.RSRV_NUM1,a.RSRV_NUM2,a.RSRV_NUM3,a.RSRV_NUM4,a.RSRV_NUM5,a.RSRV_STR1,a.RSRV_STR2,a.RSRV_STR3,a.RSRV_STR4,a.RSRV_STR5,a.RSRV_DATE1,a.RSRV_DATE2,a.RSRV_DATE3,a.RSRV_TAG1,a.RSRV_TAG2,a.RSRV_TAG3
        FROM TF_F_POSTINFO a
        WHERE a.id = :USER_ID
        AND a.id_type = '1'