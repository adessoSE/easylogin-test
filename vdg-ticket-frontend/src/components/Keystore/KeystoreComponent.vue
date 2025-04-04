<script setup lang="ts">
import { ref, watch } from 'vue'
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { keystorePutReqeust } from '@/services/keystoreService';

const serverStatusStore = useServerStatusStore();

const keystorePassword = ref('');
const keystoreAlias = ref('');
const keystoreAliasPassword = ref('');
const keystoreDigitalSignature = ref('');
const enableKeystoreAliasPasswordTextfield = ref(false);
const validationStatus = ref<string>('');
const isFormValid = ref(false);
const isKeystoreFileFilled = ref(false);
const keystoreFile = ref();

const handleFileChange = async (event: Event) => {
    const target = event.target as HTMLInputElement;

    if (target.files && target.files.length > 0) {
        keystoreFile.value = target.files[0];
        isKeystoreFileFilled.value = true;
    }
}

watch(validationStatus, (newValidationStatus) => {
    if (newValidationStatus === 'accepted') {
        serverStatusStore.setIsKeystoreAccepted('accepted');
    } else {
        serverStatusStore.setIsKeystoreAccepted('rejected');
    }
})

watch(enableKeystoreAliasPasswordTextfield, (newStatus) => {
    if (newStatus === true) {
        keystoreAliasPassword.value = keystorePassword.value;
    }
})

const required = (inputField: any) => !!inputField || 'Field is required';

const clearFileInputField = () => {
    keystoreFile.value = null;
    isKeystoreFileFilled.value = false;
}

const onSubmit = async () => {
    if (enableKeystoreAliasPasswordTextfield.value === true) {
        keystoreAliasPassword.value = keystorePassword.value;
    }
   
    keystorePutReqeust(keystoreFile.value, 
                keystoreAlias.value, 
                keystorePassword.value, 
                keystoreAliasPassword.value, 
                keystoreDigitalSignature.value)
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
</script>

<template>
    <v-form ref="form" v-model="isFormValid" @submit.prevent>
        <v-file-input chips @change="handleFileChange" @click:clear="clearFileInputField" label="Keystore File" variant="outlined" :rules="[required]" required>
        </v-file-input>
        <v-text-field v-model="keystorePassword" label="Keystore Password" type="password" persistent-hint :rules="[required]" required>
        </v-text-field>
        <v-text-field v-model="keystoreAlias" label="Keystore Alias" type="text" persistent-hint :rules="[required]" required>
        </v-text-field>
        <v-text-field v-model="keystoreAliasPassword" label="Alias Password" type="password" persistent-hint :rules="[required]" required :disabled="enableKeystoreAliasPasswordTextfield">
        </v-text-field>
        <v-checkbox label="Are the keystore and keystore alias passwords the same?" v-model="enableKeystoreAliasPasswordTextfield">
        </v-checkbox>
        <v-select chips label="Digital Signature" :items="['SHA256withRSA']" variant="underlined" v-model="keystoreDigitalSignature" :rules="[required]" required>
        </v-select>
        <v-btn color="success" size="large" type="submit" variant="elevated" :disabled="(!isFormValid || !isKeystoreFileFilled)" @click="onSubmit">                
            Save
        </v-btn>
    </v-form>
</template>


<style scoped></style>