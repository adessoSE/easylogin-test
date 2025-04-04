<script setup lang="ts">
import Publickey from '@/components/Encryption/EncryptionComponent.vue';
import KeystoreComponent from '@/components/Keystore/KeystoreComponent.vue';
import Result from '@/components/Result/ResultComponent.vue';
import TicketComponent from '@/components/Ticket/TicketComponent.vue';
import { useServerStatusStore } from '@/store/serverAcceptanceStatus';
import { onMounted, watch } from 'vue';

onMounted(() => {
    serverStatusStore.setIsKeystoreAccepted('');
    serverStatusStore.setIsTicketAccepted('');
    serverStatusStore.setIsPublicKeyAccepted('');
})

const serverStatusStore = useServerStatusStore();

watch(() => serverStatusStore.gotResult, (newStatus, oldStatus) => {
    if(newStatus === true){
        setTimeout(() => {
            serverStatusStore.setGotResult(false);
        }, 2000);
    }
});
</script>

<template>
    <v-container max-width="700px">
      <v-stepper>
        <v-stepper-header>
            <v-container>
                <v-stepper-item
                    v-if="serverStatusStore.keystoreAccepted === 'accepted'"
                    title="Keystore"
                    value="1"
                    editable
                    complete
                    style="background-color: lightgreen; border-radius: 25px;"
                ></v-stepper-item>
                <v-stepper-item
                    v-if="serverStatusStore.keystoreAccepted === 'rejected'"
                    title="Keystore"
                    value="1"
                    editable
                    :rules="[() => false]"
                    style="background-color: lightcoral; border-radius: 25px;"
                >
                </v-stepper-item>
                <v-stepper-item
                v-if="serverStatusStore.keystoreAccepted === ''"
                    title="Keystore"
                    value="1"
                    editable
                    style="background-color: lightgrey; border-radius: 25px;"
                >
                </v-stepper-item>
            </v-container>
            <v-divider></v-divider>
            <v-container>
                <v-stepper-item
                    v-if="serverStatusStore.ticketAccepted === 'accepted'"
                    title="Ticket"
                    value="2"
                    editable
                    complete
                    style="background-color: lightgreen; border-radius: 25px;"
                ></v-stepper-item>
                <v-stepper-item
                    v-if="serverStatusStore.ticketAccepted === 'rejected'"
                    title="Ticket"
                    value="2"
                    editable
                    style="background-color: lightcoral; border-radius: 25px;"
                ></v-stepper-item>
                <v-stepper-item
                    v-if="serverStatusStore.ticketAccepted === ''"
                    title="Ticket"
                    value="2"
                    editable
                    style="background-color: lightgrey; border-radius: 25px;"
                >
                </v-stepper-item>
            </v-container>
            <v-divider></v-divider>
            <v-container>
                <v-stepper-item
                    v-if="serverStatusStore.publicKeyAccepted === 'accepted'"
                    title="Encryption" 
                    value="3" 
                    editable
                    complete
                    style="background-color: lightgreen; border-radius: 25px;"
                    >
                </v-stepper-item>
                <v-stepper-item
                    v-if="serverStatusStore.publicKeyAccepted === 'rejected'"
                    title="Encryption" 
                    value="3" 
                    editable
                    style="background-color: lightcoral; border-radius: 25px;"
                    >
                </v-stepper-item>
                <v-stepper-item
                    v-if="serverStatusStore.publicKeyAccepted === ''"
                    title="Encryption" 
                    value="3" 
                    editable
                    style="background-color: lightgrey; border-radius: 25px;"
                    >
                </v-stepper-item>
            </v-container>
            <v-divider></v-divider>
            <v-container>
                <v-stepper-item 
                title="Result" 
                value="4"
                v-if="serverStatusStore.keystoreAccepted === '' 
                        || serverStatusStore.ticketAccepted === '' 
                        ||serverStatusStore.publicKeyAccepted === ''
                        && serverStatusStore.gotResult === false"
                >
                </v-stepper-item>
                <v-stepper-item 
                    title="Result" 
                    value="4" 
                    v-if="(serverStatusStore.keystoreAccepted === 'accepted' 
                            && serverStatusStore.ticketAccepted === 'accepted' 
                            && serverStatusStore.publicKeyAccepted === 'accepted')"
                    editable
                    style="background-color: lightgreen; border-radius: 25px;"
                    >
                </v-stepper-item>
            </v-container>
        </v-stepper-header>
        <v-stepper-window>
            <v-window-item value="1">
                <KeystoreComponent/>
            </v-window-item>
            <v-window-item value="2">
                <TicketComponent/>
            </v-window-item>
            <v-window-item value="3">
                <Publickey/>
            </v-window-item>
            <v-window-item value="4">
                <Result/>
            </v-window-item>
        </v-stepper-window>
      </v-stepper>
    </v-container>
</template>

<style scoped>

</style>