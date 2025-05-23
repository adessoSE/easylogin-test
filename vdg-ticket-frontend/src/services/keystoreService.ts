import { apiClientFormData } from "./apiClientService";
 

export const keystorePutRequest = async (keystoreFile: File, keystoreAlias: string, keystorePassword: string, keystoreAliasPassword: string, keystoreDigitalSignature: string) => {

    const form = new FormData();
    form.append("keystoreFile", keystoreFile);
    form.append("keystoreAlias", keystoreAlias);
    form.append("keystorePassword", keystorePassword);
    form.append("keystoreAliasPassword", keystoreAliasPassword);
    form.append("keystoreDigitalSignature", keystoreDigitalSignature);
    
    return await apiClientFormData.put('/keystore', form);
};
  