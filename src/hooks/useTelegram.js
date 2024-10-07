import {useEffect, useState} from "react";

const initTg = window.Telegram?.WebApp

export function useTelegram() {
    const [tg, setTg] = useState(null);
    const [webAppUser, setUser] = useState(null);

    useEffect(() => {
        if (initTg) {
            const webApp = window.Telegram.WebApp;
            setTg(webApp);

            try {
                webApp.expand();
                webApp.ready();
                setUser(webApp.initDataUnsafe.user);
            } catch (error) {
                console.error('Error initializing Telegram Web App:', error);
            }
        }
    }, [initTg]);

    const onClose = () => {
        tg?.close();
    };

    return {
        onClose,
        tg,
        webAppUser,
        isReady: !!tg,
    };
}