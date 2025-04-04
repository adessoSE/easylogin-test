<script setup lang="ts">
import { ref, watch } from 'vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import {ticketPutRequest} from '@/services/ticketService'

const serverStatusStore = useServerStatusStore();

const ticketId = ref('');
const targetId = ref('');
const targetUserId = ref('');
const issuerUserId = ref('');
const userIPAddress = ref('');
const authLevel = ref('');
const authMethod = ref('');
const issuerId = ref('');
const validationStatus = ref<string>('');
const isFormValid = ref(false);

const onSubmit = async () => {
    ticketPutRequest(ticketId.value,
        targetId.value,
        targetUserId.value,
        issuerUserId.value,
        userIPAddress.value,
        authLevel.value,
        authMethod.value,
        issuerId.value)
        .then(data => {
            if (data.status === 202) {
                validationStatus.value = 'accepted';
                serverStatusStore.setNewUserInput(true);
                console.log(data.status, ' - accepted');
            } else {
                validationStatus.value = 'rejected';
                console.log("error");
            }
        }).catch(error => {
            if (error.response) {
                validationStatus.value = 'rejected';
                console.log(error.response.status, ' - Error Response Data:', error.response.data);
            }
        });
};

watch(validationStatus, (newValidationStatus) => {
    if (newValidationStatus === 'accepted') {
        serverStatusStore.setIsTicketAccepted('accepted');
    } else {
        serverStatusStore.setIsTicketAccepted('rejected');
    }
})

const required = (inputField: any) => !!inputField || 'Field is required';
</script>


<template>
    <v-form ref="form" v-model="isFormValid" @submit.prevent>
        <v-text-field :rules="[required]" class="mb-2" v-model="ticketId" label="TicketID" variant="underlined">
        </v-text-field>
        <v-text-field :rules="[required]" class="mb-2" variant="underlined" label="TargetID" v-model="targetId">
        </v-text-field>
        <v-text-field :rules="[required]" class="mb-2" v-model="issuerUserId" label="Issuer UserID"
            variant="underlined">
        </v-text-field>
        <v-text-field :rules="[required]" class="mb-2" v-model="targetUserId" label="Target UserID"
            variant="underlined">
        </v-text-field>
        <v-text-field :rules="[required]" class="mb-2" v-model="userIPAddress" label="User IP-Address"
            variant="underlined">
        </v-text-field>
        <v-text-field :rules="[required]" class="mb-2" v-model="authLevel" label="Authentification Level"
            variant="underlined">
        </v-text-field>
        <v-text-field :rules="[required]" class="mb-2" v-model="authMethod" label="Authentification Method"
            variant="underlined">
        </v-text-field>
        <v-text-field :rules="[required]" class="mb-2" v-model="issuerId" label="IssuerId" variant="underlined">
        </v-text-field>
        <v-btn color="success" size="large" type="submit" variant="elevated" :disabled="!isFormValid" @click="onSubmit">
            Save
        </v-btn>
    </v-form>
</template>

<style></style>
