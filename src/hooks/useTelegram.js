import {useEffect} from "react";

const tg = window.Telegram.WebApp;

export function useTelegram() {

    useEffect(() => {
        tg.ready();
    }, []);

    const onClose = () => {
        tg.close()
    }


    return {
        onClose,
        tg,
        webAppUser: tg.initDataUnsafe.user,
        queryId: tg.initDataUnsafe.query_id,
    }
}