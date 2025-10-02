<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { encryptionPutRequest } from '@/services/encryptionService';
import { useToast } from 'vue-toastification';
import type { VForm } from 'vuetify/components/VForm';

const toast = useToast();
const serverStatusStore = useServerStatusStore();

const formRef = ref<VForm | null>(null);
const hasPublicKeyDataChanged = ref(false);
const validationStatus = ref<string>('');

const ticketData = reactive({
    publicKeyString: ''
});

watch(() => ({ ...ticketData }),
    (newVal, oldVal) => {
        hasPublicKeyDataChanged.value = true;
    },
    { deep: true }
);

watch(validationStatus, (newValidationStatus) => {
    if (newValidationStatus === 'accepted') {
        serverStatusStore.setIsPublicKeyAccepted('accepted');
    } else {
        serverStatusStore.setIsPublicKeyAccepted('rejected');
    }
});

const validateAndSubmit = async (): Promise<boolean> => {
    const result = await formRef.value?.validate();
    const { valid, errors } = result || {};

    if (!valid) {
        return false;
    }
    return await submitToBackend();
};

const submitToBackend = async (): Promise<boolean> => {
    if (hasPublicKeyDataChanged.value) {
        hasPublicKeyDataChanged.value = false;

        return await encryptionPutRequest(ticketData.publicKeyString)
            .then(response => {
                if (response.status === 202) {
                    validationStatus.value = 'accepted';
                    serverStatusStore.setNewUserInput(true);
                    console.log(response.status, ' - Public Key was accepted');
                    toast.success("Public Key was accepted!", { timeout: 3000 });
                    return true;
                }
                validationStatus.value = 'rejected';
                console.log("error - Public Key was rejected");
                toast.error("Public Key was rejected!", { timeout: 3000 });
                return false;
            }).catch(error => {
                if (error.response) {
                    validationStatus.value = 'rejected';
                    console.log(error.response.status, 'Public Key was rejected - Error Response:', error.response.data);
                    toast.error("Public Key was rejected! Input is not a valid Public Key.", { timeout: 5000 });
                }
                return false;
            });
    } else {
        // Daten haben sich nicht geändert
        return serverStatusStore.publicKeyAccepted === 'accepted';
    }
};

const required = (inputField: any) => !!inputField || 'Field is required';

defineExpose({ validateAndSubmit });
</script>

<template>
    <v-form ref="formRef">
        <v-container>
            <v-textarea clearable auto-grow label="Public Key" variant="outlined" v-model="ticketData.publicKeyString"
                :rules="[required]" required></v-textarea>
        </v-container>
    </v-form>
</template>

<style scoped></style>