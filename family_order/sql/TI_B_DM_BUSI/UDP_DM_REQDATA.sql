UPDATE TI_B_DM_BUSI
   SET phonenum=:PHONENUM,imeinum=:IMEINUM,resultstatus=:RESULTSTATUS,replydate=sysdate,terminalid=:TERMINALID,softwareeditionnum=:SOFTWAREEDITIONNUM,configureagreement=:CONFIGUREAGREEMENT,
 begintime=TO_DATE(:BEGINTIME,'YYYY-MM-DD HH24:MI:SS'),endtime=TO_DATE(:ENDTIME,'YYYY-MM-DD HH24:MI:SS'),add_tag=:ADD_TAG,failtype=:FAILTYPE,failreason=:FAILREASON
 WHERE ibsysid=:IBSYSID