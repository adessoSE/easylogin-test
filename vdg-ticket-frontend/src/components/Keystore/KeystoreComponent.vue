<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { keystorePutRequest } from '@/services/keystoreService';
import { useToast } from 'vue-toastification';
import type { VForm } from 'vuetify/components/VForm';

const toast = useToast();
const serverStatusStore = useServerStatusStore();

const formRef = ref<VForm | null>(null);
const hasKeystoreDataChanged = ref(false);

const keystoreData = reactive({
    keystoreAlias: '',
    keystorePassword: '',
    keystoreAliasPassword: '',
    keystoreDigitalSignature: ''
});

const keystoreFile = ref();
const enableKeystoreAliasPasswordTextfield = ref(false);
const validationStatus = ref<string>('');
const isKeystoreFileFilled = ref(false);

watch(() => ({ ...keystoreData }),
    (newVal, oldVal) => {
        hasKeystoreDataChanged.value = true;
    },
    { deep: true }
)

const handleFileChange = async (event: Event) => {
    const target = event.target as HTMLInputElement;

    if (target.files && target.files.length > 0) {
        keystoreFile.value = target.files[0];
        isKeystoreFileFilled.value = true;
        hasKeystoreDataChanged.value = true;
    }
};

watch(validationStatus, (newValidationStatus) => {
    if (newValidationStatus === 'accepted') {
        serverStatusStore.setIsKeystoreAccepted('accepted');
    } else {
        serverStatusStore.setIsKeystoreAccepted('rejected');
    }
});

watch(enableKeystoreAliasPasswordTextfield, (newStatus) => {
    if (newStatus === true) {
        keystoreData.keystoreAliasPassword = keystoreData.keystorePassword;
    }
});

const clearFileInputField = () => {
    keystoreFile.value = null;
    isKeystoreFileFilled.value = false;
};

const validateAndSubmit = async (): Promise<boolean> => {
    console.log("keystoreForm validateAndSubmit");
    const result = await formRef.value?.validate();
    const { valid, errors } = result || {};

    if (!valid) {
        return false;
    }
    return await submitToBackend();
};

const submitToBackend = async (): Promise<boolean> => {
    if (enableKeystoreAliasPasswordTextfield.value === true) {
        keystoreData.keystoreAliasPassword = keystoreData.keystorePassword;
    }

    if (hasKeystoreDataChanged.value) {
        hasKeystoreDataChanged.value = false;
        const backendResponse = await keystorePutRequest(keystoreFile.value,
            keystoreData.keystoreAlias,
            keystoreData.keystorePassword,
            keystoreData.keystoreAliasPassword,
            keystoreData.keystoreDigitalSignature)
            .then(response => {
                if (response.status === 202) {
                    validationStatus.value = 'accepted';
                    serverStatusStore.setNewUserInput(true);
                    console.log(response.status, ' - Keystore was accepted');
                    toast.success("Keystore was accepted!", {
                        timeout: 3000
                    });
                }
                return true;
            }).catch(error => {
                if (error.response) {
                    validationStatus.value = 'rejected';
                    console.log(error.response.status, ' Keystore was rejected - Error Response:', error.response.data);
                    toast.error("Keystore was rejected! " + error.response.data, { timeout: 5000 });
                }
                return false;
            });

        return backendResponse;
    }
    if (serverStatusStore.keystoreAccepted === 'rejected') return false;
    return true;
};

const required = (inputField: any) => !!inputField || 'Field is required';

defineExpose({ validateAndSubmit });
</script>

<template>
    <v-form ref="formRef" validate-on="submit">
        <v-file-input chips @change="handleFileChange" @click:clear="clearFileInputField" label="Keystore File"
            variant="outlined" :rules="[required]" required></v-file-input>
        <v-text-field v-model="keystoreData.keystorePassword" label="Keystore Password" type="password" persistent-hint
            :rules="[required]" variant="outlined" required></v-text-field>
        <v-text-field v-model="keystoreData.keystoreAlias" label="Keystore Alias" type="text" persistent-hint
            :rules="[required]" variant="outlined" required></v-text-field>
        <v-text-field v-model="keystoreData.keystoreAliasPassword" label="Alias Password" type="password"
            persistent-hint variant="outlined" :rules="[required]" required
            :disabled="enableKeystoreAliasPasswordTextfield"></v-text-field>
        <v-checkbox label="Are the keystore and keystore alias passwords identical?"
            v-model="enableKeystoreAliasPasswordTextfield"></v-checkbox>
        <v-select chips label="Digital Signature" :items="['SHA256withRSA']" variant="outlined"
            v-model="keystoreData.keystoreDigitalSignature" :rules="[required]" required></v-select>
    </v-form>
</template>

<style scoped></style>