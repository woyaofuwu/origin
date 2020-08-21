SELECT T.BUSIFORM_NODE_ID,
       T.STEP_ID,
       T.BUSIFORM_ID,
       T.ACCEPT_MONTH,
       T.EXT_ID,
       T.TRANS_CODE,
       T.TRANS_VALUE,
       T.DATA0,
       T.DATA1,
       T.DATA2,
       T.DATA3,
       T.DATA4,
       T.DATA5,
       T.DATA6,
       T.DATA7,
       T.DATA8,
       T.DATA9
  FROM TF_B_EWE_TRANSFER T
 WHERE T.BUSIFORM_ID = :BUSIFORM_ID
