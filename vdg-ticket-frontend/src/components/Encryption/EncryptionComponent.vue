<script setup lang="ts">
import { ref, watch } from 'vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { encryptionPutRequest } from '@/services/encryptionService';

const serverStatusStore = useServerStatusStore();
const validationStatus = ref<string>('');
const publicKeyString = ref('');
const isFormValid = ref(false);

watch(validationStatus, (newValidationStatus) => {
    if (newValidationStatus === 'accepted') {
        serverStatusStore.setIsPublicKeyAccepted('accepted');
    } else {
        serverStatusStore.setIsPublicKeyAccepted('rejected');
    }
})

const onSubmit = async () => {
    encryptionPutRequest(publicKeyString.value)
        .then(data => {
            if (data.status === 202) {
                validationStatus.value = 'accepted';
                serverStatusStore.setNewUserInput(true);
                console.log(data.status, ' - accepted');
            }
        }).catch(error => {
            if (error.response) {
                validationStatus.value = 'rejected';
                console.log(error.response.status, ' - Error Response:', error.response.data);
            }
        });
};

const required = (inputField: any) => !!inputField || 'Field is required';

</script>

<template>
    <v-form ref="form" v-model="isFormValid" @submit.prevent>
        <v-container>
            <v-textarea label="Public Key" variant="outlined" v-model="publicKeyString" :rules="[required]" required></v-textarea>
        </v-container>
        <v-btn color="success" size="large" type="submit" variant="elevated" :disabled="!isFormValid" @click="onSubmit">
            Save
        </v-btn>
    </v-form>
</template>

<style scoped></style>