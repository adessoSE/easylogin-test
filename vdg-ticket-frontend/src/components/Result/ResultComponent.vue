<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { useServerStatusStore } from "@/store/serverAcceptanceStatus";
import { ticketGetRequest } from "@/services/resultService";

const serverStatusStore = useServerStatusStore();

onMounted(() => {
  serverStatusStore.setNewUserInput(false);
  getEncryptedTicket();
});

const ticketBase64 = ref("");
const isTextcopied = ref(false);

const getEncryptedTicket = async () => {
  ticketGetRequest()
    .then((data) => {
      if (data.status === 200) {
        ticketBase64.value = data.data;
        serverStatusStore.setGotResult(true);
      }
    })
    .catch((error) => {
      if (error.response) {
        console.log(
          error.response.status,
          " - Error Response:",
          error.response.data
        );
      }
    });
};

watch(
  () => serverStatusStore.newUserInput,
  (newStatus, oldStatus) => {
    if (
      serverStatusStore.keystoreAccepted === "accepted" &&
      serverStatusStore.ticketAccepted === "accepted" &&
      serverStatusStore.publicKeyAccepted === "accepted"
    ) {
      if (newStatus === true) {
        getEncryptedTicket();
        serverStatusStore.setNewUserInput(false);
        isTextcopied.value = false;
      } else {
        serverStatusStore.setNewUserInput(false);
      }
    } else {
      serverStatusStore.setNewUserInput(false);
    }
  }
);

const copyToClipboard = async () => {
  try {
    await navigator.clipboard.writeText(ticketBase64.value);
    isTextcopied.value = true;
  } catch (err) {
    console.error("Failed to copy text:", err);
  }
};
</script>

<template>
  <v-container>
    <p>Encrypted Ticket</p>
    <br />
    <v-btn v-if="isTextcopied === false" color="info" size="small" type="submit" variant="elevated" @click="copyToClipboard">
        Copy
    </v-btn>
    <v-btn v-if="isTextcopied === true" color="info" size="small" type="submit" variant="elevated" @click="copyToClipboard">
        copied
    </v-btn>
    <br/>
    <v-textarea v-model="ticketBase64" auto-grow> </v-textarea>
  </v-container>
</template>

<style scoped></style>
