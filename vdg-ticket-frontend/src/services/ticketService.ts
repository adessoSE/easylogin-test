import { apiClientApplicationJson } from "./apiClientService";


export const ticketPutRequest = async (
    ticketId: string, 
    targetId: string, 
    targetUserId: string, 
    issuerUserId: string, 
    userIPAddress: string, 
    authLevel: string, 
    authMethod: string, 
    issuerId: string, ) => {

    const data = {
        ticketId: ticketId,
        targetId: targetId,
        targetUserId: targetUserId,
        issuerUserId: issuerUserId,
        userIPAddress: userIPAddress,
        authLevel: authLevel,
        authMethod: authMethod,
        issuerId: issuerId,
    };

    return await apiClientApplicationJson.put('/ticket', data);
};