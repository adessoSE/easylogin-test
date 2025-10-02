<script setup lang="ts">
import { ref, watch, defineExpose, reactive } from 'vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { ticketPutRequest } from '@/services/ticketService';
import { useToast } from 'vue-toastification';
import type { VForm } from 'vuetify/components/VForm';

const toast = useToast();

const formRef = ref<VForm | null>(null);
const serverStatusStore = useServerStatusStore();
const validationStatus = ref<string>('');
const hasTicketDataChanged = ref(false);

const ticketData = reactive({
    ticketId: '',
    targetId: '',
    targetUserId: '',
    issuerUserId: '',
    userIPAddress: '',
    authLevel: '',
    authMethod: '',
    issuerId: ''
});

watch(() => ({ ...ticketData }),
    (newVal, oldVal) => {
        hasTicketDataChanged.value = true;
    },
    { deep: true }
);

const validateAndSubmit = async (): Promise<boolean> => {
    const result = await formRef.value?.validate();
    const { valid, errors } = result || {};

    if (!valid) {
        return false;
    }
    return await submitToBackend();
};

const submitToBackend = async (): Promise<boolean> => {
    if (hasTicketDataChanged.value) {
        hasTicketDataChanged.value = false;
        return await ticketPutRequest(ticketData)
            .then(response => {
                if (response.status === 202) {
                    validationStatus.value = 'accepted';
                    serverStatusStore.setNewUserInput(true);
                    console.log(response.status, ' - Ticket was accepted');
                    toast.success("Ticket was accepted!", { timeout: 3000 });
                    return true;
                }
                validationStatus.value = 'rejected';
                console.log("error - Ticket was rejected");
                toast.error("Ticket was rejected!", { timeout: 3000 });
                return false;
            }).catch(error => {
                if (error.response) {
                    validationStatus.value = 'rejected';
                    console.log(error.response.status, ' - Error Response Data:', error.response.data);
                }
                return false;
            });
    } else {
        // Daten haben sich nicht geändert
        return serverStatusStore.ticketAccepted === 'accepted';
    }
};

watch(validationStatus, (newValidationStatus) => {
    if (newValidationStatus === 'accepted') {
        serverStatusStore.setIsTicketAccepted('accepted');
    } else {
        serverStatusStore.setIsTicketAccepted('rejected');
    }
});

const required = (inputField: any) => !!inputField || 'Field is required';

defineExpose({ validateAndSubmit });
</script>

<template>
    <v-form ref="formRef">
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.ticketId" label="TicketID"
            variant="outlined"></v-text-field>
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.targetId" label="TargetID"
            variant="outlined"></v-text-field>
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.issuerUserId" label="Issuer UserID"
            variant="outlined"></v-text-field>
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.targetUserId" label="Target UserID"
            variant="outlined"></v-text-field>
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.userIPAddress" label="User IP-Address"
            variant="outlined"></v-text-field>
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.authLevel" label="Authentification Level"
            variant="outlined"></v-text-field>
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.authMethod" label="Authentification Method"
            variant="outlined"></v-text-field>
        <v-text-field :rules="[required]" class="mb-1" v-model="ticketData.issuerId" label="IssuerId"
            variant="outlined"></v-text-field>
    </v-form>
</template>

<style></style>
