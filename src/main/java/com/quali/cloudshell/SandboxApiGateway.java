package com.quali.cloudshell;

import com.quali.cloudshell.QsExceptions.ReserveBluePrintConflictException;
import com.quali.cloudshell.QsExceptions.SandboxApiException;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class SandboxApiGateway
{
    private final SandboxAPIProxy proxy;
    private final QsLogger logger;

    /*
        Tomer:
            TODO:
                Add Validations for parameters
                Add "test Connection" to make sure the gateway initialized properly
     */

    public SandboxApiGateway(String serverAddress, String user, String pw, String domain, boolean ignoreSSL, QsLogger qsLogger)
    {
        this.logger = qsLogger;
        this.proxy = new SandboxAPIProxy(new QsServerDetails(serverAddress, user, pw, domain, ignoreSSL), qsLogger);
    }

    public SandboxApiGateway(QsLogger qsLogger, QsServerDetails qsServerDetails)
    {
        this.logger = qsLogger;
        this.proxy = new SandboxAPIProxy(qsServerDetails, qsLogger);
    }

    public String GetSandboxDetails(String sandboxId)
            throws SandboxApiException, UnsupportedEncodingException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        logger.Info("GetSandboxDetails Starting to run");
        return proxy.SandboxDetails(sandboxId).toString();
    }

    public void StopSandbox(String sandboxId, boolean isSync)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        logger.Info("StopSandbox Starting to run");
        proxy.StopSandbox(sandboxId, isSync);
    }

    public void WaitForSandBox(String sandboxId, String status, int timeoutSec, boolean ignoreSSL) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        proxy.WaitForSandBox(sandboxId,status,timeoutSec,ignoreSSL);
    }

    public String StartBlueprint(String blueprintName, int duration, boolean isSync, String sandboxName)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnsupportedEncodingException {

        if (StringUtils.isBlank(sandboxName))
            sandboxName = blueprintName + "_" + java.util.UUID.randomUUID().toString().substring(0, 5);

        logger.Info("StartBlueprint: sandbox name set to be " + sandboxName);

        try {
            String sandboxId = proxy.StartBluePrint(blueprintName, sandboxName, duration, isSync);
            logger.Info("StartBlueprint: sandbox started with id: " + sandboxId);
            return sandboxId;
        }
        catch (ReserveBluePrintConflictException ce){
            logger.Error(Constants.BLUEPRINT_CONFLICT_ERROR);
        }
        return null;
    }
}
