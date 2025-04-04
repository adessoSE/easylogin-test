import { apiClientApplicationJson } from "./apiClientService";


export const encryptionPutRequest = async (publicKey: string) => {

    const data = {
        publicKey: publicKey,
    };

    return await apiClientApplicationJson.put('/encryption', data);
};