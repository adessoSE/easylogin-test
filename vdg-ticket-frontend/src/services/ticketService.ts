import { apiClientApplicationJson } from "./apiClientService";

export const ticketPutRequest = async (data: {
  ticketId: string;
  targetId: string;
  targetUserId: string;
  issuerUserId: string;
  userIPAddress: string;
  authLevel: string;
  authMethod: string;
  issuerId: string;
}) => {
  return await apiClientApplicationJson.put("/ticket", data);
};
