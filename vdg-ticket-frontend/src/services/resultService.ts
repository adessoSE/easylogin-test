import { apiClient } from "./apiClientService";


export const ticketGetRequest = async () => {
    return await apiClient.get("/ticket");
};