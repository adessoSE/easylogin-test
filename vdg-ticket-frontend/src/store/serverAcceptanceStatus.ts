import { defineStore } from "pinia";

export const useServerStatusStore = defineStore("serverStatusstore", {
  state: () => ({
    /* 
        Three possible values
        1. '' an empty string -> Nothing was send to the server yet
        2. 'accepted' -> The server accepted the sended data
        3. 'rejected' -> the server rejected the sended data e.g. wrong keystore password, bad public key
        */
    keystoreAccepted: "" as string,
    ticketAccepted: "" as string,
    publicKeyAccepted: "" as string,

    gotResult: false as boolean,
    newUserInput: false as boolean,
  }),
  actions: {
    setIsKeystoreAccepted(isKeystoreAccepted: string) {
      this.keystoreAccepted = isKeystoreAccepted;
    },
    setIsTicketAccepted(isTicketAccepted: string) {
      this.ticketAccepted = isTicketAccepted;
    },
    setIsPublicKeyAccepted(isPublicKeyAccepted: string) {
      this.publicKeyAccepted = isPublicKeyAccepted;
    },
    setNewUserInput(bool: boolean) {
      this.newUserInput = bool;
    },
    setGotResult(bool: boolean) {
      this.gotResult = bool;
    },
  },
});
