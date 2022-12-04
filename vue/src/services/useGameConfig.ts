import { Client } from "@stomp/stompjs";
import { reactive, readonly } from "vue";

interface IGameConfig {
    gamename: String
}

interface IGameConfigValidation {
    validationSuccess: Boolean
}

const valSuccess : IGameConfigValidation = reactive ( { 
    validationSuccess : false
})

async function sendConfig(gamename: string) {

    try {
        const controller = new AbortController();
        const URL = '/api/game/game-config';
        
        const data = {mapName: "test", sessionName: gamename};

        const id = setTimeout(() => controller.abort(), 8000);

        const response = await fetch(URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            signal: controller.signal,
            body: JSON.stringify(data)
        });

        const jsonData: IGameConfigValidation = await response.json();

        valSuccess.validationSuccess = jsonData.validationSuccess

        console.log(valSuccess.validationSuccess)

        clearTimeout(id);

        if(!response.ok) {
            return false;
        }
        

    } catch(reason) {
        console.log(`ERROR: Config Map failed: ${reason}`);
    }
}

export function useGameConfig() {
    return { 
        sendConfig, 
        valSuccess : valSuccess} 
}