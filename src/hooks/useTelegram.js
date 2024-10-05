import {useEffect} from "react";

const tg = window.Telegram.WebApp;

export function useTelegram() {

    useEffect(() => {
        tg.expand()
        tg.ready();
    }, []);

    const onClose = () => {
        tg.close()
    }

    return {
        onClose,
        tg,
        webAppUser: tg.initDataUnsafe.user,
    }
}