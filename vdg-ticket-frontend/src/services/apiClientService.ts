import axios from "axios";

const baseURL = 'http://localhost:8080';

export const apiClientFormData = 
    axios.create({
        baseURL: baseURL, 
        headers: { "Content-Type": "multipart/form-data" }
    }
);

export const apiClientApplicationJson = 
    axios.create({
        baseURL: baseURL, 
        headers: { 
            Accept: "application/json",
            "Content-Type": "application/json" 
        }
    }
);

export const apiClient = 
    axios.create({
        baseURL: baseURL
    }
);