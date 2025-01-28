package tja.software.crypto.log;

import feign.FeignException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class LogForMethodAspect {

    private static final Logger logger = LoggerFactory.getLogger("Aspect Logging");


    @Around("@annotation(logForMethod)")
    public Object logInput(ProceedingJoinPoint joinPoint, LogForMethod logForMethod) {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArguments = joinPoint.getArgs();

        StringBuffer sbParametersPart = new StringBuffer();
        StringBuffer sbUUIDPart = new StringBuffer();
        StringBuffer sbResultmessage = new StringBuffer();

        String uuid = getMessageParts(sbParametersPart, sbUUIDPart, methodArguments, methodName);

        logWithLevel(logForMethod.inputUuid(), sbUUIDPart);
        logWithLevel(logForMethod.parameters(), sbParametersPart);
        Object result=null;
        try{
            result = joinPoint.proceed();
        }catch(FeignException e){
            logger.error("For request with uuid {}, error was thrown with message: {}", uuid, e.getMessage(), e);
            throw e;
        }catch(Throwable e){
            logger.error("For request with uuid {}, error was thrown with message: {}", uuid, e.getMessage(), e);
        }
        getResultMessage(sbResultmessage, result, uuid, methodName);
        logWithLevel(logForMethod.result(), sbResultmessage);

        return result;
    }

    private void getResultMessage(StringBuffer sbResultmessage, Object result, String uuid, String methodName) {
        sbResultmessage.append("Method: " + methodName);
        if(uuid!=null && !uuid.isEmpty()){
            sbResultmessage.append(" with uuid: "+uuid);
        }
        sbResultmessage.append(" finisshed with result: "+result);
    }

    private void logWithLevel(String level, StringBuffer message) {
        if(message==null || message.isEmpty()){
            return;
        }
        switch (level) {
            case "INFO":
                logger.info(message.toString());
                return;
            case "WARN":
                logger.warn(message.toString());
                return;
            case "ERROR":
                logger.error(message.toString());
                return;
            case "DEBUG":
                logger.debug(message.toString());
                return;
            case "TRACE":
                logger.trace(message.toString());
                return;
            default:
                logger.debug(message.toString());
                return;
        }
    }




    private String getMessageParts(StringBuffer sbParametersPart,StringBuffer sbUUIDPart, Object[] methodArguments, String methodName) {
        StringBuffer sbTmp = new StringBuffer();
        String uuid = null;
        boolean argumentsFound = false;
        for (int i = 0; i < methodArguments.length; i++) {
            if(!(methodArguments[i] instanceof UUID) ){
                sbTmp.append("\n"+methodArguments[i]);
                argumentsFound = true;
            }else{
                uuid =  methodArguments[i].toString();
            }
        }

        if(argumentsFound){
            sbParametersPart.append("Parameters for method ").append(methodName);
        }
        if(uuid!=null){
            sbUUIDPart.append("Method ").append(methodName).append(" started with UUID: ").append(uuid);
            sbParametersPart.append("called with uuid: ").append(uuid);
        }
        sbParametersPart.append(": ").append(sbTmp).append(") \n");

        return uuid;
    }
}
