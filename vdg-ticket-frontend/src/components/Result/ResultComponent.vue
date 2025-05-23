<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useServerStatusStore } from "@/store/serverAcceptanceStatus";
import { ticketGetRequest } from "@/services/resultService";
import { useToast } from "vue-toastification";

const toast = useToast();
const serverStatusStore = useServerStatusStore();

const ticketBase64 = ref("");
const url = ref('https://');
const headline = ref('');
const currentDate = ref('');

onMounted(() => {
  serverStatusStore.setNewUserInput(false);
  getEncryptedTicket();
});

const getEncryptedTicket = async () => {
  ticketGetRequest()
    .then((response) => {
      if (response.status === 200) {
        console.log(response.status, " - GET Encrypted ticket from server");
        ticketBase64.value = response.data;
        serverStatusStore.setGotResult(true);
        toast.info("Ticket is available", {
          timeout: 3000,
        });
        currentDate.value = getCurrentDate();
        headline.value = "The ticket was created on " + currentDate.value;
      }
    })
    .catch((error) => {
      if (error.response) {
        console.log(error.response.status, " - Error Response:", error.response.data);
        headline.value = "The ticket could not be created. Please refresh the page.";
        ticketBase64.value = '';
      }
    });
};

const copyToClipboard = async () => {
  try {
    await navigator.clipboard.writeText(ticketBase64.value);
    toast.info("Text has been copied", {
      timeout: 3000,
    });
  } catch (err) {
    console.error("Failed to copy text:", err);
  }
};

const handleURLSubmit = () => {

  if (url.value !== 'https://' && ticketBase64.value !== '') {
    const completeURL = url.value + ticketBase64.value;

    console.log("Redirected to " + completeURL);

    window.open(completeURL, '_blank')?.focus();
  }
};

const getCurrentDate = (): string => {
  const dateNow = new Date();
  return addNullIfOneDigit(dateNow.getDate())
    + '.' + addNullIfOneDigit(dateNow.getMonth() + 1)
    + '.' + addNullIfOneDigit(dateNow.getFullYear())
    + ' at ' + addNullIfOneDigit(dateNow.getHours())
    + ':' + addNullIfOneDigit(dateNow.getMinutes())
    + ':' + addNullIfOneDigit(dateNow.getSeconds());
};

const addNullIfOneDigit = (num: number): string => {
  return num < 10 ? '0' + num : '' + num;
};

defineExpose({ getEncryptedTicket });
</script>

<template>
  <v-container>
    <v-container>
      <strong>{{ headline }}</strong>
    </v-container>
    <v-container class="mt-n14">
      <div class="text-end">
        <v-btn color="info" prepend-icon="mdi-reload" size="small" @click="getEncryptedTicket">Refresh</v-btn>
      </div>
      <v-textarea v-model="ticketBase64" auto-grow variant="outlined" />
      <div class="text-end">
        <v-btn color="info" size="small" type="submit" variant="elevated" @click="copyToClipboard"
          class="mt-n10">Copy</v-btn>
      </div>
    </v-container>
    <v-container class="mt-n10">
      URL (e.g. https://your-url/easylogin?ticket=)
      <v-text-field v-model="url" variant="outlined"></v-text-field>
      <v-btn @click="handleURLSubmit" border="opacity-25 sm">Send</v-btn>
    </v-container>
  </v-container>
</template>

<style scoped></style>