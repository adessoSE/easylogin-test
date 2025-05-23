<script setup lang="ts">
import Publickey from '@/components/Encryption/EncryptionComponent.vue';
import KeystoreComponent from '@/components/Keystore/KeystoreComponent.vue';
import Result from '@/components/Result/ResultComponent.vue';
import TicketComponent from '@/components/Ticket/TicketComponent.vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { onMounted, ref, watch } from 'vue';

const tabs = ["Keystore", "Ticket", "Public Key", "Results"];
const step = ref(0);

const keystoreForm = ref();
const ticketForm = ref();
const encryptionForm = ref();
const resultForm = ref();

onMounted(() => {
    serverStatusStore.setIsKeystoreAccepted('');
    serverStatusStore.setIsTicketAccepted('');
    serverStatusStore.setIsPublicKeyAccepted('');
});

const serverStatusStore = useServerStatusStore();

watch(() => serverStatusStore.gotResult, (newStatus) => {
    if (newStatus === true) {
        setTimeout(() => {
            serverStatusStore.setGotResult(false);
        }, 2000);
    }
});

const next = () => {
    if (step.value === 0) {
        // Keystore Tab
        const valid: Promise<Boolean> = keystoreForm.value.validateAndSubmit();
        valid.then(isValid => {
            if (isValid) {
                if (step.value < tabs.length - 2) {
                    step.value++;
                }
            }
        })
    } else if (step.value === 1) {
        // Ticket Tab
        const valid: Promise<Boolean> = ticketForm.value.validateAndSubmit();
        valid.then(isValid => {
            if (isValid) {
                if (step.value < tabs.length - 2) {
                    step.value++;
                }
            }
        })
    } else if (step.value === 2) {
        // Public Key / Encryption Tab
        const valid: Promise<Boolean> = encryptionForm.value.validateAndSubmit();
        valid.then(isValid => {
            if (isValid) {
                if (step.value < tabs.length - 1) {
                    step.value++;
                }
                if (serverStatusStore.newUserInput) {
                    const newTicket: Promise<Boolean> = resultForm.value.getEncryptedTicket();
                    newTicket.then(newTicket => {
                        if (newTicket) {
                            console.log("Ticket success")
                        }
                    })
                    serverStatusStore.setNewUserInput(false);
                }
            }
        })
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
        <v-stepper v-model="step" show-actions border="opacity-25 sm">
            <v-stepper-header>
                <v-container>
                    <v-stepper-item v-if="serverStatusStore.keystoreAccepted === ''" title="Keystore" value="0"
                        icon="mdi-lock-outline"></v-stepper-item>
                    <v-stepper-item v-if="serverStatusStore.keystoreAccepted === 'accepted'" :rules="[() => true]"
                        completed color="green" title="Keystore" subtitle="Accepted" value="0"
                        icon="mdi-lock-outline"></v-stepper-item>
                    <v-stepper-item v-if="serverStatusStore.keystoreAccepted === 'rejected'" :rules="[() => false]"
                        title="Keystore" subtitle="Rejected" value="0" icon="mdi-lock-outline"></v-stepper-item>
                </v-container>

                <v-divider></v-divider>

                <v-container>
                    <v-stepper-item v-if="serverStatusStore.ticketAccepted === ''" title="Ticket" value="1"
                        icon="mdi-format-page-break"></v-stepper-item>
                    <v-stepper-item v-if="serverStatusStore.ticketAccepted === 'accepted'" :rules="[() => true]"
                        title="Ticket" subtitle="Accepted" value="1" icon="mdi-format-page-break"
                        color="green"></v-stepper-item>
                    <v-stepper-item v-if="serverStatusStore.ticketAccepted === 'rejected'" :rules="[() => false]"
                        title="Ticket" subtitle="Rejected" value="1" icon="mdi-format-page-break"></v-stepper-item>
                </v-container>

                <v-divider></v-divider>

                <v-container>
                    <v-stepper-item v-if="serverStatusStore.publicKeyAccepted === ''" title="Public Key" value="2"
                        icon="mdi-download-lock"></v-stepper-item>
                    <v-stepper-item v-if="serverStatusStore.publicKeyAccepted === 'accepted'" :rules="[() => true]"
                        completed color="green" title="Public Key" subtitle="Accepted" value="2"></v-stepper-item>
                    <v-stepper-item v-if="serverStatusStore.publicKeyAccepted === 'rejected'" :rules="[() => false]"
                        title="Public Key" subtitle="Rejected" value="2"></v-stepper-item>
                </v-container>

                <v-divider></v-divider>

                <v-container>
                    <v-stepper-item title="Result" value="3" v-if="serverStatusStore.keystoreAccepted === '' || serverStatusStore.keystoreAccepted === 'rejected'
                        || serverStatusStore.ticketAccepted === '' || serverStatusStore.ticketAccepted === 'rejected'
                        || serverStatusStore.publicKeyAccepted === '' || serverStatusStore.publicKeyAccepted === 'rejected'
                        && serverStatusStore.gotResult === false" icon="mdi-tray-arrow-down">

                    </v-stepper-item>
                    <v-stepper-item title="Result" value="3" v-if="(serverStatusStore.keystoreAccepted === 'accepted'
                        && serverStatusStore.ticketAccepted === 'accepted'
                        && serverStatusStore.publicKeyAccepted === 'accepted')" icon="mdi-tray-arrow-down"
                        color="green">
                    </v-stepper-item>
                </v-container>
            </v-stepper-header>
            <v-window v-model="step">
                <v-window-item>
                    <KeystoreComponent ref="keystoreForm" class="pa-4" />
                </v-window-item>
                <v-window-item>
                    <TicketComponent ref="ticketForm" class="pa-4" />
                </v-window-item>
                <v-window-item>
                    <Publickey ref="encryptionForm" class="pa-4" />
                </v-window-item>
                <v-window-item>
                    <Result ref="resultForm" class="pa-4" />
                </v-window-item>
            </v-window>
            <v-stepper-actions v-model="step" @click:next="next" @click:prev="back" prev-text="Back" next-text="Save">
            </v-stepper-actions>
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