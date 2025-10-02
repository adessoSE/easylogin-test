<script setup lang="ts">
import Publickey from '@/components/Encryption/EncryptionComponent.vue';
import KeystoreComponent from '@/components/Keystore/KeystoreComponent.vue';
import Result from '@/components/Result/ResultComponent.vue';
import TicketComponent from '@/components/Ticket/TicketComponent.vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { onMounted, ref, watch } from 'vue';

const tabs = ["Keystore", "Ticket", "Public Key", "Results"];
const step = ref<number>(0);
const isValid = ref(false);
const isProcessing = ref(false);

const keystoreForm = ref();
const ticketForm = ref();
const encryptionForm = ref();
const resultForm = ref();

onMounted(() => {
    serverStatusStore.setIsKeystoreAccepted('');
    serverStatusStore.setIsTicketAccepted('');
    serverStatusStore.setIsPublicKeyAccepted('');
    step.value = 0;
});

const serverStatusStore = useServerStatusStore();

watch(() => serverStatusStore.gotResult, (newStatus) => {
    if (newStatus === true) {
        setTimeout(() => {
            serverStatusStore.setGotResult(false);
        }, 2000);
    }
});

const next = async () => {
    if (isProcessing.value) {
        return;
    }

    try {
        isProcessing.value = true;
        isValid.value = false;
        switch (step.value) {
            case 0:
                // Keystore Tab
                isValid.value = await keystoreForm.value?.validateAndSubmit();
                break;
            case 1:
                // Ticket Tab 
                isValid.value = await ticketForm.value?.validateAndSubmit();
                break;

            case 2:
                // Public Key Tab
                isValid.value = await encryptionForm.value?.validateAndSubmit();
                break;
            case 3:
                // Result Tab 
                isValid.value = true;
                break;

            default:
                isValid.value = false;
        }

        if (isValid.value && step.value < tabs.length - 1) {
            step.value++;
        }
    } catch (error) {
        console.error("ERROR: ", error);
    } finally {
        isProcessing.value = false;
    }
};

const back = () => {
    if (step.value > 0) {
        step.value--;
    }
};
</script>

<template>
    <v-container max-width="700px">
        <v-stepper v-model="step" show-actions class="opacity-25 sm">
            <v-stepper-header>
                <v-container>
                    <v-stepper-item title="Keystore"
                        :subtitle="serverStatusStore.keystoreAccepted === 'accepted' ? 'Accepted' : (serverStatusStore.keystoreAccepted === 'rejected' ? 'Rejected' : undefined)"
                        :color="serverStatusStore.keystoreAccepted === 'accepted' ? 'green' : undefined"
                        :completed="serverStatusStore.keystoreAccepted === 'accepted'"
                        :rules="[() => serverStatusStore.keystoreAccepted !== 'rejected']" value="0"
                        icon="mdi-lock-outline">
                    </v-stepper-item>
                </v-container>

                <v-divider></v-divider>

                <v-container>
                    <v-stepper-item title="Ticket"
                        :subtitle="serverStatusStore.ticketAccepted === 'accepted' ? 'Accepted' : (serverStatusStore.ticketAccepted === 'rejected' ? 'Rejected' : undefined)"
                        :color="serverStatusStore.ticketAccepted === 'accepted' ? 'green' : undefined"
                        :completed="serverStatusStore.ticketAccepted === 'accepted'"
                        :rules="[() => serverStatusStore.ticketAccepted !== 'rejected']" value="1"
                        icon="mdi-format-page-break">
                    </v-stepper-item>
                </v-container>

                <v-divider></v-divider>

                <v-container>
                    <v-stepper-item title="Public Key"
                        :subtitle="serverStatusStore.publicKeyAccepted === 'accepted' ? 'Accepted' : (serverStatusStore.publicKeyAccepted === 'rejected' ? 'Rejected' : undefined)"
                        :color="serverStatusStore.publicKeyAccepted === 'accepted' ? 'green' : undefined"
                        :completed="serverStatusStore.publicKeyAccepted === 'accepted'"
                        :rules="[() => serverStatusStore.publicKeyAccepted !== 'rejected']" value="2"
                        icon="mdi-download-lock">
                    </v-stepper-item>
                </v-container>

                <v-divider></v-divider>

                <v-container>
                    <v-stepper-item title="Result"
                        :color="(serverStatusStore.keystoreAccepted === 'accepted' && serverStatusStore.ticketAccepted === 'accepted' && serverStatusStore.publicKeyAccepted === 'accepted') ? 'green' : undefined"
                        value="3" icon="mdi-tray-arrow-down">
                    </v-stepper-item>
                </v-container>
            </v-stepper-header>
            <v-window v-model="step">
                <v-window-item :value="0">
                    <KeystoreComponent ref="keystoreForm" class="pa-4" />
                </v-window-item>
                <v-window-item :value="1">
                    <TicketComponent ref="ticketForm" class="pa-4" />
                </v-window-item>
                <v-window-item :value="2">
                    <Publickey ref="encryptionForm" class="pa-4" />
                </v-window-item>
                <v-window-item :value="3">
                    <Result ref="resultForm" class="pa-4" />
                </v-window-item>
            </v-window>
            <v-stepper-actions @click:next="next" @click:prev="back" prev-text="Back" next-text="Save" />
        </v-stepper>
    </v-container>
</template>

<style scoped>
.v-stepper-header {
    min-height: 50px !important;
    height: 80px !important;
    padding: 0 !important;
    margin: 0 !important;
}

:deep(.v-stepper-item__avatar) {
    width: 32px !important;
    height: 32px !important;
    font-size: 0.8rem !important;
    margin: 1 !important;
}

:deep(.v-stepper-item__title) {
    font-size: 0.9rem !important;
    line-height: 1.2 !important;
    white-space: nowrap !important;
    overflow: visible !important;
    text-overflow: unset !important;

}

:deep(.v-stepper-item__subtitle) {
    font-size: 0.85rem !important;
    line-height: 1.2 !important;
}

:deep(.v-stepper-item__content) {
    padding-top: 0 !important;
    padding-bottom: 0 !important;
    align-items: center !important;
}

:deep(.v-stepper-item) {
    padding: 0 8px !important;
    margin: 0 !important;
}

:deep(.v-divider) {
    border-color: #000000 !important;
    border-width: 2px !important;
    border-style: solid !important;
    margin: 0 10px !important;
}
</style>